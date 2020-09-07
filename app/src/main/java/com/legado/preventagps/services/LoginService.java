package com.legado.preventagps.services;

import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.PaqueteUsuario;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by __Adrian__ on 12/03/2019.
 */

public interface LoginService {
    @GET("login")
    Call<JsonRespuesta> login(@QueryMap Map<String, String> parametros);

    @POST("registrarToken")
    Call<JsonRespuesta<Integer>> registrarToken(@Body PaqueteUsuario paqueteUsuario);

    @GET("validarUsuario")
    Call<JsonRespuesta> validarUsuario(@QueryMap Map<String,String> options);
}
