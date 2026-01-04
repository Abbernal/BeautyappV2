package com.example.beautyapp.ui.employee;

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
import com.example.beautyapp.ui.appointments.AppointmentStatusEditActivity;

import java.util.ArrayList;
import java.util.List;

public class EmployeeAppointmentsAdapter extends RecyclerView.Adapter<EmployeeAppointmentsAdapter.ViewHolder> {
    private List<Cita> citas;
    private android.app.Activity activity;
    private BeautyAppDatabase db;
    private String filterType; // "pending", "confirmed", "all"
    
    public EmployeeAppointmentsAdapter(List<Cita> citas, android.app.Activity activity, BeautyAppDatabase db, String filterType) {
        this.citas = citas != null ? citas : new ArrayList<>();
        this.activity = activity;
        this.db = db;
        this.filterType = filterType;
    }
    
    public void updateList(List<Cita> newCitas) {
        this.citas = newCitas != null ? newCitas : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public List<Cita> getCurrentList() {
        return citas;
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
            String estadoNombre = estado.getNombre();
            if ("Confirmada".equals(estadoNombre)) {
                holder.tvEstado.setTextColor(activity.getResources().getColor(R.color.success));
            } else if ("Cancelada".equals(estadoNombre)) {
                holder.tvEstado.setTextColor(activity.getResources().getColor(R.color.error));
            } else if ("Realizada".equals(estadoNombre)) {
                holder.tvEstado.setTextColor(activity.getResources().getColor(android.R.color.darker_gray));
            } else {
                holder.tvEstado.setTextColor(activity.getResources().getColor(R.color.warning));
            }
        }
        
        holder.itemView.setOnClickListener(v -> {
            // Verificar que la cita no esté en estado "Realizada"
            if (estado != null && activity.getString(R.string.status_completed).equals(estado.getNombre())) {
                android.widget.Toast.makeText(activity, R.string.cannot_edit_completed_appointment, android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            
            Intent intent = new Intent(activity, AppointmentStatusEditActivity.class);
            intent.putExtra("appointmentId", cita.getId());
            intent.putExtra("filterType", filterType);
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

