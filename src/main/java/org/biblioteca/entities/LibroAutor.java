package org.biblioteca.entities;

public class LibroAutor {

    private int libroID;
    private int autorID;
    private short ordenAutor;

    public LibroAutor() {
    }

    // Constructor ÚNICO para cargar o registrar la relación
    public LibroAutor(int libroID, int autorID, short ordenAutor) {
        this.libroID = libroID;
        this.autorID = autorID;
        this.ordenAutor = ordenAutor;
    }

    public int getLibroID() {
        return libroID;
    }

    public void setLibroID(int libroID) {
        this.libroID = libroID;
    }

    public int getAutorID() {
        return autorID;
    }

    public void setAutorID(int autorID) {
        this.autorID = autorID;
    }

    public short getOrdenAutor() {
        return ordenAutor;
    }

    public void setOrdenAutor(short ordenAutor) {
        this.ordenAutor = ordenAutor;
    }
}
