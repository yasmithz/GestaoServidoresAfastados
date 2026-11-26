
package org.example.dao;

import br.edu.ifpa.gestaoescola.database.ConexaoMySQL;
import org.example.model.AlertaPrazo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlertaPrazoDAO {

    public void cadastrar(AlertaPrazo alerta)
            throws SQLException {

        String sql = "INSERT INTO alerta_prazo " +
                "(id_pendencia, mensagem_alerta, dias_antecedencia, " +
                "status_envio, data_envio) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, alerta.getIdPendencia());
            stmt.setString(2, alerta.getMensagemAlerta());
            stmt.setInt(3, alerta.getDiasAntecedencia());
            stmt.setString(4, alerta.getStatusEnvio());
            stmt.setObject(5, alerta.getDataEnvio());

            stmt.executeUpdate();

            System.out.println("Alerta cadastrado com sucesso!");
        }
    }

    public List<AlertaPrazo> listarTodos() throws SQLException {

        List<AlertaPrazo> alertas = new ArrayList<>();

        String sql = "SELECT * FROM alerta_prazo ORDER BY id_alerta";

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                AlertaPrazo alerta = new AlertaPrazo(
                        rs.getLong("id_alerta"),
                        rs.getLong("id_pendencia"),
                        rs.getString("mensagem_alerta"),
                        rs.getInt("dias_antecedencia"),
                        rs.getString("status_envio"),
                        rs.getObject("data_envio",
                                java.time.LocalDateTime.class)
                );

                alertas.add(alerta);
            }
        }

        return alertas;
    }
}
