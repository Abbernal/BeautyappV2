package com.example.beautyapp.ui.services;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.beautyapp.R;
import com.example.beautyapp.db.BeautyAppDatabase;
import com.example.beautyapp.db.entity.Servicio;
import com.example.beautyapp.ui.base.BaseActivity;

/**
 * Activity para crear y editar servicios del centro.
 * 
 * Permite a los administradores crear nuevos servicios o editar servicios
 * existentes. Un servicio incluye nombre, descripción, precio y duración.
 * 
 * Contexto de uso: Se accede desde ListaServiciosActivity cuando el
 * administrador hace click en el FAB para crear un nuevo servicio, o
 * cuando hace click en un servicio existente para editarlo.
 * 
 * Funcionalidades:
 * - Crear nuevo servicio: Todos los campos editables
 * - Editar servicio existente: Carga los datos actuales y permite modificarlos
 * - Campos del formulario:
 *   - Nombre del servicio
 *   - Descripción
 *   - Precio (en euros)
 *   - Duración en minutos
 * - Validaciones:
 *   - Todos los campos son obligatorios
 *   - Precio debe ser un número válido
 *   - Duración debe ser un número entero positivo
 * - Botón eliminar: Solo visible en modo edición
 * 
 * Flujo: CrearEditarServicioActivity → (guardar) → ListaServiciosActivity
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class CrearEditarServicioActivity extends BaseActivity {
    private EditText etNombre, etDescripcion, etPrecio, etDuracion;
    private Button btnSave, btnDelete;
    private BeautyAppDatabase db;
    private int serviceId = -1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_form);
        
        View appBar = findViewById(R.id.appBar);
        if (appBar != null) {
            setupAppBar(getString(R.string.add_service));
        }
        
        db = BeautyAppDatabase.getInstance(this);
        
        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPrecio = findViewById(R.id.etPrecio);
        etDuracion = findViewById(R.id.etDuracion);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        
        serviceId = getIntent().getIntExtra("serviceId", -1);
        
        if (serviceId != -1) {
            // Modo edición
            if (appBar != null) {
                setupAppBar(getString(R.string.edit_service));
            }
            loadService();
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.GONE);
        }
        
        btnSave.setOnClickListener(v -> saveService());
        btnDelete.setOnClickListener(v -> deleteService());
    }
    
    private void loadService() {
        Servicio servicio = db.servicioDao().getById(serviceId);
        if (servicio != null) {
            etNombre.setText(servicio.getNombre());
            etDescripcion.setText(servicio.getDescripcion());
            etPrecio.setText(String.valueOf(servicio.getPrecio()));
            etDuracion.setText(String.valueOf(servicio.getDuracionMinutos()));
        }
    }
    
    private void saveService() {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String duracionStr = etDuracion.getText().toString().trim();
        
        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError(getString(R.string.required_field));
            return;
        }
        
        if (TextUtils.isEmpty(precioStr)) {
            etPrecio.setError(getString(R.string.required_field));
            return;
        }
        
        if (TextUtils.isEmpty(duracionStr)) {
            etDuracion.setError(getString(R.string.required_field));
            return;
        }
        
        try {
            double precio = Double.parseDouble(precioStr);
            int duracion = Integer.parseInt(duracionStr);
            
            Servicio servicio;
            if (serviceId != -1) {
                servicio = db.servicioDao().getById(serviceId);
                servicio.setNombre(nombre);
                servicio.setDescripcion(descripcion);
                servicio.setPrecio(precio);
                servicio.setDuracionMinutos(duracion);
                db.servicioDao().update(servicio);
            } else {
                servicio = new Servicio(nombre, descripcion, precio, duracion);
                db.servicioDao().insert(servicio);
            }
            
            Toast.makeText(this, R.string.service_saved, Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valores numéricos inválidos", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void deleteService() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.confirm_delete)
            .setMessage("¿Está seguro de eliminar este servicio?")
            .setPositiveButton(R.string.yes, (dialog, which) -> {
                Servicio servicio = db.servicioDao().getById(serviceId);
                if (servicio != null) {
                    db.servicioDao().delete(servicio);
                    Toast.makeText(this, R.string.service_deleted, Toast.LENGTH_SHORT).show();
                    finish();
                }
            })
            .setNegativeButton(R.string.no, null)
            .show();
    }
}

