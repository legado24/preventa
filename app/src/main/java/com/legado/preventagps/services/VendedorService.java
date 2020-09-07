package com.legado.preventagps.services;

import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.CarritoCompras;
import com.legado.preventagps.modelo.Cliente;
import com.legado.preventagps.modelo.ConsultaPagos;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by __Adrian__ on 15/03/2019.
 */

public interface VendedorService {

    @GET("datosUsuario")
    Call<JsonRespuesta> datosUsuario(@QueryMap Map<String, String> parametros);

    @GET("condiciones")
    Call<JsonRespuesta> condiciones(@QueryMap Map<String, String> parametros);

    @GET("datosUsuarioAlta")
    Call<JsonRespuesta> datosUsuarioAlta(@QueryMap Map<String, String> parametros);

    @GET("datosVendedor")
    Call<JsonRespuesta> datosVendedor(@QueryMap Map<String, String> parametros);




    @GET("pagosByUsuario")
    Call<JsonRespuesta<ConsultaPagos>> pagosByUsuario(@QueryMap Map<String, String> options);

    @GET("permiteOutRuta")
    Call<JsonRespuesta> permiteOutRuta(@QueryMap Map<String, String> parametros);

    @GET("focusByUsuario")
    Call<JsonRespuesta<CarritoCompras>> focusByUsuario(@QueryMap Map<String, String> parametros);

    //OFFLINE




    @GET("vendedoresBySupervisor")
    Call<JsonRespuesta> vendedoresBySupervisor(@QueryMap Map<String, String> parametros);

    @GET("validarEdicion")
    Call<JsonRespuesta> validarEdicion(@QueryMap Map<String, String> parametros);

}
