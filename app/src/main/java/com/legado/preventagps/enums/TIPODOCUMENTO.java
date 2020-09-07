package com.legado.preventagps.enums;

public enum TIPODOCUMENTO {
    FACTURA("01","F"),
    BOLETA("02","B") ;

    private String cod;
    private String letra;
    TIPODOCUMENTO(String cod,String letra) {
        this.cod=cod;
        this.letra=letra;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }
}
