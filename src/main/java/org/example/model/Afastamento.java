
package org.example.model;

import java.time.LocalDate;

public class Afastamento {

    private long idAfastamento;
    private long idServidor;
    private String tipoAfastamento;
    private LocalDate dataInicio;
    private LocalDate dataFimPrevista;
    private LocalDate dataFimEfetiva;
    private String status;
    private String observacoes;

    public Afastamento(long idAfastamento, long idServidor,
                       String tipoAfastamento,
                       LocalDate dataInicio,
                       LocalDate dataFimPrevista,
                       LocalDate dataFimEfetiva,
                       String status, String observacoes) {

        this.idAfastamento = idAfastamento;
        this.idServidor = idServidor;
        this.tipoAfastamento = tipoAfastamento;
        this.dataInicio = dataInicio;
        this.dataFimPrevista = dataFimPrevista;
        this.dataFimEfetiva = dataFimEfetiva;
        this.status = status;
        this.observacoes = observacoes;
    }

    public long getIdAfastamento() {
        return idAfastamento;
    }

    public long getIdServidor() {
        return idServidor;
    }

    public String getTipoAfastamento() {
        return tipoAfastamento;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFimPrevista() {
        return dataFimPrevista;
    }

    public LocalDate getDataFimEfetiva() {
        return dataFimEfetiva;
    }

    public String getStatus() {
        return status;
    }

    public String getObservacoes() {
        return observacoes;
    }
}

