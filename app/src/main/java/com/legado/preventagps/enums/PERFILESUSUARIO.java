package com.legado.preventagps.enums;

public enum PERFILESUSUARIO {

    VENDEDOR("06"),
    JEFEDEVENTAS("03"),
    COORDINADOR("05") ;
    private String clave;

    PERFILESUSUARIO(String clave) {
        this.clave=clave;
    }

    public String getClave() {
        return clave;
    }
}
