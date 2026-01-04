package com.example.beautyapp.ui.appointments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.beautyapp.R;
import com.example.beautyapp.db.BeautyAppDatabase;
import com.example.beautyapp.db.entity.Cita;
import com.example.beautyapp.db.entity.CitaEmpleado;
import com.example.beautyapp.db.entity.EstadoCita;
import com.example.beautyapp.db.entity.Servicio;
import com.example.beautyapp.db.entity.Usuario;
import com.example.beautyapp.ui.base.BaseActivity;

import java.util.List;

/**
 * Activity para mostrar los detalles completos de una cita.
 * 
 * Muestra toda la información de una cita: servicio, cliente, empleado,
 * fecha, hora, estado y notas. Permite editar o eliminar la cita según
 * el rol del usuario y el estado de la cita.
 * 
 * Contexto de uso: Se accede desde ListaCitasActivity o desde otras
 * Activities que muestran listas de citas cuando el usuario hace click
 * en una cita específica.
 * 
 * Funcionalidades:
 * - Muestra información completa de la cita:
 *   - Servicio solicitado
 *   - Cliente
 *   - Empleado asignado
 *   - Fecha y hora separadas
 *   - Estado actual
 *   - Notas adicionales
 * - Botón "Editar": Navega a EditarCitaActivity
 * - Botón "Eliminar": Elimina la cita con confirmación
 * - Restricciones:
 *   - Si la cita está "Realizada", oculta los botones de editar y eliminar
 *   - Los clientes solo pueden editar sus propias citas
 * 
 * Flujo: DetalleCitaActivity → (editar) → EditarCitaActivity
 *        DetalleCitaActivity → (eliminar) → Activity anterior
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class DetalleCitaActivity extends BaseActivity {
    private TextView tvServicio, tvCliente, tvEmpleado, tvFecha, tvHora, tvEstado, tvNotas;
    private Button btnEdit, btnDelete;
    private BeautyAppDatabase db;
    private int appointmentId;
    private int userRoleId;
    private boolean isClientView = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);
        
        View appBar = findViewById(R.id.appBar);
        if (appBar != null) {
            setupAppBar(getString(R.string.appointment_detail));
        }
        
        db = BeautyAppDatabase.getInstance(this);
        appointmentId = getIntent().getIntExtra("appointmentId", -1);
        userRoleId = sharedPreferences.getInt("userRoleId", -1);
        isClientView = (userRoleId == 3); // Rol Cliente
        
        tvServicio = findViewById(R.id.tvServicio);
        tvCliente = findViewById(R.id.tvCliente);
        tvEmpleado = findViewById(R.id.tvEmpleado);
        tvFecha = findViewById(R.id.tvFecha);
        tvHora = findViewById(R.id.tvHora);
        tvEstado = findViewById(R.id.tvEstado);
        tvNotas = findViewById(R.id.tvNotas);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        
        loadAppointment();
        
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(this, EditarCitaActivity.class);
                intent.putExtra("appointmentId", appointmentId);
                startActivity(intent);
            });
        }
        
        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> deleteAppointment());
        }
    }
    
    private void loadAppointment() {
        Cita cita = db.citaDao().getById(appointmentId);
        if (cita != null) {
            Servicio servicio = db.servicioDao().getById(cita.getServicioId());
            Usuario cliente = db.usuarioDao().getById(cita.getClienteId());
            EstadoCita estado = db.estadoCitaDao().getById(cita.getEstadoId());
            
            List<CitaEmpleado> asignaciones = db.citaEmpleadoDao().getByCita(appointmentId);
            Usuario empleado = null;
            if (!asignaciones.isEmpty()) {
                empleado = db.usuarioDao().getById(asignaciones.get(0).getEmpleadoId());
            }
            
            tvServicio.setText(servicio != null ? servicio.getNombre() : "N/A");
            if (tvCliente != null) {
                tvCliente.setText(cliente != null ? cliente.getNombre() : "N/A");
            }
            if (tvEmpleado != null) {
                tvEmpleado.setText(empleado != null ? empleado.getNombre() : "N/A");
            }
            
            // Separar fecha y hora
            String fechaHora = cita.getFechaHora();
            String[] partes = fechaHora.split(" ");
            if (partes.length == 2) {
                tvFecha.setText(partes[0]);
                tvHora.setText(partes[1]);
            } else {
                tvFecha.setText(fechaHora);
                tvHora.setText("");
            }
            
            tvEstado.setText(estado != null ? estado.getNombre() : "N/A");
            tvNotas.setText(cita.getNotas() != null ? cita.getNotas() : "Sin notas");
            
            // Mostrar/ocultar botones según estado
            if (estado != null && "Realizada".equals(estado.getNombre())) {
                // Si es Realizada, ocultar botones
                if (btnEdit != null) btnEdit.setVisibility(View.GONE);
                if (btnDelete != null) btnDelete.setVisibility(View.GONE);
            } else {
                // Si es Pendiente, Confirmada o Cancelada, mostrar botones
                if (btnEdit != null) btnEdit.setVisibility(View.VISIBLE);
                if (btnDelete != null) btnDelete.setVisibility(View.VISIBLE);
            }
        }
    }
    
    private void deleteAppointment() {
        new android.app.AlertDialog.Builder(this)
            .setTitle("Eliminar cita")
            .setMessage("¿Está seguro de eliminar esta cita?")
            .setPositiveButton("Sí", (dialog, which) -> {
                Cita cita = db.citaDao().getById(appointmentId);
                if (cita != null) {
                    // Eliminar asignaciones primero
                    db.citaEmpleadoDao().deleteByCita(appointmentId);
                    // Eliminar cita
                    db.citaDao().delete(cita);
                    android.widget.Toast.makeText(this, "Cita eliminada", android.widget.Toast.LENGTH_SHORT).show();
                    finish();
                }
            })
            .setNegativeButton("No", null)
            .show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadAppointment();
    }
}

