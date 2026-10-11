package com.vendas.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ItemVenda e Venda (calculos)")
class VendaTest {

    private static final double DELTA = 0.0001;

    private Cliente cliente;
    private Produto cimento;
    private Produto tijolo;

    @BeforeEach
    void preparar() {
        cliente = new Cliente(1, "Joao Silva", "11144477735", "11999999999", "Rua A, 123");
        cimento = new Produto(1, "Cimento", "Construcao", 45.0, 10);
        tijolo = new Produto(2, "Tijolo", "Construcao", 2.5, 100);
    }

    @Test
    @DisplayName("CT-VEN-01: subtotal = preco x quantidade")
    void subtotalDoItem() {
        assertEquals(90.0, new ItemVenda(cimento, 2).getSubtotal(), DELTA);
    }

    @Test
    @DisplayName("CT-VEN-02: subtotal com preco decimal")
    void subtotalComPrecoDecimal() {
        assertEquals(50.0, new ItemVenda(tijolo, 20).getSubtotal(), DELTA);
    }

    @Test
    @DisplayName("CT-VEN-03: venda sem itens tem total zero")
    void vendaVaziaTotalZero() {
        assertEquals(0.0, new Venda(1, "05/10/2026", cliente).getValorTotal(), DELTA);
    }

    @Test
    @DisplayName("CT-VEN-04: total da venda soma os subtotais dos itens")
    void totalSomaSubtotais() {
        Venda venda = new Venda(1, "05/10/2026", cliente);
        venda.adicionarItem(new ItemVenda(cimento, 2));
        venda.adicionarItem(new ItemVenda(tijolo, 20));
        assertEquals(140.0, venda.getValorTotal(), DELTA);
        assertEquals(2, venda.getItens().size());
    }

    @Test
    @DisplayName("CT-VEN-05: lista de itens da venda nao pode ser alterada por fora")
    void listaDeItensImutavel() {
        Venda venda = new Venda(1, "05/10/2026", cliente);
        assertThrows(UnsupportedOperationException.class,
                () -> venda.getItens().add(new ItemVenda(cimento, 1)));
    }

    @Test
    @DisplayName("CT-VEN-06: resumo traz cliente, itens e total")
    void resumoContemDados() {
        Venda venda = new Venda(1, "05/10/2026", cliente);
        venda.adicionarItem(new ItemVenda(cimento, 2));
        String resumo = venda.resumo();
        assertTrue(resumo.contains("Joao Silva"));
        assertTrue(resumo.contains("Cimento"));
        assertTrue(resumo.contains("Total da venda: R$ 90.0"));
    }
}
