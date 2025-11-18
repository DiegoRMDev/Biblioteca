package org.biblioteca.dao;

public class MultaResumen {
    private int lectorID;
    private String nombre;
    private double multaTotal;

    public MultaResumen() {}

    public MultaResumen(int lectorID, String nombre, double multaTotal) {
        this.lectorID = lectorID;
        this.nombre = nombre;
        this.multaTotal = multaTotal;
    }

    public int getLectorID() { return lectorID; }
    public void setLectorID(int lectorID) { this.lectorID = lectorID; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getMultaTotal() { return multaTotal; }
    public void setMultaTotal(double multaTotal) { this.multaTotal = multaTotal; }
}

