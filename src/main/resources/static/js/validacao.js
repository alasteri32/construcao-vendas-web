const Validacao = {
  soNumeros: s => (s || '').replace(/\D/g, ''),
  mascaraCpf(s) {
    const n = this.soNumeros(s).slice(0, 11);
    return n.replace(/(\d{3})(\d)/, '$1.$2').replace(/(\d{3})(\d)/, '$1.$2').replace(/(\d{3})(\d{1,2})$/, '$1-$2');
  },
  cpfValido(cpf) {
    const n = this.soNumeros(cpf);
    if (n.length !== 11 || /^(\d)\1{10}$/.test(n)) return false;
    const dv = q => {
      let soma = 0;
      for (let i = 0; i < q; i++) soma += +n[i] * (q + 1 - i);
      const r = soma % 11;
      return r < 2 ? 0 : 11 - r;
    };
    return dv(9) === +n[9] && dv(10) === +n[10];
  },
  limpar(form) {
    form.querySelectorAll('input,select').forEach(i => i.classList.remove('invalido'));
    form.querySelectorAll('.erro-campo').forEach(e => e.textContent = '');
  },
  erro(campo, msg) {
    campo.classList.add('invalido');
    const s = campo.parentElement.querySelector('.erro-campo');
    if (s) s.textContent = msg;
    return false;
  },
  obrigatorio: (c, nome) => c.value.trim() ? true : Validacao.erro(c, nome + ' é obrigatório'),
  numeroMin(c, nome, min) {
    const v = parseFloat(c.value);
    return !isNaN(v) && v >= min ? true : this.erro(c, nome + ' deve ser um número ≥ ' + min);
  }
};
