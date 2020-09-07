package com.legado.preventagps.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by __Adrian__ on 4/04/2019.
 */

public class PaqueteCarrito {
    List<CarritoCompras> listaCarrito=new ArrayList<>();

    public List<CarritoCompras> getListaCarrito() {
        return listaCarrito;
    }

    public void setListaCarrito(List<CarritoCompras> listaCarrito) {
        this.listaCarrito = listaCarrito;
    }
}
