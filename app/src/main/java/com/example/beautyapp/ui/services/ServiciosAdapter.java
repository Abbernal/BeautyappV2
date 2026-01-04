package com.example.beautyapp.ui.services;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautyapp.R;
import com.example.beautyapp.db.entity.Servicio;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter para mostrar servicios en un RecyclerView.
 * 
 * Proporciona la funcionalidad para mostrar una lista de servicios en un RecyclerView,
 * incluyendo nombre, descripción, precio y duración. El comportamiento varía según
 * si es vista de cliente (solo lectura) o de administrador (editable).
 * 
 * Contexto de uso: Se utiliza en ListaServiciosActivity para mostrar el catálogo
 * de servicios. Puede funcionar en modo solo lectura (clientes) o editable (administradores).
 * 
 * Funcionalidades:
 * - Muestra información de cada servicio:
 *   - Nombre del servicio
 *   - Descripción
 *   - Precio formateado en euros (€)
 *   - Duración en minutos
 * - Modo cliente (clientView = true):
 *   - Items no clickeables (solo visualización)
 * - Modo administrador (clientView = false):
 *   - Click en un item: Navega a CrearEditarServicioActivity para editar el servicio
 * - Método updateList: Permite actualizar la lista sin recrear el adapter
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class ServiciosAdapter extends RecyclerView.Adapter<ServiciosAdapter.ViewHolder> {
    private List<Servicio> servicios;
    private ListaServiciosActivity activity;
    private boolean clientView;
    
    public ServiciosAdapter(List<Servicio> servicios, ListaServiciosActivity activity) {
        this(servicios, activity, false);
    }
    
    public ServiciosAdapter(List<Servicio> servicios, ListaServiciosActivity activity, boolean clientView) {
        this.servicios = servicios;
        this.activity = activity;
        this.clientView = clientView;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_service, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Servicio servicio = servicios.get(position);
        holder.tvNombre.setText(servicio.getNombre());
        holder.tvDescripcion.setText(servicio.getDescripcion());
        holder.tvPrecio.setText(NumberFormat.getCurrencyInstance(new Locale("es", "ES"))
            .format(servicio.getPrecio()));
        holder.tvDuracion.setText(servicio.getDuracionMinutos() + " min");
        
        // Solo permitir editar si no es vista de cliente
        if (!clientView) {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(activity, CrearEditarServicioActivity.class);
                intent.putExtra("serviceId", servicio.getId());
                activity.startActivity(intent);
            });
        }
    }
    
    @Override
    public int getItemCount() {
        return servicios.size();
    }
    
    public void updateList(List<Servicio> newList) {
        this.servicios = newList;
        notifyDataSetChanged();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvPrecio, tvDuracion;
        
        ViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvDuracion = itemView.findViewById(R.id.tvDuracion);
        }
    }
}

