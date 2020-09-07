package com.legado.preventagps.modelo;

/**
 * Created by __Adrian__ on 7/05/2019.
 */

public class NoPedido {
    private String codCliente;
    private String descCliente;
    private String codEmpresa;
    private String usuario;
    //private String codVendedor;
        private String codSede;
    private String descMotivo;
    private String codLocal;
    private String direccion;
    private String codRuta;
    private String fechaTransaccion;
    private String coordenadasNoPedido;
    private String statusNoPedido;
    private String idNoPedidoLocal;


    public String getIdNoPedidoLocal() {
        return idNoPedidoLocal;
    }

    public void setIdNoPedidoLocal(String idNoPedidoLocal) {
        this.idNoPedidoLocal = idNoPedidoLocal;
    }

    public String getStatusNoPedido() {
        return statusNoPedido;
    }

    public void setStatusNoPedido(String statusNoPedido) {
        this.statusNoPedido = statusNoPedido;
    }

    public String getCodSede() {
        return codSede;
    }

    public void setCodSede(String codSede) {
        this.codSede = codSede;
    }

    public String getCoordenadasNoPedido() {
        return coordenadasNoPedido;
    }

    public void setCoordenadasNoPedido(String coordenadasNoPedido) {
        this.coordenadasNoPedido = coordenadasNoPedido;
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

    public String getCodEmpresa() {
        return codEmpresa;
    }

    public void setCodEmpresa(String codEmpresa) {
        this.codEmpresa = codEmpresa;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

//    public String getCodVendedor() {
//        return codVendedor;
//    }
//
//    public void setCodVendedor(String codVendedor) {
//        this.codVendedor = codVendedor;
//    }
//
//    public String getCodSede() {
//        return codSede;
//    }
//
//    public void setCodSede(String codSede) {
//        this.codSede = codSede;
//    }

    public String getDescMotivo() {
        return descMotivo;
    }

    public void setDescMotivo(String descMotivo) {
        this.descMotivo = descMotivo;
    }

    public String getCodLocal() {
        return codLocal;
    }

    public void setCodLocal(String codLocal) {
        this.codLocal = codLocal;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCodRuta() {
        return codRuta;
    }

    public void setCodRuta(String codRuta) {
        this.codRuta = codRuta;
    }

    public String getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(String fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }
}
