package com.monja.reportesapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.io.IOException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private LinearLayout root;
    private RecyclerView recycler;
    private ReporteAdapter adapter;
    private Button siguiente;
    private int offset = 0;
    private boolean ultimaPagina = false;
    private static final int LIMITE = 5;
    private final int verde = Color.rgb(23, 107, 82);
    private final int tinta = Color.rgb(36, 53, 45);

    @Override protected void onCreate(Bundle state) { super.onCreate(state); mostrarInicio(); }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + .5f);
    }

    private void base(String titulo) {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(14), dp(18), dp(12));
        root.setBackgroundColor(Color.rgb(245, 247, 243));

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(20), dp(18), dp(20), dp(18));
        header.setBackgroundResource(R.drawable.bg_header);

        TextView eyebrow = new TextView(this);
        eyebrow.setText("LIMPIEZA PÚBLICA  ·  MUNICIPALIDAD");
        eyebrow.setTextSize(11);
        eyebrow.setLetterSpacing(.08f);
        eyebrow.setTextColor(Color.rgb(205, 237, 217));
        header.addView(eyebrow);

        TextView heading = new TextView(this);
        heading.setText(titulo);
        heading.setTextSize(25);
        heading.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        heading.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(-1, -2);
        titleParams.topMargin = dp(5);
        header.addView(heading, titleParams);

        TextView subtitle = new TextView(this);
        subtitle.setText("Reporta y consulta puntos de acumulación de residuos");
        subtitle.setTextSize(13);
        subtitle.setTextColor(Color.rgb(224, 241, 229));
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(-1, -2);
        subParams.topMargin = dp(3);
        header.addView(subtitle, subParams);

        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(-1, -2);
        headerParams.bottomMargin = dp(14);
        root.addView(header, headerParams);
    }

    private TextView label(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(14);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setTextColor(tinta);
        view.setPadding(dp(2), dp(8), dp(2), dp(5));
        return view;
    }

    private EditText field(LinearLayout parent, String title, String hint, boolean numeric) {
        parent.addView(label(title));
        EditText edit = new EditText(this);
        edit.setSingleLine(true);
        edit.setTextSize(15);
        edit.setHint(hint);
        edit.setTextColor(tinta);
        edit.setHintTextColor(Color.rgb(145, 157, 149));
        edit.setInputType(numeric ? InputType.TYPE_CLASS_NUMBER :
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        edit.setPadding(dp(14), 0, dp(14), 0);
        edit.setMinHeight(dp(50));
        edit.setBackgroundResource(R.drawable.bg_field);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.bottomMargin = dp(7);
        parent.addView(edit, params);
        return edit;
    }

    private Button boton(String text, boolean principal, Runnable accion) {
        MaterialButton button = new MaterialButton(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(15);
        button.setCornerRadius(dp(13));
        button.setInsetTop(0);
        button.setInsetBottom(0);
        button.setElevation(dp(1));
        button.setBackgroundTintList(ColorStateList.valueOf(
                principal ? verde : Color.rgb(225, 239, 229)));
        button.setTextColor(principal ? Color.WHITE : verde);
        button.setOnClickListener(v -> accion.run());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp(48));
        params.topMargin = dp(4);
        params.bottomMargin = dp(4);
        root.addView(button, params);
        return button;
    }

    private LinearLayout panel() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(10), dp(14), dp(12));
        card.setBackgroundResource(R.drawable.bg_surface);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.bottomMargin = dp(8);
        root.addView(card, params);
        return card;
    }

    private void mostrarEnScroll() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.addView(root, new ScrollView.LayoutParams(-1, -2));
        setContentView(scroll);
    }

    private void mensaje(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private String errorServidor(Response<?> response) {
        String detalle = "";
        try {
            if (response.errorBody() != null) detalle = response.errorBody().string();
        } catch (IOException ignored) {
            detalle = "No se pudo leer el detalle del servidor";
        }
        return "HTTP " + response.code() + (detalle.isEmpty() ? "" : ": " + detalle);
    }

    private void mostrarInicio() {
        base("Reportes de limpieza");
        boton("+  Registrar reporte", true, this::mostrarFormulario);

        LinearLayout searchPanel = panel();
        searchPanel.addView(label("Buscar por ciudadano"));
        EditText dni = new EditText(this);
        dni.setSingleLine(true);
        dni.setTextSize(15);
        dni.setInputType(InputType.TYPE_CLASS_NUMBER);
        dni.setHint("DNI de 8 dígitos");
        dni.setTextColor(tinta);
        dni.setHintTextColor(Color.rgb(145, 157, 149));
        dni.setPadding(dp(14), 0, dp(14), 0);
        dni.setMinHeight(dp(48));
        dni.setBackgroundResource(R.drawable.bg_field);
        searchPanel.addView(dni);

        MaterialButton search = new MaterialButton(this);
        search.setText("Buscar reportes");
        search.setAllCaps(false);
        search.setCornerRadius(dp(12));
        search.setInsetTop(0);
        search.setInsetBottom(0);
        search.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(225, 239, 229)));
        search.setTextColor(verde);
        LinearLayout.LayoutParams searchParams = new LinearLayout.LayoutParams(-1, dp(44));
        searchParams.topMargin = dp(8);
        searchPanel.addView(search, searchParams);
        search.setOnClickListener(v -> buscar(dni.getText().toString().trim()));

        MaterialButton all = new MaterialButton(this);
        all.setText("Mostrar todos");
        all.setAllCaps(false);
        all.setCornerRadius(dp(12));
        all.setInsetTop(0);
        all.setInsetBottom(0);
        all.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(239, 243, 239)));
        all.setTextColor(tinta);
        LinearLayout.LayoutParams allParams = new LinearLayout.LayoutParams(-1, dp(40));
        allParams.topMargin = dp(5);
        searchPanel.addView(all, allParams);
        all.setOnClickListener(v -> cargarPagina(true));

        TextView recent = label("Reportes recientes");
        recent.setTextSize(17);
        root.addView(recent);
        recycler = new RecyclerView(this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReporteAdapter(this::mostrarDetalle);
        recycler.setAdapter(adapter);
        root.addView(recycler, new LinearLayout.LayoutParams(-1, 0, 1));
        siguiente = boton("Cargar los siguientes 5", false, () -> cargarPagina(false));
        setContentView(root);
        cargarPagina(true);
    }

    private void cargarPagina(boolean reiniciar) {
        if (reiniciar) {
            offset = 0;
            ultimaPagina = false;
            adapter.limpiar();
            siguiente.setVisibility(View.VISIBLE);
        } else if (ultimaPagina) return;
        siguiente.setEnabled(false);
        ApiClient.service().listar(offset, LIMITE).enqueue(new Callback<PaginaReportes>() {
            @Override public void onResponse(Call<PaginaReportes> call, Response<PaginaReportes> response) {
                siguiente.setEnabled(true);
                if (!response.isSuccessful() || response.body() == null) {
                    mensaje("No se pudieron cargar los reportes (" + response.code() + ")");
                    return;
                }
                List<Reporte> datos = response.body().content;
                adapter.agregar(datos);
                offset += LIMITE;
                ultimaPagina = response.body().last || datos.isEmpty();
                siguiente.setVisibility(ultimaPagina ? View.GONE : View.VISIBLE);
            }
            @Override public void onFailure(Call<PaginaReportes> call, Throwable error) {
                siguiente.setEnabled(true);
                mensaje("Error de conexión: " + error.getMessage());
            }
        });
    }

    private void buscar(String dni) {
        if (!dni.matches("\\d{8}")) {
            mensaje("Ingresa un DNI de 8 dígitos");
            return;
        }
        ApiClient.service().porCiudadano(dni).enqueue(new Callback<List<Reporte>>() {
            @Override public void onResponse(Call<List<Reporte>> call, Response<List<Reporte>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.reemplazar(response.body());
                    siguiente.setVisibility(View.GONE);
                    mensaje("Reportes encontrados: " + response.body().size());
                } else mensaje("No se pudo buscar el DNI (" + response.code() + ")");
            }
            @Override public void onFailure(Call<List<Reporte>> call, Throwable error) {
                mensaje("Error: " + error.getMessage());
            }
        });
    }

    private void mostrarFormulario() {
        base("Nuevo reporte");
        LinearLayout form = panel();
        EditText dni = field(form, "DNI del ciudadano", "8 dígitos", true);

        form.addView(label("Tipo de residuo"));
        Spinner tipo = new Spinner(this);
        ArrayAdapter<CharSequence> tipos = ArrayAdapter.createFromResource(this,
                R.array.tipos_reporte, android.R.layout.simple_spinner_item);
        tipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo.setAdapter(tipos);
        tipo.setPadding(dp(10), dp(3), dp(10), dp(3));
        tipo.setBackgroundResource(R.drawable.bg_field);
        form.addView(tipo, new LinearLayout.LayoutParams(-1, dp(50)));

        EditText direccion = field(form, "Dirección", "Ej.: Av. Los Álamos 123", false);
        EditText referencia = field(form, "Referencia (opcional)", "Cerca del parque, esquina...", false);

        boton("Registrar reporte", true, () -> {
            if (!dni.getText().toString().trim().matches("\\d{8}")) {
                mensaje("El DNI debe tener 8 dígitos");
                return;
            }
            if (direccion.getText().toString().trim().isEmpty()) {
                mensaje("Completa la dirección");
                return;
            }
            Reporte r = new Reporte();
            r.dni = dni.getText().toString().trim();
            r.tipo = tipo.getSelectedItem().toString();
            r.direccion = direccion.getText().toString().trim();
            r.referencia = referencia.getText().toString().trim().isEmpty()
                    ? null : referencia.getText().toString().trim();
            ApiClient.service().registrar(r).enqueue(new Callback<Reporte>() {
                @Override public void onResponse(Call<Reporte> call, Response<Reporte> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        mensaje("Reporte #" + response.body().id + " registrado correctamente");
                        mostrarInicio();
                    } else mensaje("No se pudo registrar. " + errorServidor(response));
                }
                @Override public void onFailure(Call<Reporte> call, Throwable error) {
                    mensaje("No se pudo conectar con la API. " + error.getMessage());
                }
            });
        });
        boton("Volver", false, this::mostrarInicio);
        mostrarEnScroll();
    }

    private void mostrarDetalle(Reporte reporte) {
        ApiClient.service().obtener(reporte.id).enqueue(new Callback<Reporte>() {
            @Override public void onResponse(Call<Reporte> call, Response<Reporte> response) {
                mostrarDetalleLocal(response.isSuccessful() && response.body() != null
                        ? response.body() : reporte);
            }
            @Override public void onFailure(Call<Reporte> call, Throwable error) {
                mostrarDetalleLocal(reporte);
            }
        });
    }

    private TextView valor(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(16);
        view.setTextColor(tinta);
        view.setPadding(dp(2), dp(2), dp(2), dp(8));
        return view;
    }

    private void mostrarDetalleLocal(Reporte r) {
        base("Detalle del reporte");
        LinearLayout details = panel();
        details.addView(label("Código  #" + r.id));
        details.addView(label("Tipo de residuo"));
        details.addView(valor(r.tipo));
        details.addView(label("Dirección"));
        details.addView(valor(r.direccion));
        details.addView(label("Referencia"));
        details.addView(valor(r.referencia == null ? "Sin referencia" : r.referencia));
        details.addView(label("DNI del ciudadano"));
        details.addView(valor(r.dni));
        details.addView(label("Fecha"));
        details.addView(valor(r.fecha));
        TextView estado = valor(r.estado != null && r.estado == 1 ? "●  Pendiente" : "✓  Atendido");
        estado.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        estado.setTextColor(r.estado != null && r.estado == 1 ? Color.rgb(163, 101, 15) : verde);
        details.addView(estado);

        if (r.estado != null && r.estado == 1) {
            boton("Marcar como atendido", true, () -> ApiClient.service().atender(r.id)
                    .enqueue(new Callback<Reporte>() {
                        @Override public void onResponse(Call<Reporte> call, Response<Reporte> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                mensaje("Reporte atendido");
                                mostrarDetalleLocal(response.body());
                            } else mensaje("No se pudo atender (" + response.code() + ")");
                        }
                        @Override public void onFailure(Call<Reporte> call, Throwable error) {
                            mensaje("Error: " + error.getMessage());
                        }
                    }));
        }
        boton("Volver a los reportes", false, this::mostrarInicio);
        mostrarEnScroll();
    }
}
