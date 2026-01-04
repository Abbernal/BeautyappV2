package com.example.beautyapp.db.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entidad que representa un usuario del sistema.
 * 
 * Almacena la información de todos los usuarios de la aplicación (administradores,
 * empleados y clientes). La contraseña se almacena como hash SHA-256, no en texto plano.
 * 
 * Contexto de uso: Se utiliza en la tabla USUARIOS de la base de datos.
 * Cada usuario tiene un rolId que referencia a la tabla ROLES para determinar
 * sus permisos y funcionalidades disponibles.
 * 
 * Relaciones:
 * - Un Usuario pertenece a un Role (many-to-one)
 * - Un Usuario puede tener múltiples Citas como cliente
 * - Un Usuario puede estar asignado a múltiples Citas como empleado (a través de CitaEmpleado)
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
@Entity(
    tableName = "USUARIOS",
    foreignKeys = @ForeignKey(
        entity = Role.class,
        parentColumns = "id",
        childColumns = "rolId",
        onDelete = ForeignKey.RESTRICT
    ),
    indices = {@Index("rolId"), @Index("email")}
)
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String nombre;
    private String email;
    private String password; // Hash de la contraseña
    private String telefono;
    private int rolId;
    
    public Usuario() {}
    
    public Usuario(String nombre, String email, String password, String telefono, int rolId) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.rolId = rolId;
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public int getRolId() {
        return rolId;
    }
    
    public void setRolId(int rolId) {
        this.rolId = rolId;
    }
}

