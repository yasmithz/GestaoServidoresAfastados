
package br.edu.ifpa.gestaoescola.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoMySQL {

    private static final String URL =
            "jdbc:mysql://localhost:3306/gestao_servidores_afastados";

    private static final String USER = "root";
    private static final String PASS = "root";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static Connection getConnection() throws SQLException {
        return conectar();
    }
}
