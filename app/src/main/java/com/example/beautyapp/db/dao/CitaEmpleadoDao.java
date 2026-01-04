package com.example.beautyapp.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;

import com.example.beautyapp.db.entity.CitaEmpleado;

import java.util.List;

@Dao
public interface CitaEmpleadoDao {
    @Query("SELECT * FROM CITA_EMPLEADO")
    List<CitaEmpleado> getAll();
    
    @Query("SELECT * FROM CITA_EMPLEADO WHERE citaId = :citaId")
    List<CitaEmpleado> getByCita(int citaId);
    
    @Query("SELECT * FROM CITA_EMPLEADO WHERE empleadoId = :empleadoId")
    List<CitaEmpleado> getByEmpleado(int empleadoId);
    
    @Query("SELECT empleadoId FROM CITA_EMPLEADO WHERE citaId = :citaId")
    List<Integer> getEmpleadoIdsByCita(int citaId);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CitaEmpleado citaEmpleado);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CitaEmpleado> citaEmpleados);
    
    @Delete
    void delete(CitaEmpleado citaEmpleado);
    
    @Query("DELETE FROM CITA_EMPLEADO WHERE citaId = :citaId")
    void deleteByCita(int citaId);
}

