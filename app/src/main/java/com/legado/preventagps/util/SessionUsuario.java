package com.legado.preventagps.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.legado.preventagps.modelo.Cliente;
import com.legado.preventagps.modelo.PaqueteAlta;
import com.legado.preventagps.modelo.PaqueteCarrito;
import com.legado.preventagps.modelo.PaqueteCliente;
import com.legado.preventagps.modelo.PaqueteUsuario;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SessionUsuario {

    SharedPreferences preferences; //Lista de Preferencias
    SharedPreferences.Editor editor; // Editor de Preferencias
    Context _context; // Contexto
    int PRIVATE_MODE = 0; // Modo de Preferencias -> es el modo seguro solo la aplicacion accede a este archivo xml
    public static final String PREFER_NAME = "sessionpreventa";// Nombre de archivo de preferencias compartidas
    public static final String IS_USER_LOGIN = "isUserLoggedIn"; // Todas las claves de preferencias compartidas
    public static final String KEY_NAME = "nombre"; // Nombre de usuario (hacer pública la variable para acceder desde fuera)
    public static final String KEY_EMAIL = "email"; // Dirección de correo electrónico (hacer pública la variable para acceder desde fuera)
    Gson gson=new Gson();

    public SessionUsuario(Context context){
        this._context = context;
        preferences = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public void IniciarSession(Boolean seinicio, String usuario, String codAplicacion){
        editor.putBoolean("login",seinicio);
        editor.putString("usuario",usuario);
         if(seinicio) {
            editor.putString("codigo", codAplicacion);
        }
        editor.commit();
    }

    public Boolean getSession(){
        return preferences.getBoolean("login",false);
    }

    public String getCodigoAplicacion(){
        String codigo = preferences.getString("codigo","");
        return codigo;
    }


    public void guardarUsuario(String username){
        editor.putString("usuario",username);
        editor.commit();
    }
    public void guardarProvider(String provider){
        editor.putString("provider",provider);
        editor.commit();
    }
//    public String getProvider(){
//        return  preferences.getString("provider","");
//    }
    public String getUsuario(){
        return  preferences.getString("usuario","");
    }

    public void CargarCodigo(String codAplicacion){
        editor.putString("codigo",codAplicacion);
        editor.commit();
    }

//    public void guardarPaqueteClienteAux(PaqueteCliente paquete){
//        Gson gson = new Gson();
//        String paqueteJson = gson.toJson(paquete);
//        editor.putString("paqueteclienteaux",paqueteJson);
//        editor.commit();
//    }
//    public PaqueteCliente getPaqueteClienteAux(){
//        String json = preferences.getString("paqueteclienteaux", "");
//        PaqueteCliente obj = gson.fromJson(json, PaqueteCliente.class);
//        return obj;
//    }
//

//
//    public void guardarPaqueteCliente(PaqueteCliente paquete){
//        Gson gson = new Gson();
//        String paqueteJson = gson.toJson(paquete);
//        editor.putString("paquetecliente",paqueteJson);
//        editor.commit();
//    }
//
//    public PaqueteCliente getPaqueteCliente(){
//        String json = preferences.getString("paquetecliente", "");
//        PaqueteCliente obj = gson.fromJson(json, PaqueteCliente.class);
//        return obj;
//    }

    public void guardarPaquetePlanillaCobranza(PaquetePlanillaCobranza paquete){
        Gson gson = new Gson();
        String paqueteJson = gson.toJson(paquete);
        editor.putString("paqueteplanillacobranza",paqueteJson);
        editor.commit();
    }

    public PaquetePlanillaCobranza getPaquetePlanillaCobranza(){
        String json = preferences.getString("paqueteplanillacobranza", "");
        PaquetePlanillaCobranza obj = gson.fromJson(json, PaquetePlanillaCobranza.class);
        return obj;
    }

    public void guardarPaqueteUsuario(PaqueteUsuario paquete){
        Gson gson = new Gson();
        String paqueteJson = gson.toJson(paquete);
        editor.putString("paqueteusuario",paqueteJson);
        editor.commit();
    }

    public PaqueteUsuario getPaqueteUsuario(){
        String json = preferences.getString("paqueteusuario", "");
        PaqueteUsuario obj = gson.fromJson(json, PaqueteUsuario.class);
        return obj;
    }


    public void guardarPaqueteCarrito(PaqueteCarrito paquete){
        Gson gson = new Gson();
        String paqueteJson = gson.toJson(paquete);
        editor.putString("paquetecarrito",paqueteJson);
        editor.commit();
    }
    public PaqueteCarrito getPaqueteCarrito(){
        String json = preferences.getString("paquetecarrito", "");
        PaqueteCarrito obj = gson.fromJson(json, PaqueteCarrito.class);
        return obj;
    }

    public void guardarUbicacion(String ubicacion){
        editor.putString("ubicacion",ubicacion);
        editor.commit();
    }

    public String getUbicacion(){
        return  preferences.getString("ubicacion","");
    }
    public void guardarUbicacionLast(String ubicacion){
        editor.putString("ubicacionlast",ubicacion);
        editor.commit();
    }

    public String getUbicacionLast(){
        return  preferences.getString("ubicacionlast","");
    }

public void guardarDeudaVencida(String deuda) {
    editor.putString("deudavencida", deuda);
    editor.commit();
}


    public String getDeudaVencida(){
        return  preferences.getString("deudavencida","");
    }

    public void guardarDeudaNoVencida(String deuda) {
        editor.putString("deudanovencida", deuda);
        editor.commit();
    }


    public String getDeudaNoVencida(){
        return  preferences.getString("deudanovencida","");
    }





    public void guardarPaqueteAlta(PaqueteAlta paquete){
        Gson gson = new Gson();
        String paqueteJson = gson.toJson(paquete);
        editor.putString("paquetealta",paqueteJson);
        editor.commit();
    }

    public PaqueteAlta getPaqueteAlta(){
        String json = preferences.getString("paquetealta", "");
        PaqueteAlta obj = gson.fromJson(json, PaqueteAlta.class);
        return obj;
    }

    public void guardarArrayListSugeridos(ArrayList sugeridos){
        Gson gson = new Gson();
        String paqueteJson = gson.toJson(sugeridos);
        editor.putString("arraySugeridos",paqueteJson);
        editor.commit();
    }
    public ArrayList getArrayListSugeridos(){
        String json = preferences.getString("arraySugeridos", "");
        ArrayList obj = gson.fromJson(json, ArrayList.class);
        return obj;
    }



    public void guardarEstadoFragment(boolean guardar){
        editor.putBoolean("saveFragment",guardar);
        editor.commit();
    }
    public boolean getEstadoFragment(){
       return preferences.getBoolean("saveFragment",false);
    }
    public void guardarBandSugerido(boolean guardar){
        editor.putBoolean("bandSugerido",guardar);
        editor.commit();
    }

    public boolean getBandSugerido(){
        return preferences.getBoolean("bandSugerido",false);
    }


    public void guardarBandSettings(boolean entro) {
        editor.putBoolean("setting", entro);
        editor.commit();
    }



    public void guardarUrlPreventa(String url) {
        editor.putString("URLPREVENTA", url);
        editor.commit();
    }
    public String getUrlPreventa(){
        return preferences.getString("URLPREVENTA","");
    }



    public boolean getIsTodoSincronizado(){
        return preferences.getBoolean("todoOk",false);
    }



    public void guardarIsTodoSincronizado(boolean entro) {
        editor.putBoolean("todoOk", entro);
        editor.commit();
    }

    public boolean getIsOnlyOnline(){
        return preferences.getBoolean("online",false);
    }


    public void guardarIsOnlyOnline(boolean entro) {
        editor.putBoolean("online", entro);
        editor.commit();
    }

    public String getStringNotificacion(){
        return preferences.getString("stringNotificacion","");
    }


    public void guardarStringNotificacion(String  stringNotificacion) {
        editor.putString("stringNotificacion", stringNotificacion);
        editor.commit();
    }



    public boolean getBandSettings(){
        return  preferences.getBoolean("setting",false);
    }


    public void borrarDatosSesion(){
        IniciarSession(false,null,null);//desconectarme
        guardarUsuario(null);
       //guardarPaqueteCliente(null);
        guardarUsuario(null);
        guardarIsTodoSincronizado(false);
        guardarIsOnlyOnline(false);
        guardarPaqueteAlta(null);
        guardarDeudaVencida(null);
        guardarUbicacionLast(null);
        guardarUbicacion(null);
        guardarPaqueteCarrito(null);
        guardarUrlPreventa(null);
        CargarCodigo(null);
    }



    public void guardarPaqueteClienteByVendedor(PaqueteCliente paqueteCliente){
        Gson gson = new Gson();
        String paqueteJson = gson.toJson(paqueteCliente);
        editor.putString("paqueteClienteVendedor",paqueteJson);
        editor.commit();
    }
    public PaqueteCliente getPaqueteClienteByVendedor(){
        String json = preferences.getString("paqueteClienteVendedor", "");
       PaqueteCliente obj = gson.fromJson(json, PaqueteCliente.class);
        return obj;
    }


    public void guardarPaqueteUtilLocales(PaqueteUtilLocales paquete){
        Gson gson = new Gson();
        String paqueteJson = gson.toJson(paquete);
        editor.putString("paqueteutillocales",paqueteJson);
        editor.commit();
    }

    public PaqueteUtilLocales getPaqueteUtilLocales(){
        String json = preferences.getString("paqueteutillocales", "");
        PaqueteUtilLocales obj = gson.fromJson(json, PaqueteUtilLocales.class);
        return obj;
    }


}