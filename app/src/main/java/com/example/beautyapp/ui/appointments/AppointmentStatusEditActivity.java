package com.example.beautyapp.ui.appointments;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.beautyapp.R;
import com.example.beautyapp.db.BeautyAppDatabase;
import com.example.beautyapp.db.entity.Cita;
import com.example.beautyapp.db.entity.EstadoCita;
import com.example.beautyapp.db.entity.Servicio;
import com.example.beautyapp.db.entity.Usuario;
import com.example.beautyapp.ui.base.BaseActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentStatusEditActivity extends BaseActivity {
    private TextInputEditText etCliente, etServicio, etFecha, etHora, etNotas;
    private TextInputLayout tilNotas;
    private Spinner spinnerEstado;
    private Button btnSave;
    private BeautyAppDatabase db;
    private int appointmentId;
    private List<EstadoCita> estadosPermitidos = new ArrayList<>();
    private Cita citaActual;
    private String filterType; // "pending", "confirmed", "all"
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_status_edit);
        
        View appBar = findViewById(R.id.appBar);
        if (appBar != null) {
            setupAppBar(getString(R.string.edit_appointment_status));
        }
        
        db = BeautyAppDatabase.getInstance(this);
        appointmentId = getIntent().getIntExtra("appointmentId", -1);
        filterType = getIntent().getStringExtra("filterType");
        
        etCliente = findViewById(R.id.etCliente);
        etServicio = findViewById(R.id.etServicio);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        etNotas = findViewById(R.id.etNotas);
        tilNotas = findViewById(R.id.tilNotas);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        btnSave = findViewById(R.id.btnSave);
        
        loadAppointment();
        loadEstadosPermitidos();
        
        btnSave.setOnClickListener(v -> saveStatus());
    }
    
    private void loadAppointment() {
        citaActual = db.citaDao().getById(appointmentId);
        if (citaActual == null) {
            Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Verificar que la cita no esté en estado "Realizada"
        EstadoCita estadoActual = db.estadoCitaDao().getById(citaActual.getEstadoId());
        if (estadoActual != null && getString(R.string.status_completed).equals(estadoActual.getNombre())) {
            Toast.makeText(this, R.string.cannot_edit_completed_appointment, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Cargar datos de la cita en modo solo lectura
        Usuario cliente = db.usuarioDao().getById(citaActual.getClienteId());
        Servicio servicio = db.servicioDao().getById(citaActual.getServicioId());
        
        String[] fechaHora = citaActual.getFechaHora().split(" ");
        String fecha = fechaHora.length > 0 ? fechaHora[0] : "";
        String hora = fechaHora.length > 1 ? fechaHora[1] : "";
        
        etCliente.setText(cliente != null ? cliente.getNombre() : "N/A");
        etServicio.setText(servicio != null ? servicio.getNombre() : "N/A");
        etFecha.setText(fecha);
        etHora.setText(hora);
        
        // Mostrar notas solo si existen
        String notas = citaActual.getNotas();
        if (notas != null && !notas.trim().isEmpty()) {
            etNotas.setText(notas);
            tilNotas.setVisibility(View.VISIBLE);
        } else {
            tilNotas.setVisibility(View.GONE);
        }
    }
    
    private void loadEstadosPermitidos() {
        List<EstadoCita> todosEstados = db.estadoCitaDao().getAll();
        estadosPermitidos.clear();
        
        String estadoPendiente = getString(R.string.status_pending);
        String estadoConfirmada = getString(R.string.status_confirmed);
        String estadoRealizada = getString(R.string.status_completed);
        String estadoCancelada = getString(R.string.status_cancelled);
        
        EstadoCita estadoActual = db.estadoCitaDao().getById(citaActual.getEstadoId());
        String nombreEstadoActual = estadoActual != null ? estadoActual.getNombre() : "";
        
        // Determinar estados permitidos según el estado actual y el contexto
        if (estadoCancelada.equals(nombreEstadoActual)) {
            // Desde cancelada: puede cambiar a Realizada o Confirmada
            for (EstadoCita estado : todosEstados) {
                String nombreEstado = estado.getNombre();
                if (estadoRealizada.equals(nombreEstado) || estadoConfirmada.equals(nombreEstado)) {
                    estadosPermitidos.add(estado);
                }
            }
        } else if ("pending".equals(filterType) && estadoPendiente.equals(nombreEstadoActual)) {
            // Desde pendiente: puede cambiar a cualquier otro estado excepto pendiente
            for (EstadoCita estado : todosEstados) {
                if (!estadoPendiente.equals(estado.getNombre())) {
                    estadosPermitidos.add(estado);
                }
            }
        } else if ("confirmed".equals(filterType) && estadoConfirmada.equals(nombreEstadoActual)) {
            // Desde confirmada: solo puede cambiar a Realizada (si es hoy) o Cancelada
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String fechaHoy = sdf.format(new Date());
            String fechaCita = citaActual.getFechaHora().split(" ")[0];
            boolean esHoy = fechaHoy.equals(fechaCita);
            
            for (EstadoCita estado : todosEstados) {
                String nombreEstado = estado.getNombre();
                if (estadoCancelada.equals(nombreEstado)) {
                    // Siempre puede cancelar
                    estadosPermitidos.add(estado);
                } else if (estadoRealizada.equals(nombreEstado) && esHoy) {
                    // Solo puede marcar como realizada si es hoy
                    estadosPermitidos.add(estado);
                }
            }
        } else {
            // Para "all" o casos generales: permitir Realizada y Cancelada
            for (EstadoCita estado : todosEstados) {
                String nombreEstado = estado.getNombre();
                if (estadoRealizada.equals(nombreEstado) || estadoCancelada.equals(nombreEstado)) {
                    estadosPermitidos.add(estado);
                }
            }
        }
        
        List<String> nombresEstados = new ArrayList<>();
        for (EstadoCita estado : estadosPermitidos) {
            nombresEstados.add(estado.getNombre());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            nombresEstados
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);
        
        // Preseleccionar el estado actual si está en la lista permitida
        if (estadoActual != null) {
            for (int i = 0; i < estadosPermitidos.size(); i++) {
                if (estadosPermitidos.get(i).getId() == estadoActual.getId()) {
                    spinnerEstado.setSelection(i);
                    break;
                }
            }
        }
    }
    
    private void saveStatus() {
        if (citaActual == null) {
            return;
        }
        
        int selectedIndex = spinnerEstado.getSelectedItemPosition();
        if (selectedIndex < 0 || selectedIndex >= estadosPermitidos.size()) {
            Toast.makeText(this, R.string.required_field, Toast.LENGTH_SHORT).show();
            return;
        }
        
        EstadoCita nuevoEstado = estadosPermitidos.get(selectedIndex);
        EstadoCita estadoActual = db.estadoCitaDao().getById(citaActual.getEstadoId());
        
        // Validación adicional para confirmadas: solo puede cambiar a Realizada si es hoy
        if ("confirmed".equals(filterType) && estadoActual != null && 
            getString(R.string.status_confirmed).equals(estadoActual.getNombre()) &&
            getString(R.string.status_completed).equals(nuevoEstado.getNombre())) {
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String fechaHoy = sdf.format(new Date());
            String fechaCita = citaActual.getFechaHora().split(" ")[0];
            
            if (!fechaHoy.equals(fechaCita)) {
                Toast.makeText(this, R.string.cannot_complete_future_appointment, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        citaActual.setEstadoId(nuevoEstado.getId());
        db.citaDao().update(citaActual);
        
        Toast.makeText(this, R.string.appointment_status_updated, Toast.LENGTH_SHORT).show();
        finish();
    }
}

