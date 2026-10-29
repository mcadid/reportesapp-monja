package com.monja.reportesapp;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public final class ApiClient {
    // Para Render, reemplazar por la URL HTTPS pública terminada en /.
    private static final String BASE_URL = "http://10.0.2.2:8080/";
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
