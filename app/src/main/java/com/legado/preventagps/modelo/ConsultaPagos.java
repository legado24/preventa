package com.legado.preventagps.modelo;

import java.math.BigDecimal;

/**
 * Created by __Adrian__ on 23/05/2019.
 */

public class ConsultaPagos {
    private String serie;
    private String preimpreso;
    private String fecha;
    private BigDecimal saldo;
    private String descCliente;
    private BigDecimal montoCobrado;
    private String statusCobranza;

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getPreimpreso() {
        return preimpreso;
    }

    public void setPreimpreso(String preimpreso) {
        this.preimpreso = preimpreso;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getDescCliente() {
        return descCliente;
    }

    public void setDescCliente(String descCliente) {
        this.descCliente = descCliente;
    }

    public BigDecimal getMontoCobrado() {
        return montoCobrado;
    }

    public void setMontoCobrado(BigDecimal montoCobrado) {
        this.montoCobrado = montoCobrado;
    }

    public String getStatusCobranza() {
        return statusCobranza;
    }

    public void setStatusCobranza(String statusCobranza) {
        this.statusCobranza = statusCobranza;
    }
}
