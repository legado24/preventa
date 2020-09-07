package com.legado.preventagps.modelo;

import java.math.BigDecimal;

/**
 * Created by __Adrian__ on 13/08/2019.
 */

public class MontoMes {
    private String nombreMes;
    private BigDecimal valor;

    public String getNombreMes() {
        return nombreMes;
    }

    public void setNombreMes(String nombreMes) {
        this.nombreMes = nombreMes;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
