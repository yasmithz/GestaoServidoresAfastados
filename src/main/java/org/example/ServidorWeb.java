package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.example.dao.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

public class ServidorWeb {
    public static void main(String[] args) throws IOException {
        byte[] pagina;
        try (InputStream arquivo = ServidorWeb.class.getResourceAsStream("/static/index.html")) {
            if (arquivo == null) {
                throw new IOException("Coloque ifpa.html em src/main/resources.");
            }
            pagina = arquivo.readAllBytes();
        }

        HttpServer servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 8080), 0);
        servidor.createContext("/", req -> {
            try (req) {
                if (!"GET".equals(req.getRequestMethod())) {
                    req.getResponseHeaders().set("Allow", "GET");
                    responder(req, 405, "text/plain", "Método não permitido");
                    return;
                }

                switch (req.getRequestURI().getPath()) {
                    case "/", "/ifpa.html" -> responder(req, 200, "text/html", pagina);
                    case "/api/painel" -> {
                        String dados;
                        try {
                            dados = painel();
                        } catch (SQLException erro) {
                            erro.printStackTrace();
                            responder(req, 500, "text/plain", "Falha ao consultar o MySQL.");
                            return;
                        }
                        responder(req, 200, "application/json", dados);
                    }
                    default -> responder(req, 404, "text/plain", "Página não encontrada");
                }
            }
        });

        servidor.start();
        System.out.println("Abra http://127.0.0.1:8080/");
    }

    private static String painel() throws SQLException {
        return "{" +
                "\"servidores\":" + linhas(new ServidorDAO().listarTodos(), s -> linha(
                s.getIdServidor(), s.getNome(), s.getMatricula(), s.getCargo(), s.getDepartamento()
        )) +
                ",\"afastamentos\":" + linhas(new AfastamentoDAO().listarTodos(), a -> linha(
                a.getIdAfastamento(), a.getIdServidor(), a.getTipoAfastamento(), a.getDataInicio(),
                a.getDataFimPrevista(), a.getDataFimEfetiva(), a.getObservacoes(), a.getStatus()
        )) +
                ",\"relatorios\":" + linhas(new RelatorioSemestralDAO().listarTodos(), r -> linha(
                r.getIdRelatorio(), r.getIdAfastamento(), r.getAnoReferencia() + "." + r.getSemestre(),
                r.getDataLimiteSubmissao(), r.getDataEntrega(), r.getArquivoRelatorio(), r.getStatusEntrega()
        )) +
                ",\"pendencias\":" + linhas(new PendenciaDAO().listarTodos(), p -> linha(
                p.getIdPendencia(), p.getIdAfastamento(), p.getIdRelatorio(), p.getDescricao(),
                p.getDataAbertura(), p.getDataResolucao(), p.getStatusPendencia()
        )) +
                ",\"alertas\":" + linhas(new AlertaPrazoDAO().listarTodos(), a -> linha(
                a.getIdAlerta(), a.getIdPendencia(), a.getMensagemAlerta(),
                a.getDiasAntecedencia(), a.getStatusEnvio(), a.getDataEnvio()
        )) + "}";
    }

    private static <T> String linhas(List<T> dados, Function<T, String> converter) {
        var json = new StringJoiner(",", "[", "]");
        dados.forEach(item -> json.add(converter.apply(item)));
        return json.toString();
    }

    private static String linha(Object... valores) {
        var json = new StringJoiner(",", "[", "]");
        for (Object valor : valores) {
            if (valor == null || valor instanceof Number) {
                json.add(String.valueOf(valor));
            } else {
                var campo = new StringBuilder("\"");
                for (char letra : valor.toString().toCharArray()) {
                    if (letra == '"' || letra == '\\') campo.append('\\');
                    if (letra < 32) campo.append(String.format("\\u%04x", (int) letra));
                    else campo.append(letra);
                }
                json.add(campo.append('"').toString());
            }
        }
        return json.toString();
    }

    private static void responder(HttpExchange req, int codigo, String tipo, String corpo)
            throws IOException {
        responder(req, codigo, tipo, corpo.getBytes(StandardCharsets.UTF_8));
    }

    private static void responder(HttpExchange req, int codigo, String tipo, byte[] corpo)
            throws IOException {
        req.getResponseHeaders().set("Content-Type", tipo + "; charset=UTF-8");
        req.getResponseHeaders().set("Cache-Control", "no-store");
        req.sendResponseHeaders(codigo, corpo.length);
        req.getResponseBody().write(corpo);
    }
}