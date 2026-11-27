
package org.example.api;

import com.google.gson.*;
import com.sun.net.httpserver.*;

import br.edu.ifpa.gestaoescola.database.ConexaoMySQL;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class ApiServidor {

    private static final Gson gson = new Gson();

    public static void main(String[] args) throws IOException {

        HttpServer servidor = HttpServer.create(
                new InetSocketAddress(8080), 0);

        servidor.createContext("/", exchange -> {
            if (!exchange.getRequestURI().getPath().equals("/")) {
                responder(exchange, 404, "Página não encontrada",
                        "text/plain");
                return;
            }

            try (InputStream arquivo =
                         ApiServidor.class.getResourceAsStream(
                                 "/static/index.html")) {

                if (arquivo == null) {
                    responder(exchange, 404,
                            "index.html não encontrado", "text/plain");
                    return;
                }

                byte[] dados = arquivo.readAllBytes();
                exchange.getResponseHeaders().set(
                        "Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, dados.length);

                try (OutputStream saida = exchange.getResponseBody()) {
                    saida.write(dados);
                }
            }
        });

        servidor.createContext("/api/", ApiServidor::processar);

        servidor.start();

        System.out.println("Sistema iniciado!");
        System.out.println("http://localhost:8080");
    }

    private static void processar(HttpExchange ex) throws IOException {

        String[] partes = ex.getRequestURI().getPath()
                .replace("/api/", "").split("/");

        String entidade = partes[0];
        String metodo = ex.getRequestMethod();

        Long id = partes.length > 1
                ? Long.parseLong(partes[1]) : null;

        try {
            Object resultado;

            if (metodo.equals("GET")) {
                resultado = listar(entidade);

            } else if (metodo.equals("POST")) {
                JsonObject dados = lerJson(ex);
                cadastrar(entidade, dados);
                resultado = Map.of("mensagem", "Cadastro realizado!");

            } else if (metodo.equals("PUT") && id != null) {
                JsonObject dados = lerJson(ex);
                atualizar(entidade, id, dados);
                resultado = Map.of("mensagem", "Atualização realizada!");

            } else if (metodo.equals("DELETE") && id != null) {
                excluir(entidade, id);
                resultado = Map.of("mensagem", "Exclusão realizada!");

            } else {
                responder(ex, 405, "{\"erro\":\"Método inválido\"}",
                        "application/json");
                return;
            }

            responder(ex, 200, gson.toJson(resultado),
                    "application/json; charset=UTF-8");

        } catch (Exception e) {
            e.printStackTrace();

            responder(ex, 400,
                    gson.toJson(Map.of("erro",
                            e.getMessage() == null
                                    ? "Erro na operação" : e.getMessage())),
                    "application/json; charset=UTF-8");
        }
    }

    private static List<Map<String, Object>> listar(String entidade)
            throws SQLException {

        String sql = switch (entidade) {
            case "servidores" -> """
                SELECT id_servidor AS id, nome, matricula, cpf,
                       departamento, cargo
                FROM servidor ORDER BY nome
                """;

            case "afastamentos" -> """
                SELECT id_afastamento AS id, id_servidor AS servidorId,
                       tipo_afastamento AS tipo,
                       data_inicio AS dataInicio,
                       data_fim_prevista AS dataFimPrevista,
                       data_fim_efetiva AS dataFimEfetiva,
                       status, observacoes
                FROM afastamento ORDER BY data_inicio
                """;

            case "relatorios" -> """
                SELECT id_relatorio AS id,
                       id_afastamento AS afastamentoId,
                       arquivo_relatorio AS arquivoRelatorio,
                       status_entrega AS statusEntrega,
                       ano_referencia AS anoReferencia,
                       semestre,
                       data_limite_submissao AS dataLimiteSubmissao,
                       data_entrega AS dataEntrega
                FROM relatorio_semestral ORDER BY ano_referencia, semestre
                """;

            case "pendencias" -> """
                SELECT id_pendencia AS id,
                       id_afastamento AS afastamentoId,
                       id_relatorio AS relatorioId,
                       descricao, data_abertura AS dataAbertura,
                       data_resolucao AS dataResolucao,
                       status_pendencia AS statusPendencia
                FROM pendencia ORDER BY data_abertura
                """;

            case "alertas" -> """
                SELECT id_alerta AS id,
                       id_pendencia AS pendenciaId,
                       mensagem_alerta AS mensagemAlerta,
                       dias_antecedencia AS diasAntecedencia,
                       status_envio AS statusEnvio,
                       data_envio AS dataEnvio
                FROM alerta_prazo ORDER BY id_alerta
                """;

            default -> throw new IllegalArgumentException(
                    "Entidade desconhecida: " + entidade);
        };

        List<Map<String, Object>> lista = new ArrayList<>();

        try (Connection c = ConexaoMySQL.conectar();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();

            while (rs.next()) {
                Map<String, Object> linha = new LinkedHashMap<>();

                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    Object valor = rs.getObject(i);

                    if (valor instanceof java.util.Date) {
                        valor = valor.toString();
                    } else if (valor instanceof Timestamp) {
                        valor = valor.toString();
                    }

                    linha.put(meta.getColumnLabel(i), valor);
                }

                lista.add(linha);
            }
        }

        return lista;
    }

    private static void cadastrar(String entidade, JsonObject d)
            throws SQLException {

        String sql = switch (entidade) {
            case "servidores" ->
                    "INSERT INTO servidor (nome, matricula, cpf, departamento, cargo) VALUES (?, ?, ?, ?, ?)";

            case "afastamentos" ->
                    "INSERT INTO afastamento (id_servidor, tipo_afastamento, data_inicio, data_fim_prevista, data_fim_efetiva, status, observacoes) VALUES (?, ?, ?, ?, ?, ?, ?)";

            case "relatorios" ->
                    "INSERT INTO relatorio_semestral (id_afastamento, arquivo_relatorio, status_entrega, ano_referencia, semestre, data_limite_submissao, data_entrega) VALUES (?, ?, ?, ?, ?, ?, ?)";

            case "pendencias" ->
                    "INSERT INTO pendencia (id_afastamento, id_relatorio, descricao, data_abertura, data_resolucao, status_pendencia) VALUES (?, ?, ?, ?, ?, ?)";

            case "alertas" ->
                    "INSERT INTO alerta_prazo (id_pendencia, mensagem_alerta, dias_antecedencia, status_envio, data_envio) VALUES (?, ?, ?, ?, ?)";

            default -> throw new IllegalArgumentException("Entidade inválida");
        };

        try (Connection c = ConexaoMySQL.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            preencher(entidade, d, ps);
            ps.executeUpdate();
        }
    }

    private static void atualizar(String entidade, long id, JsonObject d)
            throws SQLException {

        String sql = switch (entidade) {
            case "servidores" ->
                    "UPDATE servidor SET nome=?, matricula=?, cpf=?, departamento=?, cargo=? WHERE id_servidor=?";

            case "afastamentos" ->
                    "UPDATE afastamento SET id_servidor=?, tipo_afastamento=?, data_inicio=?, data_fim_prevista=?, data_fim_efetiva=?, status=?, observacoes=? WHERE id_afastamento=?";

            case "relatorios" ->
                    "UPDATE relatorio_semestral SET id_afastamento=?, arquivo_relatorio=?, status_entrega=?, ano_referencia=?, semestre=?, data_limite_submissao=?, data_entrega=? WHERE id_relatorio=?";

            case "pendencias" ->
                    "UPDATE pendencia SET id_afastamento=?, id_relatorio=?, descricao=?, data_abertura=?, data_resolucao=?, status_pendencia=? WHERE id_pendencia=?";

            case "alertas" ->
                    "UPDATE alerta_prazo SET id_pendencia=?, mensagem_alerta=?, dias_antecedencia=?, status_envio=?, data_envio=? WHERE id_alerta=?";

            default -> throw new IllegalArgumentException("Entidade inválida");
        };

        try (Connection c = ConexaoMySQL.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            preencher(entidade, d, ps);
            ps.setLong(quantidadeCampos(entidade) + 1, id);
            ps.executeUpdate();
        }
    }

    private static int quantidadeCampos(String entidade) {
        return switch (entidade) {
            case "servidores" -> 5;
            case "afastamentos" -> 7;
            case "relatorios" -> 7;
            case "pendencias" -> 6;
            case "alertas" -> 5;
            default -> throw new IllegalArgumentException("Entidade inválida");
        };
    }

    private static void preencher(
            String entidade, JsonObject d, PreparedStatement ps)
            throws SQLException {

        switch (entidade) {

            case "servidores" -> {
                ps.setString(1, texto(d, "nome"));
                ps.setString(2, texto(d, "matricula"));
                ps.setString(3, texto(d, "cpf"));
                ps.setString(4, texto(d, "departamento"));
                ps.setString(5, texto(d, "cargo"));
            }

            case "afastamentos" -> {
                ps.setLong(1, numero(d, "servidorId"));
                ps.setString(2, texto(d, "tipo"));
                ps.setDate(3, data(d, "dataInicio"));
                ps.setDate(4, data(d, "dataFimPrevista"));
                ps.setDate(5, data(d, "dataFimEfetiva"));
                ps.setString(6, texto(d, "status"));
                ps.setString(7, texto(d, "observacoes"));
            }

            case "relatorios" -> {
                ps.setLong(1, numero(d, "afastamentoId"));
                ps.setString(2, texto(d, "arquivoRelatorio"));
                ps.setString(3, texto(d, "statusEntrega"));
                ps.setInt(4, (int) numero(d, "anoReferencia"));
                ps.setInt(5, (int) numero(d, "semestre"));
                ps.setDate(6, data(d, "dataLimiteSubmissao"));
                ps.setDate(7, data(d, "dataEntrega"));
            }

            case "pendencias" -> {
                ps.setLong(1, numero(d, "afastamentoId"));
                setLongNullable(ps, 2, d, "relatorioId");
                ps.setString(3, texto(d, "descricao"));
                ps.setDate(4, data(d, "dataAbertura"));
                ps.setDate(5, data(d, "dataResolucao"));
                ps.setString(6, texto(d, "statusPendencia"));
            }

            case "alertas" -> {
                ps.setLong(1, numero(d, "pendenciaId"));
                ps.setString(2, texto(d, "mensagemAlerta"));
                ps.setInt(3, (int) numero(d, "diasAntecedencia"));
                ps.setString(4, texto(d, "statusEnvio"));
                ps.setTimestamp(5, timestamp(d, "dataEnvio"));
            }

            default -> throw new IllegalArgumentException("Entidade inválida");
        }
    }

    private static void excluir(String entidade, long id)
            throws SQLException {

        String tabela = switch (entidade) {
            case "servidores" -> "servidor";
            case "afastamentos" -> "afastamento";
            case "relatorios" -> "relatorio_semestral";
            case "pendencias" -> "pendencia";
            case "alertas" -> "alerta_prazo";
            default -> throw new IllegalArgumentException("Entidade inválida");
        };

        String coluna = switch (entidade) {
            case "servidores" -> "id_servidor";
            case "afastamentos" -> "id_afastamento";
            case "relatorios" -> "id_relatorio";
            case "pendencias" -> "id_pendencia";
            case "alertas" -> "id_alerta";
            default -> throw new IllegalArgumentException("Entidade inválida");
        };

        try (Connection c = ConexaoMySQL.conectar();
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM " + tabela + " WHERE " + coluna + "=?")) {

            ps.setLong(1, id);
            int linhas = ps.executeUpdate();

            if (linhas == 0) {
                throw new SQLException("Registro não encontrado.");
            }
        }
    }

    private static JsonObject lerJson(HttpExchange ex) throws IOException {
        try (InputStream entrada = ex.getRequestBody()) {
            return JsonParser.parseReader(
                            new InputStreamReader(entrada, StandardCharsets.UTF_8))
                    .getAsJsonObject();
        }
    }

    private static String texto(JsonObject d, String campo) {
        return d.has(campo) && !d.get(campo).isJsonNull()
                ? d.get(campo).getAsString() : null;
    }

    private static long numero(JsonObject d, String campo) {
        return d.get(campo).getAsLong();
    }

    private static java.sql.Date data(JsonObject d, String campo) {
        String valor = texto(d, campo);
        return valor == null || valor.isBlank()
                ? null : java.sql.Date.valueOf(valor);
    }

    private static Timestamp timestamp(JsonObject d, String campo) {
        String valor = texto(d, campo);
        return valor == null || valor.isBlank()
                ? null : Timestamp.valueOf(valor.replace("T", " "));
    }

    private static void setLongNullable(
            PreparedStatement ps, int pos, JsonObject d, String campo)
            throws SQLException {

        if (!d.has(campo) || d.get(campo).isJsonNull()) {
            ps.setNull(pos, Types.BIGINT);
        } else {
            ps.setLong(pos, d.get(campo).getAsLong());
        }
    }

    private static void responder(
            HttpExchange ex, int status, String conteudo, String tipo)
            throws IOException {

        byte[] bytes = conteudo.getBytes(StandardCharsets.UTF_8);

        ex.getResponseHeaders().set("Content-Type", tipo);
        ex.sendResponseHeaders(status, bytes.length);

        try (OutputStream saida = ex.getResponseBody()) {
            saida.write(bytes);
        }
    }
}
