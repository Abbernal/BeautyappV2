package com.example.beautyapp.ui.services;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautyapp.R;
import com.example.beautyapp.db.BeautyAppDatabase;
import com.example.beautyapp.db.entity.Servicio;
import com.example.beautyapp.ui.base.BaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Activity para listar servicios del centro.
 * 
 * Muestra una lista de todos los servicios disponibles en el centro de belleza.
 * Puede funcionar en dos modos: vista de administrador (editable) o vista de cliente
 * (solo lectura).
 * 
 * Contexto de uso: Se accede desde diferentes lugares según el rol:
 * - Administrador: Desde AdminDashboardActivity (modo editable)
 * - Cliente: Desde ClienteHomeActivity (modo solo lectura)
 * 
 * Funcionalidades:
 * - Lista de servicios en RecyclerView con ServiciosAdapter
 * - Modo administrador:
 *   - FAB visible para crear nuevos servicios
 *   - Click en un servicio: Editar el servicio
 * - Modo cliente:
 *   - FAB oculto
 *   - Items no clickeables (solo visualización)
 *   - Muestra catálogo completo de servicios disponibles
 * - Refresco automático: Al volver a la Activity, se actualiza la lista
 * 
 * Flujo: ListaServiciosActivity → (FAB o click en servicio) → CrearEditarServicioActivity
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class ListaServiciosActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private ServiciosAdapter adapter;
    private FloatingActionButton fabAdd;
    private BeautyAppDatabase db;
    
    /**
     * Método llamado cuando la Activity se crea.
     * 
     * Configura el AppBar, inicializa el RecyclerView con la lista de servicios
     * y configura el FAB según el modo (administrador o cliente).
     * 
     * Contexto: Se ejecuta cuando se accede a la lista de servicios desde
     * cualquier parte de la aplicación.
     * 
     * @param savedInstanceState Estado previo de la Activity (si existe)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_list);
        
        View appBar = findViewById(R.id.appBar);
        if (appBar != null) {
            setupAppBar(getString(R.string.services_title));
        }
        
        db = BeautyAppDatabase.getInstance(this);
        
        boolean clientView = getIntent().getBooleanExtra("clientView", false);
        
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        List<Servicio> servicios = db.servicioDao().getAll();
        adapter = new ServiciosAdapter(servicios, this, clientView);
        recyclerView.setAdapter(adapter);
        
        // Configurar FAB: visible por defecto, solo ocultar si es vista de cliente
        if (clientView) {
            fabAdd.setVisibility(View.GONE);
        } else {
            fabAdd.setVisibility(View.VISIBLE);
            fabAdd.setOnClickListener(v -> {
                Intent intent = new Intent(this, CrearEditarServicioActivity.class);
                startActivity(intent);
            });
        }
    }
    
    /**
     * Método llamado cuando la Activity vuelve al primer plano.
     * 
     * Refresca la lista de servicios para mostrar cualquier cambio que haya
     * ocurrido mientras la Activity estaba en segundo plano (por ejemplo,
     * si se creó o editó un servicio).
     * 
     * Contexto: Se ejecuta automáticamente cuando el usuario vuelve a esta
     * Activity desde otra pantalla.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Refrescar lista
        List<Servicio> servicios = db.servicioDao().getAll();
        adapter.updateList(servicios);
    }
}

