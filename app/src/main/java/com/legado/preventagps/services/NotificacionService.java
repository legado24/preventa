package com.legado.preventagps.services;

import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Articulo;
import com.legado.preventagps.modelo.BonificacionItem;
import com.legado.preventagps.modelo.BonificacionPaquete;
import com.legado.preventagps.modelo.IngresoCompra;
import com.legado.preventagps.modelo.PaqueteMensaje;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by __Adrian__ on 15/03/2019.
 */

public interface NotificacionService {
    @GET("bandejaIngresos")
    Call<JsonRespuesta<IngresoCompra>> bandejaIngresos(@QueryMap Map<String, String> parametros);

    @GET("bandejaBonifItem")
    Call<JsonRespuesta<BonificacionItem>> bandejaBonifItem(@QueryMap Map<String, String> parametros);

    @GET("bandejaBonifPaquete")
    Call<JsonRespuesta> bandejaBonifPaquete(@QueryMap Map<String, String> parametros);


    @GET("bandejaRepartoVenta")
    Call<JsonRespuesta> bandejaRepartoVenta(@QueryMap Map<String, String> parametros);


    @POST("enviarUbicacionVendedor")
    Call<JsonRespuesta> enviarUbicacionVendedor(@Body PaqueteMensaje paqueteMensaje);


}
