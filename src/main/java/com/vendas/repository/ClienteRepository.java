package com.vendas.repository;

import com.vendas.model.Cliente;
import java.util.List;

public interface ClienteRepository {

    Cliente salvar(Cliente cliente);

    List<Cliente> listarTodos();

    Cliente buscarPorNome(String nome);

    void atualizar(Cliente cliente);

    void excluir(int id);
}
