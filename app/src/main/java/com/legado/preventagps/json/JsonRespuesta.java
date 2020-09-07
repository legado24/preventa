package com.legado.preventagps.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by __Adrian__ on 11/5/2017.
 */

public class JsonRespuesta<T> {

    @SerializedName("est")
    private Integer estado;
    @SerializedName("mens")
    private String mensaje;

    @SerializedName("data")
    private List<T> data;
    @SerializedName("item")
    private T item;

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

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
}
