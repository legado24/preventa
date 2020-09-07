package com.legado.preventagps.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by __Adrian__ on 11/5/2017.
 */

public class JsonUrl<T> {

    @SerializedName("estado")
    private Integer estado;
    @SerializedName("mensaje")
    private String mensaje;
    @SerializedName("datos")
     private Datos datos;




    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Datos getDatos() {
        return datos;
    }

    public void setDatos(Datos datos) {
        this.datos = datos;
    }
}
