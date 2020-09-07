package com.legado.preventagps.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by __Adrian__ on 11/5/2017.
 */

public class JsonDni<T> {

    @SerializedName("success")
    private String success;
    @SerializedName("dni")
    private String dni;
    @SerializedName("nombre")
     private String nombre;
    @SerializedName("actualizado_en")
    private String actualizado_en;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getActualizado_en() {
        return actualizado_en;
    }

    public void setActualizado_en(String actualizado_en) {
        this.actualizado_en = actualizado_en;
    }
}
