package com.vendas.web;

import com.vendas.repository.ClienteRepository;
import com.vendas.repository.ProdutoRepository;
import com.vendas.repository.jdbc.ClienteRepositoryJdbc;
import com.vendas.repository.jdbc.ProdutoRepositoryJdbc;
import com.vendas.service.AutenticacaoService;
import com.vendas.service.ClienteService;
import com.vendas.service.ProdutoService;
import com.vendas.service.ProvedorCredenciaisFixo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    ClienteRepository clienteRepository() {
        return new ClienteRepositoryJdbc();
    }

    @Bean
    ProdutoRepository produtoRepository() {
        return new ProdutoRepositoryJdbc();
    }

    @Bean
    ClienteService clienteService(ClienteRepository repo) {
        return new ClienteService(repo);
    }

    @Bean
    ProdutoService produtoService(ProdutoRepository repo) {
        return new ProdutoService(repo);
    }

    @Bean
    AutenticacaoService autenticacaoService() {
        return new AutenticacaoService(new ProvedorCredenciaisFixo());
    }
}
