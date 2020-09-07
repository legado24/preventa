package com.legado.preventagps.modelo;

import java.math.BigDecimal;

/**
 * Created by __Adrian__ on 18/5/2017.
 */

public class Articulo {
    private String codItem;
    private String descItem;
    private String um;
    private String codLista;
    private BigDecimal precioBase;
    private BigDecimal saldo;

    private String codAlmacen;
    private String condicionPago;
    private BigDecimal precioSugerido;
    private String tipo;
    private BigDecimal piso;
    private BigDecimal disponibleCanal;
    private Integer isRestringido;

    public Integer getIsRestringido() {
        return isRestringido;
    }

    public void setIsRestringido(Integer isRestringido) {
        this.isRestringido = isRestringido;
    }

    public String getTipo() {
        return tipo;
    }

    public BigDecimal getDisponibleCanal() {
        return disponibleCanal;
    }

    public void setDisponibleCanal(BigDecimal disponibleCanal) {
        this.disponibleCanal = disponibleCanal;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPiso() {
        return piso;
    }

    public void setPiso(BigDecimal piso) {
        this.piso = piso;
    }

    //    private Integer isSugerido;
//    private Integer isFocus;
//
//    public Integer getIsFocus() {
//        return isFocus;
//    }
//
//    public void setIsFocus(Integer isFocus) {
//        this.isFocus = isFocus;
//    }
//
//    public Integer getIsSugerido() {
//        return isSugerido;
//    }
//
//    public void setIsSugerido(Integer isSugerido) {
//        this.isSugerido = isSugerido;
//    }

    public String getCondicionPago() {
        return condicionPago;
    }

    public void setCondicionPago(String condicionPago) {
        this.condicionPago = condicionPago;
    }

    public BigDecimal getPrecioSugerido() {
        return precioSugerido;
    }

    public void setPrecioSugerido(BigDecimal precioSugerido) {
        this.precioSugerido = precioSugerido;
    }

    public Articulo() {
    }

    public Articulo(String codItem, String descItem, String um, String codLista, BigDecimal precioBase, BigDecimal saldo, String codAlmacen) {
        this.codItem = codItem;
        this.descItem = descItem;
        this.um = um;
        this.codLista = codLista;
        this.precioBase = precioBase;
        this.saldo = saldo;
        this.codAlmacen = codAlmacen;
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

    public String getCodLista() {
        return codLista;
    }

    public void setCodLista(String codLista) {
        this.codLista = codLista;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getCodAlmacen() {
        return codAlmacen;
    }

    public void setCodAlmacen(String codAlmacen) {
        this.codAlmacen = codAlmacen;
    }
}
