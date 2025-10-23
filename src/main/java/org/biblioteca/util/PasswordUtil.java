package org.biblioteca.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Clase de utilidad para gestionar el hashing de contraseñas usando SHA-256 y un salt.
 * Diseñada para trabajar con los tipos VARBINARY(512) (Hash) y VARBINARY(64) (Salt) de SQL Server.
 */

public class PasswordUtil {
    // 64 bytes para el salt, coincidiendo con VARBINARY(64) en la BD
    private static final int SALT_LENGTH = 64;
    private static final String HASH_ALGORITHM = "SHA-256";


    public static byte[] getNewSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }


    public static byte[] hashPassword(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            // 1. Aplicar el salt
            md.update(salt);
            // 2. Generar el hash de la contraseña combinada
            byte[] hashedPassword = md.digest(password.getBytes());
            return hashedPassword;
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error de algoritmo de hashing: " + e.getMessage());
            return null;
        }
    }


    public static boolean verifyPassword(String password, byte[] storedHash, byte[] storedSalt) {
        // 1. Hashear la contraseña ingresada con el salt ALMACENADO
        byte[] newHash = hashPassword(password, storedSalt);

        if (newHash == null) {
            return false;
        }
        // 2. Comparar el nuevo hash con el hash almacenado (comparación segura de arrays de bytes)
        return Arrays.equals(storedHash, newHash);
    }
}
