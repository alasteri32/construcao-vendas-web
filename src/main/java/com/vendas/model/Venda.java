package com.vendas.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Venda {

    private final int id;
    private final String data;
    private final Cliente cliente;
    private final List<ItemVenda> itens;
    private double valorTotal;

    public Venda(int id, String data, Cliente cliente) {
        this.id = id;
        this.data = data;
        this.cliente = cliente;
        this.itens = new ArrayList<>();
    }

    public void adicionarItem(ItemVenda item) {
        itens.add(item);
        recalcularTotal();
    }

    private void recalcularTotal() {
        valorTotal = 0;
        for (ItemVenda item : itens) {
            valorTotal += item.getSubtotal();
        }
    }

    public String resumo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cliente: ").append(cliente.getNome()).append(System.lineSeparator());
        sb.append("Itens:").append(System.lineSeparator());
        for (ItemVenda item : itens) {
            sb.append("- ").append(item.getProduto().getNome())
                    .append(" | Qtd: ").append(item.getQuantidade())
                    .append(" | Subtotal: R$ ").append(item.getSubtotal())
                    .append(System.lineSeparator());
        }
        sb.append("Total da venda: R$ ").append(valorTotal);
        return sb.toString();
    }

    public int getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<ItemVenda> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public double getValorTotal() {
        return valorTotal;
    }
}
