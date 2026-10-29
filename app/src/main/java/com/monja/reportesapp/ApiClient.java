package com.monja.reportesapp;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public final class ApiClient {
    // API pública desplegada en Render.
    private static final String BASE_URL = "https://reporteservice-monja.onrender.com/";
    private static ApiService service;
    private ApiClient() {}
    public static String baseUrl() { return BASE_URL; }
    public static ApiService service() {
        if (service == null) {
            OkHttpClient http = new OkHttpClient.Builder().connectTimeout(75, TimeUnit.SECONDS)
                    .readTimeout(75, TimeUnit.SECONDS).writeTimeout(75, TimeUnit.SECONDS).build();
            service = new Retrofit.Builder().baseUrl(BASE_URL).client(http)
                    .addConverterFactory(GsonConverterFactory.create()).build().create(ApiService.class);
        }
        return service;
    }
}
