package com.legado.preventagps.enums;

/**
 * Created by __Adrian__ on 23/04/2019.
 */

public enum STATUSREPARTO {
    ENTREGADO("ENTREGADO"),
    REBOTE("REBOTE") ;

    private String cod;

    STATUSREPARTO(String cod) {
        this.cod=cod;

    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

}
