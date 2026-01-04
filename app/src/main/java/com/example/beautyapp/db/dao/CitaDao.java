package com.example.beautyapp.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;

import com.example.beautyapp.db.entity.Cita;

import java.util.List;

/**
 * Data Access Object (DAO) para operaciones con la tabla CITAS.
 * 
 * Proporciona métodos para realizar consultas y operaciones CRUD sobre las citas.
 * Incluye consultas complejas con JOINs para obtener citas filtradas por empleado,
 * cliente, estado, fecha, y validaciones de conflictos de horarios.
 * 
 * Contexto de uso: Se utiliza extensivamente en:
 * - ReservarCitaActivity: Para validar conflictos y crear nuevas citas
 * - CrearCitaActivity: Para crear y editar citas (admin)
 * - EditarCitaActivity: Para editar citas (admin y cliente)
 * - ListaCitasActivity: Para listar todas las citas o filtrar
 * - ClienteHomeActivity: Para mostrar próximas citas del cliente
 * - EmployeeDashboardActivity: Para mostrar citas asignadas hoy al empleado
 * - EmployeePendingAppointmentsActivity: Para listar citas pendientes
 * - EmployeeConfirmedAppointmentsActivity: Para listar citas confirmadas
 * - EmployeeAllAppointmentsActivity: Para listar todas las citas del empleado
 * 
 * Consultas especiales:
 * - getByEmpleadoAndFecha: Obtiene citas de un empleado en una fecha específica
 * - getByEmpleadoAndEstado: Obtiene citas de un empleado con un estado específico
 * - countByFechaHoraAndServicio: Valida conflictos de horario al reservar
 * - countByClienteServicioAndFecha: Valida que un cliente no tenga el mismo servicio dos veces en un día
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
@Dao
public interface CitaDao {
    @Query("SELECT * FROM CITAS")
    List<Cita> getAll();
    
    @Query("SELECT * FROM CITAS WHERE id = :id")
    Cita getById(int id);
    
    @Query("SELECT * FROM CITAS WHERE clienteId = :clienteId")
    List<Cita> getByCliente(int clienteId);
    
    @Query("SELECT * FROM CITAS WHERE estadoId = :estadoId")
    List<Cita> getByEstado(int estadoId);
    
    @Query("SELECT c.* FROM CITAS c " +
           "INNER JOIN CITA_EMPLEADO ce ON c.id = ce.citaId " +
           "WHERE ce.empleadoId = :empleadoId")
    List<Cita> getByEmpleado(int empleadoId);
    
    @Query("SELECT c.* FROM CITAS c " +
           "INNER JOIN CITA_EMPLEADO ce ON c.id = ce.citaId " +
           "WHERE ce.empleadoId = :empleadoId AND c.fechaHora LIKE :fecha || '%'")
    List<Cita> getByEmpleadoAndFecha(int empleadoId, String fecha);
    
    @Query("SELECT c.* FROM CITAS c " +
           "INNER JOIN CITA_EMPLEADO ce ON c.id = ce.citaId " +
           "INNER JOIN ESTADOS_CITA e ON e.id = c.estadoId " +
           "WHERE ce.empleadoId = :empleadoId AND e.nombre = :estadoNombre " +
           "ORDER BY datetime(c.fechaHora) ASC")
    List<Cita> getByEmpleadoAndEstado(int empleadoId, String estadoNombre);

    @Query("SELECT c.* FROM CITAS c " +
           "INNER JOIN ESTADOS_CITA e ON e.id = c.estadoId " +
           "WHERE c.clienteId = :clienteId " +
           "AND datetime(c.fechaHora) >= datetime(:desdeFechaHora) " +
           "AND e.nombre != :estadoExcluido " +
           "ORDER BY datetime(c.fechaHora) ASC " +
           "LIMIT :limit")
    List<Cita> getUpcomingByCliente(int clienteId, String desdeFechaHora, String estadoExcluido, int limit);

    @Query("SELECT COUNT(*) FROM CITAS WHERE fechaHora = :fechaHora AND servicioId = :servicioId")
    int countByFechaHoraAndServicio(String fechaHora, int servicioId);
    
    @Query("SELECT c.* FROM CITAS c " +
           "INNER JOIN ESTADOS_CITA e ON e.id = c.estadoId " +
           "WHERE c.clienteId = :clienteId " +
           "AND c.fechaHora LIKE :fecha || '%' " +
           "AND e.nombre != :estadoExcluido")
    List<Cita> getByClienteAndFecha(int clienteId, String fecha, String estadoExcluido);
    
    @Query("SELECT COUNT(*) FROM CITAS c " +
           "INNER JOIN ESTADOS_CITA e ON e.id = c.estadoId " +
           "WHERE c.clienteId = :clienteId " +
           "AND c.servicioId = :servicioId " +
           "AND c.fechaHora LIKE :fecha || '%' " +
           "AND e.nombre != :estadoExcluido")
    int countByClienteServicioAndFecha(int clienteId, int servicioId, String fecha, String estadoExcluido);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Cita cita);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Cita> citas);
    
    @Update
    void update(Cita cita);
    
    @Delete
    void delete(Cita cita);
    
    @Query("DELETE FROM CITAS WHERE id = :id")
    void deleteById(int id);
}

