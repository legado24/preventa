package com.legado.preventagps.services;

import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Cliente;
import com.legado.preventagps.modelo.ClienteAlta;
import com.legado.preventagps.modelo.Cobranza;
import com.legado.preventagps.modelo.DocumentoDeuda;
import com.legado.preventagps.modelo.LocalCliente;
import com.legado.preventagps.modelo.PlanillaCobranza;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by __Adrian__ on 15/03/2019.
 */

public interface CobranzaService {

    @POST("registrarCobranza")
    Call<JsonRespuesta<Integer>> registrarCobranza(@Body Cobranza cobranza);
    @GET("cobranzaByUsuario")
    Call<JsonRespuesta<Cobranza>> consultaCobranza(@QueryMap Map<String, String> options);

    @GET("getPlanillaCobranza")
    Call<JsonRespuesta<PlanillaCobranza>> getPlanillaCobranza(@QueryMap Map<String, String> options);





}
