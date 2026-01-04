package com.example.beautyapp;

import android.app.Application;

import com.example.beautyapp.utils.DatabaseSeeder;

/**
 * Clase principal de la aplicación Android.
 * 
 * Esta clase extiende Application y se ejecuta cuando la aplicación se inicia.
 * Su función principal es inicializar la base de datos con datos de prueba
 * mediante el DatabaseSeeder.
 * 
 * Contexto de uso: Se ejecuta automáticamente al iniciar la aplicación,
 * antes de que cualquier Activity se cree.
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class BeautyAppApplication extends Application {
    
    /**
     * Método llamado cuando la aplicación se crea por primera vez.
     * 
     * Inicializa la base de datos con datos de prueba si está vacía.
     * Esto asegura que la aplicación tenga datos iniciales para funcionar
     * correctamente desde el primer inicio.
     * 
     * Contexto: Se ejecuta una sola vez al iniciar la aplicación,
     * antes de que MainActivity se cree.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // Inicializar seed data
        DatabaseSeeder.seedDatabase(this);
    }
}

