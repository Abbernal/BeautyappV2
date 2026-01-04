package com.example.beautyapp.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entidad que representa un rol de usuario en el sistema.
 * 
 * Los roles definen los tipos de usuarios que pueden acceder a la aplicación:
 * - Administrador: Control total del sistema
 * - Empleado: Gestión de citas asignadas
 * - Cliente: Reserva y consulta de sus propias citas
 * 
 * Contexto de uso: Se utiliza en la tabla ROLES de la base de datos para
 * almacenar los diferentes roles disponibles. Cada usuario tiene un rolId
 * que referencia a un Role.
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
@Entity(tableName = "ROLES")
public class Role {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String nombre;
    private String descripcion;
    
    public Role() {}
    
    public Role(String nombre, String descripcion) {
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

