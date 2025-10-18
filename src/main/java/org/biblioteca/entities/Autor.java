package org.biblioteca.entities;

import java.util.List;

public class Autor {

    private int autorID;
    private String nombre;
    private String apellido;
    private String nacionalidad;

    // Nuevo campo para la relación N:M
    private List<LibroAutor> librosAsociados;


    public Autor() {
    }

    // Constructor para CARGAR datos desde la BD
    public Autor(int autorID, String nombre, String apellido, String nacionalidad) {
        this.autorID = autorID;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nacionalidad = nacionalidad;
    }

    // Constructor para REGISTRAR un nuevo autor
    public Autor(String nombre, String apellido, String nacionalidad) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.nacionalidad = nacionalidad;
    }

    public int getAutorID() {
        return autorID;
    }

    public void setAutorID(int autorID) {
        this.autorID = autorID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public List<LibroAutor> getLibrosAsociados() {
        return librosAsociados;
    }

    public void setLibrosAsociados(List<LibroAutor> librosAsociados) {
        this.librosAsociados = librosAsociados;
    }
}
