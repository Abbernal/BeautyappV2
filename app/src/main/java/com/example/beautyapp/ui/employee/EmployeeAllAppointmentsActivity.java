package com.example.beautyapp.ui.employee;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautyapp.R;
import com.example.beautyapp.db.BeautyAppDatabase;
import com.example.beautyapp.db.entity.Cita;
import com.example.beautyapp.db.entity.EstadoCita;
import com.example.beautyapp.db.entity.Servicio;
import com.example.beautyapp.db.entity.Usuario;
import com.example.beautyapp.ui.base.BaseActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class EmployeeAllAppointmentsActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private EmployeeAppointmentsAdapter adapter;
    private BeautyAppDatabase db;
    private int empleadoId;
    private List<Cita> allAppointments;
    private TextInputEditText etSearch;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_all_appointments);
        
        View appBar = findViewById(R.id.appBar);
        if (appBar != null) {
            setupAppBar(getString(R.string.all_my_appointments));
        }
        
        db = BeautyAppDatabase.getInstance(this);
        empleadoId = sharedPreferences.getInt("userId", -1);
        
        recyclerView = findViewById(R.id.recyclerView);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        TextInputLayout tilSearch = findViewById(R.id.tilSearch);
        etSearch = findViewById(R.id.etSearch);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        loadAllAppointments();
        
        // Configurar buscador
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterAppointments(s.toString());
                }
                
                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }
    
    private void loadAllAppointments() {
        allAppointments = db.citaDao().getByEmpleado(empleadoId);
        
        // Ordenar por fecha y hora ascendente
        if (allAppointments != null && !allAppointments.isEmpty()) {
            Collections.sort(allAppointments, new Comparator<Cita>() {
                @Override
                public int compare(Cita c1, Cita c2) {
                    try {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                        java.util.Date d1 = sdf.parse(c1.getFechaHora());
                        java.util.Date d2 = sdf.parse(c2.getFechaHora());
                        if (d1 != null && d2 != null) {
                            return d1.compareTo(d2);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
        }
        
        adapter = new EmployeeAppointmentsAdapter(allAppointments, this, db, "all");
        recyclerView.setAdapter(adapter);
        
        updateEmptyState();
    }
    
    private void filterAppointments(String query) {
        if (allAppointments == null) {
            return;
        }
        
        if (query == null || query.trim().isEmpty()) {
            adapter.updateList(allAppointments);
            updateEmptyState();
            return;
        }
        
        query = query.toLowerCase().trim();
        List<Cita> filtered = new ArrayList<>();
        
        for (Cita cita : allAppointments) {
            Usuario cliente = db.usuarioDao().getById(cita.getClienteId());
            Servicio servicio = db.servicioDao().getById(cita.getServicioId());
            
            String nombreCliente = cliente != null ? cliente.getNombre().toLowerCase() : "";
            String nombreServicio = servicio != null ? servicio.getNombre().toLowerCase() : "";
            String fechaHora = cita.getFechaHora().toLowerCase();
            
            if (nombreCliente.contains(query) || 
                nombreServicio.contains(query) || 
                fechaHora.contains(query)) {
                filtered.add(cita);
            }
        }
        
        adapter.updateList(filtered);
        updateEmptyState();
    }
    
    private void updateEmptyState() {
        if (tvEmptyState != null) {
            List<Cita> currentList = adapter.getCurrentList();
            if (currentList == null || currentList.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                String searchText = etSearch != null && etSearch.getText() != null ? 
                    etSearch.getText().toString().trim() : "";
                if (searchText.isEmpty()) {
                    tvEmptyState.setText(getString(R.string.no_appointments_found));
                } else {
                    tvEmptyState.setText(getString(R.string.no_appointments_match_search));
                }
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
        loadAllAppointments();
        // Restaurar filtro si existe
        if (etSearch != null && etSearch.getText() != null) {
            String searchText = etSearch.getText().toString();
            if (!searchText.isEmpty()) {
                filterAppointments(searchText);
            }
        }
    }
}


