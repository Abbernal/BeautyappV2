package com.example.beautyapp.ui.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beautyapp.R;
import com.example.beautyapp.ui.login.LoginActivity;

/**
 * Clase base abstracta para todas las Activities de la aplicación.
 * 
 * Proporciona funcionalidad común a todas las pantallas, incluyendo:
 * - Gestión de SharedPreferences para almacenar datos de sesión
 * - Configuración del AppBar (toolbar) con título, subtítulo y botones
 * - Funcionalidad de logout con confirmación
 * - Control de navegación (botón de retroceso)
 * - Botón de perfil opcional
 * 
 * Contexto de uso: Todas las Activities de la aplicación (excepto LoginActivity
 * y RegisterActivity) extienden esta clase para heredar la funcionalidad común.
 * 
 * Características principales:
 * - Maneja la sesión del usuario mediante SharedPreferences
 * - Previene que el usuario vuelva al login usando el botón atrás desde pantallas raíz
 * - Proporciona una interfaz consistente en todas las pantallas
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public abstract class BaseActivity extends AppCompatActivity {
    /** Preferencias compartidas para almacenar datos de sesión del usuario */
    protected SharedPreferences sharedPreferences;
    /** TextView que muestra el título principal en el AppBar */
    protected TextView tvTitle;
    /** TextView que muestra el subtítulo en el AppBar */
    protected TextView tvSubtitle;
    /** Botón para cerrar sesión */
    protected ImageButton btnLogout;
    /** Botón para volver a la pantalla anterior */
    protected ImageButton btnBack;
    /** Botón para acceder al perfil del usuario */
    protected ImageButton btnProfile;
    /** Indica si esta Activity es la pantalla raíz (primera pantalla tras login) */
    protected boolean isRootActivity = false;
    
    /**
     * Método llamado cuando la Activity se crea.
     * 
     * Inicializa las SharedPreferences que se usarán para almacenar y recuperar
     * datos de sesión del usuario (ID, nombre, rol, etc.).
     * 
     * Contexto: Se ejecuta automáticamente cuando cualquier Activity que extiende
     * BaseActivity se crea.
     * 
     * @param savedInstanceState Estado previo de la Activity (si existe)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("BeautyAppPrefs", MODE_PRIVATE);
    }
    
    /**
     * Configura el AppBar (toolbar) con un título.
     * 
     * Versión simplificada que establece la Activity como no raíz.
     * 
     * Contexto: Se llama desde onCreate() de las Activities hijas para configurar
     * la barra superior de la aplicación.
     * 
     * @param title Título a mostrar en el AppBar
     */
    protected void setupAppBar(String title) {
        setupAppBar(title, false);
    }
    
    /**
     * Configura el AppBar (toolbar) con título y opción de marcarlo como raíz.
     * 
     * Configura todos los elementos del AppBar: título, subtítulo, botones de
     * retroceso, logout y perfil. Si la Activity es raíz, oculta el botón de retroceso
     * para evitar que el usuario vuelva al login.
     * 
     * Contexto: Se llama desde onCreate() de las Activities hijas para configurar
     * la barra superior. Las pantallas principales (dashboards) se marcan como raíz.
     * 
     * @param title Título a mostrar en el AppBar
     * @param isRoot true si esta es la pantalla principal tras login, false en caso contrario
     */
    protected void setupAppBar(String title, boolean isRoot) {
        this.isRootActivity = isRoot;
        View appBar = findViewById(R.id.appBar);
        if (appBar != null) {
            tvTitle = appBar.findViewById(R.id.tvTitle);
            btnLogout = appBar.findViewById(R.id.btnLogout);
            btnBack = appBar.findViewById(R.id.btnBack);
            
            if (tvTitle != null) {
                tvTitle.setText(title);
            }

            tvSubtitle = appBar.findViewById(R.id.tvSubtitle);
            if (tvSubtitle != null) {
                tvSubtitle.setVisibility(View.GONE);
            }
            
            // Configurar botón de retroceso
            if (btnBack != null) {
                if (isRoot) {
                    // Si es la pantalla raíz, ocultar el botón de retroceso
                    btnBack.setVisibility(View.GONE);
                } else {
                    // Mostrar botón de retroceso
                    btnBack.setVisibility(View.VISIBLE);
                    btnBack.setOnClickListener(v -> onBackPressed());
                }
            }
            
            // Configurar botón de logout
            if (btnLogout != null) {
                btnLogout.setOnClickListener(v -> showLogoutDialog());
            }

            btnProfile = appBar.findViewById(R.id.btnProfile);
            if (btnProfile != null) {
                btnProfile.setVisibility(View.GONE);
                btnProfile.setOnClickListener(null);
            }
        }
    }
    
    /**
     * Maneja el evento de presionar el botón de retroceso.
     * 
     * Si la Activity es raíz (pantalla principal), no hace nada para evitar
     * que el usuario vuelva al login. En caso contrario, ejecuta el comportamiento
     * estándar de Android que cierra la Activity actual.
     * 
     * Contexto: Se ejecuta automáticamente cuando el usuario presiona el botón
     * físico o virtual de retroceso del dispositivo.
     */
    @Override
    public void onBackPressed() {
        // Si es la pantalla raíz, no hacer nada (no cerrar sesión ni volver al login)
        if (isRootActivity) {
            return;
        }
        
        // Para actividades que no son raíz, usar el comportamiento estándar
        // que finaliza la actividad actual y vuelve a la anterior en el stack
        super.onBackPressed();
    }
    
    /**
     * Muestra un diálogo de confirmación antes de cerrar sesión.
     * 
     * Presenta un AlertDialog preguntando al usuario si está seguro de cerrar sesión.
     * Si confirma, llama al método logout().
     * 
     * Contexto: Se ejecuta cuando el usuario presiona el botón de logout en el AppBar.
     */
    protected void showLogoutDialog() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.logout_dialog_title)
            .setMessage(R.string.logout_dialog_message)
            .setPositiveButton(R.string.yes, (dialog, which) -> logout())
            .setNegativeButton(R.string.cancel, null)
            .show();
    }
    
    /**
     * Cierra la sesión del usuario y redirige al login.
     * 
     * Limpia todas las preferencias compartidas (datos de sesión) y navega
     * a LoginActivity, limpiando el stack de Activities para que el usuario
     * no pueda volver atrás usando el botón de retroceso.
     * 
     * Contexto: Se ejecuta cuando el usuario confirma que desea cerrar sesión
     * desde el diálogo de confirmación.
     */
    protected void logout() {
        // Limpiar preferencias de sesión
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        
        // Navegar al login limpiando el stack
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Establece o oculta el subtítulo en el AppBar.
     * 
     * Muestra un texto secundario debajo del título principal en el AppBar.
     * Si el subtítulo es null o vacío, oculta el TextView.
     * 
     * Contexto: Se usa en las pantallas principales (dashboards) para mostrar
     * información adicional como el nombre del módulo o descripción.
     * 
     * @param subtitle Texto del subtítulo a mostrar, o null para ocultarlo
     */
    protected void setToolbarSubtitle(CharSequence subtitle) {
        if (tvSubtitle == null) {
            return;
        }
        if (subtitle == null || TextUtils.isEmpty(subtitle.toString())) {
            tvSubtitle.setVisibility(View.GONE);
        } else {
            tvSubtitle.setVisibility(View.VISIBLE);
            tvSubtitle.setText(subtitle);
        }
    }
}

