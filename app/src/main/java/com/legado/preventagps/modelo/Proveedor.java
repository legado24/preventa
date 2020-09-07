package com.legado.preventagps.modelo;

import java.util.List;

/**
 * Created by __Adrian__ on 13/08/2019.
 */

public class Proveedor {
    private String codProveedor;
    private String descProveedor;
    private List<TipoColumna> listaTiposColumnas;

    public String getCodProveedor() {
        return codProveedor;
    }

    public void setCodProveedor(String codProveedor) {
        this.codProveedor = codProveedor;
    }

    public String getDescProveedor() {
        return descProveedor;
    }

    public void setDescProveedor(String descProveedor) {
        this.descProveedor = descProveedor;
    }

    public List<TipoColumna> getListaTiposColumnas() {
        return listaTiposColumnas;
    }

    public void setListaTiposColumnas(List<TipoColumna> listaTiposColumnas) {
        this.listaTiposColumnas = listaTiposColumnas;
    }
}
