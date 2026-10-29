package com.monja.reportesapp;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
public class ReporteAdapter extends RecyclerView.Adapter<ReporteAdapter.Holder> {
    public interface OnClick { void seleccionar(Reporte reporte); }
    private final List<Reporte> items = new ArrayList<>();
    private final OnClick onClick;
    public ReporteAdapter(OnClick onClick) { this.onClick = onClick; }
    public void agregar(List<Reporte> nuevos) { int start = items.size(); items.addAll(nuevos); notifyItemRangeInserted(start, nuevos.size()); }
    public void reemplazar(List<Reporte> nuevos) { items.clear(); items.addAll(nuevos); notifyDataSetChanged(); }
    public void limpiar() { items.clear(); notifyDataSetChanged(); }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        TextView view = new TextView(parent.getContext());
        float density = parent.getResources().getDisplayMetrics().density;
        int pad = (int) (16 * density + .5f);
        view.setTextSize(15);
        view.setTextColor(Color.rgb(36, 53, 45));
        view.setLineSpacing(3 * density, 1f);
        view.setPadding(pad, pad, pad, pad);
        view.setBackgroundResource(R.drawable.bg_report_card);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = (int) (9 * density + .5f);
        view.setLayoutParams(params);
        return new Holder(view);
    }
    @Override public void onBindViewHolder(@NonNull Holder holder, int position) {
        Reporte r = items.get(position);
        String estado = r.estado != null && r.estado == 1 ? "●  Pendiente" : "✓  Atendido";
        holder.text.setText("#" + r.id + "   " + r.tipo + "\n" + r.direccion
                + "\nDNI " + r.dni + "     " + estado);
        holder.text.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        holder.text.setElevation(2);
        holder.text.setOnClickListener(v -> onClick.seleccionar(r));
    }
    @Override public int getItemCount() { return items.size(); }
    static class Holder extends RecyclerView.ViewHolder {
        final TextView text;
        Holder(TextView view) { super(view); text = view; }
    }
}
