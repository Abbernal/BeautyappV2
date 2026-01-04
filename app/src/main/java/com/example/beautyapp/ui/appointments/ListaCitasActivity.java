package com.example.beautyapp.ui.appointments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautyapp.R;
import com.example.beautyapp.db.BeautyAppDatabase;
import com.example.beautyapp.db.entity.Cita;
import com.example.beautyapp.ui.base.BaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Activity para listar citas del sistema.
 * 
 * Muestra una lista de todas las citas, con opciones de filtrado según
 * el contexto de uso. Permite ver detalles de cada cita y crear nuevas
 * citas (solo para administradores).
 * 
 * Contexto de uso: Se accede desde diferentes lugares según el rol:
 * - Administrador: Desde AdminDashboardActivity (todas las citas)
 * - Cliente: Desde ClienteHomeActivity (solo sus citas, filtradas)
 * - Empleado: Puede filtrarse por empleado
 * 
 * Funcionalidades:
 * - Lista de citas en RecyclerView con CitasAdapter
 * - Filtrado opcional:
 *   - Por cliente: Solo muestra citas del cliente logueado
 *   - Por empleado: Solo muestra citas asignadas a un empleado
 * - FAB (Floating Action Button) para crear nueva cita:
 *   - Visible solo para administradores
 *   - Oculto cuando se filtra por cliente
 * - Click en una cita: Navega a DetalleCitaActivity
 * - Mensaje de estado vacío si no hay citas
 * - Ordenamiento: Por fecha y hora (más próximas primero)
 * 
 * Flujo: ListaCitasActivity → (click en cita) → DetalleCitaActivity
 *        ListaCitasActivity → (FAB) → CrearCitaActivity
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class ListaCitasActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private CitasAdapter adapter;
    private FloatingActionButton fabAdd;
    private BeautyAppDatabase db;
    private boolean filterByClient = false;
    private boolean filterByEmployee = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments_list);
        
        View appBar = findViewById(R.id.appBar);
        if (appBar != null) {
            setupAppBar(getString(R.string.appointments_title));
        }
        
        db = BeautyAppDatabase.getInstance(this);
        
        filterByClient = getIntent().getBooleanExtra("filterByClient", false);
        filterByEmployee = getIntent().getBooleanExtra("filterByEmployee", false);
        boolean createNew = getIntent().getBooleanExtra("createNew", false);
        
        recyclerView = findViewById(R.id.recyclerView);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        fabAdd = findViewById(R.id.fabAdd);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Ocultar FAB si se filtra por cliente (los clientes reservan desde "Reservar Cita")
        if (filterByClient) {
            fabAdd.setVisibility(View.GONE);
        } else {
            if (createNew) {
                fabAdd.performClick();
            } else {
                fabAdd.setOnClickListener(v -> {
                    Intent intent = new Intent(this, CrearCitaActivity.class);
                    startActivity(intent);
                });
            }
        }
        
        loadAppointments();
    }
    
    private void loadAppointments() {
        List<Cita> citas;
        
        if (filterByClient) {
            int clienteId = sharedPreferences.getInt("userId", -1);
            citas = db.citaDao().getByCliente(clienteId);
            // Ordenar por fecha y hora (más próximas primero)
            citas.sort((c1, c2) -> {
                try {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
                    java.util.Date d1 = sdf.parse(c1.getFechaHora());
                    java.util.Date d2 = sdf.parse(c2.getFechaHora());
                    if (d1 != null && d2 != null) {
                        return d1.compareTo(d2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            });
        } else if (filterByEmployee) {
            int empleadoId = sharedPreferences.getInt("userId", -1);
            citas = db.citaDao().getByEmpleado(empleadoId);
        } else {
            citas = db.citaDao().getAll();
        }
        
        adapter = new CitasAdapter(citas, this, db, filterByClient);
        recyclerView.setAdapter(adapter);

        if (tvEmptyState != null) {
            if (filterByClient && (citas == null || citas.isEmpty())) {
                tvEmptyState.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                tvEmptyState.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadAppointments();
    }
}

