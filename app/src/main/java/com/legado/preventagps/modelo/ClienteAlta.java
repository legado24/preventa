package com.legado.preventagps.modelo;

import java.util.List;

/**
 * Created by __Adrian__ on 18/04/2019.
 */

public class ClienteAlta {
    private String codCliente;
    private String ruc;
    private String celular;
    private String telefonoFijo;
    private String codLp;
    private String descripcion;
    private String direccion;
    private String ubigeo;
    private String codTipoCliente;
    private String codCanal;
    private String codGiroNegocio;
    private String dni;
    private String referencia;
    private String email;
    private String fechaNacimiento;
    private String sexo;
    private String usuarioRegistra;
    private List<LocalCliente> locales;
    private Integer operacion;

    private String status;
    private String observaciones;
    private String formatoDireccion;
    private String newDirecion;
//
//    private String nombres;
//    private String apellidos;
    private String direccionNew;


//    public String getNombres() {
//        return nombres;
//    }
//
//    public void setNombres(String nombres) {
//        this.nombres = nombres;
//    }
//
//    public String getApellidos() {
//        return apellidos;
//    }

//    public void setApellidos(String apellidos) {
//        this.apellidos = apellidos;
//    }

    public String getDireccionNew() {
        return direccionNew;
    }

    public void setDireccionNew(String direccionNew) {
        this.direccionNew = direccionNew;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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

    public Integer getOperacion() {
        return operacion;
    }

    public void setOperacion(Integer operacion) {
        this.operacion = operacion;
    }

    public List<LocalCliente> getLocales() {
        return locales;
    }

    public void setLocales(List<LocalCliente> locales) {
        this.locales = locales;
    }

    public String getCodLp() {
        return codLp;
    }

    public void setCodLp(String codLp) {
        this.codLp = codLp;
    }

    public String getCodCliente() {
        return codCliente;
    }

    public void setCodCliente(String codCliente) {
        this.codCliente = codCliente;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getTelefonoFijo() {
        return telefonoFijo;
    }

    public void setTelefonoFijo(String telefonoFijo) {
        this.telefonoFijo = telefonoFijo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public String getCodTipoCliente() {
        return codTipoCliente;
    }

    public void setCodTipoCliente(String codTipoCliente) {
        this.codTipoCliente = codTipoCliente;
    }

    public String getCodCanal() {
        return codCanal;
    }

    public void setCodCanal(String codCanal) {
        this.codCanal = codCanal;
    }

    public String getCodGiroNegocio() {
        return codGiroNegocio;
    }

    public void setCodGiroNegocio(String codGiroNegocio) {
        this.codGiroNegocio = codGiroNegocio;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getUsuarioRegistra() {
        return usuarioRegistra;
    }

    public void setUsuarioRegistra(String usuarioRegistra) {
        this.usuarioRegistra = usuarioRegistra;
    }
}
