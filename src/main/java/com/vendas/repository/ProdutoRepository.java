package com.vendas.repository;

import com.vendas.model.Produto;
import java.util.List;

public interface ProdutoRepository {

    Produto salvar(Produto produto);

    List<Produto> listarTodos();

    Produto buscarPorNome(String nome);

    void atualizarQuantidade(String nome, int novaQuantidade);
}
