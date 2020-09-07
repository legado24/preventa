package com.legado.preventagps.enums;

/**
 * Created by __Adrian__ on 26/04/2019.
 */

public enum CLIENTEENUM {
    CODCLIENTE("codCliente");
    private String clave;

    CLIENTEENUM(String clave) {
        this.clave=clave;
    }

    public String getClave() {
        return clave;
    }
}
