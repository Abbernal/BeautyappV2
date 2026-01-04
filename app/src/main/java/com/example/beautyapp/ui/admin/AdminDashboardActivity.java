package com.example.beautyapp.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.cardview.widget.CardView;

import com.example.beautyapp.R;
import com.example.beautyapp.ui.base.BaseActivity;
import com.example.beautyapp.ui.appointments.ListaCitasActivity;
import com.example.beautyapp.ui.services.ListaServiciosActivity;
import com.example.beautyapp.ui.users.ListaUsuariosActivity;

/**
 * Activity principal del módulo de Administrador.
 * 
 * Es la pantalla de inicio para los usuarios con rol de Administrador.
 * Muestra tres CardViews que permiten acceder a las diferentes secciones
 * de gestión: Servicios, Citas y Usuarios.
 * 
 * Contexto de uso: Se muestra automáticamente después del login cuando
 * el usuario tiene rol de Administrador. Es una pantalla raíz, por lo que
 * el botón de retroceso está oculto.
 * 
 * Funcionalidades:
 * - Muestra el nombre del administrador en el toolbar
 * - Tres CardViews navegables:
 *   - Servicios: Gestionar servicios del centro (crear, editar, eliminar)
 *   - Citas: Ver y gestionar todas las citas del sistema
 *   - Usuarios: Gestionar usuarios (crear, editar, eliminar, buscar)
 * 
 * Flujo: AdminDashboardActivity → (según CardView seleccionado)
 *   - ListaServiciosActivity
 *   - ListaCitasActivity
 *   - ListaUsuariosActivity
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class AdminDashboardActivity extends BaseActivity {
    private CardView cardServices, cardAppointments, cardUsers;
    
    /**
     * Método llamado cuando la Activity se crea.
     * 
     * Configura el AppBar con el nombre del administrador y el subtítulo,
     * inicializa los CardViews y establece los listeners para navegar
     * a las diferentes secciones de gestión.
     * 
     * Contexto: Se ejecuta cuando el administrador inicia sesión o accede
     * a esta pantalla desde otra parte de la aplicación.
     * 
     * @param savedInstanceState Estado previo de la Activity (si existe)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        
        View appBar = findViewById(R.id.appBar);
        if (appBar != null) {
            setupAppBar(getString(R.string.admin_dashboard), true); // true = es pantalla raíz
            String nombreAdmin = sharedPreferences.getString("userName", "");
            if (nombreAdmin == null || nombreAdmin.isEmpty()) {
                nombreAdmin = getString(R.string.admin_dashboard);
            }
            if (tvTitle != null) {
                tvTitle.setText(nombreAdmin);
            }
            setToolbarSubtitle(getString(R.string.admin_toolbar_subtitle));
        }
        
        cardServices = findViewById(R.id.cardServices);
        cardAppointments = findViewById(R.id.cardAppointments);
        cardUsers = findViewById(R.id.cardUsers);
        
        cardServices.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListaServiciosActivity.class);
            startActivity(intent);
        });
        
        cardAppointments.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListaCitasActivity.class);
            startActivity(intent);
        });
        
        cardUsers.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListaUsuariosActivity.class);
            startActivity(intent);
        });
    }
}

