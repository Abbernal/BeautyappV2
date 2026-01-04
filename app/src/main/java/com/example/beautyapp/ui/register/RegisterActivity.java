package com.example.beautyapp.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beautyapp.R;
import com.example.beautyapp.db.BeautyAppDatabase;
import com.example.beautyapp.db.entity.Role;
import com.example.beautyapp.db.entity.Usuario;
import com.example.beautyapp.ui.login.LoginActivity;
import com.example.beautyapp.utils.PasswordHasher;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Activity de registro de nuevos usuarios.
 * 
 * Permite a los usuarios crear una nueva cuenta en el sistema proporcionando
 * su información personal (nombre, email, contraseña, teléfono) y seleccionando
 * un rol. Valida que el email no esté ya registrado y que los datos cumplan
 * con los requisitos establecidos.
 * 
 * Contexto de uso: Se accede desde LoginActivity cuando un usuario nuevo quiere
 * registrarse. Después de un registro exitoso, redirige a LoginActivity para que
 * el usuario pueda iniciar sesión.
 * 
 * Funcionalidades:
 * - Validación de campos requeridos (nombre, email, contraseña, teléfono)
 * - Validación de formato de email usando expresión regular
 * - Validación de longitud mínima de contraseña (6 caracteres)
 * - Verificación de que el email no esté ya registrado
 * - Selección de rol mediante spinner (Administrador, Empleado, Cliente)
 * - Hash de contraseña antes de almacenarla (usando PasswordHasher)
 * - Navegación de vuelta a LoginActivity
 * 
 * Flujo: RegisterActivity → (registro exitoso) → LoginActivity
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class RegisterActivity extends AppCompatActivity {
    private EditText etNombre, etEmail, etPassword, etTelefono;
    private Spinner spinnerRole;
    private Button btnRegister;
    private TextView tvBackToLogin;
    private BeautyAppDatabase db;
    private List<Role> roles;
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    
    /**
     * Método llamado cuando la Activity se crea.
     * 
     * Inicializa los componentes de la interfaz y carga los roles disponibles
     * en el spinner, mostrando los nombres reales (Administrador, Empleado, Cliente)
     * en lugar de referencias internas.
     * 
     * Contexto: Se ejecuta cuando el usuario accede a la pantalla de registro.
     * 
     * @param savedInstanceState Estado previo de la Activity (si existe)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        db = BeautyAppDatabase.getInstance(this);
        
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etTelefono = findViewById(R.id.etTelefono);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnRegister = findViewById(R.id.btnRegister);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
        
        // Cargar roles en el spinner - mostrar nombres reales
        roles = db.roleDao().getAll();
        List<String> nombresRoles = new ArrayList<>();
        for (Role role : roles) {
            nombresRoles.add(role.getNombre());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            nombresRoles
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
        
        btnRegister.setOnClickListener(v -> attemptRegister());
        tvBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
    
    /**
     * Intenta registrar un nuevo usuario en el sistema.
     * 
     * Valida todos los campos del formulario (nombre, email, contraseña, teléfono),
     * verifica que el email no esté ya registrado, hashea la contraseña y crea
     * el nuevo usuario en la base de datos. Si el registro es exitoso, muestra
     * un mensaje de confirmación y redirige a LoginActivity.
     * 
     * Contexto: Se ejecuta cuando el usuario presiona el botón de registro.
     * 
     * Validaciones realizadas:
     * - Nombre: No puede estar vacío
     * - Email: Debe tener formato válido y no estar ya registrado
     * - Contraseña: Mínimo 6 caracteres
     * - Teléfono: No puede estar vacío
     * - Rol: Debe estar seleccionado
     */
    private void attemptRegister() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String telefono = etTelefono.getText().toString().trim();
        
        // Obtener el rol seleccionado desde la lista de respaldo usando el índice
        int roleIndex = spinnerRole.getSelectedItemPosition();
        if (roleIndex < 0 || roleIndex >= roles.size()) {
            Toast.makeText(this, "Debe seleccionar un rol", Toast.LENGTH_SHORT).show();
            return;
        }
        Role selectedRole = roles.get(roleIndex);
        
        // Validaciones
        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError(getString(R.string.required_field));
            return;
        }
        
        if (TextUtils.isEmpty(email) || !EMAIL_PATTERN.matcher(email).matches()) {
            etEmail.setError(getString(R.string.invalid_email));
            return;
        }
        
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError(getString(R.string.password_too_short));
            return;
        }
        
        if (TextUtils.isEmpty(telefono)) {
            etTelefono.setError(getString(R.string.required_field));
            return;
        }
        
        // Verificar si el email ya existe
        if (db.usuarioDao().getByEmail(email) != null) {
            etEmail.setError(getString(R.string.email_exists));
            return;
        }
        
        // Crear usuario
        Usuario nuevoUsuario = new Usuario(
            nombre,
            email,
            PasswordHasher.hash(password),
            telefono,
            selectedRole.getId()
        );
        
        long id = db.usuarioDao().insert(nuevoUsuario);
        if (id > 0) {
            Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, R.string.register_error, Toast.LENGTH_SHORT).show();
        }
    }
}

