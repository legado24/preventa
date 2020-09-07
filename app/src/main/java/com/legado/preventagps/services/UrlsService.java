package com.legado.preventagps.services;

import com.legado.preventagps.json.JsonDni;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.json.JsonUrl;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface UrlsService {
    @GET("geturl.php")
    Call<JsonUrl> getUrl();


    @POST("https://api.migo.pe/api/v1/dni")
    Call<JsonDni> getDni(@QueryMap Map<String, String> parametros);
}
