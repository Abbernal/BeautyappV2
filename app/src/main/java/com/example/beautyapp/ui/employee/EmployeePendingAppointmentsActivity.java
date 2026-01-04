package com.example.beautyapp.ui.employee;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautyapp.R;
import com.example.beautyapp.db.BeautyAppDatabase;
import com.example.beautyapp.db.entity.Cita;
import com.example.beautyapp.ui.appointments.AppointmentStatusEditActivity;
import com.example.beautyapp.ui.base.BaseActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class EmployeePendingAppointmentsActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private EmployeeAppointmentsAdapter adapter;
    private BeautyAppDatabase db;
    private int empleadoId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_appointments_list);
        
        View appBar = findViewById(R.id.appBar);
        if (appBar != null) {
            setupAppBar(getString(R.string.pending_appointments));
        }
        
        db = BeautyAppDatabase.getInstance(this);
        empleadoId = sharedPreferences.getInt("userId", -1);
        
        recyclerView = findViewById(R.id.recyclerView);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        loadAppointments();
    }
    
    private void loadAppointments() {
        List<Cita> citas = db.citaDao().getByEmpleadoAndEstado(
            empleadoId,
            getString(R.string.status_pending)
        );
        
        // Ordenar por fecha y hora ascendente
        if (citas != null && !citas.isEmpty()) {
            Collections.sort(citas, new Comparator<Cita>() {
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
        
        adapter = new EmployeeAppointmentsAdapter(citas, this, db, "pending");
        recyclerView.setAdapter(adapter);
        
        if (tvEmptyState != null) {
            if (citas == null || citas.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                tvEmptyState.setText(getString(R.string.no_pending_appointments));
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


