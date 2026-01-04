package com.example.beautyapp.ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.example.beautyapp.R;
import com.example.beautyapp.db.BeautyAppDatabase;
import com.example.beautyapp.db.entity.Cita;
import com.example.beautyapp.db.entity.EstadoCita;
import com.example.beautyapp.db.entity.Servicio;
import com.example.beautyapp.ui.appointments.DetalleCitaActivity;
import com.example.beautyapp.ui.appointments.ReservarCitaActivity;
import com.example.beautyapp.ui.appointments.ListaCitasActivity;
import com.example.beautyapp.ui.base.BaseActivity;
import com.example.beautyapp.ui.profile.EditarPerfilClienteActivity;
import com.example.beautyapp.ui.services.ListaServiciosActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity principal del módulo de Cliente.
 * 
 * Es la pantalla de inicio para los usuarios con rol de Cliente.
 * Muestra tres CardViews principales y una sección de "Próximas citas"
 * que muestra las 3 citas más próximas del cliente.
 * 
 * Contexto de uso: Se muestra automáticamente después del login cuando
 * el usuario tiene rol de Cliente. Es una pantalla raíz, por lo que
 * el botón de retroceso está oculto.
 * 
 * Funcionalidades:
 * - Muestra saludo personalizado con el nombre del cliente en el toolbar
 * - Botón de perfil en el toolbar para editar datos personales
 * - Tres CardViews navegables:
 *   - Mis Citas: Ver todas las citas del cliente (filtradas)
 *   - Servicios Disponibles: Ver catálogo de servicios (solo lectura)
 *   - Reservar Cita: Crear una nueva cita
 * - Sección "Próximas citas": Muestra las 3 citas más próximas con:
 *   - Nombre del servicio
 *   - Fecha y hora
 *   - Estado de la cita
 *   - Click para ver detalles y editar
 * 
 * Flujo: ClienteHomeActivity → (según CardView seleccionado)
 *   - ListaCitasActivity (con filtro de cliente)
 *   - ListaServiciosActivity (vista de solo lectura)
 *   - ReservarCitaActivity
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class ClienteHomeActivity extends BaseActivity {
    private CardView cardMyAppointments, cardBookAppointment, cardAvailableServices;
    private LinearLayout layoutUpcomingAppointments;
    private BeautyAppDatabase db;
    private int clienteId;
    
    /**
     * Método llamado cuando la Activity se crea.
     * 
     * Configura el AppBar con saludo personalizado, inicializa los CardViews,
     * configura el botón de perfil y carga las próximas citas del cliente.
     * 
     * Contexto: Se ejecuta cuando el cliente inicia sesión o accede
     * a esta pantalla desde otra parte de la aplicación.
     * 
     * @param savedInstanceState Estado previo de la Activity (si existe)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_dashboard);
        
        View appBar = findViewById(R.id.appBar);
        if (appBar != null) {
            setupAppBar(getString(R.string.client_dashboard), true); // true = es pantalla raíz
            String nombreCliente = sharedPreferences.getString("userName", "");
            if (nombreCliente == null || nombreCliente.isEmpty()) {
                nombreCliente = getString(R.string.client_dashboard);
            }
            if (tvTitle != null) {
                tvTitle.setText(getString(R.string.client_welcome_title, nombreCliente));
            }
            setToolbarSubtitle(getString(R.string.client_toolbar_subtitle));
        }
        
        db = BeautyAppDatabase.getInstance(this);
        clienteId = sharedPreferences.getInt("userId", -1);
        
        // Validar que el clienteId sea válido
        if (clienteId == -1) {
            Toast.makeText(this, "Error: Sesión inválida. Por favor, inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        cardMyAppointments = findViewById(R.id.cardMyAppointments);
        cardBookAppointment = findViewById(R.id.cardBookAppointment);
        cardAvailableServices = findViewById(R.id.cardAvailableServices);
        layoutUpcomingAppointments = findViewById(R.id.layoutUpcomingAppointments);
        
        // Validar que los componentes existan
        if (cardMyAppointments == null || cardBookAppointment == null || 
            cardAvailableServices == null || layoutUpcomingAppointments == null) {
            Toast.makeText(this, "Error: Componentes de la interfaz no encontrados.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (btnProfile != null) {
            btnProfile.setVisibility(View.VISIBLE);
            btnProfile.setOnClickListener(v -> {
                Intent intent = new Intent(this, EditarPerfilClienteActivity.class);
                startActivity(intent);
            });
        }
        
        // 1. Mis citas
        cardMyAppointments.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListaCitasActivity.class);
            intent.putExtra("filterByClient", true);
            startActivity(intent);
        });
        
        // 2. Servicios disponibles
        cardAvailableServices.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListaServiciosActivity.class);
            intent.putExtra("clientView", true); // Solo lectura para clientes
            startActivity(intent);
        });
        
        // 3. Reservar Cita
        cardBookAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReservarCitaActivity.class);
            startActivity(intent);
        });
        
        // 4. Cargar próximas citas
        loadUpcomingAppointments();
    }
    
    /**
     * Carga y muestra las próximas citas del cliente en la sección correspondiente.
     * 
     * Obtiene las 3 citas más próximas del cliente (excluyendo canceladas)
     * y las muestra en CardViews dentro del layout. Cada cita muestra el servicio,
     * fecha, hora y estado, y es clickeable para ver detalles.
     * 
     * Contexto: Se ejecuta al crear la Activity y cada vez que el usuario
     * vuelve a esta pantalla (en onResume).
     */
    private void loadUpcomingAppointments() {
        try {
            if (layoutUpcomingAppointments == null) {
                return;
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String ahora = sdf.format(new Date());
            List<Cita> citasFuturas = db.citaDao().getUpcomingByCliente(
                clienteId,
                ahora,
                getString(R.string.status_cancelled),
                3
            );
            
            layoutUpcomingAppointments.removeAllViews();
            
            if (citasFuturas == null || citasFuturas.isEmpty()) {
                TextView tvEmpty = new TextView(this);
                tvEmpty.setText(getString(R.string.no_upcoming_appointments));
                tvEmpty.setGravity(Gravity.CENTER);
                tvEmpty.setTextColor(getResources().getColor(R.color.text_dark, null));
                tvEmpty.setPadding(32, 48, 32, 48);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                );
                tvEmpty.setLayoutParams(params);
                layoutUpcomingAppointments.addView(tvEmpty);
                return;
            }
            
            for (Cita cita : citasFuturas) {
                if (cita != null) {
                    View itemView = createUpcomingAppointmentItem(cita);
                    if (itemView != null) {
                        layoutUpcomingAppointments.addView(itemView);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Si hay un error, mostrar mensaje vacío en lugar de crashear
            if (layoutUpcomingAppointments != null) {
                layoutUpcomingAppointments.removeAllViews();
                TextView tvError = new TextView(this);
                tvError.setText(getString(R.string.no_upcoming_appointments));
                tvError.setGravity(Gravity.CENTER);
                tvError.setPadding(32, 48, 32, 48);
                layoutUpcomingAppointments.addView(tvError);
            }
        }
    }
    
    /**
     * Crea un item de cita próxima usando el layout XML.
     * 
     * Infla el layout item_upcoming_appointment.xml y lo rellena con los datos
     * de la cita proporcionada. Configura los colores del estado y el listener
     * de click para navegar a los detalles.
     * 
     * @param cita La cita a mostrar
     * @return La vista del item creada
     */
    private View createUpcomingAppointmentItem(Cita cita) {
        if (cita == null) {
            return null;
        }
        
        View itemView = getLayoutInflater().inflate(R.layout.item_upcoming_appointment, layoutUpcomingAppointments, false);
        
        if (itemView == null) {
            return null;
        }
        
        Servicio servicio = db.servicioDao().getById(cita.getServicioId());
        EstadoCita estado = db.estadoCitaDao().getById(cita.getEstadoId());
        
        TextView tvServicio = itemView.findViewById(R.id.tvServicio);
        TextView tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
        TextView tvEstado = itemView.findViewById(R.id.tvEstado);
        
        if (tvServicio != null) {
            tvServicio.setText(servicio != null ? servicio.getNombre() : "N/A");
        }
        
        if (tvFechaHora != null) {
            String fechaHora = cita.getFechaHora();
            if (fechaHora != null && !fechaHora.isEmpty()) {
                String[] partes = fechaHora.split(" ");
                if (partes.length == 2) {
                    tvFechaHora.setText(partes[0] + " " + partes[1]);
                } else {
                    tvFechaHora.setText(fechaHora);
                }
            } else {
                tvFechaHora.setText("Fecha no disponible");
            }
        }
        
        if (tvEstado != null) {
            tvEstado.setText(estado != null ? estado.getNombre() : "N/A");
        }
        
        // Color según estado
        if (estado != null && tvEstado != null) {
            String estadoNombre = estado.getNombre();
            if (estadoNombre != null) {
                switch (estadoNombre) {
                    case "Confirmada":
                        tvEstado.setTextColor(getResources().getColor(R.color.success, null));
                        break;
                    case "Cancelada":
                        tvEstado.setTextColor(getResources().getColor(R.color.error, null));
                        break;
                    default:
                        tvEstado.setTextColor(getResources().getColor(R.color.warning, null));
                }
            }
        }
        
        itemView.setOnClickListener(v -> {
            if (cita != null) {
                Intent intent = new Intent(this, DetalleCitaActivity.class);
                intent.putExtra("appointmentId", cita.getId());
                startActivity(intent);
            }
        });
        
        return itemView;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadUpcomingAppointments();
    }
}

