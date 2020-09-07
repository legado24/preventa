package com.legado.preventagps.modelo;

import java.util.List;

/**
 * Created by __Adrian__ on 13/08/2019.
 */

public class TipoColumna {
    private String nombre;
    private String valor;
    List<ValorColumna> listaValores;


    public List<ValorColumna> getListaValores() {
        return listaValores;
    }

    public void setListaValores(List<ValorColumna> listaValores) {
        this.listaValores = listaValores;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
