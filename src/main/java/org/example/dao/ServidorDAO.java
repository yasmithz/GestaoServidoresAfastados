
package org.example.dao;

import br.edu.ifpa.gestaoescola.database.ConexaoMySQL;
import org.example.model.Servidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServidorDAO {

    // CONSULTAR TODOS OS SERVIDORES
    public List<Servidor> listarTodos() throws SQLException {

        List<Servidor> servidores = new ArrayList<>();

        String sql = """
                SELECT id_servidor, nome, matricula,
                       cpf, departamento, cargo
                FROM servidor
                ORDER BY nome
                """;

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {

                Servidor servidor = new Servidor(
                        resultado.getInt("id_servidor"),
                        resultado.getString("nome"),
                        resultado.getString("matricula"),
                        resultado.getString("cpf"),
                        resultado.getString("departamento"),
                        resultado.getString("cargo")
                );

                servidores.add(servidor);
            }
        }

        return servidores;
    }

    // CADASTRAR UM NOVO SERVIDOR
    public void cadastrar(Servidor servidor) throws SQLException {

        String sql = """
                INSERT INTO servidor
                (nome, matricula, cpf, departamento, cargo)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, servidor.getNome());
            stmt.setString(2, servidor.getMatricula());
            stmt.setString(3, servidor.getCpf());
            stmt.setString(4, servidor.getDepartamento());
            stmt.setString(5, servidor.getCargo());

            stmt.executeUpdate();
        }
    }
}

