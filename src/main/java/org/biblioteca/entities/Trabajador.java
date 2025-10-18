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

    // Constructor para REGISTRAR un nuevo trabajador
    public Trabajador(String nombre, String usuarioLogin, String contrasena, int rolID) {
        this.nombre = nombre;
        this.usuarioLogin = usuarioLogin;
        this.contrasena = contrasena;
        this.rolID = rolID;
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

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuarioLogin() {
        return usuarioLogin;
    }

    public void setUsuarioLogin(String usuarioLogin) {
        this.usuarioLogin = usuarioLogin;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public int getRolID() {
        return rolID;
    }

    public void setRolID(int rolID) {
        this.rolID = rolID;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
