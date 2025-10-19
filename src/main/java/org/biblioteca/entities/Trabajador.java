package org.biblioteca.entities;

import java.sql.Timestamp;

public class Trabajador {

    private int trabajadorID;
    private String nombre;
    private String usuarioLogin;
    private String contrasena; // Contraseña plana, temporalmente usada para hashear en el DAO
    private int rolID;
    private Timestamp fechaRegistro;

    public Trabajador() {
    }

    // Constructor para CARGAR datos desde la BD
    public Trabajador(int trabajadorID, String nombre, String usuarioLogin, int rolID, Timestamp fechaRegistro) {
        this.trabajadorID = trabajadorID;
        this.nombre = nombre;
        this.usuarioLogin = usuarioLogin;
        this.rolID = rolID;
        this.fechaRegistro = fechaRegistro;
    }

    // Constructor para REGISTRAR un nuevo trabajador (Debe usar setters para validar)
    public Trabajador(String nombre, String usuarioLogin, String contrasena, int rolID) {
        this.setNombre(nombre);
        this.setUsuarioLogin(usuarioLogin);
        this.setContrasena(contrasena);
        this.setRolID(rolID);
    }

    public int getTrabajadorID() {
        return trabajadorID;
    }

    public void setTrabajadorID(int trabajadorID) {
        this.trabajadorID = trabajadorID;
    }

    public String getNombre() {
        return nombre;
    }

    /**
     * Valida y establece el nombre. (NOT NULL y NVARCHAR(100))
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del trabajador es obligatorio.");
        }

        String nombreLimpio = nombre.trim();

        if (nombreLimpio.length() > 100) { // Límite de la BD
            throw new IllegalArgumentException("El nombre no puede exceder los 100 caracteres.");
        }

        this.nombre = nombreLimpio;
    }

    public String getUsuarioLogin() {
        return usuarioLogin;
    }

    public void setUsuarioLogin(String usuarioLogin) {
        if (usuarioLogin == null || usuarioLogin.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario para login es obligatorio.");
        }

        String usuarioLimpio = usuarioLogin.trim();

        if (usuarioLimpio.length() > 50) { // Límite de la BD
            throw new IllegalArgumentException("El usuario de login no puede exceder los 50 caracteres.");
        }


        if (!usuarioLimpio.matches("^[a-zA-Z0-9]+$")) {
        throw new IllegalArgumentException("El usuario solo puede contener letras y números.");
        }

        this.usuarioLogin = usuarioLimpio;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }

        String contrasenaLimpia = contrasena.trim();

        // Regla de Negocio: Longitud Mínima (Ej: 6 caracteres es un buen mínimo)
        if (contrasenaLimpia.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }

        this.contrasena = contrasenaLimpia;
    }

    public int getRolID() {
        return rolID;
    }

    /**
     * Valida y establece el ID del Rol. (Debe ser un ID válido)
     */
    public void setRolID(int rolID) {
        // Se valida que el ID sea un valor positivo (regla básica para una FK)
        if (rolID <= 0) {
            throw new IllegalArgumentException("El ID del Rol es inválido. Debe ser un valor positivo.");
        }
        this.rolID = rolID;
    }
    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
