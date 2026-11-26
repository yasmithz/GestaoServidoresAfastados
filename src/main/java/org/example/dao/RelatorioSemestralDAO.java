package org.example.dao;

import br.edu.ifpa.gestaoescola.database.ConexaoMySQL;
import org.example.model.RelatorioSemestral;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RelatorioSemestralDAO {

    public void cadastrar(RelatorioSemestral relatorio)
            throws SQLException {

        String sql = "INSERT INTO relatorio_semestral " +
                "(id_afastamento, arquivo_relatorio, status_entrega, " +
                "ano_referencia, semestre, data_limite_submissao, data_entrega) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, relatorio.getIdAfastamento());
            stmt.setString(2, relatorio.getArquivoRelatorio());
            stmt.setString(3, relatorio.getStatusEntrega());
            stmt.setInt(4, relatorio.getAnoReferencia());
            stmt.setInt(5, relatorio.getSemestre());
            stmt.setObject(6, relatorio.getDataLimiteSubmissao());
            stmt.setObject(7, relatorio.getDataEntrega());

            stmt.executeUpdate();

            System.out.println("Relatório cadastrado com sucesso!");
        }
    }

    public List<RelatorioSemestral> listarTodos()
            throws SQLException {

        List<RelatorioSemestral> relatorios = new ArrayList<>();

        String sql = "SELECT * FROM relatorio_semestral";

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                RelatorioSemestral relatorio =
                        new RelatorioSemestral(
                                rs.getLong("id_relatorio"),
                                rs.getLong("id_afastamento"),
                                rs.getString("arquivo_relatorio"),
                                rs.getString("status_entrega"),
                                rs.getInt("ano_referencia"),
                                rs.getInt("semestre"),
                                rs.getObject("data_limite_submissao",
                                        java.time.LocalDate.class),
                                rs.getObject("data_entrega",
                                        java.time.LocalDate.class)
                        );

                relatorios.add(relatorio);
            }
        }

        return relatorios;
    }

    public void listarRelatoriosAtrasados() throws SQLException {

        String sql = "SELECT s.nome, s.matricula, " +
                "a.id_afastamento, r.id_relatorio, " +
                "r.ano_referencia, r.semestre, " +
                "r.data_limite_submissao, " +
                "DATEDIFF(CURDATE(), r.data_limite_submissao) AS dias_atraso " +
                "FROM relatorio_semestral r " +
                "INNER JOIN afastamento a " +
                "ON a.id_afastamento = r.id_afastamento " +
                "INNER JOIN servidor s " +
                "ON s.id_servidor = a.id_servidor " +
                "WHERE r.status_entrega = 'PENDENTE' " +
                "AND r.data_limite_submissao < CURDATE() " +
                "AND a.status <> 'CANCELADO' " +
                "ORDER BY r.data_limite_submissao, s.nome";

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n===== RELATÓRIOS ATRASADOS =====");

            boolean encontrou = false;

            while (rs.next()) {

                encontrou = true;

                System.out.println("----------------------------");
                System.out.println("Servidor: " + rs.getString("nome"));
                System.out.println("Matrícula: " + rs.getString("matricula"));
                System.out.println("Ano: " + rs.getInt("ano_referencia"));
                System.out.println("Semestre: " + rs.getInt("semestre"));
                System.out.println("Prazo: " +
                        rs.getDate("data_limite_submissao"));
                System.out.println("Dias de atraso: " +
                        rs.getInt("dias_atraso"));
            }

            if (!encontrou) {
                System.out.println("Nenhum relatório atrasado encontrado.");
            }
        }
    }
}


