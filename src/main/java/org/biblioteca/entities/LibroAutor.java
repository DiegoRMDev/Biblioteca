package org.biblioteca.entities;

public class LibroAutor {

    private int libroID;
    private int autorID;
    private short ordenAutor;

    public LibroAutor() {
    }

    // Constructor ÚNICO para cargar o registrar la relación (DEBE usar setters para validar)
    public LibroAutor(int libroID, int autorID, short ordenAutor) {
        // Al registrar, usamos los setters para activar las validaciones integradas
        this.setLibroID(libroID);
        this.setAutorID(autorID);
        this.setOrdenAutor(ordenAutor);
    }

    public int getLibroID() {
        return libroID;
    }

    /**
     * Valida y establece el ID del Libro. (NOT NULL, ID positivo)
     */
    public void setLibroID(int libroID) {
        if (libroID <= 0) {
            throw new IllegalArgumentException("El ID del Libro es inválido. Debe ser un valor positivo.");
        }
        this.libroID = libroID;
    }

    public int getAutorID() {
        return autorID;
    }

    /**
     * Valida y establece el ID del Autor. (NOT NULL, ID positivo)
     */
    public void setAutorID(int autorID) {
        if (autorID <= 0) {
            throw new IllegalArgumentException("El ID del Autor es inválido. Debe ser un valor positivo.");
        }
        this.autorID = autorID;
    }

    public short getOrdenAutor() {
        return ordenAutor;
    }

    /**
     * Valida y establece el orden del Autor. (Valor no negativo)
     */
    public void setOrdenAutor(short ordenAutor) {

        if (ordenAutor < 1) {
            throw new IllegalArgumentException("El Orden del Autor debe ser 1 o superior.");
        }
        this.ordenAutor = ordenAutor;
    }
}
