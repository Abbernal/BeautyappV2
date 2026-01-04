package com.example.beautyapp.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad para hashear y verificar contraseñas.
 * 
 * Esta clase proporciona métodos estáticos para convertir contraseñas en texto plano
 * a hashes SHA-256, que se almacenan en la base de datos en lugar de las contraseñas
 * originales por seguridad.
 * 
 * Contexto de uso: Se utiliza en el proceso de registro de usuarios y en la
 * verificación de credenciales durante el login.
 * 
 * IMPORTANTE: Esta implementación con SHA-256 es solo para fines educativos/demostrativos.
 * En producción se debe usar algoritmos más seguros como bcrypt o Argon2.
 * 
 * @author Antonio Benitez
 * @version 1.0
 */
public class PasswordHasher {
    
    /**
     * Convierte una contraseña en texto plano a su hash SHA-256.
     * 
     * Toma una contraseña como string y la convierte en un hash hexadecimal
     * de 64 caracteres usando el algoritmo SHA-256.
     * 
     * Contexto: Se usa al registrar un nuevo usuario o al cambiar una contraseña.
     * El hash resultante se almacena en la base de datos en lugar de la contraseña original.
     * 
     * @param password Contraseña en texto plano a hashear
     * @return Hash SHA-256 de la contraseña en formato hexadecimal (64 caracteres)
     *         Si ocurre un error, devuelve la contraseña original (no recomendado)
     */
    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // Fallback (no recomendado en producción)
        }
    }
    
    /**
     * Verifica si una contraseña en texto plano coincide con un hash almacenado.
     * 
     * Compara una contraseña ingresada por el usuario con el hash almacenado
     * en la base de datos para determinar si son equivalentes.
     * 
     * Contexto: Se usa durante el proceso de login para validar las credenciales
     * del usuario sin necesidad de almacenar la contraseña original.
     * 
     * @param password Contraseña en texto plano ingresada por el usuario
     * @param hash Hash almacenado en la base de datos
     * @return true si la contraseña coincide con el hash, false en caso contrario
     */
    public static boolean verify(String password, String hash) {
        return hash(password).equals(hash);
    }
}

