package com.vendas.repository.memory;

import com.vendas.model.Cliente;
import com.vendas.repository.ClienteRepository;
import java.util.ArrayList;
import java.util.List;

public class ClienteRepositoryMemoria implements ClienteRepository {

    private final List<Cliente> clientes = new ArrayList<>();
    private int proximoId = 1;

    @Override
    public Cliente salvar(Cliente cliente) {
        Cliente salvo = new Cliente(proximoId++, cliente.getNome(), cliente.getCpf(),
                cliente.getTelefone(), cliente.getEndereco());
        clientes.add(salvo);
        return salvo;
    }

    @Override
    public List<Cliente> listarTodos() {
        return new ArrayList<>(clientes);
    }

    @Override
    public Cliente buscarPorNome(String nome) {
        return clientes.stream()
                .filter(c -> c.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void atualizar(Cliente cliente) {
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getId() == cliente.getId()) {
                clientes.set(i, cliente);
                return;
            }
        }
    }

    @Override
    public void excluir(int id) {
        clientes.removeIf(c -> c.getId() == id);
    }
}
