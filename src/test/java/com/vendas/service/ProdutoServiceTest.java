package com.vendas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.vendas.exception.ValidacaoException;
import com.vendas.model.Produto;
import com.vendas.repository.memory.ProdutoRepositoryMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProdutoService (cadastro e estoque)")
class ProdutoServiceTest {

    private ProdutoService service;

    @BeforeEach
    void preparar() {
        service = new ProdutoService(new ProdutoRepositoryMemoria());
    }

    @Test
    @DisplayName("CT-PRO-01: cadastra produto valido e gera id")
    void cadastraProdutoValido() throws ValidacaoException {
        Produto p = service.cadastrar("Cimento", "Construcao", 10, 45.0);
        assertTrue(p.getId() > 0);
        assertEquals(1, service.listarTodos().size());
    }

    @Test
    @DisplayName("CT-PRO-02: nome vazio e rejeitado")
    void rejeitaNomeVazio() {
        assertThrows(ValidacaoException.class, () -> service.cadastrar("  ", "Construcao", 1, 1.0));
        assertThrows(ValidacaoException.class, () -> service.cadastrar(null, "Construcao", 1, 1.0));
    }

    @Test
    @DisplayName("CT-PRO-03: quantidade negativa e rejeitada")
    void rejeitaQuantidadeNegativa() {
        assertThrows(ValidacaoException.class, () -> service.cadastrar("Tijolo", "Construcao", -5, 2.5));
    }

    @Test
    @DisplayName("CT-PRO-04: preco negativo e rejeitado")
    void rejeitaPrecoNegativo() {
        assertThrows(ValidacaoException.class, () -> service.cadastrar("Tijolo", "Construcao", 5, -2.5));
    }

    @Test
    @DisplayName("CT-PRO-05: quantidade zero e preco zero sao aceitos (limite)")
    void aceitaLimiteZero() throws ValidacaoException {
        assertNotNull(service.cadastrar("Brinde", "Outros", 0, 0.0));
    }

    @Test
    @DisplayName("CT-PRO-06: atualizar quantidade altera o estoque")
    void atualizaQuantidade() throws ValidacaoException {
        service.cadastrar("Cimento", "Construcao", 10, 45.0);
        service.atualizarQuantidade("Cimento", 25);
        assertEquals(25, service.buscarPorNome("Cimento").getQuantidade());
    }

    @Test
    @DisplayName("CT-PRO-07: atualizar para quantidade negativa e rejeitado")
    void atualizaQuantidadeNegativa() throws ValidacaoException {
        service.cadastrar("Cimento", "Construcao", 10, 45.0);
        assertThrows(ValidacaoException.class, () -> service.atualizarQuantidade("Cimento", -1));
        assertEquals(10, service.buscarPorNome("Cimento").getQuantidade());
    }

    @Test
    @DisplayName("CT-PRO-08: buscar produto inexistente lanca erro")
    void buscaInexistente() {
        assertThrows(ValidacaoException.class, () -> service.buscarPorNome("Areia"));
    }
}
