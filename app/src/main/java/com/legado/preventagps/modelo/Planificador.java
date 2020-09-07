package com.legado.preventagps.modelo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by __Adrian__ on 13/08/2019.
 */

public class Planificador {
    private String codCliente;
    private String descCliente;
    private String direccion;
    private List<MontoMes> columnaMeses;
    private List<Proveedor> listaProveedores;

    public String getCodCliente() {
        return codCliente;
    }

    public void setCodCliente(String codCliente) {
        this.codCliente = codCliente;
    }

    public String getDescCliente() {
        return descCliente;
    }

    public void setDescCliente(String descCliente) {
        this.descCliente = descCliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<MontoMes> getColumnaMeses() {
        return columnaMeses;
    }

    public void setColumnaMeses(List<MontoMes> columnaMeses) {
        this.columnaMeses = columnaMeses;
    }

    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    public void setListaProveedores(List<Proveedor> listaProveedores) {
        this.listaProveedores = listaProveedores;
    }
}
