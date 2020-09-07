package com.legado.preventagps.modelo;

/**
 * Created by __Adrian__ on 12/06/2019.
 */


public class Notification {
    private String title;
    private String body;
    private String mensaje;
    private String clickAction;

    public String getClickAction() {
        return clickAction;
    }

    public void setClickAction(String clickAction) {
        this.clickAction = clickAction;
    }

    public Notification(String title, String body, String mensaje) {
        this.title = title;
        this.body = body;
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
