package com.legado.preventagps.services;

import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.ConsultaPagos;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by __Adrian__ on 15/03/2019.
 */

public interface AlmacenService {

    @GET("almacenesByLocalidad")
    Call<JsonRespuesta> almacenesByLocalidad(@QueryMap Map<String, String> parametros);




}
