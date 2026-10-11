package com.vendas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.vendas.exception.ValidacaoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AutenticacaoService (login)")
class AutenticacaoServiceTest {

    private final AutenticacaoService service = new AutenticacaoService(new ProvedorCredenciaisFixo());

    @Test
    @DisplayName("CT-LOG-01: credenciais corretas sao aceitas")
    void aceitaCredenciaisCorretas() {
        assertDoesNotThrow(() -> service.autenticar("admin", "123"));
    }

    @Test
    @DisplayName("CT-LOG-02: senha incorreta e rejeitada")
    void rejeitaSenhaIncorreta() {
        assertThrows(ValidacaoException.class, () -> service.autenticar("admin", "errada"));
    }

    @Test
    @DisplayName("CT-LOG-03: usuario incorreto e rejeitado")
    void rejeitaUsuarioIncorreto() {
        assertThrows(ValidacaoException.class, () -> service.autenticar("outro", "123"));
    }

    @Test
    @DisplayName("CT-LOG-04: login e senha nulos sao rejeitados")
    void rejeitaNulos() {
        assertThrows(ValidacaoException.class, () -> service.autenticar(null, null));
    }

    @Test
    @DisplayName("CT-LOG-05: provedor substituivel (OCP) - credencial customizada")
    void provedorSubstituivel() {
        AutenticacaoService custom = new AutenticacaoService((l, s) -> "ana".equals(l) && "xyz".equals(s));
        assertDoesNotThrow(() -> custom.autenticar("ana", "xyz"));
        assertThrows(ValidacaoException.class, () -> custom.autenticar("admin", "123"));
    }
}
