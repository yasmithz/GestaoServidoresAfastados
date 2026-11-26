package org.example.dao;

import br.edu.ifpa.gestaoescola.database.ConexaoMySQL;
import org.example.model.Pendencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PendenciaDAO {

    public void cadastrar(Pendencia pendencia)
            throws SQLException {

        String sql = "INSERT INTO pendencia " +
                "(id_afastamento, id_relatorio, descricao, " +
                "data_abertura, data_resolucao, status_pendencia) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, pendencia.getIdAfastamento());

            if (pendencia.getIdRelatorio() == null) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setLong(2, pendencia.getIdRelatorio());
            }

            stmt.setString(3, pendencia.getDescricao());
            stmt.setObject(4, pendencia.getDataAbertura());
            stmt.setObject(5, pendencia.getDataResolucao());
            stmt.setString(6, pendencia.getStatusPendencia());

            stmt.executeUpdate();

            System.out.println("Pendência cadastrada com sucesso!");
        }
    }

    public List<Pendencia> listarTodos() throws SQLException {

        List<Pendencia> pendencias = new ArrayList<>();

        String sql = "SELECT * FROM pendencia ORDER BY data_abertura";

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                long idRelatorioBanco = rs.getLong("id_relatorio");

                Long idRelatorio = rs.wasNull()
                        ? null : idRelatorioBanco;

                Pendencia pendencia = new Pendencia(
                        rs.getLong("id_pendencia"),
                        rs.getLong("id_afastamento"),
                        idRelatorio,
                        rs.getString("descricao"),
                        rs.getObject("data_abertura",
                                java.time.LocalDate.class),
                        rs.getObject("data_resolucao",
                                java.time.LocalDate.class),
                        rs.getString("status_pendencia")
                );

                pendencias.add(pendencia);
            }
        }

        return pendencias;
    }
}
