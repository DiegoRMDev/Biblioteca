package org.biblioteca.entities;

import java.sql.Timestamp;

public class Lector {

    private int lectorID;
    private String dni;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private Timestamp fechaRegistro;

    public Lector() {
    }

    // Constructor para CARGAR datos desde la BD
    public Lector(int lectorID, String dni, String nombre, String direccion, String telefono, String email, Timestamp fechaRegistro) {
        this.lectorID = lectorID;
        this.dni = dni;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.fechaRegistro = fechaRegistro;
    }

    // Constructor para REGISTRAR un nuevo lector (Debe usar setters para validar)
    public Lector(String dni, String nombre, String direccion, String telefono, String email) {
        // Al registrar, usamos los setters para activar las validaciones
        this.setDni(dni);
        this.setNombre(nombre);
        this.setDireccion(direccion);
        this.setTelefono(telefono);
        this.setEmail(email);
    }

    public int getLectorID() {
        return lectorID;
    }

    public void setLectorID(int lectorID) {
        this.lectorID = lectorID;
    }

    public String getDni() {
        return dni;
    }

    /**
     * Valida y establece el DNI. (NOT NULL, 8 dígitos numéricos)
     */
    public void setDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI es obligatorio.");
        }

        String cleanDni = dni.trim();

        // Validación: Exactamente 8 dígitos numéricos

        if (!cleanDni.matches("^\\d{8}$")) {
            throw new IllegalArgumentException("El DNI debe contener exactamente 8 dígitos numéricos.");
        }

        this.dni = cleanDni;
    }

    public String getNombre() {
        return nombre;
    }

    /**
     * Valida y establece el Nombre. (NOT NULL y limpieza básica)
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El Nombre del Lector es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = (direccion != null) ? direccion.trim() : null;
    }

    public String getTelefono() {
        return telefono;
    }

    /**
     * Valida y establece el Teléfono. (NULL permitido, 6 a 12 dígitos numéricos)
     */
    public void setTelefono(String telefono) {
        if (telefono != null && !telefono.trim().isEmpty()) {
            String cleanTelefono = telefono.trim();

            // Validación: Entre 6 y 12 dígitos numéricos (^[\\d]{6,12}$)
            // Esto replica la regla CHK_Telefono_Formato de tu BD
            if (!cleanTelefono.matches("^[\\d]{6,12}$")) {
                throw new IllegalArgumentException("El Teléfono debe contener entre 6 y 12 dígitos numéricos.");
            }
            this.telefono = cleanTelefono;
        } else {
            this.telefono = null; // Permite que sea nulo o vacío
        }
    }

    public String getEmail() {
        return email;
    }

    /**
     * Valida y establece el Email. (NULL/Vacío permitido, formato básico de email)
     */
    public void setEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            String cleanEmail = email.trim();

            // Validación de formato: Patrón básico (mismo que CHK_Lectores_Email: x@y.z)

            if (!cleanEmail.matches(".*@.*\\..*")) {
                throw new IllegalArgumentException("El formato del Email es inválido (ej: correo@dominio.com).");
            }
            this.email = cleanEmail;
        } else {
            this.email = null; // Permite que sea nulo o vacío
        }
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {

        return this.nombre + " (DNI: " + this.dni + ")";
    }
}
