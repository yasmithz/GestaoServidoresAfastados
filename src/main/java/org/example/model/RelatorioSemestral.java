
package org.example.model;

import java.time.LocalDate;

public class RelatorioSemestral {

    private long idRelatorio;
    private long idAfastamento;
    private String arquivoRelatorio;
    private String statusEntrega;
    private int anoReferencia;
    private int semestre;
    private LocalDate dataLimiteSubmissao;
    private LocalDate dataEntrega;

    public RelatorioSemestral(long idRelatorio,
                              long idAfastamento,
                              String arquivoRelatorio,
                              String statusEntrega,
                              int anoReferencia,
                              int semestre,
                              LocalDate dataLimiteSubmissao,
                              LocalDate dataEntrega) {

        this.idRelatorio = idRelatorio;
        this.idAfastamento = idAfastamento;
        this.arquivoRelatorio = arquivoRelatorio;
        this.statusEntrega = statusEntrega;
        this.anoReferencia = anoReferencia;
        this.semestre = semestre;
        this.dataLimiteSubmissao = dataLimiteSubmissao;
        this.dataEntrega = dataEntrega;
    }

    public long getIdRelatorio() {
        return idRelatorio;
    }

    public long getIdAfastamento() {
        return idAfastamento;
    }

    public String getArquivoRelatorio() {
        return arquivoRelatorio;
    }

    public String getStatusEntrega() {
        return statusEntrega;
    }

    public int getAnoReferencia() {
        return anoReferencia;
    }

    public int getSemestre() {
        return semestre;
    }

    public LocalDate getDataLimiteSubmissao() {
        return dataLimiteSubmissao;
    }

    public LocalDate getDataEntrega() {
        return dataEntrega;
    }
}
