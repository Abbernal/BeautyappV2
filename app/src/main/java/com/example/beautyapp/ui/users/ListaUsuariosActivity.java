package com.example.beautyapp.ui.users;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautyapp.R;
import com.example.beautyapp.db.BeautyAppDatabase;
import com.example.beautyapp.db.entity.Usuario;
import com.example.beautyapp.ui.base.BaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity para listar usuarios del sistema.
 * 
 * Muestra una lista de todos los usuarios registrados en el sistema.
 * Incluye funcionalidad de búsqueda en tiempo real para filtrar usuarios
 * por nombre, apellidos o email.
 * 
 * Contexto de uso: Se accede desde AdminDashboardActivity cuando el
 * administrador hace click en el CardView "Usuarios".
 * 
 * Funcionalidades:
 * - Lista de usuarios en RecyclerView con UsuariosAdapter
 * - Barra de búsqueda en tiempo real:
 *   - Filtra por nombre, apellidos o email
 *   - Actualiza la lista automáticamente mientras se escribe
 * - FAB (Floating Action Button) para crear nuevo usuario
 * - Click en un usuario: Navega a CrearEditarUsuarioActivity para editarlo
 * - Refresco automático: Al volver a la Activity, se actualiza la lista
 * 
 * Flujo: ListaUsuariosActivity → (FAB o click en usuario) → CrearEditarUsuarioActivity
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class ListaUsuariosActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private UsuariosAdapter adapter;
    private FloatingActionButton fabAdd;
    private TextInputEditText etSearch;
    private BeautyAppDatabase db;
    private List<Usuario> allUsers;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        
        View appBar = findViewById(R.id.appBar);
        if (appBar != null) {
            setupAppBar(getString(R.string.users_title));
        }
        
        db = BeautyAppDatabase.getInstance(this);
        
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        etSearch = findViewById(R.id.etSearch);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        allUsers = db.usuarioDao().getAll();
        adapter = new UsuariosAdapter(allUsers, this, db);
        recyclerView.setAdapter(adapter);
        
        setupSearchView();
        
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearEditarUsuarioActivity.class);
            startActivity(intent);
        });
    }
    
    private void setupSearchView() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No necesario
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                // No necesario
            }
        });
    }
    
    private void filterUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            adapter.updateList(allUsers);
            return;
        }
        
        String searchQuery = query.toLowerCase().trim();
        List<Usuario> filteredList = new ArrayList<>();
        
        for (Usuario usuario : allUsers) {
            String nombre = usuario.getNombre() != null ? usuario.getNombre().toLowerCase() : "";
            String email = usuario.getEmail() != null ? usuario.getEmail().toLowerCase() : "";
            
            // Buscar por nombre, apellidos (si están en el nombre) o email
            if (nombre.contains(searchQuery) || email.contains(searchQuery)) {
                filteredList.add(usuario);
            }
        }
        
        adapter.updateList(filteredList);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        allUsers = db.usuarioDao().getAll();
        String currentQuery = etSearch.getText() != null ? etSearch.getText().toString() : "";
        if (currentQuery.isEmpty()) {
            adapter.updateList(allUsers);
        } else {
            filterUsers(currentQuery);
        }
    }
}

