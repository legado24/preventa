package com.legado.preventagps.enums;

/**
 * Created by __Adrian__ on 23/04/2019.
 */

public enum STATUSCLIENTE {
    ACTIVO("A","ACTIVO"),
    INACTIVO("I","INACTIVO"),
    PENDIENTE("P","PENDIENTE");

    private String cod;
    private String desc;
    STATUSCLIENTE(String cod, String desc) {
        this.cod=cod;
        this.desc=desc;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
