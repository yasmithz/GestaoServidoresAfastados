
package br.edu.ifpa.gestaoescola.database;

import java.sql.*;

public class TesteConexao {

    public static void main(String[] args) {

        try (Connection conexao = ConexaoMySQL.conectar()) {

            System.out.println("==================================");
            System.out.println("       BANCO DE DADOS MYSQL");
            System.out.println("==================================");

            DatabaseMetaData meta = conexao.getMetaData();

            ResultSet tabelas = meta.getTables(
                    conexao.getCatalog(),
                    null,
                    "%",
                    new String[]{"TABLE"}
            );

            while (tabelas.next()) {

                String nomeTabela = tabelas.getString("TABLE_NAME");

                System.out.println("\n==================================");
                System.out.println("TABELA: " + nomeTabela);
                System.out.println("==================================");

                String sql = "SELECT * FROM `" + nomeTabela + "`";

                try (Statement stmt = conexao.createStatement();
                     ResultSet resultado = stmt.executeQuery(sql)) {

                    ResultSetMetaData colunas = resultado.getMetaData();

                    int quantidadeColunas = colunas.getColumnCount();

                    // Mostra os nomes das colunas
                    for (int i = 1; i <= quantidadeColunas; i++) {
                        System.out.print(colunas.getColumnName(i) + "\t");
                    }

                    System.out.println("\n----------------------------------");

                    // Mostra os registros
                    boolean encontrou = false;

                    while (resultado.next()) {

                        encontrou = true;

                        for (int i = 1; i <= quantidadeColunas; i++) {
                            System.out.print(
                                    resultado.getString(i) + "\t"
                            );
                        }

                        System.out.println();
                    }

                    if (!encontrou) {
                        System.out.println("Nenhum registro encontrado.");
                    }
                }
            }

            tabelas.close();

        } catch (SQLException e) {

            System.out.println("Erro ao consultar o banco:");
            e.printStackTrace();

        }
    }
}
