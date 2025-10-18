package org.biblioteca.entities;

public class Categoria {

    private int categoriaID;
    private String nombre;
    private String descripcion;

    public Categoria() {
    }

    // Constructor para CARGAR datos desde la BD
    public Categoria(int categoriaID, String nombre, String descripcion) {
        this.categoriaID = categoriaID;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Constructor para REGISTRAR una nueva categoría
    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getCategoriaID() {
        return categoriaID;
    }

    public void setCategoriaID(int categoriaID) {
        this.categoriaID = categoriaID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
