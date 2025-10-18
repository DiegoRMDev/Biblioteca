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

    // Constructor para REGISTRAR un nuevo lector (sin ID ni fecha)
    public Lector(String dni, String nombre, String direccion, String telefono, String email) {
        this.dni = dni;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
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

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
