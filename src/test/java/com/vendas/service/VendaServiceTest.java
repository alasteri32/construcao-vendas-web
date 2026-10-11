package com.vendas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.vendas.exception.ValidacaoException;
import com.vendas.model.Cliente;
import com.vendas.model.Venda;
import com.vendas.repository.ProdutoRepository;
import com.vendas.repository.memory.ProdutoRepositoryMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("VendaService (carrinho, total e baixa de estoque)")
class VendaServiceTest {

    private static final double DELTA = 0.0001;

    private ProdutoRepository repo;
    private VendaService vendaService;
    private Cliente cliente;

    @BeforeEach
    void preparar() throws ValidacaoException {
        repo = new ProdutoRepositoryMemoria();
        ProdutoService produtoService = new ProdutoService(repo);
        produtoService.cadastrar("Cimento", "Construcao", 10, 45.0);
        produtoService.cadastrar("Tijolo", "Construcao", 100, 2.5);
        vendaService = new VendaService(repo);
        cliente = new Cliente(1, "Joao Silva", "11144477735", "11999999999", "Rua A, 123");
    }

    @Test
    @DisplayName("CT-CAR-01: adiciona item ao carrinho e calcula o total")
    void adicionaItensECalculaTotal() throws ValidacaoException {
        vendaService.adicionarAoCarrinho("Cimento", 2);
        vendaService.adicionarAoCarrinho("Tijolo", 20);
        assertEquals(2, vendaService.getCarrinho().size());
        assertEquals(140.0, vendaService.calcularTotalCarrinho(), DELTA);
    }

    @Test
    @DisplayName("CT-CAR-02: carrinho vazio tem total zero")
    void carrinhoVazioTotalZero() {
        assertEquals(0.0, vendaService.calcularTotalCarrinho(), DELTA);
    }

    @Test
    @DisplayName("CT-CAR-03: quantidade maior que o estoque e rejeitada")
    void rejeitaEstoqueInsuficiente() {
        ValidacaoException e = assertThrows(ValidacaoException.class,
                () -> vendaService.adicionarAoCarrinho("Cimento", 11));
        assertTrue(e.getMessage().contains("Estoque insuficiente"));
    }

    @Test
    @DisplayName("CT-CAR-04: quantidade igual ao estoque e aceita (limite)")
    void aceitaQuantidadeIgualAoEstoque() throws ValidacaoException {
        vendaService.adicionarAoCarrinho("Cimento", 10);
        assertEquals(450.0, vendaService.calcularTotalCarrinho(), DELTA);
    }

    @Test
    @DisplayName("CT-CAR-05: quantidade zero ou negativa e rejeitada")
    void rejeitaQuantidadeInvalida() {
        assertThrows(ValidacaoException.class, () -> vendaService.adicionarAoCarrinho("Cimento", 0));
        assertThrows(ValidacaoException.class, () -> vendaService.adicionarAoCarrinho("Cimento", -3));
    }

    @Test
    @DisplayName("CT-CAR-06: produto inexistente e rejeitado")
    void rejeitaProdutoInexistente() {
        ValidacaoException e = assertThrows(ValidacaoException.class,
                () -> vendaService.adicionarAoCarrinho("Areia", 1));
        assertTrue(e.getMessage().contains("Produto não encontrado"));
    }

    @Test
    @DisplayName("CT-VEN-07: finalizar venda da baixa no estoque e esvazia o carrinho")
    void finalizarVendaDaBaixaNoEstoque() throws ValidacaoException {
        vendaService.adicionarAoCarrinho("Cimento", 2);
        vendaService.adicionarAoCarrinho("Tijolo", 20);

        Venda venda = vendaService.finalizarVenda(1, "05/10/2026", cliente);

        assertEquals(140.0, venda.getValorTotal(), DELTA);
        assertEquals(8, repo.buscarPorNome("Cimento").getQuantidade());
        assertEquals(80, repo.buscarPorNome("Tijolo").getQuantidade());
        assertTrue(vendaService.getCarrinho().isEmpty());
    }

    @Test
    @DisplayName("CT-VEN-08: finalizar venda com carrinho vazio e rejeitado")
    void finalizarComCarrinhoVazio() {
        ValidacaoException e = assertThrows(ValidacaoException.class,
                () -> vendaService.finalizarVenda(1, "05/10/2026", cliente));
        assertTrue(e.getMessage().contains("Carrinho vazio"));
    }
}
