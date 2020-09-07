package com.legado.preventagps.modelo;

/**
 * Created by __Adrian__ on 23/04/2019.
 */

public class PaqueteUsuario {
    private String usuario;
    private String codCanal;
    public String codPerfil;
    private String descCanal;
    private String token;
    private  String mac;
    private String codEmpresa;
    private String imei;
    private Integer ignoreGps;

    public Integer getIgnoreGps() {
        return ignoreGps;
    }

    public void setIgnoreGps(Integer ignoreGps) {
        this.ignoreGps = ignoreGps;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getCodPerfil() {
        return codPerfil;
    }

    public void setCodPerfil(String codPerfil) {
        this.codPerfil = codPerfil;
    }

    public String getCodEmpresa() {
        return codEmpresa;
    }

    public void setCodEmpresa(String codEmpresa) {
        this.codEmpresa = codEmpresa;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCodCanal() {
        return codCanal;
    }

    public void setCodCanal(String codCanal) {
        this.codCanal = codCanal;
    }

    public String getDescCanal() {
        return descCanal;
    }

    public void setDescCanal(String descCanal) {
        this.descCanal = descCanal;
    }

    public String getUsuario() {

        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
