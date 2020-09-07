package com.legado.preventagps.enums;

/**
 * Created by __Adrian__ on 23/04/2019.
 */

public enum CANAL {
    COBERTURA("1"),
    MAYORISTA("2");

    private String codCanal;
    CANAL(String codCanal) {
        this.codCanal=codCanal;
    }

    public String getCodCanal() {
        return codCanal;
    }

    public void setCodCanal(String codCanal) {
        this.codCanal = codCanal;
    }
}
