package com.vendas.web;

import com.vendas.exception.ValidacaoException;
import com.vendas.model.Cliente;
import com.vendas.model.Produto;
import com.vendas.model.Venda;
import com.vendas.repository.ProdutoRepository;
import com.vendas.service.AutenticacaoService;
import com.vendas.service.ClienteService;
import com.vendas.service.ProdutoService;
import com.vendas.service.VendaService;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    record Login(String login, String senha) {}
    record NovoCliente(String nome, String cpf, String telefone, String endereco) {}
    record NovoProduto(String nome, String categoria, int quantidade, double preco) {}
    record Quantidade(String nome, int quantidade) {}
    record ItemReq(String produto, int quantidade) {}
    record VendaReq(int clienteId, List<ItemReq> itens) {}

    private final ClienteService clientes;
    private final ProdutoService produtos;
    private final ProdutoRepository produtoRepository;
    private final AutenticacaoService autenticacao;

    public ApiController(ClienteService clientes, ProdutoService produtos,
            ProdutoRepository produtoRepository, AutenticacaoService autenticacao) {
        this.clientes = clientes;
        this.produtos = produtos;
        this.produtoRepository = produtoRepository;
        this.autenticacao = autenticacao;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Login dados) throws ValidacaoException {
        autenticacao.autenticar(dados.login(), dados.senha());
        return Map.of("ok", true);
    }

    @GetMapping("/clientes")
    public List<Cliente> listarClientes() {
        return clientes.listarTodos();
    }

    @PostMapping("/clientes")
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente cadastrarCliente(@RequestBody NovoCliente c) throws ValidacaoException {
        return clientes.cadastrar(c.nome(), c.cpf(), c.telefone(), c.endereco());
    }

    @DeleteMapping("/clientes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirCliente(@PathVariable int id) {
        clientes.excluir(id);
    }

    @GetMapping("/produtos")
    public List<Produto> listarProdutos() {
        return produtos.listarTodos();
    }

    @PostMapping("/produtos")
    @ResponseStatus(HttpStatus.CREATED)
    public Produto cadastrarProduto(@RequestBody NovoProduto p) throws ValidacaoException {
        return produtos.cadastrar(p.nome(), p.categoria(), p.quantidade(), p.preco());
    }

    @PutMapping("/produtos/quantidade")
    public Map<String, Object> atualizarQuantidade(@RequestBody Quantidade q) throws ValidacaoException {
        produtos.atualizarQuantidade(q.nome(), q.quantidade());
        return Map.of("ok", true);
    }

    @PostMapping("/vendas")
    public Map<String, Object> finalizarVenda(@RequestBody VendaReq req) throws ValidacaoException {
        Cliente cliente = clientes.listarTodos().stream()
                .filter(c -> c.getId() == req.clienteId()).findFirst()
                .orElseThrow(() -> new ValidacaoException("Cliente não encontrado"));

        Map<String, Integer> agrupado = new LinkedHashMap<>();
        if (req.itens() != null) {
            req.itens().forEach(i -> agrupado.merge(i.produto(), i.quantidade(), Integer::sum));
        }

        VendaService venda = new VendaService(produtoRepository);
        for (Map.Entry<String, Integer> e : agrupado.entrySet()) {
            venda.adicionarAoCarrinho(e.getKey(), e.getValue());
        }
        Venda v = venda.finalizarVenda(0, LocalDate.now().toString(), cliente);
        return Map.of("resumo", v.resumo(), "total", v.getValorTotal());
    }
}
