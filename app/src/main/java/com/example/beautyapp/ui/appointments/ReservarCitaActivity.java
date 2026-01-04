package com.example.beautyapp.ui.appointments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.beautyapp.R;
import com.example.beautyapp.db.BeautyAppDatabase;
import com.example.beautyapp.db.entity.Cita;
import com.example.beautyapp.db.entity.CitaEmpleado;
import com.example.beautyapp.db.entity.EstadoCita;
import com.example.beautyapp.db.entity.Servicio;
import com.example.beautyapp.db.entity.Usuario;
import com.example.beautyapp.ui.base.BaseActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity para que los clientes reserven nuevas citas.
 * 
 * Permite a los clientes crear una nueva cita seleccionando un servicio,
 * fecha, hora y opcionalmente añadir notas. Incluye validaciones exhaustivas
 * para evitar conflictos de horarios y asegurar que se cumplan las reglas
 * de negocio.
 * 
 * Contexto de uso: Se accede desde ClienteHomeActivity cuando el cliente
 * hace click en el CardView "Reservar Cita". Solo está disponible para usuarios
 * con rol de Cliente.
 * 
 * Funcionalidades:
 * - Selección de servicio mediante spinner (muestra nombres reales)
 * - DatePicker para seleccionar fecha (fecha mínima = hoy)
 * - TimePicker para seleccionar hora con validaciones:
 *   - Solo horarios permitidos: 9:00-14:00 y 17:00-20:00
 *   - Intervalos de 30 minutos (9:00, 9:30, 10:00, etc.)
 * - Campo opcional de notas
 * - Validaciones al reservar:
 *   - No permitir reservar si existe otra cita (de cualquier cliente) con el mismo
 *     servicio, día y hora
 *   - No permitir que un cliente tenga el mismo servicio dos veces en un mismo día
 *   - No permitir que un cliente tenga citas con menos de 30 minutos de diferencia
 *   - Validar que la fecha/hora no sea en el pasado
 * - Asignación automática de un empleado disponible a la cita
 * - Estado inicial: "Pendiente"
 * 
 * Flujo: ReservarCitaActivity → (reserva exitosa) → ClienteHomeActivity
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class ReservarCitaActivity extends BaseActivity {
    private EditText etFecha, etHora, etNotas;
    private Spinner spinnerServicio;
    private Button btnReserve;
    private BeautyAppDatabase db;
    private int clienteId;
    private List<Servicio> serviciosDisponibles = new ArrayList<>();

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_reserve);

        View appBar = findViewById(R.id.appBar);
        if (appBar != null) {
            setupAppBar(getString(R.string.book_appointment));
        }

        db = BeautyAppDatabase.getInstance(this);
        clienteId = sharedPreferences.getInt("userId", -1);

        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        etNotas = findViewById(R.id.etNotas);
        spinnerServicio = findViewById(R.id.spinnerServicio);
        btnReserve = findViewById(R.id.btnReserve);

        setupPickers();
        loadServicios();

        btnReserve.setOnClickListener(v -> reserveAppointment());
    }

    private void setupPickers() {
        etFecha.setKeyListener(null);
        etFecha.setFocusable(false);
        etFecha.setOnClickListener(v -> showDatePicker());
        etFecha.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showDatePicker();
        });

        etHora.setKeyListener(null);
        etHora.setFocusable(false);
        etHora.setOnClickListener(v -> showTimePicker());
        etHora.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showTimePicker();
        });
    }

    private void loadServicios() {
        serviciosDisponibles = db.servicioDao().getAll();
        List<String> nombresServicios = new ArrayList<>();
        for (Servicio servicio : serviciosDisponibles) {
            nombresServicios.add(servicio.getNombre());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            nombresServicios
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServicio.setAdapter(adapter);

        boolean hayServicios = !serviciosDisponibles.isEmpty();
        spinnerServicio.setEnabled(hayServicios);
        btnReserve.setEnabled(hayServicios);

        if (!hayServicios) {
            Toast.makeText(this, R.string.no_services_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, dayOfMonth);
                etFecha.setText(dateFormatter.format(selected.getTime()));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int startHour = Math.max(9, calendar.get(Calendar.HOUR_OF_DAY));
        if (startHour >= 14 && startHour < 17) {
            startHour = 17;
        } else if (startHour > 20) {
            startHour = 9;
        }
        int startMinute = calendar.get(Calendar.MINUTE) < 30 ? 0 : 30;

        TimePickerDialog dialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                int adjustedMinute = minute < 30 ? 0 : 30;
                if (!isAllowedSlot(hourOfDay, adjustedMinute)) {
                    Toast.makeText(this, R.string.invalid_schedule_range, Toast.LENGTH_SHORT).show();
                    showTimePicker();
                    return;
                }
                etHora.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, adjustedMinute));
            },
            startHour,
            startMinute,
            true
        );

        dialog.show();
    }

    private boolean isAllowedSlot(String hora) {
        try {
            Date date = timeFormatter.parse(hora);
            if (date == null) return false;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return isAllowedSlot(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isAllowedSlot(int hour, int minute) {
        if (!(minute == 0 || minute == 30)) return false;
        boolean inMorning = (hour >= 9 && hour < 14) || (hour == 14 && minute == 0);
        boolean inEvening = (hour >= 17 && hour < 20) || (hour == 20 && minute == 0);
        return inMorning || inEvening;
    }

    private boolean validateClientTimeSlot(int clienteId, String fecha, String fechaHora) {
        // Obtener todas las citas del cliente en el mismo día (excluyendo canceladas)
        List<Cita> citasDelDia = db.citaDao().getByClienteAndFecha(
            clienteId,
            fecha,
            getString(R.string.status_cancelled)
        );

        if (citasDelDia == null || citasDelDia.isEmpty()) {
            return true; // No hay citas previas, se puede reservar
        }

        try {
            Date nuevaCita = dateTimeFormatter.parse(fechaHora);
            if (nuevaCita == null) {
                return false;
            }

            // Verificar cada cita existente
            for (Cita citaExistente : citasDelDia) {
                Date citaExistenteDate = dateTimeFormatter.parse(citaExistente.getFechaHora());
                if (citaExistenteDate == null) {
                    continue;
                }

                // Calcular la diferencia en milisegundos
                long diferenciaMs = Math.abs(nuevaCita.getTime() - citaExistenteDate.getTime());
                long diferenciaMinutos = diferenciaMs / (60 * 1000);

                // Si hay una cita a la misma hora exacta o con menos de 30 minutos de diferencia, no se permite
                if (diferenciaMinutos < 30) {
                    return false;
                }
            }

            return true; // Todas las citas tienen al menos 30 minutos de diferencia
        } catch (ParseException e) {
            return false;
        }
    }

    private void reserveAppointment() {
        String fecha = etFecha.getText().toString().trim();
        String hora = etHora.getText().toString().trim();
        String notas = etNotas.getText().toString().trim();

        if (TextUtils.isEmpty(fecha) || TextUtils.isEmpty(hora)) {
            Toast.makeText(this, R.string.required_field, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isAllowedSlot(hora)) {
            Toast.makeText(this, R.string.invalid_time_slot, Toast.LENGTH_SHORT).show();
            return;
        }

        Servicio servicio = null;
        int selectedIndex = spinnerServicio.getSelectedItemPosition();
        if (selectedIndex >= 0 && selectedIndex < serviciosDisponibles.size()) {
            servicio = serviciosDisponibles.get(selectedIndex);
        }

        if (servicio == null) {
            Toast.makeText(this, R.string.no_services_available, Toast.LENGTH_SHORT).show();
            return;
        }

        EstadoCita pendiente = db.estadoCitaDao().getByNombre(getString(R.string.status_pending));
        if (pendiente == null) {
            Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
            return;
        }

        String fechaHora = fecha + " " + hora;
        try {
            Date seleccion = dateTimeFormatter.parse(fechaHora);
            if (seleccion == null || seleccion.before(new Date())) {
                Toast.makeText(this, R.string.invalid_schedule_range, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (ParseException e) {
            Toast.makeText(this, R.string.invalid_schedule_range, Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que no haya conflicto con el mismo servicio a la misma hora (para cualquier cliente)
        int citasMismoSlot = db.citaDao().countByFechaHoraAndServicio(fechaHora, servicio.getId());
        if (citasMismoSlot > 0) {
            Toast.makeText(this, R.string.appointment_conflict_error, Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que el mismo cliente no tenga ya una cita del mismo servicio el mismo día
        int citasMismoServicioDia = db.citaDao().countByClienteServicioAndFecha(
            clienteId,
            servicio.getId(),
            fecha,
            getString(R.string.status_cancelled)
        );
        if (citasMismoServicioDia > 0) {
            Toast.makeText(this, R.string.client_same_service_same_day_error, Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que el mismo cliente no tenga otra cita el mismo día a la misma hora o con menos de 30 minutos de diferencia
        if (!validateClientTimeSlot(clienteId, fecha, fechaHora)) {
            Toast.makeText(this, R.string.client_appointment_time_conflict, Toast.LENGTH_SHORT).show();
            return;
        }

        Cita cita = new Cita(clienteId, servicio.getId(), pendiente.getId(), fechaHora, notas);
        long citaId = db.citaDao().insert(cita);

        List<Usuario> empleados = db.usuarioDao().getByRol(2); // Rol Empleado
        if (!empleados.isEmpty()) {
            Usuario empleado = empleados.get(0);
            db.citaEmpleadoDao().insert(new CitaEmpleado((int) citaId, empleado.getId()));
        }

        Toast.makeText(this, R.string.appointment_saved, Toast.LENGTH_SHORT).show();
        finish();
    }
}

