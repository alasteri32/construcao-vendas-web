package com.vendas.repository.memory;

import com.vendas.model.Produto;
import com.vendas.repository.ProdutoRepository;
import java.util.ArrayList;
import java.util.List;

public class ProdutoRepositoryMemoria implements ProdutoRepository {

    private final List<Produto> produtos = new ArrayList<>();
    private int proximoId = 1;

    @Override
    public Produto salvar(Produto produto) {
        Produto salvo = new Produto(proximoId++, produto.getNome(), produto.getCategoria(),
                produto.getPreco(), produto.getQuantidade());
        produtos.add(salvo);
        return salvo;
    }

    @Override
    public List<Produto> listarTodos() {
        return new ArrayList<>(produtos);
    }

    @Override
    public Produto buscarPorNome(String nome) {
        return produtos.stream()
                .filter(p -> p.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void atualizarQuantidade(String nome, int novaQuantidade) {
        Produto produto = buscarPorNome(nome);
        if (produto != null) {
            produto.setQuantidade(novaQuantidade);
        }
    }
}
