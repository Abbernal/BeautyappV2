package com.example.beautyapp.db.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Entidad que representa una cita en el sistema.
 * 
 * Almacena la información de las citas reservadas por los clientes, incluyendo
 * el cliente, el servicio solicitado, el estado actual, fecha/hora y notas adicionales.
 * 
 * Contexto de uso: Se utiliza en la tabla CITAS de la base de datos.
 * Es la entidad central del sistema, ya que conecta clientes, servicios, estados
 * y empleados (a través de CitaEmpleado).
 * 
 * Relaciones:
 * - Una Cita pertenece a un Usuario (cliente) - many-to-one
 * - Una Cita pertenece a un Servicio - many-to-one
 * - Una Cita pertenece a un EstadoCita - many-to-one
 * - Una Cita puede tener uno o más empleados asignados (a través de CitaEmpleado)
 * 
 * Formato de fechaHora: "YYYY-MM-DD HH:MM" (ejemplo: "2024-12-20 10:00")
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
@Entity(
    tableName = "CITAS",
    foreignKeys = {
        @ForeignKey(
            entity = Usuario.class,
            parentColumns = "id",
            childColumns = "clienteId",
            onDelete = ForeignKey.RESTRICT
        ),
        @ForeignKey(
            entity = Servicio.class,
            parentColumns = "id",
            childColumns = "servicioId",
            onDelete = ForeignKey.RESTRICT
        ),
        @ForeignKey(
            entity = EstadoCita.class,
            parentColumns = "id",
            childColumns = "estadoId",
            onDelete = ForeignKey.RESTRICT
        )
    },
    indices = {@Index("clienteId"), @Index("servicioId"), @Index("estadoId")}
)
public class Cita {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private int clienteId;
    private int servicioId;
    private int estadoId;
    private String fechaHora; // Formato: "YYYY-MM-DD HH:MM"
    private String notas;
    
    public Cita() {}
    
    public Cita(int clienteId, int servicioId, int estadoId, String fechaHora, String notas) {
        this.clienteId = clienteId;
        this.servicioId = servicioId;
        this.estadoId = estadoId;
        this.fechaHora = fechaHora;
        this.notas = notas;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }
    
    public int getServicioId() {
        return servicioId;
    }
    
    public void setServicioId(int servicioId) {
        this.servicioId = servicioId;
    }
    
    public int getEstadoId() {
        return estadoId;
    }
    
    public void setEstadoId(int estadoId) {
        this.estadoId = estadoId;
    }
    
    public String getFechaHora() {
        return fechaHora;
    }
    
    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }
    
    public String getNotas() {
        return notas;
    }
    
    public void setNotas(String notas) {
        this.notas = notas;
    }
}

