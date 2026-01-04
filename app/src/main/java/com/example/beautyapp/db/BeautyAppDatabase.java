package com.example.beautyapp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.beautyapp.db.dao.CitaDao;
import com.example.beautyapp.db.dao.CitaEmpleadoDao;
import com.example.beautyapp.db.dao.EstadoCitaDao;
import com.example.beautyapp.db.dao.RoleDao;
import com.example.beautyapp.db.dao.ServicioDao;
import com.example.beautyapp.db.dao.UsuarioDao;
import com.example.beautyapp.db.entity.Cita;
import com.example.beautyapp.db.entity.CitaEmpleado;
import com.example.beautyapp.db.entity.EstadoCita;
import com.example.beautyapp.db.entity.Role;
import com.example.beautyapp.db.entity.Servicio;
import com.example.beautyapp.db.entity.Usuario;

/**
 * Clase de base de datos principal usando Room Persistence Library.
 * 
 * Esta clase abstracta define la estructura de la base de datos SQLite de la aplicación.
 * Room genera automáticamente la implementación de esta clase en tiempo de compilación.
 * 
 * Contexto de uso: Se utiliza en toda la aplicación para acceder a los datos almacenados
 * localmente. Todas las operaciones de base de datos pasan por esta clase mediante los DAOs.
 * 
 * Características:
 * - Patrón Singleton: Solo existe una instancia de la base de datos
 * - Permite consultas en el hilo principal (solo para simplificación, en producción usar coroutines)
 * - Migraciones destructivas: Si cambia la versión, se recrea la BD (solo para desarrollo)
 * 
 * Entidades incluidas:
 * - Role: Roles de usuario (Administrador, Empleado, Cliente)
 * - Usuario: Usuarios del sistema
 * - Servicio: Servicios ofrecidos por el centro
 * - EstadoCita: Estados posibles de una cita
 * - Cita: Citas reservadas
 * - CitaEmpleado: Relación entre citas y empleados
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
@Database(
    entities = {
        Role.class,
        Usuario.class,
        Servicio.class,
        EstadoCita.class,
        Cita.class,
        CitaEmpleado.class
    },
    version = 1,
    exportSchema = false
)
public abstract class BeautyAppDatabase extends RoomDatabase {
    /** Instancia única de la base de datos (patrón Singleton) */
    private static BeautyAppDatabase INSTANCE;
    
    /**
     * Obtiene el DAO para operaciones con Roles.
     * @return Instancia de RoleDao
     */
    public abstract RoleDao roleDao();
    
    /**
     * Obtiene el DAO para operaciones con Usuarios.
     * @return Instancia de UsuarioDao
     */
    public abstract UsuarioDao usuarioDao();
    
    /**
     * Obtiene el DAO para operaciones con Servicios.
     * @return Instancia de ServicioDao
     */
    public abstract ServicioDao servicioDao();
    
    /**
     * Obtiene el DAO para operaciones con Estados de Cita.
     * @return Instancia de EstadoCitaDao
     */
    public abstract EstadoCitaDao estadoCitaDao();
    
    /**
     * Obtiene el DAO para operaciones con Citas.
     * @return Instancia de CitaDao
     */
    public abstract CitaDao citaDao();
    
    /**
     * Obtiene el DAO para operaciones con asignaciones Cita-Empleado.
     * @return Instancia de CitaEmpleadoDao
     */
    public abstract CitaEmpleadoDao citaEmpleadoDao();
    
    /**
     * Obtiene la instancia única de la base de datos (patrón Singleton).
     * 
     * Si la instancia no existe, la crea usando Room.databaseBuilder.
     * Si ya existe, devuelve la instancia existente.
     * 
     * Contexto: Se llama desde cualquier Activity o clase que necesite acceder
     * a la base de datos. Garantiza que solo existe una instancia en toda la app.
     * 
     * @param context Contexto de la aplicación (necesario para crear la BD)
     * @return Instancia única de BeautyAppDatabase
     */
    public static synchronized BeautyAppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                context.getApplicationContext(),
                BeautyAppDatabase.class,
                "beautyapp_database"
            )
            .allowMainThreadQueries() // Para simplificar, en producción usar coroutines/threads
            .fallbackToDestructiveMigration()
            .build();
        }
        return INSTANCE;
    }
}

