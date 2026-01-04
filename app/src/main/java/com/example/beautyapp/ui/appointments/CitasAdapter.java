package com.example.beautyapp.ui.appointments;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautyapp.R;
import com.example.beautyapp.db.BeautyAppDatabase;
import com.example.beautyapp.db.entity.Cita;
import com.example.beautyapp.db.entity.EstadoCita;
import com.example.beautyapp.db.entity.Servicio;
import com.example.beautyapp.db.entity.Usuario;

import java.util.List;

/**
 * Adapter para mostrar citas en un RecyclerView.
 * 
 * Proporciona la funcionalidad para mostrar una lista de citas en un RecyclerView,
 * incluyendo información del servicio, cliente, fecha/hora y estado. Cada item
 * es clickeable para ver los detalles de la cita.
 * 
 * Contexto de uso: Se utiliza en ListaCitasActivity para mostrar la lista
 * de citas. Puede mostrar todas las citas o filtrar por cliente según el contexto.
 * 
 * Funcionalidades:
 * - Muestra información de cada cita:
 *   - Nombre del servicio
 *   - Nombre del cliente
 *   - Fecha y hora
 *   - Estado de la cita (con colores según el estado)
 * - Click en un item: Navega a DetalleCitaActivity
 * - Soporte para vista de cliente: Puede ocultar cierta información si es necesario
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class CitasAdapter extends RecyclerView.Adapter<CitasAdapter.ViewHolder> {
    private List<Cita> citas;
    private ListaCitasActivity activity;
    private BeautyAppDatabase db;
    private boolean isClientView;
    
    public CitasAdapter(List<Cita> citas, ListaCitasActivity activity, BeautyAppDatabase db) {
        this(citas, activity, db, false);
    }
    
    public CitasAdapter(List<Cita> citas, ListaCitasActivity activity, BeautyAppDatabase db, boolean isClientView) {
        this.citas = citas;
        this.activity = activity;
        this.db = db;
        this.isClientView = isClientView;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cita cita = citas.get(position);
        
        Servicio servicio = db.servicioDao().getById(cita.getServicioId());
        Usuario cliente = db.usuarioDao().getById(cita.getClienteId());
        EstadoCita estado = db.estadoCitaDao().getById(cita.getEstadoId());
        
        holder.tvServicio.setText(servicio != null ? servicio.getNombre() : "N/A");
        holder.tvCliente.setText(cliente != null ? cliente.getNombre() : "N/A");
        holder.tvFechaHora.setText(cita.getFechaHora());
        holder.tvEstado.setText(estado != null ? estado.getNombre() : "N/A");
        
        // Color según estado
        if (estado != null) {
            switch (estado.getNombre()) {
                case "Confirmada":
                    holder.tvEstado.setTextColor(activity.getResources().getColor(R.color.success));
                    break;
                case "Cancelada":
                    holder.tvEstado.setTextColor(activity.getResources().getColor(R.color.error));
                    break;
                case "Realizada":
                    holder.tvEstado.setTextColor(activity.getResources().getColor(android.R.color.darker_gray));
                    break;
                default:
                    holder.tvEstado.setTextColor(activity.getResources().getColor(R.color.warning));
            }
        }
        
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, DetalleCitaActivity.class);
            intent.putExtra("appointmentId", cita.getId());
            activity.startActivity(intent);
        });
    }
    
    @Override
    public int getItemCount() {
        return citas.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvServicio, tvCliente, tvFechaHora, tvEstado;
        
        ViewHolder(View itemView) {
            super(itemView);
            tvServicio = itemView.findViewById(R.id.tvServicio);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
            tvEstado = itemView.findViewById(R.id.tvEstado);
        }
    }
}

