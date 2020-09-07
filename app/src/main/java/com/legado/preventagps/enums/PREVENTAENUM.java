package com.legado.preventagps.enums;

/**
 * Created by __Adrian__ on 26/04/2019.
 */

public enum PREVENTAENUM {
    CODEMPRESA("codEmpresa"),
    CODLOCALIDAD("codLocalidad"),
    CODSEDE("codSede"),
    CODALMACEN("codAlmacen"),
    CODCLIENTE("codCliente"),
    DESCCLIENTE("descCliente"),
    DIRCLIENTE("dirCliente"),
    CODRUTA("codRuta"),
    CODLOCAL("codLocal"),
    SECUENCIA("secuencia"),
    CODLISTA("codLista"),
    ISONLINE("isOnline");
    private String clave;

    PREVENTAENUM(String clave) {
        this.clave=clave;
    }

    public String getClave() {
        return clave;
    }
}
