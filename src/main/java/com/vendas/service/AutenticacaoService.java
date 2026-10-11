package com.vendas.service;

import com.vendas.exception.ValidacaoException;

public class AutenticacaoService {

    private final ProvedorCredenciais provedorCredenciais;

    public AutenticacaoService(ProvedorCredenciais provedorCredenciais) {
        this.provedorCredenciais = provedorCredenciais;
    }

    public void autenticar(String login, String senha) throws ValidacaoException {
        if (!provedorCredenciais.credenciaisValidas(login, senha)) {
            throw new ValidacaoException("Usuário ou senha incorretos!");
        }
    }
}
