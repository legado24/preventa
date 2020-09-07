package com.legado.preventagps.modelo;

import java.util.List;

/**
 * Created by __Adrian__ on 14/03/2019.
 */

public class Cliente implements Comparable<Cliente> {
    private Integer secuencia;
    private String codCliente;
    private String fecha;
    private String descCliente;
    private String coordenadas;
    private Double distanciaOrigen;
    private String codRuta;
    private String descRuta;
    private String direccion;
    private String codLocal;
    private String codLista;
    private String codEmpresa;
    private Integer flagDespachado;
    private Integer flagIgnoreSec;
    private String estadoJornada;
    private String estadoGeoSis;
    private String status;
    private String nombres;
    private String apellidos;
    private String newDireccion;
    private Integer isAuditado;

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNewDireccion() {
        return newDireccion;
    }

    public void setNewDireccion(String newDireccion) {
        this.newDireccion = newDireccion;
    }

    public Integer getIsAuditado() {
        return isAuditado;
    }

    public void setIsAuditado(Integer isAuditado) {
        this.isAuditado = isAuditado;
    }

    public String getNewTelefono() {
        return newTelefono;
    }

    private String newTelefono;

    public void setNewTelefono(String newTelefono) {
        this.newTelefono = newTelefono;
    }

    private String fechaCumple;
    private String tipoNegocio;
    private Integer cantidadPedidos;
    private Double velocidadPago;
    private String antiguedad;
    private Double deudaVencida;
    private Double deudaNoVencida;
    private Double limiteCredito;
    private String descListaPrecios;

    public String getFechaCumple() {
        return fechaCumple;
    }

    public void setFechaCumple(String fechaCumple) {
        this.fechaCumple = fechaCumple;
    }

    public String getTipoNegocio() {
        return tipoNegocio;
    }

    public void setTipoNegocio(String tipoNegocio) {
        this.tipoNegocio = tipoNegocio;
    }

    public Integer getCantidadPedidos() {
        return cantidadPedidos;
    }

    public void setCantidadPedidos(Integer cantidadPedidos) {
        this.cantidadPedidos = cantidadPedidos;
    }

    public Double getVelocidadPago() {
        return velocidadPago;
    }

    public void setVelocidadPago(Double velocidadPago) {
        this.velocidadPago = velocidadPago;
    }

    public String getAntiguedad() {
        return antiguedad;
    }

    public void setAntiguedad(String antiguedad) {
        this.antiguedad = antiguedad;
    }

    public Double getDeudaVencida() {
        return deudaVencida;
    }

    public void setDeudaVencida(Double deudaVencida) {
        this.deudaVencida = deudaVencida;
    }

    public Double getDeudaNoVencida() {
        return deudaNoVencida;
    }

    public void setDeudaNoVencida(Double deudaNoVencida) {
        this.deudaNoVencida = deudaNoVencida;
    }

    public Double getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(Double limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    public String getDescListaPrecios() {
        return descListaPrecios;
    }

    public void setDescListaPrecios(String descListaPrecios) {
        this.descListaPrecios = descListaPrecios;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstadoGeoSis() {
        return estadoGeoSis;
    }

    public void setEstadoGeoSis(String estadoGeoSis) {
        this.estadoGeoSis = estadoGeoSis;
    }

    public String getEstadoJornada() {
        return estadoJornada;
    }

    public void setEstadoJornada(String estadoJornada) {
        this.estadoJornada = estadoJornada;
    }

    public Integer getFlagIgnoreSec() {
        return flagIgnoreSec;
    }

    public void setFlagIgnoreSec(Integer flagIgnoreSec) {
        this.flagIgnoreSec = flagIgnoreSec;
    }

    public Cliente() {
    }

    public Cliente(String codCliente, String descCliente, String coordenadas) {
        this.codCliente = codCliente;
        this.descCliente = descCliente;
        this.coordenadas = coordenadas;
    }

    public Integer getFlagDespachado() {
        return flagDespachado;
    }

    public void setFlagDespachado(Integer flagDespachado) {
        this.flagDespachado = flagDespachado;
    }

    public Double getDistanciaOrigen() {
        return distanciaOrigen;
    }

    public void setDistanciaOrigen(Double distanciaOrigen) {
        this.distanciaOrigen = distanciaOrigen;
    }

    public Integer getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(Integer secuencia) {
        this.secuencia = secuencia;
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

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public String getCodRuta() {
        return codRuta;
    }

    public void setCodRuta(String codRuta) {
        this.codRuta = codRuta;
    }

    public String getDescRuta() {
        return descRuta;
    }

    public void setDescRuta(String descRuta) {
        this.descRuta = descRuta;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

//    public String getCodSede() {
//        return codSede;
//    }
//
//    public void setCodSede(String codSede) {
//        this.codSede = codSede;
//    }


    public String getCodLocal() {
        return codLocal;
    }

    public void setCodLocal(String codLocal) {
        this.codLocal = codLocal;
    }

    public String getCodLista() {
        return codLista;
    }

    public void setCodLista(String codLista) {
        this.codLista = codLista;
    }

    public String getCodEmpresa() {
        return codEmpresa;
    }

    public void setCodEmpresa(String codEmpresa) {
        this.codEmpresa = codEmpresa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int compareTo(Cliente o) {
        if (distanciaOrigen < o.distanciaOrigen) {
            return -1;
        }
        if (distanciaOrigen > o.distanciaOrigen) {
            return 1;
        }
        return 0;
    }


}
