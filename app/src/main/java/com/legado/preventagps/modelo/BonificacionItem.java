package com.legado.preventagps.modelo;

import java.math.BigDecimal;

public class BonificacionItem {
    private String codLista;
    private String codItemVenta;
    private String descItem;
    private String umItemVenta;
    private Integer cantMinima;
    private Integer cantMaxima;
    private Integer prioridad;
    private String codItemBonif;
    private String descItemBonif;
    private String umItemBonif;
    private Integer cantBonif;
    private Integer multiplo;




    private Integer estado; //esto solo para paquete reutilizo esta clase
    private String codPaquete;//esto solo para paquete  reutilizo esta clase
    private Integer modoParticipacion;//esto solo para paquete  reutilizo esta clase
    private String umPaquete;//esto solo para paquete  reutilizo esta clase

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getCodPaquete() {
        return codPaquete;
    }

    public void setCodPaquete(String codPaquete) {
        this.codPaquete = codPaquete;
    }

    public Integer getModoParticipacion() {
        return modoParticipacion;
    }

    public void setModoParticipacion(Integer modoParticipacion) {
        this.modoParticipacion = modoParticipacion;
    }

    public String getUmPaquete() {
        return umPaquete;
    }

    public void setUmPaquete(String umPaquete) {
        this.umPaquete = umPaquete;
    }

    public String getCodLista() {
        return codLista;
    }

    public void setCodLista(String codLista) {
        this.codLista = codLista;
    }

    public String getCodItemVenta() {
        return codItemVenta;
    }

    public void setCodItemVenta(String codItemVenta) {
        this.codItemVenta = codItemVenta;
    }

    public String getDescItem() {
        return descItem;
    }

    public void setDescItem(String descItem) {
        this.descItem = descItem;
    }

    public String getUmItemVenta() {
        return umItemVenta;
    }

    public void setUmItemVenta(String umItemVenta) {
        this.umItemVenta = umItemVenta;
    }

    public Integer getCantMinima() {
        return cantMinima;
    }

    public void setCantMinima(Integer cantMinima) {
        this.cantMinima = cantMinima;
    }

    public Integer getCantMaxima() {
        return cantMaxima;
    }

    public void setCantMaxima(Integer cantMaxima) {
        this.cantMaxima = cantMaxima;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    public String getCodItemBonif() {
        return codItemBonif;
    }

    public void setCodItemBonif(String codItemBonif) {
        this.codItemBonif = codItemBonif;
    }

    public String getDescItemBonif() {
        return descItemBonif;
    }

    public void setDescItemBonif(String descItemBonif) {
        this.descItemBonif = descItemBonif;
    }

    public String getUmItemBonif() {
        return umItemBonif;
    }

    public void setUmItemBonif(String umItemBonif) {
        this.umItemBonif = umItemBonif;
    }

    public Integer getCantBonif() {
        return cantBonif;
    }

    public void setCantBonif(Integer cantBonif) {
        this.cantBonif = cantBonif;
    }

    public Integer getMultiplo() {
        return multiplo;
    }

    public void setMultiplo(Integer multiplo) {
        this.multiplo = multiplo;
    }
}
