
package org.example.model;

import java.time.LocalDateTime;

public class AlertaPrazo {

    private long idAlerta;
    private long idPendencia;
    private String mensagemAlerta;
    private int diasAntecedencia;
    private String statusEnvio;
    private LocalDateTime dataEnvio;

    public AlertaPrazo(long idAlerta, long idPendencia,
                       String mensagemAlerta,
                       int diasAntecedencia,
                       String statusEnvio,
                       LocalDateTime dataEnvio) {

        this.idAlerta = idAlerta;
        this.idPendencia = idPendencia;
        this.mensagemAlerta = mensagemAlerta;
        this.diasAntecedencia = diasAntecedencia;
        this.statusEnvio = statusEnvio;
        this.dataEnvio = dataEnvio;
    }

    public long getIdAlerta() {
        return idAlerta;
    }

    public long getIdPendencia() {
        return idPendencia;
    }

    public String getMensagemAlerta() {
        return mensagemAlerta;
    }

    public int getDiasAntecedencia() {
        return diasAntecedencia;
    }

    public String getStatusEnvio() {
        return statusEnvio;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }
}

