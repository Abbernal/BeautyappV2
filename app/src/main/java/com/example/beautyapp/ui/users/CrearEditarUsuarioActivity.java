package com.example.beautyapp.ui.users;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.beautyapp.R;
import com.example.beautyapp.db.BeautyAppDatabase;
import com.example.beautyapp.db.entity.Role;
import com.example.beautyapp.db.entity.Usuario;
import com.example.beautyapp.ui.base.BaseActivity;
import com.example.beautyapp.utils.PasswordHasher;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Activity para crear y editar usuarios del sistema.
 * 
 * Permite a los administradores crear nuevos usuarios o editar usuarios
 * existentes. Un usuario incluye nombre, email, teléfono, contraseña y rol.
 * 
 * Contexto de uso: Se accede desde ListaUsuariosActivity cuando el
 * administrador hace click en el FAB para crear un nuevo usuario, o
 * cuando hace click en un usuario existente para editarlo.
 * 
 * Funcionalidades:
 * - Crear nuevo usuario: Todos los campos editables
 * - Editar usuario existente: Carga los datos actuales y permite modificarlos
 * - Campos del formulario:
 *   - Nombre completo
 *   - Email (con validación de formato y unicidad)
 *   - Teléfono
 *   - Contraseña (obligatoria al crear, opcional al editar)
 *   - Rol (Administrador, Empleado, Cliente) - muestra nombres reales
 * - Validaciones:
 *   - Nombre: No puede estar vacío
 *   - Email: Formato válido y no duplicado
 *   - Teléfono: Solo números
 *   - Contraseña: Mínimo 6 caracteres
 * - Botón eliminar: Solo visible en modo edición
 * 
 * Flujo: CrearEditarUsuarioActivity → (guardar) → ListaUsuariosActivity
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class CrearEditarUsuarioActivity extends BaseActivity {
    private EditText etNombre, etEmail, etPassword, etTelefono;
    private Spinner spinnerRole;
    private Button btnSave, btnDelete;
    private BeautyAppDatabase db;
    private int userId = -1;
    private List<Role> rolesDisponibles = new ArrayList<>();
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);
        
        View appBar = findViewById(R.id.appBar);
        if (appBar != null) {
            setupAppBar(getString(R.string.add_user));
        }
        
        db = BeautyAppDatabase.getInstance(this);
        
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etTelefono = findViewById(R.id.etTelefono);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        
        userId = getIntent().getIntExtra("userId", -1);
        
        // Cargar roles con nombres reales
        rolesDisponibles = db.roleDao().getAll();
        List<String> nombresRoles = new ArrayList<>();
        for (Role role : rolesDisponibles) {
            nombresRoles.add(role.getNombre());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            nombresRoles
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
        
        if (userId != -1) {
            if (appBar != null) {
                setupAppBar(getString(R.string.edit_user));
            }
            loadUser();
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.GONE);
        }
        
        btnSave.setOnClickListener(v -> saveUser());
        btnDelete.setOnClickListener(v -> deleteUser());
    }
    
    private void loadUser() {
        Usuario usuario = db.usuarioDao().getById(userId);
        if (usuario != null) {
            etNombre.setText(usuario.getNombre());
            etEmail.setText(usuario.getEmail());
            etTelefono.setText(usuario.getTelefono());
            
            // Seleccionar rol por ID
            int rolId = usuario.getRolId();
            for (int i = 0; i < rolesDisponibles.size(); i++) {
                if (rolesDisponibles.get(i).getId() == rolId) {
                    spinnerRole.setSelection(i);
                    break;
                }
            }
        }
    }
    
    private void saveUser() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String telefono = etTelefono.getText().toString().trim();
        
        int selectedIndex = spinnerRole.getSelectedItemPosition();
        if (selectedIndex < 0 || selectedIndex >= rolesDisponibles.size()) {
            Toast.makeText(this, "Debe seleccionar un rol", Toast.LENGTH_SHORT).show();
            return;
        }
        Role selectedRole = rolesDisponibles.get(selectedIndex);
        
        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError(getString(R.string.required_field));
            return;
        }
        
        if (TextUtils.isEmpty(email) || !EMAIL_PATTERN.matcher(email).matches()) {
            etEmail.setError(getString(R.string.invalid_email));
            return;
        }
        
        if (TextUtils.isEmpty(telefono)) {
            etTelefono.setError(getString(R.string.required_field));
            return;
        }
        
        Usuario usuario;
        if (userId != -1) {
            usuario = db.usuarioDao().getById(userId);
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setTelefono(telefono);
            usuario.setRolId(selectedRole.getId());
            
            if (!TextUtils.isEmpty(password)) {
                if (password.length() < 6) {
                    etPassword.setError(getString(R.string.password_too_short));
                    return;
                }
                usuario.setPassword(PasswordHasher.hash(password));
            }
            
            // Verificar email único
            Usuario existing = db.usuarioDao().getByEmail(email);
            if (existing != null && existing.getId() != userId) {
                etEmail.setError(getString(R.string.email_exists));
                return;
            }
            
            db.usuarioDao().update(usuario);
        } else {
            if (TextUtils.isEmpty(password) || password.length() < 6) {
                etPassword.setError(getString(R.string.password_too_short));
                return;
            }
            
            // Verificar email único
            if (db.usuarioDao().getByEmail(email) != null) {
                etEmail.setError(getString(R.string.email_exists));
                return;
            }
            
            usuario = new Usuario(nombre, email, PasswordHasher.hash(password), telefono, selectedRole.getId());
            db.usuarioDao().insert(usuario);
        }
        
        Toast.makeText(this, R.string.user_saved, Toast.LENGTH_SHORT).show();
        finish();
    }
    
    private void deleteUser() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.confirm_delete)
            .setMessage("¿Está seguro de eliminar este usuario?")
            .setPositiveButton(R.string.yes, (dialog, which) -> {
                Usuario usuario = db.usuarioDao().getById(userId);
                if (usuario != null) {
                    db.usuarioDao().delete(usuario);
                    Toast.makeText(this, R.string.user_deleted, Toast.LENGTH_SHORT).show();
                    finish();
                }
            })
            .setNegativeButton(R.string.no, null)
            .show();
    }
}

