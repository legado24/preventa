package com.legado.preventagps.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by __Adrian__ on 4/04/2019.
 */

public class PaqueteCliente {
    List<Cliente> clientesSecuenciados=new ArrayList<>();

    public List<Cliente> getClientesSecuenciados() {
        return clientesSecuenciados;
    }

    public void setClientesSecuenciados(List<Cliente> clientesSecuenciados) {
        this.clientesSecuenciados = clientesSecuenciados;
    }
}
