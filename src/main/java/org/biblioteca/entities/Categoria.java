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

    // Constructor para REGISTRAR una nueva categoría (Debe usar setters para validar)
    public Categoria(String nombre, String descripcion) {
        // Al registrar, usamos los setters para activar las validaciones integradas
        this.setNombre(nombre);
        this.setDescripcion(descripcion);
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

    /**
     * Valida y establece el nombre de la categoría. (NOT NULL y Límite Realista de 50)
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El Nombre de la Categoría es obligatorio.");
        }

        String nombreLimpio = nombre.trim();
        final int MAX_LENGHT = 50; // Límite realista, menor al NVARCHAR(100) de la BD

        if (nombreLimpio.length() > MAX_LENGHT) {
            throw new IllegalArgumentException("El Nombre no puede exceder los " + MAX_LENGHT + " caracteres.");
        }

        this.nombre = nombreLimpio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción de la categoría. (Opcional y Límite Realista de 150)
     */
    public void setDescripcion(String descripcion) {
        if (descripcion != null && !descripcion.trim().isEmpty()) {
            String descripcionLimpia = descripcion.trim();
            final int MAX_LENGHT = 150;

            if (descripcionLimpia.length() > MAX_LENGHT) {
                throw new IllegalArgumentException("La Descripción no puede exceder los " + MAX_LENGHT + " caracteres.");
            }
            this.descripcion = descripcionLimpia;
        } else {
            this.descripcion = null; // Permite nulo o vacío
        }
    }

    @Override
    public String toString() {
        return this.nombre;
    }
}
