package com.legado.preventagps.services;

import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Cliente;
import com.legado.preventagps.modelo.ClienteAlta;
import com.legado.preventagps.modelo.DocumentoDeuda;
import com.legado.preventagps.modelo.LocalCliente;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by __Adrian__ on 15/03/2019.
 */

public interface ClienteService {

    @GET("clientesByDia")
    Call<JsonRespuesta<Cliente>> clientesByDia(@QueryMap Map<String, String> parametros);

    @GET("datosCliente")
    Call<JsonRespuesta> datosCliente(@QueryMap Map<String, String> parametros);

    @GET("consultarDocumento")
    Call<JsonRespuesta> datosAltaCliente(@QueryMap Map<String, String> parametros);

    @GET("datosUbicacionGeo")
    Call<JsonRespuesta> datosUbicacionGeo(@QueryMap Map<String, String> parametros);
    @GET("listTipoClientes")
    Call<JsonRespuesta> listTipoClientes();

    @GET("listGiroClientes")
    Call<JsonRespuesta> listGiroClientes();

    @GET("listaLp")
    Call<JsonRespuesta> listLp(@QueryMap Map<String, String> parametros);

    @GET("cargarDatosAlta")
    Call<JsonRespuesta> cargarDatosAlta(@QueryMap Map<String, String> parametros);
    @POST("registrarCliente")
    Call<JsonRespuesta<Integer>> registrarCliente(@Body ClienteAlta clienteAlta);
    @POST("mantenimientoLocal")
    Call<JsonRespuesta<Integer>> mantenimientoLocal(@Body LocalCliente localCliente);

    @GET("cargarDatosClienteUpdate")
    Call<JsonRespuesta> cargarDatosClienteUpdate(@QueryMap Map<String, String> parametros);


    @GET("deudaByCliente")
    Call<JsonRespuesta<DocumentoDeuda>> deudaByCliente(@QueryMap Map<String, String> parametros);

    @GET("clientesSinCoordenadas")
    Call<JsonRespuesta<Cliente>> clientesSinCoordenadas(@QueryMap Map<String, String> parametros);

    @GET("datosClienteV1")
    Call<JsonRespuesta> datosClienteV1(@QueryMap Map<String, String> parametros);

    @GET("clientesOutRuta")
    Call<JsonRespuesta<Cliente>> clientesOutRuta(@QueryMap Map<String, String> parametros);


    @GET("clientesByDiaV2")
    Call<JsonRespuesta> clientesByDiaV2(@QueryMap Map<String, String> parametros);

    @GET("getRutasByUsuario")
    Call<JsonRespuesta> getRutasByUsuario(@QueryMap Map<String, String> parametros);



    @GET("getTiposByGiro")
    Call<JsonRespuesta> getTiposByGiro(@QueryMap Map<String, String> parametros);



}
