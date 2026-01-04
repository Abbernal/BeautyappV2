package com.example.beautyapp.ui.users;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautyapp.R;
import com.example.beautyapp.db.BeautyAppDatabase;
import com.example.beautyapp.db.entity.Role;
import com.example.beautyapp.db.entity.Usuario;

import java.util.List;

/**
 * Adapter para mostrar usuarios en un RecyclerView.
 * 
 * Proporciona la funcionalidad para mostrar una lista de usuarios en un RecyclerView,
 * incluyendo nombre, email, teléfono y rol. Cada item es clickeable para editar
 * el usuario.
 * 
 * Contexto de uso: Se utiliza en ListaUsuariosActivity para mostrar la lista de usuarios
 * del sistema. Solo está disponible para administradores.
 * 
 * Funcionalidades:
 * - Muestra información de cada usuario:
 *   - Nombre completo
 *   - Email
 *   - Teléfono
 *   - Rol (Administrador, Empleado, Cliente) - muestra nombre real
 * - Click en un item: Navega a CrearEditarUsuarioActivity para editar el usuario
 * - Método updateList: Permite actualizar la lista sin recrear el adapter
 *   (útil para filtrado en tiempo real)
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.ViewHolder> {
    private List<Usuario> usuarios;
    private ListaUsuariosActivity activity;
    private BeautyAppDatabase db;
    
    public UsuariosAdapter(List<Usuario> usuarios, ListaUsuariosActivity activity, BeautyAppDatabase db) {
        this.usuarios = usuarios;
        this.activity = activity;
        this.db = db;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);
        Role role = db.roleDao().getById(usuario.getRolId());
        
        holder.tvNombre.setText(usuario.getNombre());
        holder.tvEmail.setText(usuario.getEmail());
        holder.tvTelefono.setText(usuario.getTelefono());
        holder.tvRol.setText(role != null ? role.getNombre() : "N/A");
        
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, CrearEditarUsuarioActivity.class);
            intent.putExtra("userId", usuario.getId());
            activity.startActivity(intent);
        });
    }
    
    @Override
    public int getItemCount() {
        return usuarios.size();
    }
    
    public void updateList(List<Usuario> newList) {
        this.usuarios = newList;
        notifyDataSetChanged();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvEmail, tvTelefono, tvRol;
        
        ViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvTelefono = itemView.findViewById(R.id.tvTelefono);
            tvRol = itemView.findViewById(R.id.tvRol);
        }
    }
}

