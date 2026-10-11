package com.vendas.repository.jdbc;

import com.vendas.exception.PersistenciaException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexaoFactory {

    private static final String URL = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/loja_construcao");
    private static final String USUARIO = System.getenv().getOrDefault("DB_USER", "root");
    private static final String SENHA = System.getenv().getOrDefault("DB_PASS", "root");

    private ConexaoFactory() {
    }

    public static Connection conectar() {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            throw new PersistenciaException("Não foi possível conectar ao banco de dados", e);
        }
    }
}
