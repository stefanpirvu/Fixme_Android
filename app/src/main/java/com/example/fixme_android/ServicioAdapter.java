package com.example.fixme_android;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServicioAdapter extends RecyclerView.Adapter<ServicioAdapter.ServicioViewHolder> {
    private List<Servicio> servicios;
    private final MainActivity_PedirServicio activity;

    public ServicioAdapter(List<Servicio> servicios, MainActivity_PedirServicio activity) {
        this.servicios = servicios;
        this.activity = activity;
    }

    @Override
    public ServicioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ServicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ServicioViewHolder holder, int position) {
        Servicio servicio = servicios.get(position);
        holder.text1.setText(servicio.getTitulo());
        holder.text2.setText(servicio.getCategoria() + " - €" + servicio.getPrecio());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, MainActivity_MostrarServicioSeleccionado.class);
            intent.putExtra("idServicio", servicio.getIdServicio());
            intent.putExtra("titulo", servicio.getTitulo());
            intent.putExtra("categoria", servicio.getCategoria());
            intent.putExtra("precio", servicio.getPrecio());
            intent.putExtra("descripcion", servicio.getDescripcion());
            intent.putExtra("idUsuario", servicio.getIdUsuario());
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return servicios.size();
    }

    public void updateList(List<Servicio> newServicios) {
        this.servicios = newServicios;
        notifyDataSetChanged();
    }

    static class ServicioViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;

        public ServicioViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}