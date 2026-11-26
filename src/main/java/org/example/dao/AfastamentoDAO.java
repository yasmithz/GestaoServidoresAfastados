
package org.example.dao;

import br.edu.ifpa.gestaoescola.database.ConexaoMySQL;
import org.example.model.Afastamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AfastamentoDAO {

    public void cadastrar(Afastamento afastamento)
            throws SQLException {

        String sql = "INSERT INTO afastamento " +
                "(id_servidor, tipo_afastamento, data_inicio, " +
                "data_fim_prevista, data_fim_efetiva, status, observacoes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, afastamento.getIdServidor());
            stmt.setString(2, afastamento.getTipoAfastamento());
            stmt.setObject(3, afastamento.getDataInicio());
            stmt.setObject(4, afastamento.getDataFimPrevista());
            stmt.setObject(5, afastamento.getDataFimEfetiva());
            stmt.setString(6, afastamento.getStatus());
            stmt.setString(7, afastamento.getObservacoes());

            stmt.executeUpdate();

            System.out.println("Afastamento cadastrado com sucesso!");
        }
    }

    public List<Afastamento> listarTodos() throws SQLException {

        List<Afastamento> afastamentos = new ArrayList<>();

        String sql = "SELECT * FROM afastamento ORDER BY data_inicio";

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Afastamento afastamento = new Afastamento(
                        rs.getLong("id_afastamento"),
                        rs.getLong("id_servidor"),
                        rs.getString("tipo_afastamento"),
                        rs.getObject("data_inicio", java.time.LocalDate.class),
                        rs.getObject("data_fim_prevista", java.time.LocalDate.class),
                        rs.getObject("data_fim_efetiva", java.time.LocalDate.class),
                        rs.getString("status"),
                        rs.getString("observacoes")
                );

                afastamentos.add(afastamento);
            }
        }

        return afastamentos;
    }
}
