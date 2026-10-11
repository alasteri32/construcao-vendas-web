package com.vendas.service;

import com.vendas.exception.ValidacaoException;
import com.vendas.model.Cliente;
import com.vendas.repository.ClienteRepository;
import com.vendas.util.CpfValidator;
import java.util.List;

public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente cadastrar(String nome, String cpf, String telefone, String endereco)
            throws ValidacaoException {

        if (nome == null || nome.trim().isEmpty()) {
            throw new ValidacaoException("O nome do cliente é obrigatório");
        }
        if (!CpfValidator.isValido(cpf)) {
            throw new ValidacaoException("CPF inválido");
        }

        Cliente cliente = new Cliente(0, nome.trim(), CpfValidator.apenasNumeros(cpf),
                telefone, endereco);

        return clienteRepository.salvar(cliente);
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.listarTodos();
    }

    public Cliente buscarPorNome(String nome) throws ValidacaoException {
        Cliente cliente = clienteRepository.buscarPorNome(nome);
        if (cliente == null) {
            throw new ValidacaoException("Cliente não encontrado: " + nome);
        }
        return cliente;
    }

    public void excluir(int id) {
        clienteRepository.excluir(id);
    }
}
