package com.legado.preventagps.modelo;

import java.math.BigDecimal;

/**
 * Created by __Adrian__ on 13/08/2019.
 */

public class TipoValor {
    private String codTipoValor;
    private BigDecimal valorTipoValor;

    public String getCodTipoValor() {
        return codTipoValor;
    }

    public void setCodTipoValor(String codTipoValor) {
        this.codTipoValor = codTipoValor;
    }

    public BigDecimal getValorTipoValor() {
        return valorTipoValor;
    }

    public void setValorTipoValor(BigDecimal valorTipoValor) {
        this.valorTipoValor = valorTipoValor;
    }
}
