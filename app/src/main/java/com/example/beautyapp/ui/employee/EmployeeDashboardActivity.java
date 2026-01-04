package com.example.beautyapp.ui.employee;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.beautyapp.R;
import com.example.beautyapp.db.BeautyAppDatabase;
import com.example.beautyapp.db.entity.Cita;
import com.example.beautyapp.db.entity.EstadoCita;
import com.example.beautyapp.db.entity.Servicio;
import com.example.beautyapp.db.entity.Usuario;
import com.example.beautyapp.ui.appointments.AppointmentStatusEditActivity;
import com.example.beautyapp.ui.base.BaseActivity;
import com.example.beautyapp.ui.profile.EmployeeProfileEditActivity;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity principal del módulo de Empleado.
 * 
 * Es la pantalla de inicio para los usuarios con rol de Empleado.
 * Muestra un CardView para gestionar la agenda y una sección de
 * "Citas asignadas hoy" que lista todas las citas del día actual
 * ordenadas por hora.
 * 
 * Contexto de uso: Se muestra automáticamente después del login cuando
 * el usuario tiene rol de Empleado. Es una pantalla raíz, por lo que
 * el botón de retroceso está oculto.
 * 
 * Funcionalidades:
 * - Muestra saludo personalizado con el nombre del empleado en el toolbar
 * - Botón de perfil en el toolbar para editar datos personales
 * - CardView "Gestionar agenda": Acceso a la gestión completa de citas
 * - Sección "Citas asignadas hoy": Muestra todas las citas del día actual con:
 *   - Nombre del cliente
 *   - Servicio
 *   - Hora
 *   - Estado de la cita
 *   - Click para editar solo el estado (Realizada o Cancelada)
 * - Si no hay citas para hoy, muestra un mensaje informativo
 * 
 * Flujo: EmployeeDashboardActivity → (según acción)
 *   - EmployeeManageScheduleActivity (al hacer click en "Gestionar agenda")
 *   - AppointmentStatusEditActivity (al hacer click en una cita de hoy)
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class EmployeeDashboardActivity extends BaseActivity {
    private CardView cardManageSchedule;
    private LinearLayout layoutTodayAppointments;
    private BeautyAppDatabase db;
    private int empleadoId;
    
    /**
     * Método llamado cuando la Activity se crea.
     * 
     * Configura el AppBar con saludo personalizado, inicializa el CardView
     * de gestión de agenda, configura el botón de perfil y carga las citas
     * asignadas para el día actual.
     * 
     * Contexto: Se ejecuta cuando el empleado inicia sesión o accede
     * a esta pantalla desde otra parte de la aplicación.
     * 
     * @param savedInstanceState Estado previo de la Activity (si existe)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);
        
        db = BeautyAppDatabase.getInstance(this);
        empleadoId = sharedPreferences.getInt("userId", -1);
        
        View appBar = findViewById(R.id.appBar);
        if (appBar != null) {
            setupAppBar(getString(R.string.employee_dashboard), true); // true = es pantalla raíz
            String nombreEmpleado = sharedPreferences.getString("userName", "");
            if (nombreEmpleado == null || nombreEmpleado.isEmpty()) {
                nombreEmpleado = getString(R.string.employee_dashboard);
            }
            if (tvTitle != null) {
                tvTitle.setText(getString(R.string.employee_welcome_title, nombreEmpleado));
            }
            setToolbarSubtitle(getString(R.string.employee_toolbar_subtitle));
        }
        
        if (btnProfile != null) {
            btnProfile.setVisibility(View.VISIBLE);
            btnProfile.setOnClickListener(v -> {
                Intent intent = new Intent(this, EmployeeProfileEditActivity.class);
                startActivity(intent);
            });
        }
        
        cardManageSchedule = findViewById(R.id.cardManageSchedule);
        layoutTodayAppointments = findViewById(R.id.layoutTodayAppointments);
        
        cardManageSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(this, EmployeeManageScheduleActivity.class);
            startActivity(intent);
        });
        
        loadTodayAppointments();
    }
    
    /**
     * Carga y muestra las citas asignadas al empleado para el día actual.
     * 
     * Obtiene todas las citas del empleado cuya fecha coincide con el día actual,
     * las ordena por hora ascendente y las muestra en CardViews. Cada cita es
     * clickeable para editar su estado (solo se permite cambiar a "Realizada"
     * o "Cancelada").
     * 
     * Contexto: Se ejecuta al crear la Activity y cada vez que el usuario
     * vuelve a esta pantalla (en onResume).
     */
    private void loadTodayAppointments() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaHoy = sdf.format(new Date());
        
        List<Cita> citasHoy = db.citaDao().getByEmpleadoAndFecha(empleadoId, fechaHoy);
        
        // Ordenar por hora ascendente
        if (citasHoy != null && !citasHoy.isEmpty()) {
            Collections.sort(citasHoy, new Comparator<Cita>() {
                @Override
                public int compare(Cita c1, Cita c2) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                        Date d1 = sdf.parse(c1.getFechaHora());
                        Date d2 = sdf.parse(c2.getFechaHora());
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
        
        layoutTodayAppointments.removeAllViews();
        
        if (citasHoy == null || citasHoy.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText(getString(R.string.no_appointments_today));
            tvEmpty.setGravity(Gravity.CENTER);
            tvEmpty.setTextColor(getResources().getColor(R.color.text_dark));
            tvEmpty.setPadding(32, 48, 32, 48);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            tvEmpty.setLayoutParams(params);
            layoutTodayAppointments.addView(tvEmpty);
            return;
        }
        
        for (Cita cita : citasHoy) {
            View itemView = createAppointmentCard(cita);
            layoutTodayAppointments.addView(itemView);
        }
    }
    
    private View createAppointmentCard(Cita cita) {
        Usuario cliente = db.usuarioDao().getById(cita.getClienteId());
        Servicio servicio = db.servicioDao().getById(cita.getServicioId());
        EstadoCita estado = db.estadoCitaDao().getById(cita.getEstadoId());
        
        String[] fechaHora = cita.getFechaHora().split(" ");
        String hora = fechaHora.length > 1 ? fechaHora[1] : "";
        
        String nombreCliente = cliente != null ? cliente.getNombre() : "N/A";
        String nombreServicio = servicio != null ? servicio.getNombre() : "N/A";
        String nombreEstado = estado != null ? estado.getNombre() : "N/A";
        
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 16);
        cardView.setLayoutParams(cardParams);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
        cardView.setCardElevation(4);
        cardView.setRadius(12);
        cardView.setClickable(true);
        cardView.setFocusable(true);
        // Usar el atributo selectableItemBackground correctamente
        android.util.TypedValue typedValue = new android.util.TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
        cardView.setForeground(getResources().getDrawable(typedValue.resourceId, null));
        
        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setOrientation(LinearLayout.HORIZONTAL);
        cardContent.setPadding(20, 20, 20, 20);
        
        // Icono de usuario
        ImageView iconView = new ImageView(this);
        iconView.setImageResource(R.drawable.ic_toolbar_profile);
        iconView.setColorFilter(getResources().getColor(R.color.purple_primary));
        int iconSize = (int) (48 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
        iconParams.setMargins(0, 0, 16, 0);
        iconView.setLayoutParams(iconParams);
        
        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f
        );
        textLayout.setLayoutParams(textParams);
        
        TextView tvCliente = new TextView(this);
        tvCliente.setText(getString(R.string.name) + ": " + nombreCliente);
        tvCliente.setTextColor(getResources().getColor(R.color.text_dark));
        tvCliente.setTextSize(16);
        tvCliente.setTypeface(null, android.graphics.Typeface.BOLD);
        
        TextView tvServicio = new TextView(this);
        tvServicio.setText(getString(R.string.services_title) + ": " + nombreServicio);
        tvServicio.setTextColor(getResources().getColor(R.color.text_dark));
        tvServicio.setTextSize(14);
        tvServicio.setPadding(0, 4, 0, 0);
        
        TextView tvHora = new TextView(this);
        tvHora.setText(getString(R.string.appointment_time) + ": " + hora);
        tvHora.setTextColor(getResources().getColor(R.color.text_dark));
        tvHora.setTextSize(14);
        tvHora.setPadding(0, 4, 0, 0);
        
        TextView tvEstado = new TextView(this);
        tvEstado.setText(getString(R.string.appointment_status) + ": " + nombreEstado);
        tvEstado.setTextColor(getResources().getColor(R.color.text_dark));
        tvEstado.setTextSize(14);
        tvEstado.setPadding(0, 4, 0, 0);
        
        textLayout.addView(tvCliente);
        textLayout.addView(tvServicio);
        textLayout.addView(tvHora);
        textLayout.addView(tvEstado);
        
        cardContent.addView(iconView);
        cardContent.addView(textLayout);
        
        cardView.addView(cardContent);
        
        // Click listener para editar estado
        cardView.setOnClickListener(v -> {
            // Verificar que la cita no esté en estado "Realizada"
            if (estado != null && getString(R.string.status_completed).equals(estado.getNombre())) {
                android.widget.Toast.makeText(this, R.string.cannot_edit_completed_appointment, android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            
            Intent intent = new Intent(this, AppointmentStatusEditActivity.class);
            intent.putExtra("appointmentId", cita.getId());
            startActivity(intent);
        });
        
        return cardView;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadTodayAppointments();
    }
}

