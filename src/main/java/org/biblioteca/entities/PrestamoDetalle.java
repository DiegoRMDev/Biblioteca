package org.biblioteca.entities;

public class PrestamoDetalle {

    private int detalleID;
    private int prestamoID;
    private int libroID;
    private int cantidad;

    public PrestamoDetalle() {
    }

    // Constructor para CARGAR datos desde la BD
    public PrestamoDetalle(int detalleID, int prestamoID, int libroID, int cantidad) {
        this.detalleID = detalleID;
        this.prestamoID = prestamoID;
        this.libroID = libroID;
        this.cantidad = cantidad;
    }

    // Constructor para REGISTRAR un nuevo detalle
    public PrestamoDetalle(int prestamoID, int libroID, int cantidad) {
        this.prestamoID = prestamoID;
        this.libroID = libroID;
        this.cantidad = cantidad;
    }

    public int getDetalleID() {
        return detalleID;
    }

    public void setDetalleID(int detalleID) {
        this.detalleID = detalleID;
    }

    public int getPrestamoID() {
        return prestamoID;
    }

    public void setPrestamoID(int prestamoID) {
        this.prestamoID = prestamoID;
    }

    public int getLibroID() {
        return libroID;
    }

    public void setLibroID(int libroID) {
        this.libroID = libroID;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
