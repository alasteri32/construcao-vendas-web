package com.vendas.repository.jdbc;

import com.vendas.exception.PersistenciaException;
import com.vendas.model.Produto;
import com.vendas.repository.ProdutoRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProdutoRepositoryJdbc implements ProdutoRepository {

    @Override
    public Produto salvar(Produto produto) {
        String sql = "INSERT INTO produto (nome, categoria, quantidade, preco) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoFactory.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getCategoria());
            stmt.setInt(3, produto.getQuantidade());
            stmt.setDouble(4, produto.getPreco());
            stmt.executeUpdate();

            int idGerado = produto.getId();
            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    idGerado = chaves.getInt(1);
                }
            }

            return new Produto(idGerado, produto.getNome(), produto.getCategoria(),
                    produto.getPreco(), produto.getQuantidade());

        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao salvar produto", e);
        }
    }

    @Override
    public List<Produto> listarTodos() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produto";

        try (Connection conn = ConexaoFactory.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar produtos", e);
        }

        return lista;
    }

    @Override
    public Produto buscarPorNome(String nome) {
        String sql = "SELECT * FROM produto WHERE nome LIKE ?";

        try (Connection conn = ConexaoFactory.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar produto", e);
        }

        return null;
    }

    @Override
    public void atualizarQuantidade(String nome, int novaQuantidade) {
        String sql = "UPDATE produto SET quantidade = ? WHERE nome = ?";

        try (Connection conn = ConexaoFactory.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, novaQuantidade);
            stmt.setString(2, nome);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao atualizar quantidade do produto", e);
        }
    }

    private Produto mapear(ResultSet rs) throws SQLException {
        return new Produto(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("categoria"),
                rs.getDouble("preco"),
                rs.getInt("quantidade")
        );
    }
}
