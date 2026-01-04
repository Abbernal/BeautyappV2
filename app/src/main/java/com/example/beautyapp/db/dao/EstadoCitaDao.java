package com.example.beautyapp.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;

import com.example.beautyapp.db.entity.EstadoCita;

import java.util.List;

@Dao
public interface EstadoCitaDao {
    @Query("SELECT * FROM ESTADOS_CITA")
    List<EstadoCita> getAll();
    
    @Query("SELECT * FROM ESTADOS_CITA WHERE id = :id")
    EstadoCita getById(int id);
    
    @Query("SELECT * FROM ESTADOS_CITA WHERE nombre = :nombre")
    EstadoCita getByNombre(String nombre);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EstadoCita estadoCita);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<EstadoCita> estados);
}

