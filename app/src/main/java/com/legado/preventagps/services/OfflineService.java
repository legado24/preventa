package com.legado.preventagps.services;

import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Cliente;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface OfflineService {
    @GET("clientesByDiaOffLine")
    Call<JsonRespuesta<Cliente>> clientesByDiaOffLine(@QueryMap Map<String, String> parametros);
    @GET("articulosOffLine")
    Call<JsonRespuesta> articulosOffLine(@QueryMap Map<String, String> parametros);
    @GET("datosUsuarioOffline")
    Call<JsonRespuesta> datosUsuarioOffline(@QueryMap Map<String, String> parametros);
    @GET("datosVendedorSueldo")
    Call<JsonRespuesta> datosVendedorSueldo(@QueryMap Map<String, String> parametros);

    @GET("onlyClientesByDiaOffLine")
    Call<JsonRespuesta> onlyClientesByDiaOffLine(@QueryMap Map<String, String> parametros);

}
