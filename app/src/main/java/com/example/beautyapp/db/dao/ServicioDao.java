package com.example.beautyapp.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;

import com.example.beautyapp.db.entity.Servicio;

import java.util.List;

@Dao
public interface ServicioDao {
    @Query("SELECT * FROM SERVICIOS")
    List<Servicio> getAll();
    
    @Query("SELECT * FROM SERVICIOS WHERE id = :id")
    Servicio getById(int id);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Servicio servicio);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Servicio> servicios);
    
    @Update
    void update(Servicio servicio);
    
    @Delete
    void delete(Servicio servicio);
    
    @Query("DELETE FROM SERVICIOS WHERE id = :id")
    void deleteById(int id);
}

