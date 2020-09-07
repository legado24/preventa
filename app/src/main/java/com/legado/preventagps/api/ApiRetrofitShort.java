package com.legado.preventagps.api;




import com.legado.preventagps.services.AlmacenService;
import com.legado.preventagps.services.ArticuloService;
import com.legado.preventagps.services.ClienteService;
import com.legado.preventagps.services.CobranzaService;
import com.legado.preventagps.services.LoginService;
import com.legado.preventagps.services.NotificacionService;
import com.legado.preventagps.services.PlanificadorService;
import com.legado.preventagps.services.VendedorService;
import com.legado.preventagps.services.VentaService;

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

public class ApiRetrofitShort {
    private static ApiRetrofitShort instance = null;

    //public static final String BASE_URL = "http://190.223.55.172:8080/PreventaGps-2.3/preventaGps/";
    private LoginService loginService;
    private ClienteService clienteService;
    private VendedorService vendedorService;
    private ArticuloService articuloService;
    private VentaService ventaService;
    private CobranzaService cobranzaService;
    private AlmacenService almacenService;

    private NotificacionService notificacionService;

    public static ApiRetrofitShort getInstance(String BASE_URL) {
        if (instance == null) {
            instance = new ApiRetrofitShort(BASE_URL);
        }

        return instance;
    }
    private ApiRetrofitShort(String BASE_URL) {
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
     .readTimeout(30, TimeUnit.SECONDS)
                 .connectTimeout(6, TimeUnit.SECONDS).build();
    }
    private void buildRetrofit(String url) {
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.create();

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(url).client(getRequestHeader())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();
        this.loginService = mRetrofit.create(LoginService.class);
        this.clienteService=mRetrofit.create(ClienteService.class);
        this.vendedorService=mRetrofit.create(VendedorService.class);
        this.articuloService=mRetrofit.create(ArticuloService.class);
        this.ventaService=mRetrofit.create(VentaService.class);
        this.cobranzaService=mRetrofit.create(CobranzaService.class);
        this.almacenService=mRetrofit.create(AlmacenService.class);
         this.notificacionService=mRetrofit.create(NotificacionService.class);
    }
    public LoginService getLoginService() {
        return this.loginService;
    }

    public ClienteService getClienteService() {
        return this.clienteService;
    }
    public VendedorService getVendedorService() {
        return this.vendedorService;
    }

    public ArticuloService getArticuloService() {
        return this.articuloService;
    }

    public VentaService getVentaService() {
        return this.ventaService;
    }

    public CobranzaService getCobranzaService() {
        return cobranzaService;
    }

    public AlmacenService getAlmacenService() {
        return almacenService;
    }


    public NotificacionService getNotificacionService() {
        return notificacionService;
    }

}
