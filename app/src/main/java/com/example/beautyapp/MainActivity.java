package com.example.beautyapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beautyapp.ui.login.LoginActivity;

/**
 * Activity principal de la aplicación.
 * 
 * Esta es la primera Activity que se ejecuta cuando el usuario abre la aplicación.
 * Su única función es redirigir inmediatamente al usuario a la pantalla de login
 * (LoginActivity), ya que no tiene interfaz propia.
 * 
 * Contexto de uso: Punto de entrada de la aplicación. Se ejecuta automáticamente
 * cuando el usuario abre la app desde el launcher.
 * 
 * Flujo: MainActivity → LoginActivity
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Método llamado cuando la Activity se crea.
     * 
     * Redirige inmediatamente al usuario a LoginActivity y finaliza esta Activity
     * para que no quede en el stack de navegación.
     * 
     * Contexto: Se ejecuta al iniciar la aplicación.
     * 
     * @param savedInstanceState Estado previo de la Activity (si existe)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Redirigir a LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}