package com.example.beautyapp.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entidad que representa un estado posible de una cita.
 * 
 * Los estados definen el ciclo de vida de una cita:
 * - Pendiente: Cita creada pero aún no confirmada
 * - Confirmada: Cita confirmada y lista para realizarse
 * - Realizada: Cita completada exitosamente
 * - Cancelada: Cita cancelada por el cliente o el centro
 * 
 * Contexto de uso: Se utiliza en la tabla ESTADOS_CITA de la base de datos.
 * Cada cita tiene un estadoId que referencia a un EstadoCita para indicar
 * su estado actual.
 * 
 * Relaciones:
 * - Un EstadoCita puede estar asociado a múltiples Citas
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
@Entity(tableName = "ESTADOS_CITA")
public class EstadoCita {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String nombre;
    private String descripcion;
    
    public EstadoCita() {}
    
    public EstadoCita(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}

