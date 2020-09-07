package com.legado.preventagps.enums;

/**
 * Created by __Adrian__ on 23/04/2019.
 */

public enum STATUS {
    ACTIVO("A","ACTIVO"),
    INACTIVO("I","INACTIVO");

    private String cod;
    private String desc;
    STATUS(String cod, String desc) {
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
