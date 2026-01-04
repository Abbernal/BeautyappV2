package com.example.beautyapp.utils;

import android.content.Context;

import com.example.beautyapp.db.BeautyAppDatabase;
import com.example.beautyapp.db.entity.Cita;
import com.example.beautyapp.db.entity.CitaEmpleado;
import com.example.beautyapp.db.entity.EstadoCita;
import com.example.beautyapp.db.entity.Role;
import com.example.beautyapp.db.entity.Servicio;
import com.example.beautyapp.db.entity.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilidad para poblar la base de datos con datos de prueba.
 * 
 * Esta clase se encarga de inicializar la base de datos con datos de ejemplo
 * cuando la aplicación se ejecuta por primera vez. Crea roles, usuarios,
 * servicios, estados de cita, citas y asignaciones de empleados.
 * 
 * Contexto de uso: Se ejecuta automáticamente al iniciar la aplicación
 * desde BeautyAppApplication.onCreate(). Solo se ejecuta si la base de datos
 * está vacía (no tiene roles).
 * 
 * Datos que crea:
 * - 3 Roles: Administrador, Empleado, Cliente
 * - 7 Usuarios: 1 administrador, 2 empleados, 4 clientes
 * - 4 Estados de Cita: Pendiente, Confirmada, Realizada, Cancelada
 * - 8 Servicios: Corte de Pelo, Tinte, Manicura, Pedicura, etc.
 * - 8 Citas de ejemplo con diferentes estados
 * - Asignaciones de empleados a citas
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class DatabaseSeeder {
    
    /**
     * Pobla la base de datos con datos de prueba si está vacía.
     * 
     * Verifica si la base de datos ya tiene datos (comprobando si existen roles).
     * Si está vacía, crea todos los datos iniciales necesarios para que la aplicación
     * funcione correctamente.
     * 
     * Contexto: Se llama desde BeautyAppApplication.onCreate() al iniciar la app.
     * Solo se ejecuta una vez, cuando la base de datos está vacía.
     * 
     * @param context Contexto de la aplicación (necesario para obtener la instancia de la BD)
     */
    public static void seedDatabase(Context context) {
        BeautyAppDatabase db = BeautyAppDatabase.getInstance(context);
        
        // Verificar si ya hay datos
        if (db.roleDao().getAll().size() > 0) {
            return; // Ya hay datos, no hacer seed
        }
        
        // 1. Roles
        List<Role> roles = new ArrayList<>();
        roles.add(new Role("Administrador", "Administrador del sistema"));
        roles.add(new Role("Empleado", "Empleado del centro"));
        roles.add(new Role("Cliente", "Cliente del centro"));
        db.roleDao().insertAll(roles);
        
        // Obtener IDs de roles
        Role adminRole = db.roleDao().getByNombre("Administrador");
        Role empleadoRole = db.roleDao().getByNombre("Empleado");
        Role clienteRole = db.roleDao().getByNombre("Cliente");
        
        // 2. Usuarios
        List<Usuario> usuarios = new ArrayList<>();
        // Admin
        usuarios.add(new Usuario(
            "Admin Principal",
            "admin@beautyapp.com",
            PasswordHasher.hash("admin123"),
            "600123456",
            adminRole.getId()
        ));
        // Empleados
        usuarios.add(new Usuario(
            "María García",
            "maria@beautyapp.com",
            PasswordHasher.hash("empleado123"),
            "600234567",
            empleadoRole.getId()
        ));
        usuarios.add(new Usuario(
            "Juan López",
            "juan@beautyapp.com",
            PasswordHasher.hash("empleado123"),
            "600345678",
            empleadoRole.getId()
        ));
        // Clientes
        usuarios.add(new Usuario(
            "Ana Martínez",
            "ana@beautyapp.com",
            PasswordHasher.hash("cliente123"),
            "600456789",
            clienteRole.getId()
        ));
        usuarios.add(new Usuario(
            "Carlos Ruiz",
            "carlos@beautyapp.com",
            PasswordHasher.hash("cliente123"),
            "600567890",
            clienteRole.getId()
        ));
        usuarios.add(new Usuario(
            "Laura Sánchez",
            "laura@beautyapp.com",
            PasswordHasher.hash("cliente123"),
            "600678901",
            clienteRole.getId()
        ));
        usuarios.add(new Usuario(
            "Pedro Torres",
            "pedro@beautyapp.com",
            PasswordHasher.hash("cliente123"),
            "600789012",
            clienteRole.getId()
        ));
        db.usuarioDao().insertAll(usuarios);
        
        // Obtener IDs de usuarios
        Usuario admin = db.usuarioDao().getByEmail("admin@beautyapp.com");
        Usuario empleado1 = db.usuarioDao().getByEmail("maria@beautyapp.com");
        Usuario empleado2 = db.usuarioDao().getByEmail("juan@beautyapp.com");
        Usuario cliente1 = db.usuarioDao().getByEmail("ana@beautyapp.com");
        Usuario cliente2 = db.usuarioDao().getByEmail("carlos@beautyapp.com");
        Usuario cliente3 = db.usuarioDao().getByEmail("laura@beautyapp.com");
        
        // 3. Estados de Cita
        List<EstadoCita> estados = new ArrayList<>();
        estados.add(new EstadoCita("Pendiente", "Cita pendiente de confirmación"));
        estados.add(new EstadoCita("Confirmada", "Cita confirmada"));
        estados.add(new EstadoCita("Realizada", "Cita completada"));
        estados.add(new EstadoCita("Cancelada", "Cita cancelada"));
        db.estadoCitaDao().insertAll(estados);
        
        EstadoCita pendiente = db.estadoCitaDao().getByNombre("Pendiente");
        EstadoCita confirmada = db.estadoCitaDao().getByNombre("Confirmada");
        EstadoCita realizada = db.estadoCitaDao().getByNombre("Realizada");
        EstadoCita cancelada = db.estadoCitaDao().getByNombre("Cancelada");
        
        // 4. Servicios
        List<Servicio> servicios = new ArrayList<>();
        servicios.add(new Servicio(
            "Corte de Pelo",
            "Corte de pelo profesional",
            25.00,
            30
        ));
        servicios.add(new Servicio(
            "Tinte",
            "Tinte completo de cabello",
            60.00,
            120
        ));
        servicios.add(new Servicio(
            "Manicura",
            "Manicura completa con esmaltado",
            20.00,
            45
        ));
        servicios.add(new Servicio(
            "Pedicura",
            "Pedicura completa con esmaltado",
            25.00,
            60
        ));
        servicios.add(new Servicio(
            "Tratamiento Facial",
            "Tratamiento facial rejuvenecedor",
            50.00,
            90
        ));
        servicios.add(new Servicio(
            "Depilación",
            "Depilación con cera",
            30.00,
            45
        ));
        servicios.add(new Servicio(
            "Maquillaje",
            "Maquillaje profesional",
            40.00,
            60
        ));
        servicios.add(new Servicio(
            "Peinado",
            "Peinado para eventos",
            35.00,
            60
        ));
        db.servicioDao().insertAll(servicios);
        
        Servicio servicio1 = db.servicioDao().getAll().get(0);
        Servicio servicio2 = db.servicioDao().getAll().get(1);
        Servicio servicio3 = db.servicioDao().getAll().get(2);
        Servicio servicio4 = db.servicioDao().getAll().get(3);
        
        // 5. Citas
        List<Cita> citas = new ArrayList<>();
        citas.add(new Cita(
            cliente1.getId(),
            servicio1.getId(),
            confirmada.getId(),
            "2024-12-20 10:00",
            "Cliente regular"
        ));
        citas.add(new Cita(
            cliente2.getId(),
            servicio2.getId(),
            pendiente.getId(),
            "2024-12-20 14:00",
            "Primera vez"
        ));
        citas.add(new Cita(
            cliente3.getId(),
            servicio3.getId(),
            confirmada.getId(),
            "2024-12-21 09:00",
            ""
        ));
        citas.add(new Cita(
            cliente1.getId(),
            servicio4.getId(),
            realizada.getId(),
            "2024-12-19 16:00",
            "Cita completada"
        ));
        citas.add(new Cita(
            cliente2.getId(),
            servicio1.getId(),
            cancelada.getId(),
            "2024-12-18 11:00",
            "Cliente canceló"
        ));
        citas.add(new Cita(
            cliente3.getId(),
            servicio2.getId(),
            confirmada.getId(),
            "2024-12-22 15:00",
            ""
        ));
        citas.add(new Cita(
            cliente1.getId(),
            servicio3.getId(),
            pendiente.getId(),
            "2024-12-23 10:30",
            ""
        ));
        citas.add(new Cita(
            cliente2.getId(),
            servicio4.getId(),
            confirmada.getId(),
            "2024-12-21 13:00",
            ""
        ));
        db.citaDao().insertAll(citas);
        
        // 6. Asignaciones Cita-Empleado
        List<Cita> todasCitas = db.citaDao().getAll();
        List<CitaEmpleado> asignaciones = new ArrayList<>();
        
        // Asignar empleados a las citas
        asignaciones.add(new CitaEmpleado(todasCitas.get(0).getId(), empleado1.getId()));
        asignaciones.add(new CitaEmpleado(todasCitas.get(1).getId(), empleado2.getId()));
        asignaciones.add(new CitaEmpleado(todasCitas.get(2).getId(), empleado1.getId()));
        asignaciones.add(new CitaEmpleado(todasCitas.get(3).getId(), empleado1.getId()));
        asignaciones.add(new CitaEmpleado(todasCitas.get(4).getId(), empleado2.getId()));
        asignaciones.add(new CitaEmpleado(todasCitas.get(5).getId(), empleado2.getId()));
        asignaciones.add(new CitaEmpleado(todasCitas.get(6).getId(), empleado1.getId()));
        asignaciones.add(new CitaEmpleado(todasCitas.get(7).getId(), empleado2.getId()));
        
        db.citaEmpleadoDao().insertAll(asignaciones);
    }
}

