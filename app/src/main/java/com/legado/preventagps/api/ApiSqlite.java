package com.legado.preventagps.api;

import android.content.Context;

import com.legado.preventagps.bd.OperacionesBaseDatos;

/**
 * Created by __Adrian__ on 18/5/2017.
 */
public class ApiSqlite {

    private static ApiSqlite instance = null;

    private OperacionesBaseDatos operacionesBaseDatos;

    public static ApiSqlite getInstance(Context context) {
        if (instance == null) {
            instance = new ApiSqlite(context);
        }
        return instance;
    }

    private ApiSqlite(Context context) {
        operacionesBaseDatos = OperacionesBaseDatos.obtenerInstancia(context);
    }

    public OperacionesBaseDatos getOperacionesBaseDatos() {
        return this.operacionesBaseDatos;
    }
}
