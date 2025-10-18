package org.biblioteca.entities;

public class Rol {

    private int rolID;
    private String nombreRol;

    public Rol() {
    }

    public Rol(int rolID, String nombreRol) {
        this.rolID = rolID;
        this.nombreRol = nombreRol;
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

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }
}
