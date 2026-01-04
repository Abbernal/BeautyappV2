package com.example.beautyapp.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;

import com.example.beautyapp.db.entity.Role;

import java.util.List;

/**
 * Data Access Object (DAO) para operaciones con la tabla ROLES.
 * 
 * Proporciona métodos para realizar consultas y operaciones CRUD (Create, Read, Update, Delete)
 * sobre los roles de usuario en la base de datos.
 * 
 * Contexto de uso: Se utiliza principalmente en:
 * - RegisterActivity: Para cargar los roles disponibles en el spinner de registro
 * - CrearEditarUsuarioActivity: Para mostrar y seleccionar roles al crear/editar usuarios
 * - DatabaseSeeder: Para insertar los roles iniciales
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
@Dao
public interface RoleDao {
    @Query("SELECT * FROM ROLES")
    List<Role> getAll();
    
    @Query("SELECT * FROM ROLES WHERE id = :id")
    Role getById(int id);
    
    @Query("SELECT * FROM ROLES WHERE nombre = :nombre")
    Role getByNombre(String nombre);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Role role);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Role> roles);
    
    @Query("DELETE FROM ROLES")
    void deleteAll();
}

