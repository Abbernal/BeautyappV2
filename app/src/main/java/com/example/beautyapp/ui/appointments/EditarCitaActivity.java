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
 * Activity para editar citas existentes.
 * 
 * Permite editar una cita existente. El comportamiento varía según el rol:
 * - Cliente: Solo puede editar servicio, fecha, hora y notas (mismo layout que ReservarCitaActivity)
 * - Administrador/Empleado: Puede editar todos los campos (cliente, servicio, empleado, estado, fecha, hora, notas)
 * 
 * Contexto de uso: Se accede desde DetalleCitaActivity cuando el usuario hace click
 * en el botón "Editar". También puede accederse desde otras Activities que muestran
 * listas de citas.
 * 
 * Funcionalidades:
 * - Carga los datos actuales de la cita
 * - Spinners con nombres reales (servicios, clientes, empleados, estados)
 * - DatePicker y TimePicker con validaciones de horario
 * - Validaciones según el rol del usuario
 * - Guarda los cambios en la base de datos
 * 
 * Flujo: EditarCitaActivity → (guardar) → Activity anterior
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class EditarCitaActivity extends BaseActivity {
    private EditText etFecha, etHora, etNotas;
    private Spinner spinnerServicio, spinnerCliente, spinnerEmpleado, spinnerEstado;
    private Button btnSave;
    private BeautyAppDatabase db;
    private int appointmentId;
    private int userRoleId;
    private boolean isClientView = false;
    private List<Servicio> serviciosDisponibles = new ArrayList<>();
    private int clienteId;
    
    // Listas de respaldo para mantener los objetos originales (para administrador)
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
        
        db = BeautyAppDatabase.getInstance(this);
        appointmentId = getIntent().getIntExtra("appointmentId", -1);
        userRoleId = sharedPreferences.getInt("userRoleId", -1);
        isClientView = (userRoleId == 3); // Rol Cliente
        
        if (appointmentId == -1) {
            Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Si es cliente, usar layout de reserva; si no, usar layout completo
        if (isClientView) {
            setContentView(R.layout.activity_appointment_reserve);
            clienteId = sharedPreferences.getInt("userId", -1);
        } else {
            setContentView(R.layout.activity_appointment_edit);
        }
        
        View appBar = findViewById(R.id.appBar);
        if (appBar != null) {
            setupAppBar(getString(R.string.edit_appointment));
        }
        
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        etNotas = findViewById(R.id.etNotas);
        spinnerServicio = findViewById(R.id.spinnerServicio);
        
        if (isClientView) {
            btnSave = findViewById(R.id.btnReserve);
            if (btnSave != null) {
                btnSave.setText(getString(R.string.save));
            }
            setupPickers();
            loadServicios();
        } else {
            spinnerCliente = findViewById(R.id.spinnerCliente);
            spinnerEmpleado = findViewById(R.id.spinnerEmpleado);
            spinnerEstado = findViewById(R.id.spinnerEstado);
            btnSave = findViewById(R.id.btnSave);
            setupPickers(); // Agregar pickers también para administrador
            loadSpinners();
        }
        
        loadAppointment();
        
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveAppointment());
        }
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
            if (etNotas != null) {
                etNotas.setText(cita.getNotas());
            }
            
            // Seleccionar servicio
            if (isClientView) {
                // Para cliente, seleccionar por índice en la lista de nombres
                int servicioId = cita.getServicioId();
                for (int i = 0; i < serviciosDisponibles.size(); i++) {
                    if (serviciosDisponibles.get(i).getId() == servicioId) {
                        spinnerServicio.setSelection(i);
                        break;
                    }
                }
            } else {
                // Para admin/empleado, seleccionar usando las listas de respaldo
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
    }
    
    private <T> void selectInSpinnerByIndex(Spinner spinner, List<T> list, int id) {
        if (spinner == null || list == null) return;
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
    
    private boolean validateClientTimeSlot(int clienteId, String fecha, String fechaHora) {
        // Obtener todas las citas del cliente en el mismo día (excluyendo canceladas y la cita actual)
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

            // Verificar cada cita existente (excluyendo la cita actual que se está editando)
            for (Cita citaExistente : citasDelDia) {
                if (citaExistente.getId() == appointmentId) {
                    continue; // Saltar la cita actual
                }
                
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
    
    private void saveAppointment() {
        String fecha = etFecha.getText().toString().trim();
        String hora = etHora.getText().toString().trim();
        String notas = etNotas != null ? etNotas.getText().toString().trim() : "";
        
        if (TextUtils.isEmpty(fecha) || TextUtils.isEmpty(hora)) {
            Toast.makeText(this, R.string.required_field, Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (isClientView) {
            // Validaciones para cliente
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
            
            // Validar que no haya conflicto con el mismo servicio a la misma hora (excluyendo la cita actual)
            Cita citaActual = db.citaDao().getById(appointmentId);
            List<Cita> todasCitas = db.citaDao().getAll();
            boolean hayConflictoServicioHora = false;
            for (Cita c : todasCitas) {
                if (c.getId() != appointmentId && 
                    c.getFechaHora().equals(fechaHora) && 
                    c.getServicioId() == servicio.getId()) {
                    hayConflictoServicioHora = true;
                    break;
                }
            }
            if (hayConflictoServicioHora) {
                Toast.makeText(this, R.string.appointment_conflict_error, Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Validar que el mismo cliente no tenga ya una cita del mismo servicio el mismo día (excluyendo la actual)
            List<Cita> citasMismoServicioDia = db.citaDao().getByClienteAndFecha(
                clienteId,
                fecha,
                getString(R.string.status_cancelled)
            );
            boolean hayMismoServicioDia = false;
            if (citaActual != null && citaActual.getServicioId() != servicio.getId()) {
                for (Cita c : citasMismoServicioDia) {
                    if (c.getId() != appointmentId && c.getServicioId() == servicio.getId()) {
                        hayMismoServicioDia = true;
                        break;
                    }
                }
            }
            if (hayMismoServicioDia) {
                Toast.makeText(this, R.string.client_same_service_same_day_error, Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Validar intervalo de tiempo
            if (!validateClientTimeSlot(clienteId, fecha, fechaHora)) {
                Toast.makeText(this, R.string.client_appointment_time_conflict, Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Guardar cambios
            if (citaActual != null) {
                citaActual.setServicioId(servicio.getId());
                citaActual.setFechaHora(fechaHora);
                citaActual.setNotas(notas);
                db.citaDao().update(citaActual);
                
                Toast.makeText(this, R.string.appointment_saved, Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            // Lógica para admin/empleado
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
            
            Cita cita = db.citaDao().getById(appointmentId);
            if (cita != null) {
                cita.setServicioId(servicio.getId());
                cita.setClienteId(cliente.getId());
                cita.setEstadoId(estado.getId());
                cita.setFechaHora(fechaHora);
                cita.setNotas(notas);
                db.citaDao().update(cita);
                
                // Actualizar empleado
                db.citaEmpleadoDao().deleteByCita(appointmentId);
                db.citaEmpleadoDao().insert(new CitaEmpleado(appointmentId, empleado.getId()));
                
                Toast.makeText(this, R.string.appointment_saved, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}

