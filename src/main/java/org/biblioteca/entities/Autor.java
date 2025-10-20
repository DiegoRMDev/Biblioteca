package org.biblioteca.entities;

import java.util.List;

public class Autor {

    private int autorID;
    private String nombre;
    private String apellido;
    private String nacionalidad;

    // Nuevo campo para la relación N:M
    private List<Libro>  libros;


    public Autor() {
    }

    // Constructor para CARGAR datos desde la BD
    public Autor(int autorID, String nombre, String apellido, String nacionalidad) {
        this.autorID = autorID;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nacionalidad = nacionalidad;
    }

    // Constructor para REGISTRAR un nuevo autor (Debe usar setters para validar)
    public Autor(String nombre, String apellido, String nacionalidad) {
        // Al registrar, usamos los setters para activar las validaciones
        this.setNombre(nombre);
        this.setApellido(apellido);
        this.setNacionalidad(nacionalidad);
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

    /**
     * Valida y establece el nombre del autor. (NOT NULL y Límite Realista de 60)
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El Nombre del Autor es obligatorio.");
        }

        String nombreLimpio = nombre.trim();
        final int MAX_LENGHT = 60;

        if (nombreLimpio.length() > MAX_LENGHT) {
            throw new IllegalArgumentException("El Nombre no puede exceder los " + MAX_LENGHT + " caracteres.");
        }

        this.nombre = nombreLimpio;
    }

    public String getApellido() {
        return apellido;
    }

    /**
     * Valida y establece el apellido del autor. (NOT NULL y Límite Realista de 60)
     */
    public void setApellido(String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("El Apellido del Autor es obligatorio.");
        }

        String apellidoLimpio = apellido.trim();
        final int MAX_LENGHT = 60;

        if (apellidoLimpio.length() > MAX_LENGHT) {
            throw new IllegalArgumentException("El Apellido no puede exceder los " + MAX_LENGHT + " caracteres.");
        }

        this.apellido = apellidoLimpio;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    /**
     * Establece la nacionalidad. (Opcional y Límite Realista de 30)
     */
    public void setNacionalidad(String nacionalidad) {
        if (nacionalidad != null && !nacionalidad.trim().isEmpty()) {
            String nacionalidadLimpia = nacionalidad.trim();
            final int MAX_LENGHT = 30;

            if (nacionalidadLimpia.length() > MAX_LENGHT) {
                throw new IllegalArgumentException("La Nacionalidad no puede exceder los " + MAX_LENGHT + " caracteres.");
            }
            this.nacionalidad = nacionalidadLimpia;
        } else {
            this.nacionalidad = null;
        }
    }


    public List<Libro> getLibros() { return libros; }
    public void setLibros(List<Libro> libros) { this.libros = libros; }

    @Override
    public String toString() {
        return this.nombre + " " + this.apellido; // o getNombre() + " " + getApellido();
    }
}
