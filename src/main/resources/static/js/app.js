const $ = s => document.querySelector(s);
const moeda = v => Number(v).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
const pagina = document.body.dataset.pagina;

async function api(url, metodo = 'GET', corpo) {
  let r;
  try {
    r = await fetch(url, { method: metodo, headers: { 'Content-Type': 'application/json' }, body: corpo ? JSON.stringify(corpo) : undefined });
  } catch (e) {
    throw new Error('Servidor indisponível');
  }
  const dados = r.status === 204 ? null : await r.json().catch(() => null);
  if (!r.ok) throw new Error((dados && dados.erro) || 'Falha na comunicação com o servidor');
  return dados;
}

function aviso(txt, tipo) { const m = $('#msg'); m.textContent = txt; m.className = tipo; }
const falha = e => aviso(e.message, 'erro');

if (pagina !== 'login') {
  if (!sessionStorage.getItem('logado')) location.href = 'login.html';
  const links = [['index', 'Início'], ['clientes', 'Clientes'], ['produtos', 'Produtos'], ['estoque', 'Estoque'], ['venda', 'Venda']];
  $('header').innerHTML = '<b>Construção Vendas</b>' + links.map(([p, t]) =>
    `<a href="${p}.html" class="${p === pagina ? 'ativo' : ''}">${t}</a>`).join('') + '<a href="#" id="sair">Sair</a>';
  $('#sair').onclick = () => { sessionStorage.removeItem('logado'); location.href = 'login.html'; };
}

const paginas = {
  login() {
    $('form').onsubmit = async e => {
      e.preventDefault(); const f = e.target; Validacao.limpar(f);
      const ok = Validacao.obrigatorio(f.usuario, 'Usuário') & Validacao.obrigatorio(f.senha, 'Senha');
      if (!ok) return;
      try {
        await api('/api/login', 'POST', { login: f.usuario.value, senha: f.senha.value });
        sessionStorage.setItem('logado', '1'); location.href = 'index.html';
      } catch (err) { falha(err); }
    };
  },
  clientes() {
    const f = $('form'); f.cpf.oninput = () => f.cpf.value = Validacao.mascaraCpf(f.cpf.value);
    const lista = async () => {
      try {
        const cs = await api('/api/clientes');
        $('#corpo').innerHTML = cs.map(c =>
          `<tr><td>${c.nome}</td><td>${Validacao.mascaraCpf(c.cpf)}</td><td>${c.telefone || ''}</td><td>${c.endereco || ''}</td><td><button class="del" data-i="${c.id}">Excluir</button></td></tr>`).join('')
          || '<tr><td colspan="5">Nenhum cliente cadastrado.</td></tr>';
      } catch (err) { falha(err); }
    };
    f.onsubmit = async e => {
      e.preventDefault(); Validacao.limpar(f);
      let ok = Validacao.obrigatorio(f.nome, 'Nome');
      if (!Validacao.cpfValido(f.cpf.value)) ok = Validacao.erro(f.cpf, 'CPF inválido') && ok;
      if (!ok) return aviso('Corrija os campos destacados.', 'erro');
      try {
        await api('/api/clientes', 'POST', { nome: f.nome.value, cpf: f.cpf.value, telefone: f.telefone.value, endereco: f.endereco.value });
        f.reset(); aviso('Cliente cadastrado com sucesso!', 'ok'); lista();
      } catch (err) { falha(err); }
    };
    $('#corpo').onclick = async e => {
      if (!e.target.dataset.i) return;
      try { await api('/api/clientes/' + e.target.dataset.i, 'DELETE'); lista(); } catch (err) { falha(err); }
    };
    lista();
  },
  produtos() {
    const f = $('form');
    f.onsubmit = async e => {
      e.preventDefault(); Validacao.limpar(f);
      const ok = [Validacao.obrigatorio(f.nome, 'Nome'), Validacao.numeroMin(f.preco, 'Preço', 0), Validacao.numeroMin(f.quantidade, 'Quantidade', 0)].every(Boolean);
      if (!ok) return aviso('Corrija os campos destacados.', 'erro');
      try {
        await api('/api/produtos', 'POST', { nome: f.nome.value, categoria: f.categoria.value, preco: +f.preco.value, quantidade: parseInt(f.quantidade.value) });
        f.reset(); aviso('Produto cadastrado com sucesso!', 'ok');
      } catch (err) { falha(err); }
    };
  },
  estoque() {
    let produtos = [];
    const desenhar = () => {
      const t = $('#busca').value.toLowerCase();
      $('#corpo').innerHTML = produtos.filter(p => p.nome.toLowerCase().includes(t)).map(p =>
        `<tr><td>${p.nome}</td><td>${p.categoria || ''}</td><td>${moeda(p.preco)}</td>
        <td><input type="number" min="0" value="${p.quantidade}" id="q${p.id}" class="${p.quantidade < 5 ? 'baixo' : ''}"></td>
        <td><button data-id="${p.id}" data-nome="${p.nome}">Salvar</button></td></tr>`).join('') || '<tr><td colspan="5">Nenhum produto encontrado.</td></tr>';
    };
    const carregar = async () => { try { produtos = await api('/api/produtos'); desenhar(); } catch (err) { falha(err); } };
    $('#busca').oninput = desenhar;
    $('#corpo').onclick = async e => {
      if (!e.target.dataset.id) return;
      const q = parseInt($('#q' + e.target.dataset.id).value);
      if (isNaN(q) || q < 0) return aviso('Quantidade não pode ser negativa.', 'erro');
      try { await api('/api/produtos/quantidade', 'PUT', { nome: e.target.dataset.nome, quantidade: q }); aviso('Estoque atualizado!', 'ok'); carregar(); } catch (err) { falha(err); }
    };
    carregar();
  },
  async venda() {
    let carrinho = [], ps = [], cs = [];
    const f = $('form');
    const opcoesProdutos = () => '<option value="">Selecione...</option>' + ps.map((p, i) => `<option value="${i}">${p.nome} (${p.quantidade} em estoque)</option>`).join('');
    try {
      [cs, ps] = await Promise.all([api('/api/clientes'), api('/api/produtos')]);
    } catch (err) { falha(err); }
    f.cliente.innerHTML = '<option value="">Selecione...</option>' + cs.map(c => `<option value="${c.id}">${c.nome}</option>`).join('');
    f.produto.innerHTML = opcoesProdutos();
    const total = () => carrinho.reduce((s, it) => s + it.preco * it.qtd, 0);
    const desenhar = () => {
      $('#corpo').innerHTML = carrinho.map((it, i) => `<tr><td>${it.nome}</td><td>${it.qtd}</td><td>${moeda(it.preco * it.qtd)}</td><td><button class="del" data-i="${i}">Remover</button></td></tr>`).join('')
        || '<tr><td colspan="4">Carrinho vazio.</td></tr>';
      $('#total').textContent = 'Total: ' + moeda(total());
    };
    f.onsubmit = e => {
      e.preventDefault(); Validacao.limpar(f);
      if (f.produto.value === '') return Validacao.erro(f.produto, 'Selecione um produto');
      if (!Validacao.numeroMin(f.qtd, 'Quantidade', 1)) return;
      const p = ps[f.produto.value], q = parseInt(f.qtd.value);
      const jaNoCarrinho = carrinho.filter(it => it.nome === p.nome).reduce((s, it) => s + it.qtd, 0);
      if (q + jaNoCarrinho > p.quantidade) return aviso('Estoque insuficiente para o produto: ' + p.nome, 'erro');
      carrinho.push({ nome: p.nome, preco: p.preco, qtd: q }); f.qtd.value = 1; aviso('Item adicionado.', 'ok'); desenhar();
    };
    $('#corpo').onclick = e => { if (e.target.dataset.i) { carrinho.splice(e.target.dataset.i, 1); desenhar(); } };
    $('#finalizar').onclick = async () => {
      Validacao.limpar(f);
      if (f.cliente.value === '') return Validacao.erro(f.cliente, 'Selecione o cliente');
      if (!carrinho.length) return aviso('Carrinho vazio!', 'erro');
      try {
        const r = await api('/api/vendas', 'POST', { clienteId: +f.cliente.value, itens: carrinho.map(it => ({ produto: it.nome, quantidade: it.qtd })) });
        aviso('Venda finalizada. Total: ' + moeda(r.total), 'ok');
        carrinho = []; desenhar();
        ps = await api('/api/produtos'); f.produto.innerHTML = opcoesProdutos();
      } catch (err) { falha(err); }
    };
    desenhar();
  },
  index() {}
};
if (paginas[pagina]) paginas[pagina]();
