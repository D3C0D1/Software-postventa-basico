package com.postventa.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilidad para hash de contraseñas usando SHA-256 con salt
 */
public class PasswordUtil {
    private static final int SALT_LENGTH = 16;

    /**
     * Genera un hash de la contraseña con salt
     * @param password Contraseña en texto plano
     * @return Hash de la contraseña en formato: salt$hash
     */
    public static String hashPassword(String password) {
        try {
            // Generar salt aleatorio
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Crear hash con salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Codificar en Base64 y combinar
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedPassword);
            
            return saltBase64 + "$" + hashBase64;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear contraseña", e);
        }
    }

    /**
     * Verifica si una contraseña coincide con su hash
     * @param password Contraseña en texto plano
     * @param storedHash Hash almacenado en formato: salt$hash
     * @return true si la contraseña es correcta
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Separar salt y hash
            String[] parts = storedHash.split("\\$");
            if (parts.length != 2) {
                // Compatibilidad con contraseñas sin hash (migración)
                return password.equals(storedHash);
            }
            
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHashBytes = Base64.getDecoder().decode(parts[1]);
            
            // Calcular hash con el mismo salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] calculatedHash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Comparar hashes de forma segura
            return MessageDigest.isEqual(storedHashBytes, calculatedHash);
        } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
            // Si hay error, intentar comparación directa (para contraseñas legacy)
            return password.equals(storedHash);
        }
    }

    /**
     * Verifica si una contraseña está hasheada
     * @param password Contraseña a verificar
     * @return true si parece estar hasheada
     */
    public static boolean isHashed(String password) {
        return password != null && password.contains("$") && password.length() > 50;
    }
}
