package org.biblioteca.entities;

public class Rol {

    private int rolID;
    private String nombreRol;

    public Rol() {
    }

    public Rol(int rolID, String nombreRol) {
        this.rolID = rolID;
        this.setNombreRol(nombreRol);
    }

    public int getRolID() {
        return rolID;
    }

    public void setRolID(int rolID) {
        this.rolID = rolID;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    /**
     * Valida y establece el nombre del rol.
     * La validación integrada asegura que el nombre no sea nulo, vacío, ni exceda los 50 caracteres.
     */

    public void setNombreRol(String nombreRol) {

        if (nombreRol == null || nombreRol.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol es obligatorio.");
        }

        String nombreLimpio = nombreRol.trim();

        if (nombreLimpio.length() > 50) {
            throw new IllegalArgumentException("El nombre del rol no puede exceder los 50 caracteres.");
        }

        this.nombreRol = nombreLimpio;
    }
}
