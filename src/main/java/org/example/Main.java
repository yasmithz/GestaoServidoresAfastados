
package org.example;

import br.edu.ifpa.gestaoescola.database.ConexaoMySQL;
import org.example.dao.ServidorDAO;
import org.example.dao.RelatorioSemestralDAO;
import org.example.model.Servidor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // TESTE DE CONEXÃO
        try (Connection conexao = ConexaoMySQL.conectar()) {

            System.out.println("Conexão realizada com sucesso!");

        } catch (SQLException e) {

            System.out.println("Erro ao conectar com o MySQL!");
            e.printStackTrace();

            return;
        }


        try {

            ServidorDAO servidorDAO = new ServidorDAO();

            List<Servidor> servidores =
                    servidorDAO.listarTodos();

            System.out.println("\\n===== SERVIDORES CADASTRADOS =====");

            for (Servidor servidor : servidores) {
                System.out.println(servidor);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao consultar servidores!");
            e.printStackTrace();
        }

        // CONSULTAR RELATÓRIOS ATRASADOS
        try {

            var relatorioDAO =
                    new RelatorioSemestralDAO();

            relatorioDAO.listarRelatoriosAtrasados();

        } catch (SQLException e) {
            System.out.println("Erro ao consultar relatórios!");
            e.printStackTrace();
        }
    }
}
