package com.example.beautyapp.ui.appointments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
 * Activity para crear y editar citas (módulo Administrador).
 * 
 * Permite a los administradores crear nuevas citas o editar citas existentes
 * con control total sobre todos los campos: cliente, servicio, empleado,
 * estado, fecha, hora y notas.
 * 
 * Contexto de uso: Se accede desde ListaCitasActivity cuando el
 * administrador hace click en el FAB para crear una nueva cita, o desde
 * DetalleCitaActivity para editar una cita existente.
 * 
 * Funcionalidades:
 * - Crear nueva cita: Todos los campos editables
 * - Editar cita existente: Carga los datos actuales y permite modificarlos
 * - Spinners con nombres reales:
 *   - Servicio: Muestra nombres reales (Corte de Pelo, Tinte, etc.)
 *   - Cliente: Muestra nombres de clientes
 *   - Empleado: Muestra nombres de empleados
 *   - Estado: Muestra nombres reales (Pendiente, Confirmada, etc.)
 * - DatePicker y TimePicker iguales a ReservarCitaActivity:
 *   - DatePicker con fecha mínima = hoy
 *   - TimePicker con horarios permitidos y intervalos de 30 minutos
 * - Validaciones:
 *   - Horario permitido (9:00-14:00 y 17:00-20:00)
 *   - Fecha/hora no en el pasado
 * - Asignación de empleado a la cita
 * 
 * Flujo: CrearCitaActivity → (guardar) → ListaCitasActivity
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class CrearCitaActivity extends BaseActivity {
    private EditText etFecha, etHora, etNotas;
    private Spinner spinnerServicio, spinnerCliente, spinnerEmpleado, spinnerEstado;
    private Button btnSave;
    private BeautyAppDatabase db;
    private int appointmentId = -1;
    private int userRoleId;
    
    // Listas de respaldo para mantener los objetos originales
    private List<Servicio> serviciosList = new ArrayList<>();
    private List<Usuario> clientesList = new ArrayList<>();
    private List<Usuario> empleadosList = new ArrayList<>();
    private List<EstadoCita> estadosList = new ArrayList<>();
    
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_form);
        
        View appBar = findViewById(R.id.appBar);
        if (appBar != null) {
            setupAppBar(getString(R.string.create_appointment));
        }
        
        db = BeautyAppDatabase.getInstance(this);
        userRoleId = sharedPreferences.getInt("userRoleId", -1);
        
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        etNotas = findViewById(R.id.etNotas);
        spinnerServicio = findViewById(R.id.spinnerServicio);
        spinnerCliente = findViewById(R.id.spinnerCliente);
        spinnerEmpleado = findViewById(R.id.spinnerEmpleado);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        btnSave = findViewById(R.id.btnSave);
        
        appointmentId = getIntent().getIntExtra("appointmentId", -1);
        
        setupPickers();
        loadSpinners();
        
        if (appointmentId != -1) {
            if (appBar != null) {
                setupAppBar(getString(R.string.edit_appointment));
            }
            loadAppointment();
        } else {
            // Si es cliente, auto-seleccionar
            if (userRoleId == 3) { // Cliente
                int clienteId = sharedPreferences.getInt("userId", -1);
                for (int i = 0; i < clientesList.size(); i++) {
                    if (clientesList.get(i).getId() == clienteId) {
                        spinnerCliente.setSelection(i);
                        break;
                    }
                }
                spinnerCliente.setEnabled(false);
            }
        }
        
        btnSave.setOnClickListener(v -> saveAppointment());
    }
    
    private void setupPickers() {
        if (etFecha != null) {
            etFecha.setKeyListener(null);
            etFecha.setFocusable(false);
            etFecha.setOnClickListener(v -> showDatePicker());
            etFecha.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) showDatePicker();
            });
        }
        
        if (etHora != null) {
            etHora.setKeyListener(null);
            etHora.setFocusable(false);
            etHora.setOnClickListener(v -> showTimePicker());
            etHora.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) showTimePicker();
            });
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
    
    private boolean isAllowedSlot(int hour, int minute) {
        if (!(minute == 0 || minute == 30)) return false;
        boolean inMorning = (hour >= 9 && hour < 14) || (hour == 14 && minute == 0);
        boolean inEvening = (hour >= 17 && hour < 20) || (hour == 20 && minute == 0);
        return inMorning || inEvening;
    }
    
    private void loadSpinners() {
        // Servicios - mostrar nombres reales
        serviciosList = db.servicioDao().getAll();
        List<String> nombresServicios = new ArrayList<>();
        for (Servicio servicio : serviciosList) {
            nombresServicios.add(servicio.getNombre());
        }
        ArrayAdapter<String> servicioAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            nombresServicios
        );
        servicioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServicio.setAdapter(servicioAdapter);
        
        // Clientes (solo rol Cliente) - mostrar nombres reales
        clientesList = db.usuarioDao().getByRol(3);
        List<String> nombresClientes = new ArrayList<>();
        for (Usuario cliente : clientesList) {
            nombresClientes.add(cliente.getNombre());
        }
        ArrayAdapter<String> clienteAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            nombresClientes
        );
        clienteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCliente.setAdapter(clienteAdapter);
        
        // Empleados (solo rol Empleado) - mostrar nombres reales
        empleadosList = db.usuarioDao().getByRol(2);
        List<String> nombresEmpleados = new ArrayList<>();
        for (Usuario empleado : empleadosList) {
            nombresEmpleados.add(empleado.getNombre());
        }
        ArrayAdapter<String> empleadoAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            nombresEmpleados
        );
        empleadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEmpleado.setAdapter(empleadoAdapter);
        
        // Estados - mostrar nombres reales
        estadosList = db.estadoCitaDao().getAll();
        List<String> nombresEstados = new ArrayList<>();
        for (EstadoCita estado : estadosList) {
            nombresEstados.add(estado.getNombre());
        }
        ArrayAdapter<String> estadoAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            nombresEstados
        );
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(estadoAdapter);
    }
    
    private void loadAppointment() {
        Cita cita = db.citaDao().getById(appointmentId);
        if (cita != null) {
            String[] fechaHora = cita.getFechaHora().split(" ");
            if (fechaHora.length == 2) {
                etFecha.setText(fechaHora[0]);
                etHora.setText(fechaHora[1]);
            }
            etNotas.setText(cita.getNotas());
            
            // Seleccionar en spinners usando las listas de respaldo
            selectInSpinnerByIndex(spinnerServicio, serviciosList, cita.getServicioId());
            selectInSpinnerByIndex(spinnerCliente, clientesList, cita.getClienteId());
            selectInSpinnerByIndex(spinnerEstado, estadosList, cita.getEstadoId());
            
            // Empleado
            List<CitaEmpleado> asignaciones = db.citaEmpleadoDao().getByCita(appointmentId);
            if (!asignaciones.isEmpty()) {
                selectInSpinnerByIndex(spinnerEmpleado, empleadosList, asignaciones.get(0).getEmpleadoId());
            }
        }
    }
    
    private <T> void selectInSpinnerByIndex(Spinner spinner, List<T> list, int id) {
        for (int i = 0; i < list.size(); i++) {
            T item = list.get(i);
            int itemId = -1;
            if (item instanceof Servicio) {
                itemId = ((Servicio) item).getId();
            } else if (item instanceof Usuario) {
                itemId = ((Usuario) item).getId();
            } else if (item instanceof EstadoCita) {
                itemId = ((EstadoCita) item).getId();
            }
            if (itemId == id) {
                spinner.setSelection(i);
                break;
            }
        }
    }
    
    private void saveAppointment() {
        String fecha = etFecha.getText().toString().trim();
        String hora = etHora.getText().toString().trim();
        String notas = etNotas.getText().toString().trim();
        
        if (TextUtils.isEmpty(fecha) || TextUtils.isEmpty(hora)) {
            Toast.makeText(this, R.string.required_field, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Validar horario permitido
        try {
            Date horaDate = timeFormatter.parse(hora);
            if (horaDate != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(horaDate);
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                if (!isAllowedSlot(hour, minute)) {
                    Toast.makeText(this, R.string.invalid_schedule_range, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } catch (ParseException e) {
            Toast.makeText(this, R.string.invalid_time_slot, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Obtener objetos desde las listas de respaldo usando el índice seleccionado
        int servicioIndex = spinnerServicio.getSelectedItemPosition();
        int clienteIndex = spinnerCliente.getSelectedItemPosition();
        int empleadoIndex = spinnerEmpleado.getSelectedItemPosition();
        int estadoIndex = spinnerEstado.getSelectedItemPosition();
        
        if (servicioIndex < 0 || servicioIndex >= serviciosList.size() ||
            clienteIndex < 0 || clienteIndex >= clientesList.size() ||
            empleadoIndex < 0 || empleadoIndex >= empleadosList.size() ||
            estadoIndex < 0 || estadoIndex >= estadosList.size()) {
            Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Servicio servicio = serviciosList.get(servicioIndex);
        Usuario cliente = clientesList.get(clienteIndex);
        Usuario empleado = empleadosList.get(empleadoIndex);
        EstadoCita estado = estadosList.get(estadoIndex);
        
        String fechaHora = fecha + " " + hora;
        
        // Validar que la fecha/hora no sea en el pasado
        try {
            Date fechaHoraDate = dateTimeFormatter.parse(fechaHora);
            if (fechaHoraDate == null || fechaHoraDate.before(new Date())) {
                Toast.makeText(this, R.string.invalid_schedule_range, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (ParseException e) {
            Toast.makeText(this, R.string.invalid_schedule_range, Toast.LENGTH_SHORT).show();
            return;
        }
        
        Cita cita;
        if (appointmentId != -1) {
            cita = db.citaDao().getById(appointmentId);
            cita.setServicioId(servicio.getId());
            cita.setClienteId(cliente.getId());
            cita.setEstadoId(estado.getId());
            cita.setFechaHora(fechaHora);
            cita.setNotas(notas);
            db.citaDao().update(cita);
            
            // Actualizar empleado
            db.citaEmpleadoDao().deleteByCita(appointmentId);
            db.citaEmpleadoDao().insert(new CitaEmpleado(appointmentId, empleado.getId()));
        } else {
            cita = new Cita(cliente.getId(), servicio.getId(), estado.getId(), fechaHora, notas);
            long id = db.citaDao().insert(cita);
            
            // Asignar empleado
            db.citaEmpleadoDao().insert(new CitaEmpleado((int) id, empleado.getId()));
        }
        
        Toast.makeText(this, R.string.appointment_saved, Toast.LENGTH_SHORT).show();
        finish();
    }
}

