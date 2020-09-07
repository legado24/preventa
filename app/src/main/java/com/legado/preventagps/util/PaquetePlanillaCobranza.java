package com.legado.preventagps.util;

import com.legado.preventagps.modelo.PlanillaCobranza;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by __Adrian__ on 8/08/2019.
 */

public class PaquetePlanillaCobranza {
    private List<PlanillaCobranza> listaPlanilla=new ArrayList<>();

    public List<PlanillaCobranza> getListaPlanilla() {
        return listaPlanilla;
    }

    public void setListaPlanilla(List<PlanillaCobranza> listaPlanilla) {
        this.listaPlanilla = listaPlanilla;
    }
}
