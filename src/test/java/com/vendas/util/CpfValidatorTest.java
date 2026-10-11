package com.vendas.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("CpfValidator")
class CpfValidatorTest {

    @Test
    @DisplayName("CT-CPF-01: aceita CPF valido com mascara")
    void aceitaCpfValidoComMascara() {
        assertTrue(CpfValidator.isValido("111.444.777-35"));
    }

    @Test
    @DisplayName("CT-CPF-02: aceita CPF valido somente com numeros")
    void aceitaCpfValidoSemMascara() {
        assertTrue(CpfValidator.isValido("11144477735"));
    }

    @ParameterizedTest(name = "CT-CPF-03: rejeita digitos repetidos {0}")
    @ValueSource(strings = {"111.111.111-11", "00000000000", "99999999999"})
    void rejeitaDigitosRepetidos(String cpf) {
        assertFalse(CpfValidator.isValido(cpf));
    }

    @ParameterizedTest(name = "CT-CPF-04: rejeita tamanho invalido [{0}]")
    @ValueSource(strings = {"", "123", "1114447773", "111444777350"})
    void rejeitaTamanhoInvalido(String cpf) {
        assertFalse(CpfValidator.isValido(cpf));
    }

    @Test
    @DisplayName("CT-CPF-05: rejeita CPF nulo")
    void rejeitaNulo() {
        assertFalse(CpfValidator.isValido(null));
    }

    @Test
    @DisplayName("CT-CPF-06: rejeita CPF com digito verificador errado")
    void rejeitaDigitoVerificadorErrado() {
        assertFalse(CpfValidator.isValido("111.444.777-36"));
        assertFalse(CpfValidator.isValido("111.444.777-45"));
    }

    @Test
    @DisplayName("CT-CPF-07: apenasNumeros remove pontos e traco")
    void apenasNumerosRemoveMascara() {
        assertEquals("11144477735", CpfValidator.apenasNumeros("111.444.777-35"));
    }

    @Test
    @DisplayName("CT-CPF-08: apenasNumeros de nulo devolve string vazia")
    void apenasNumerosNulo() {
        assertEquals("", CpfValidator.apenasNumeros(null));
    }
}
