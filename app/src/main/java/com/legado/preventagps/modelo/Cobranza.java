package com.legado.preventagps.modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by __Adrian__ on 9/05/2019.
 */

public class Cobranza {
    //private String codVendedor;
    private String usuario;
    //private String codEmpresa;
   // private String codSede;
     private String numeroOperacion;
    private String tipoPago;
    private String banco;
    private BigDecimal montoTotal;
    private Integer operacion;

    public Integer getOperacion() {
        return operacion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setOperacion(Integer operacion) {
        this.operacion = operacion;
    }

    private List<DocumentoDeuda> listaDoc=new ArrayList<>();

    public String getNumeroOperacion() {
        return numeroOperacion;
    }

    public void setNumeroOperacion(String numeroOperacion) {
        this.numeroOperacion = numeroOperacion;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }





    public List<DocumentoDeuda> getListaDoc() {
        return listaDoc;
    }

    public void setListaDoc(List<DocumentoDeuda> listaDoc) {
        this.listaDoc = listaDoc;
    }
}
