package com.example.beautyapp.db.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entidad de relación que asocia empleados con citas.
 * 
 * Esta entidad implementa una relación many-to-many entre Citas y Empleados,
 * permitiendo que una cita tenga uno o más empleados asignados y que un
 * empleado pueda estar asignado a múltiples citas.
 * 
 * Contexto de uso: Se utiliza en la tabla CITA_EMPLEADO de la base de datos.
 * Cuando se crea una cita, se asigna automáticamente un empleado disponible.
 * Los empleados pueden ver y gestionar las citas que tienen asignadas.
 * 
 * Relaciones:
 * - Una CitaEmpleado pertenece a una Cita - many-to-one
 * - Una CitaEmpleado pertenece a un Usuario (empleado) - many-to-one
 * 
 * Nota: Si se elimina una Cita, sus asignaciones se eliminan automáticamente (CASCADE).
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
@Entity(
    tableName = "CITA_EMPLEADO",
    foreignKeys = {
        @ForeignKey(
            entity = Cita.class,
            parentColumns = "id",
            childColumns = "citaId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = Usuario.class,
            parentColumns = "id",
            childColumns = "empleadoId",
            onDelete = ForeignKey.RESTRICT
        )
    },
    indices = {@Index("citaId"), @Index("empleadoId")}
)
public class CitaEmpleado {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private int citaId;
    private int empleadoId;
    
    public CitaEmpleado() {}
    
    public CitaEmpleado(int citaId, int empleadoId) {
        this.citaId = citaId;
        this.empleadoId = empleadoId;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getCitaId() {
        return citaId;
    }
    
    public void setCitaId(int citaId) {
        this.citaId = citaId;
    }
    
    public int getEmpleadoId() {
        return empleadoId;
    }
    
    public void setEmpleadoId(int empleadoId) {
        this.empleadoId = empleadoId;
    }
}

