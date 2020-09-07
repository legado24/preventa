package com.legado.preventagps.services;

import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Articulo;
import com.legado.preventagps.modelo.Cliente;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by __Adrian__ on 15/03/2019.
 */

public interface ArticuloService {
    @GET("articulos")
    Call<JsonRespuesta<Articulo>> articulos(@QueryMap Map<String, String> parametros);





    @GET("addArticulosSugeridos")
    Call<JsonRespuesta<Articulo>> addArticulosSugeridos(@QueryMap Map<String, String> parametros);

}
