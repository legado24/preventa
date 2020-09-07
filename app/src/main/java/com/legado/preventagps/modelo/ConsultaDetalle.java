package com.legado.preventagps.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by _ADRIAN_ on 18/04/2017.
 */
public class ConsultaDetalle implements Serializable {

    private String codItem;
    private String descItem;
    private String um;
    private Integer cantidad;
    private BigDecimal pu;
    private BigDecimal valorVenta;
    private BigDecimal igv;
    private BigDecimal ventaNeta;
    private Integer stock;

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public ConsultaDetalle() {
    }


    public ConsultaDetalle(String codItem, String descItem, String um, Integer cantidad, BigDecimal ventaNeta) {
        this.codItem = codItem;
        this.descItem = descItem;
        this.um = um;
        this.cantidad = cantidad;
        this.ventaNeta = ventaNeta;
    }

    public ConsultaDetalle(String codItem, String descItem, String um, Integer cantidad, BigDecimal pu, BigDecimal valorVenta, BigDecimal igv, BigDecimal ventaNeta) {
        this.codItem = codItem;
        this.descItem = descItem;
        this.um = um;
        this.cantidad = cantidad;
        this.pu = pu;
        this.valorVenta = valorVenta;
        this.igv = igv;
        this.ventaNeta = ventaNeta;
    }

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

    public String getUm() {
        return um;
    }

    public void setUm(String um) {
        this.um = um;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPu() {
        return pu;
    }

    public void setPu(BigDecimal pu) {
        this.pu = pu;
    }

    public BigDecimal getValorVenta() {
        return valorVenta;
    }

    public void setValorVenta(BigDecimal valorVenta) {
        this.valorVenta = valorVenta;
    }

    public BigDecimal getIgv() {
        return igv;
    }

    public void setIgv(BigDecimal igv) {
        this.igv = igv;
    }

    public BigDecimal getVentaNeta() {
        return ventaNeta;
    }

    public void setVentaNeta(BigDecimal ventaNeta) {
        this.ventaNeta = ventaNeta;
    }
}
