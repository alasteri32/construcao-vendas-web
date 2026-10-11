package com.vendas.service;

import com.vendas.exception.ValidacaoException;
import com.vendas.model.Cliente;
import com.vendas.model.ItemVenda;
import com.vendas.model.Produto;
import com.vendas.model.Venda;
import com.vendas.repository.ProdutoRepository;
import java.util.ArrayList;
import java.util.List;

public class VendaService {

    private final ProdutoRepository produtoRepository;
    private final List<ItemVenda> carrinho = new ArrayList<>();

    public VendaService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public ItemVenda adicionarAoCarrinho(String nomeProduto, int quantidade)
            throws ValidacaoException {

        if (quantidade <= 0) {
            throw new ValidacaoException("Quantidade deve ser maior que zero");
        }

        Produto produto = produtoRepository.buscarPorNome(nomeProduto);
        if (produto == null) {
            throw new ValidacaoException("Produto não encontrado: " + nomeProduto);
        }
        if (quantidade > produto.getQuantidade()) {
            throw new ValidacaoException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        ItemVenda item = new ItemVenda(produto, quantidade);
        carrinho.add(item);
        return item;
    }

    public double calcularTotalCarrinho() {
        double total = 0;
        for (ItemVenda item : carrinho) {
            total += item.getSubtotal();
        }
        return total;
    }

    public List<ItemVenda> getCarrinho() {
        return new ArrayList<>(carrinho);
    }

    public Venda finalizarVenda(int idVenda, String data, Cliente cliente) throws ValidacaoException {
        if (carrinho.isEmpty()) {
            throw new ValidacaoException("Carrinho vazio!");
        }

        for (ItemVenda item : carrinho) {
            Produto produto = item.getProduto();
            int novaQuantidade = produto.getQuantidade() - item.getQuantidade();

            if (novaQuantidade < 0) {
                throw new ValidacaoException("Estoque insuficiente para: " + produto.getNome());
            }
            produtoRepository.atualizarQuantidade(produto.getNome(), novaQuantidade);
        }

        Venda venda = new Venda(idVenda, data, cliente);
        for (ItemVenda item : carrinho) {
            venda.adicionarItem(item);
        }

        carrinho.clear();
        return venda;
    }
}
