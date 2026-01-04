package com.example.beautyapp.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entidad que representa un servicio ofrecido por el centro de belleza.
 * 
 * Almacena la información de los servicios disponibles como corte de pelo,
 * tinte, manicura, pedicura, tratamientos faciales, etc.
 * 
 * Contexto de uso: Se utiliza en la tabla SERVICIOS de la base de datos.
 * Los servicios se muestran a los clientes para que puedan reservar citas,
 * y los administradores pueden gestionarlos (crear, editar, eliminar).
 * 
 * Relaciones:
 * - Un Servicio puede estar asociado a múltiples Citas
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
@Entity(tableName = "SERVICIOS")
public class Servicio {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String nombre;
    private String descripcion;
    private double precio;
    private int duracionMinutos; // Duración en minutos
    
    public Servicio() {}
    
    public Servicio(String nombre, String descripcion, double precio, int duracionMinutos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.duracionMinutos = duracionMinutos;
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
    
    public double getPrecio() {
        return precio;
    }
    
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    
    public int getDuracionMinutos() {
        return duracionMinutos;
    }
    
    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }
}

