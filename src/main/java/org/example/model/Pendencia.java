
package org.example.model;

import java.time.LocalDate;

public class Pendencia {

    private long idPendencia;
    private long idAfastamento;
    private Long idRelatorio;
    private String descricao;
    private LocalDate dataAbertura;
    private LocalDate dataResolucao;
    private String statusPendencia;

    public Pendencia(long idPendencia, long idAfastamento,
                     Long idRelatorio, String descricao,
                     LocalDate dataAbertura,
                     LocalDate dataResolucao,
                     String statusPendencia) {

        this.idPendencia = idPendencia;
        this.idAfastamento = idAfastamento;
        this.idRelatorio = idRelatorio;
        this.descricao = descricao;
        this.dataAbertura = dataAbertura;
        this.dataResolucao = dataResolucao;
        this.statusPendencia = statusPendencia;
    }

    public long getIdPendencia() {
        return idPendencia;
    }

    public long getIdAfastamento() {
        return idAfastamento;
    }

    public Long getIdRelatorio() {
        return idRelatorio;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }

    public LocalDate getDataResolucao() {
        return dataResolucao;
    }

    public String getStatusPendencia() {
        return statusPendencia;
    }
}

