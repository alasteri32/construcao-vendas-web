package com.vendas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.vendas.exception.ValidacaoException;
import com.vendas.model.Cliente;
import com.vendas.repository.memory.ClienteRepositoryMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ClienteService (cadastro)")
class ClienteServiceTest {

    private ClienteService service;

    @BeforeEach
    void preparar() {
        service = new ClienteService(new ClienteRepositoryMemoria());
    }

    @Test
    @DisplayName("CT-CLI-01: cadastra cliente valido e gera id")
    void cadastraClienteValido() throws ValidacaoException {
        Cliente c = service.cadastrar("Joao Silva", "111.444.777-35", "11999999999", "Rua A, 123");
        assertTrue(c.getId() > 0);
        assertEquals(1, service.listarTodos().size());
    }

    @Test
    @DisplayName("CT-CLI-02: CPF e gravado somente com numeros")
    void cpfGravadoSemMascara() throws ValidacaoException {
        Cliente c = service.cadastrar("Joao Silva", "111.444.777-35", "11999999999", "Rua A, 123");
        assertEquals("11144477735", c.getCpf());
    }

    @Test
    @DisplayName("CT-CLI-03: nome, telefone e endereco ficam nos campos corretos")
    void camposNaOrdemCorreta() throws ValidacaoException {
        Cliente c = service.cadastrar("Joao Silva", "11144477735", "11999999999", "Rua A, 123");
        assertEquals("Joao Silva", c.getNome());
        assertEquals("11999999999", c.getTelefone());
        assertEquals("Rua A, 123", c.getEndereco());
    }

    @Test
    @DisplayName("CT-CLI-04: CPF invalido e rejeitado")
    void rejeitaCpfInvalido() {
        ValidacaoException e = assertThrows(ValidacaoException.class,
                () -> service.cadastrar("Maria", "123.456.789-00", "11988887777", "Rua B"));
        assertEquals("CPF inválido", e.getMessage());
        assertTrue(service.listarTodos().isEmpty());
    }

    @Test
    @DisplayName("CT-CLI-05: nome vazio ou nulo e rejeitado")
    void rejeitaNomeVazio() {
        assertThrows(ValidacaoException.class,
                () -> service.cadastrar("   ", "11144477735", "1", "Rua"));
        assertThrows(ValidacaoException.class,
                () -> service.cadastrar(null, "11144477735", "1", "Rua"));
    }

    @Test
    @DisplayName("CT-CLI-06: busca por nome encontra e, se nao existir, lanca erro")
    void buscaPorNome() throws ValidacaoException {
        service.cadastrar("Joao Silva", "11144477735", "1", "Rua");
        assertEquals("Joao Silva", service.buscarPorNome("joao silva").getNome());
        assertThrows(ValidacaoException.class, () -> service.buscarPorNome("Fulano"));
    }
}
