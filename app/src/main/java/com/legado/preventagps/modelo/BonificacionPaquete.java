package com.legado.preventagps.modelo;

import java.util.List;

public class BonificacionPaquete implements Comparable<BonificacionPaquete>{
    private String codLista;
    private String codPaquete;
    private Integer modoParticipacion;
    private String descPaquete;
    private String umPaquete;
    private String codItem;
    private String descItem;
    private List<BonificacionPaquete> participaciones;
    private List<BonificacionItem> bonificaciones;
    private String horaCreacion;


    public String getHoraCreacion() {
        return horaCreacion;
    }

    public void setHoraCreacion(String horaCreacion) {
        this.horaCreacion = horaCreacion;
    }
    public List<BonificacionPaquete> getParticipaciones() {
        return participaciones;
    }

    public void setParticipaciones(List<BonificacionPaquete> participaciones) {
        this.participaciones = participaciones;
    }

    public List<BonificacionItem> getBonificaciones() {
        return bonificaciones;
    }

    public void setBonificaciones(List<BonificacionItem> bonificaciones) {
        this.bonificaciones = bonificaciones;
    }

    public String getCodLista() {
        return codLista;
    }

    public void setCodLista(String codLista) {
        this.codLista = codLista;
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

    public String getDescPaquete() {
        return descPaquete;
    }

    public void setDescPaquete(String descPaquete) {
        this.descPaquete = descPaquete;
    }

    public String getUmPaquete() {
        return umPaquete;
    }

    public void setUmPaquete(String umPaquete) {
        this.umPaquete = umPaquete;
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



    @Override
    public int compareTo(BonificacionPaquete bonificacionPaquete) {
        if (new Integer(this.horaCreacion)> new Integer(bonificacionPaquete.horaCreacion)) {
            return -1;
        }
        if (new Integer(this.horaCreacion) <new Integer(bonificacionPaquete.horaCreacion)) {
            return 1;
        }
        return 0;
    }
}
