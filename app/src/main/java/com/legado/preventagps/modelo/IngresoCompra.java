package com.legado.preventagps.modelo;

public class IngresoCompra {
    private String codItem;
    private String descItem;
    private String umItem;
    private Integer cantidad;
    private String horaIngreso;

    public String getCodItem() {
        return codItem;
    }

    public void setCodItem(String codItem) {
        this.codItem = codItem;
    }

    public String getDescItem() {
        return descItem;
    }

    public void setDescItem(String descItem) {
        this.descItem = descItem;
    }

    public String getUmItem() {
        return umItem;
    }

    public void setUmItem(String umItem) {
        this.umItem = umItem;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getHoraIngreso() {
        return horaIngreso;
    }

    public void setHoraIngreso(String horaIngreso) {
        this.horaIngreso = horaIngreso;
    }
}
