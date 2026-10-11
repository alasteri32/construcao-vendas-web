package com.vendas.service;

import com.vendas.exception.ValidacaoException;
import com.vendas.model.Produto;
import com.vendas.repository.ProdutoRepository;
import java.util.List;

public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto cadastrar(String nome, String categoria, int quantidade, double preco)
            throws ValidacaoException {

        if (nome == null || nome.trim().isEmpty()) {
            throw new ValidacaoException("O nome do produto é obrigatório");
        }
        if (quantidade < 0) {
            throw new ValidacaoException("Quantidade não pode ser negativa");
        }
        if (preco < 0) {
            throw new ValidacaoException("Preço não pode ser negativo");
        }

        Produto produto = new Produto(0, nome.trim(), categoria, preco, quantidade);
        return produtoRepository.salvar(produto);
    }

    public List<Produto> listarTodos() {
        return produtoRepository.listarTodos();
    }

    public Produto buscarPorNome(String nome) throws ValidacaoException {
        Produto produto = produtoRepository.buscarPorNome(nome);
        if (produto == null) {
            throw new ValidacaoException("Produto não encontrado: " + nome);
        }
        return produto;
    }

    public void atualizarQuantidade(String nome, int novaQuantidade) throws ValidacaoException {
        if (novaQuantidade < 0) {
            throw new ValidacaoException("Quantidade não pode ser negativa");
        }
        produtoRepository.atualizarQuantidade(nome, novaQuantidade);
    }
}
