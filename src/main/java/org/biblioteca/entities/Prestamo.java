package org.biblioteca.entities;

import java.sql.Timestamp;
import java.util.List;

public class Prestamo {

    private int prestamoID;
    private int lectorID;
    private int trabajadorID;
    private Timestamp fechaPrestamo;
    private Timestamp fechaDevolucionPrevista;
    private Timestamp fechaDevolucionReal;
    private String estado;

    // Campo para la relación con los detalles (PrestamoDetalle)
    private List<PrestamoDetalle> detalles;

    public Prestamo() {
    }

    // Constructor para CARGAR datos desde la BD
    public Prestamo(int prestamoID, int lectorID, int trabajadorID, Timestamp fechaPrestamo, Timestamp fechaDevolucionPrevista, Timestamp fechaDevolucionReal, String estado) {
        this.prestamoID = prestamoID;
        this.lectorID = lectorID;
        this.trabajadorID = trabajadorID;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionPrevista = fechaDevolucionPrevista;
        this.fechaDevolucionReal = fechaDevolucionReal;
        this.estado = estado;
    }

    // Constructor para REGISTRAR un nuevo préstamo
    public Prestamo(int lectorID, int trabajadorID, Timestamp fechaPrestamo, Timestamp fechaDevolucionPrevista) {
        this.lectorID = lectorID;
        this.trabajadorID = trabajadorID;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionPrevista = fechaDevolucionPrevista;
    }

    public int getPrestamoID() {
        return prestamoID;
    }

    public void setPrestamoID(int prestamoID) {
        this.prestamoID = prestamoID;
    }

    public int getLectorID() {
        return lectorID;
    }

    public void setLectorID(int lectorID) {
        this.lectorID = lectorID;
    }

    public int getTrabajadorID() {
        return trabajadorID;
    }

    public void setTrabajadorID(int trabajadorID) {
        this.trabajadorID = trabajadorID;
    }

    public Timestamp getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(Timestamp fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public Timestamp getFechaDevolucionPrevista() {
        return fechaDevolucionPrevista;
    }

    public void setFechaDevolucionPrevista(Timestamp fechaDevolucionPrevista) {
        this.fechaDevolucionPrevista = fechaDevolucionPrevista;
    }

    public Timestamp getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }

    public void setFechaDevolucionReal(Timestamp fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<PrestamoDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<PrestamoDetalle> detalles) {
        this.detalles = detalles;
    }
}
