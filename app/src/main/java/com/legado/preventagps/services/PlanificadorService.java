package com.legado.preventagps.services;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;

/**
 * Created by __Adrian__ on 15/08/2019.
 */

public interface PlanificadorService {
    @Headers({
//            "Accept: application/vnd.yourapi.v1.full+json",
//            "User-Agent: Your-App-Name",
            "Content-Type: application/pdf",
            "Content-Disposition: filename='adrian.pdf'",
            "Access-Control-Allow-Methods: GET, POST, PUT",
            "Access-Control-Allow-Headers: Content-Type",
//Access-Control-Allow-Origin:"*"
            "Cache-Control:no-cache, no-store, must-revalidate",
            "Pragma: no-cache",
            "Expires: 0"
//Accept-Encoding:application/pdf
    })
    @GET("generarPlanificador/pdf/as.pdf")
    Call<ResponseBody> generarPlanificador(@QueryMap Map<String, String> parametros);
}
