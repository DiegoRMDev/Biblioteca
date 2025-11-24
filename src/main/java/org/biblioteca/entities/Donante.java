package org.biblioteca.entities;

public class Donante {
    private int donanteID;
    private String nombre;

    public Donante() {}

    public Donante(String nombre) {
        this.setNombre(nombre);
    }

    public Donante(int donanteID, String nombre) {
        this.donanteID = donanteID;
        this.nombre = nombre;
    }

    public int getDonanteID() { return donanteID; }
    public void setDonanteID(int donanteID) { this.donanteID = donanteID; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del donante es obligatorio.");
        }
        this.nombre = nombre.trim();
    }
}