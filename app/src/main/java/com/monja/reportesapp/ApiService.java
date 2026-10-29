package com.monja.reportesapp;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface ApiService {
    @GET("reportes/") Call<PaginaReportes> listar(@Query("offset") int offset, @Query("limit") int limit);
    @GET("reportes/{id}") Call<Reporte> obtener(@Path("id") long id);
    @GET("reportes/ciudadano/{dni}") Call<List<Reporte>> porCiudadano(@Path("dni") String dni);
    @POST("reportes/") Call<Reporte> registrar(@Body Reporte reporte);
    @PUT("reportes/{id}") Call<Reporte> atender(@Path("id") long id);
}
