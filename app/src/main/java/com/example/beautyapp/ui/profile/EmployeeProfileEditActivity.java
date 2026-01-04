package com.example.beautyapp.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.beautyapp.R;
import com.example.beautyapp.db.BeautyAppDatabase;
import com.example.beautyapp.db.entity.Usuario;
import com.example.beautyapp.ui.base.BaseActivity;
import com.example.beautyapp.utils.PasswordHasher;
import com.google.android.material.textfield.TextInputEditText;

public class EmployeeProfileEditActivity extends BaseActivity {

    private TextInputEditText etNombre;
    private TextInputEditText etEmail;
    private TextInputEditText etTelefono;
    private TextInputEditText etPassword;
    private Button btnSave;

    private BeautyAppDatabase db;
    private Usuario usuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile_edit);

        View appBar = findViewById(R.id.appBar);
        if (appBar != null) {
            setupAppBar(getString(R.string.employee_profile_title));
        }

        db = BeautyAppDatabase.getInstance(this);

        etNombre = findViewById(R.id.etProfileNombre);
        etEmail = findViewById(R.id.etProfileEmail);
        etTelefono = findViewById(R.id.etProfileTelefono);
        etPassword = findViewById(R.id.etProfilePassword);
        btnSave = findViewById(R.id.btnProfileSave);

        int userId = sharedPreferences.getInt("userId", -1);
        usuarioActual = db.usuarioDao().getById(userId);

        populateFields();

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void populateFields() {
        if (usuarioActual == null) {
            Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etNombre.setText(usuarioActual.getNombre());
        etEmail.setText(usuarioActual.getEmail());
        etTelefono.setText(usuarioActual.getTelefono());
    }

    private void saveProfile() {
        if (usuarioActual == null) {
            return;
        }

        String nombre = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String telefono = etTelefono.getText() != null ? etTelefono.getText().toString().trim() : "";
        String nuevaPassword = etPassword.getText() != null ? etPassword.getText().toString() : "";

        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError(getString(R.string.required_field));
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.invalid_email));
            return;
        }

        if (TextUtils.isEmpty(telefono)) {
            etTelefono.setError(getString(R.string.required_field));
            return;
        }

        if (!TextUtils.isDigitsOnly(telefono)) {
            etTelefono.setError(getString(R.string.profile_phone_invalid));
            return;
        }

        if (!TextUtils.isEmpty(nuevaPassword) && nuevaPassword.length() < 6) {
            etPassword.setError(getString(R.string.password_too_short));
            return;
        }

        Usuario otroUsuario = db.usuarioDao().getByEmail(email);
        if (otroUsuario != null && otroUsuario.getId() != usuarioActual.getId()) {
            etEmail.setError(getString(R.string.profile_email_in_use));
            return;
        }

        usuarioActual.setNombre(nombre);
        usuarioActual.setEmail(email);
        usuarioActual.setTelefono(telefono);

        if (!TextUtils.isEmpty(nuevaPassword)) {
            usuarioActual.setPassword(PasswordHasher.hash(nuevaPassword));
        }

        db.usuarioDao().update(usuarioActual);

        sharedPreferences.edit()
            .putString("userName", usuarioActual.getNombre())
            .putString("userEmail", usuarioActual.getEmail())
            .apply();

        Toast.makeText(this, R.string.profile_update_success, Toast.LENGTH_SHORT).show();
        finish();
    }
}


