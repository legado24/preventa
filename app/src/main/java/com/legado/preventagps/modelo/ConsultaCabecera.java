package com.legado.preventagps.modelo;

import java.math.BigDecimal;

/**
 * Created by __Adrian__ on 17/4/2017.
 */

public class ConsultaCabecera {
    private String nroPedido;
    private String codCliente;
    private String descCliente;
    private String statusPedido;
    private BigDecimal montoFinal;
    private String codEmpresa;
    private String codCondicion;
    private BigDecimal montoFacturado;
    private String codAlmacen;
    private String codLocalidad;
    private   String usuarioAnulacion;

    public String getUsuarioAnulacion() {
        return usuarioAnulacion;
    }

    public void setUsuarioAnulacion(String usuarioAnulacion) {
        this.usuarioAnulacion = usuarioAnulacion;
    }

    public String getCodLocalidad() {
        return codLocalidad;
    }

    public void setCodLocalidad(String codLocalidad) {
        this.codLocalidad = codLocalidad;
    }

    public String getCodAlmacen() {
        return codAlmacen;
    }

    public void setCodAlmacen(String codAlmacen) {
        this.codAlmacen = codAlmacen;
    }

    public BigDecimal getMontoFacturado() {
        return montoFacturado;
    }

    public void setMontoFacturado(BigDecimal montoFacturado) {
        this.montoFacturado = montoFacturado;
    }

    public String getCodCondicion() {
        return codCondicion;
    }

    public void setCodCondicion(String codCondicion) {
        this.codCondicion = codCondicion;
    }

    public String getCodEmpresa() {
        return codEmpresa;
    }

    public void setCodEmpresa(String codEmpresa) {
        this.codEmpresa = codEmpresa;
    }

    public ConsultaCabecera() {
    }

    public ConsultaCabecera(String nroPedido, String codCliente, String descCliente, String statusPedido, BigDecimal montoFinal, String codEmpresa) {
        this.nroPedido = nroPedido;
        this.codCliente = codCliente;
        this.descCliente = descCliente;
        this.statusPedido = statusPedido;
        this.montoFinal = montoFinal;
        this.codEmpresa=codEmpresa;
    }

    public String getNroPedido() {
        return nroPedido;
    }

    public void setNroPedido(String nroPedido) {
        this.nroPedido = nroPedido;
    }

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

    public String getStatusPedido() {
        return statusPedido;
    }

    public void setStatusPedido(String statusPedido) {
        this.statusPedido = statusPedido;
    }

    public BigDecimal getMontoFinal() {
        return montoFinal;
    }

    public void setMontoFinal(BigDecimal montoFinal) {
        this.montoFinal = montoFinal;
    }
}
