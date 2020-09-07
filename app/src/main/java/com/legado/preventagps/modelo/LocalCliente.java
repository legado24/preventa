package com.legado.preventagps.modelo;

/**
 * Created by __Adrian__ on 4/05/2019.
 */

public class LocalCliente {
    private String codLocal;
    private String descLocal;
    private String direccion;
    private String ubigeo;
    private String codRuta;
    private String descRuta;
    private String coordenadas;
    private String observaciones;
    private String usuario;
    private String codCliente;
    private Integer operacion;
    private String status;

    private String observacionesAudi;
    private String formatoDireccion;
    private String newDirecion;

    public String getObservacionesAudi() {
        return observacionesAudi;
    }

    public void setObservacionesAudi(String observacionesAudi) {
        this.observacionesAudi = observacionesAudi;
    }

    public String getFormatoDireccion() {
        return formatoDireccion;
    }

    public void setFormatoDireccion(String formatoDireccion) {
        this.formatoDireccion = formatoDireccion;
    }

    public String getNewDirecion() {
        return newDirecion;
    }

    public void setNewDirecion(String newDirecion) {
        this.newDirecion = newDirecion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getOperacion() {
        return operacion;
    }

    public void setOperacion(Integer operacion) {
        this.operacion = operacion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getCodCliente() {
        return codCliente;
    }

    public void setCodCliente(String codCliente) {
        this.codCliente = codCliente;
    }

    public String getDescRuta() {
        return descRuta;
    }

    public void setDescRuta(String descRuta) {
        this.descRuta = descRuta;
    }

    public String getCodLocal() {
        return codLocal;
    }

    public void setCodLocal(String codLocal) {
        this.codLocal = codLocal;
    }

    public String getDescLocal() {
        return descLocal;
    }

    public void setDescLocal(String descLocal) {
        this.descLocal = descLocal;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getUbigeo() {
        return ubigeo;
    }

    public void setUbigeo(String ubigeo) {
        this.ubigeo = ubigeo;
    }

    public String getCodRuta() {
        return codRuta;
    }

    public void setCodRuta(String codRuta) {
        this.codRuta = codRuta;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
