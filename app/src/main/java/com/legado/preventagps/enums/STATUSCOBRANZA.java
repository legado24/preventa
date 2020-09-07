package com.legado.preventagps.enums;

/**
 * Created by __Adrian__ on 23/04/2019.
 */

public enum STATUSCOBRANZA {
    LIQUIDADO("LIQUIDADO"),
    DEPOSITADO("DEPOSITADO");

    private String desc;
    STATUSCOBRANZA(String desc) {
        this.desc=desc;
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
