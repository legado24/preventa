package com.legado.preventagps.modelo;

import java.util.List;

/**
 * Created by __Adrian__ on 13/08/2019.
 */

public class ValorColumna {
    private String codValorColumna;
    private String valorColumna;
    private List<TipoValor> listaTipoValores;


    public List<TipoValor> getListaTipoValores() {
        return listaTipoValores;
    }

    public void setListaTipoValores(List<TipoValor> listaTipoValores) {
        this.listaTipoValores = listaTipoValores;
    }

    public String getCodValorColumna() {
        return codValorColumna;
    }

    public void setCodValorColumna(String codValorColumna) {
        this.codValorColumna = codValorColumna;
    }

    public String getValorColumna() {
        return valorColumna;
    }

    public void setValorColumna(String valorColumna) {
        this.valorColumna = valorColumna;
    }
}
