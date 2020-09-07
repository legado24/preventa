package com.legado.preventagps.services;

import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.ConsultaCabecera;
import com.legado.preventagps.modelo.ConsultaDetalle;
import com.legado.preventagps.modelo.NoPedido;
import com.legado.preventagps.modelo.PreVenta;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;

/**
 * Created by __Adrian__ on 9/04/2019.
 */

public interface VentaService {
    @POST("registrarPedidoMovilv2")
    Call<JsonRespuesta<Integer>> registrarPedidoLocal(@Body PreVenta preVenta);
    @POST("registrarNoPedido")
    Call<JsonRespuesta<Integer>> registrarNoPedido(@Body NoPedido noPedido);

    @GET("consultaPedidos")
    Call<JsonRespuesta<ConsultaCabecera>> consultaPedidos(@QueryMap Map<String, String> options);
    @GET("consultaDetallesPedidos")
    Call<JsonRespuesta<ConsultaDetalle>> consultaDetalllesPedidos(@QueryMap Map<String, String> options);

    @PUT("anularPedido")
    Call<JsonRespuesta<Integer>> anularPedido(@Body ConsultaCabecera cabecera);

    @GET("sueldoUsuario")
    Call<JsonRespuesta> sueldoUsuario(@QueryMap Map<String, String> options);
    //OFFLINE
    @POST("registrarPedidoMovilOff")
    Call<JsonRespuesta<Integer>> registrarPedidoLocalOffline(@Body PreVenta preVenta);


}
