package com.example.beautyapp.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;

import com.example.beautyapp.db.entity.Usuario;

import java.util.List;

/**
 * Data Access Object (DAO) para operaciones con la tabla USUARIOS.
 * 
 * Proporciona métodos para realizar consultas y operaciones CRUD sobre los usuarios
 * del sistema. Incluye métodos especializados para login, búsqueda por email y filtrado por rol.
 * 
 * Contexto de uso: Se utiliza en múltiples Activities:
 * - LoginActivity: Para autenticar usuarios (método login)
 * - RegisterActivity: Para verificar si un email ya existe y crear nuevos usuarios
 * - ListaUsuariosActivity: Para listar todos los usuarios o filtrar por rol
 * - CrearEditarUsuarioActivity: Para crear, editar y eliminar usuarios
 * - ProfileEditActivities: Para actualizar datos del perfil del usuario
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
@Dao
public interface UsuarioDao {
    @Query("SELECT * FROM USUARIOS")
    List<Usuario> getAll();
    
    @Query("SELECT * FROM USUARIOS WHERE id = :id")
    Usuario getById(int id);
    
    @Query("SELECT * FROM USUARIOS WHERE email = :email")
    Usuario getByEmail(String email);
    
    @Query("SELECT * FROM USUARIOS WHERE email = :email AND password = :password")
    Usuario login(String email, String password);
    
    @Query("SELECT * FROM USUARIOS WHERE rolId = :rolId")
    List<Usuario> getByRol(int rolId);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Usuario usuario);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Usuario> usuarios);
    
    @Update
    void update(Usuario usuario);
    
    @Delete
    void delete(Usuario usuario);
    
    @Query("DELETE FROM USUARIOS WHERE id = :id")
    void deleteById(int id);
}

