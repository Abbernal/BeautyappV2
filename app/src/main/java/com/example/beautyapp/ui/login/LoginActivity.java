package com.example.beautyapp.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beautyapp.R;
import com.example.beautyapp.db.BeautyAppDatabase;
import com.example.beautyapp.db.entity.Usuario;
import com.example.beautyapp.ui.admin.AdminDashboardActivity;
import com.example.beautyapp.ui.client.ClienteHomeActivity;
import com.example.beautyapp.ui.employee.EmployeeDashboardActivity;
import com.example.beautyapp.ui.register.RegisterActivity;
import com.example.beautyapp.utils.PasswordHasher;

/**
 * Activity de inicio de sesión de la aplicación.
 * 
 * Permite a los usuarios autenticarse en el sistema ingresando su email y contraseña.
 * Verifica las credenciales contra la base de datos y, si son correctas, guarda la sesión
 * y redirige al usuario al dashboard correspondiente según su rol.
 * 
 * Contexto de uso: Es la primera pantalla que ve el usuario al abrir la aplicación
 * (después de MainActivity). También se muestra cuando el usuario cierra sesión.
 * 
 * Funcionalidades:
 * - Validación de campos (email y contraseña requeridos)
 * - Verificación de credenciales usando PasswordHasher
 * - Gestión de sesión mediante SharedPreferences
 * - Redirección automática según el rol del usuario:
 *   - Administrador → AdminDashboardActivity
 *   - Empleado → EmployeeDashboardActivity
 *   - Cliente → ClienteHomeActivity
 * - Verificación de sesión activa: Si el usuario ya tiene sesión, redirige automáticamente
 * - Navegación a RegisterActivity para nuevos usuarios
 * 
 * Flujo: LoginActivity → (según rol) Dashboard correspondiente
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private BeautyAppDatabase db;
    private SharedPreferences sharedPreferences;
    
    /**
     * Método llamado cuando la Activity se crea.
     * 
     * Inicializa los componentes de la interfaz y verifica si el usuario ya tiene
     * una sesión activa. Si existe una sesión válida, redirige automáticamente
     * al dashboard correspondiente sin mostrar el formulario de login.
     * 
     * Contexto: Se ejecuta cuando el usuario accede a la pantalla de login.
     * 
     * @param savedInstanceState Estado previo de la Activity (si existe)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        db = BeautyAppDatabase.getInstance(this);
        sharedPreferences = getSharedPreferences("BeautyAppPrefs", MODE_PRIVATE);
        
        // Verificar si ya hay sesión activa
        int userId = sharedPreferences.getInt("userId", -1);
        if (userId != -1) {
            Usuario usuario = db.usuarioDao().getById(userId);
            if (usuario != null) {
                navigateToDashboard(usuario);
                return;
            }
        }
        
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        
        btnLogin.setOnClickListener(v -> attemptLogin());
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
    
    /**
     * Intenta autenticar al usuario con las credenciales proporcionadas.
     * 
     * Valida que los campos email y contraseña no estén vacíos, busca el usuario
     * en la base de datos por email, y verifica que la contraseña coincida usando
     * PasswordHasher. Si las credenciales son correctas, guarda la sesión y redirige
     * al dashboard correspondiente según el rol del usuario.
     * 
     * Contexto: Se ejecuta cuando el usuario presiona el botón de login.
     */
    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        
        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.required_field));
            return;
        }
        
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.required_field));
            return;
        }
        
        Usuario usuario = db.usuarioDao().getByEmail(email);
        if (usuario != null && PasswordHasher.verify(password, usuario.getPassword())) {
            // Guardar sesión
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("userId", usuario.getId());
            editor.putString("userEmail", usuario.getEmail());
            editor.putString("userName", usuario.getNombre());
            editor.putInt("userRoleId", usuario.getRolId());
            editor.apply();
            
            navigateToDashboard(usuario);
        } else {
            Toast.makeText(this, R.string.invalid_credentials, Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Navega al dashboard correspondiente según el rol del usuario.
     * 
     * Determina el rol del usuario autenticado y redirige a la Activity principal
     * (dashboard) de ese rol. Cierra LoginActivity para que no quede en el stack
     * de navegación.
     * 
     * Contexto: Se ejecuta después de una autenticación exitosa o cuando se detecta
     * una sesión activa al iniciar la aplicación.
     * 
     * @param usuario Usuario autenticado con su información completa
     */
    private void navigateToDashboard(Usuario usuario) {
        Intent intent;
        int rolId = usuario.getRolId();
        
        // Obtener nombre del rol con validación
        com.example.beautyapp.db.entity.Role role = db.roleDao().getById(rolId);
        if (role == null) {
            Toast.makeText(this, "Error: Rol no encontrado. Por favor, contacte al administrador.", Toast.LENGTH_LONG).show();
            // Limpiar sesión corrupta
            sharedPreferences.edit().clear().apply();
            return;
        }
        
        String roleName = role.getNombre();
        
        if ("Administrador".equals(roleName)) {
            intent = new Intent(this, AdminDashboardActivity.class);
        } else if ("Empleado".equals(roleName)) {
            intent = new Intent(this, EmployeeDashboardActivity.class);
        } else {
            intent = new Intent(this, ClienteHomeActivity.class);
        }
        
        startActivity(intent);
        finish();
    }
}

