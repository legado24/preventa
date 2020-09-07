package com.legado.preventagps.api;




import com.legado.preventagps.services.ClienteService;
import com.legado.preventagps.services.OfflineService;
import com.legado.preventagps.services.PlanificadorService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by __Adrian__ on 17/5/2017.
 */

public class ApiRetrofitMoreLong {
    private static ApiRetrofitMoreLong instance = null;

    //public static final String BASE_URL = "http://190.223.55.172:8080/PreventaGps-2.3/preventaGps/";

    private PlanificadorService planificadorService;

    public static ApiRetrofitMoreLong getInstance(String BASE_URL) {
        if (instance == null) {
            instance = new ApiRetrofitMoreLong(BASE_URL);
        }

        return instance;
    }
    private ApiRetrofitMoreLong(String BASE_URL) {
        buildRetrofit(BASE_URL);
    }
    private okhttp3.OkHttpClient getRequestHeader() {
        return  new okhttp3.OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                        Credentials.basic("adrian", "123456"));

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }

//        }).readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS).build();
        })
     .readTimeout(120, TimeUnit.SECONDS)
                 .connectTimeout(10, TimeUnit.SECONDS).build();
    }
    private void buildRetrofit(String url) {
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.create();

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(url).client(getRequestHeader())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();

        this.planificadorService=mRetrofit.create(PlanificadorService.class);

    }


    public PlanificadorService getPlanificadorService() {
        return planificadorService;
    }


}
