package com.legado.preventagps.json;

import com.google.gson.annotations.SerializedName;

public class RespuestaDni {
    @SerializedName("apellidos")
    private String apellidos;

    @SerializedName("nombres")
    private String nombres;

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }
}
