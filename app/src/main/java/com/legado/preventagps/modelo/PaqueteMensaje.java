package com.legado.preventagps.modelo;

import java.util.List;

/**
 * Created by __Adrian__ on 12/06/2019.
 */

public class PaqueteMensaje {
    private Notification notification;
    private List<String> usuarios;
    private boolean all;

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

//    public List<String> getTokens() {
//        return tokens;
//    }
//
//    public void setTokens(List<String> tokens) {
//        this.tokens = tokens;
//    }

    public List<String> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<String> usuarios) {
        this.usuarios = usuarios;
    }
}

