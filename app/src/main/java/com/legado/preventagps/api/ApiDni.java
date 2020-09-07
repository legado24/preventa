package com.legado.preventagps.api;




import com.legado.preventagps.services.UrlsService;

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

public class ApiDni {
    private static ApiDni instance = null;

    public static final String BASE_URL = "https://api.migo.pe/api/v1/dni";
    private UrlsService urlsService;

    public static ApiDni getInstance() {
        if (instance == null) {
            instance = new ApiDni();
        }

        return instance;
    }
    private ApiDni() {
        buildRetrofit(BASE_URL);
    }
    private okhttp3.OkHttpClient getRequestHeader() {
        return  new okhttp3.OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                Request.Builder builder = originalRequest.newBuilder();

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }

//        }).readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS).build();
        })
     .readTimeout(90, TimeUnit.SECONDS)
                 .connectTimeout(10, TimeUnit.SECONDS).build();
    }
    private void buildRetrofit(String url) {
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.create();

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(url).client(getRequestHeader())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();
        this.urlsService = mRetrofit.create(UrlsService.class);


    }

    public UrlsService getUrlsService() {
        return urlsService;
    }
}
