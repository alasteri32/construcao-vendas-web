package com.vendas.service;

public class ProvedorCredenciaisFixo implements ProvedorCredenciais {

    private static final String LOGIN_PADRAO = "admin";
    private static final String SENHA_PADRAO = "123";

    @Override
    public boolean credenciaisValidas(String login, String senha) {
        return LOGIN_PADRAO.equals(login) && SENHA_PADRAO.equals(senha);
    }
}
