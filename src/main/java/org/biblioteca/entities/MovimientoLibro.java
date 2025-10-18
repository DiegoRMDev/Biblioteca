package org.biblioteca.entities;

import java.sql.Timestamp;

public class MovimientoLibro {

    private int movimientoID;
    private int libroID;
    private Timestamp fechaMovimiento;
    private String tipoMovimiento;
    private int cantidad;
    private String observaciones;
    private Integer proveedorID; // Puede ser NULL en la BD
    private int trabajadorID;

    public MovimientoLibro() {
    }

    // Constructor para CARGAR datos desde la BD
    public MovimientoLibro(int movimientoID, int libroID, Timestamp fechaMovimiento, String tipoMovimiento, int cantidad, String observaciones, Integer proveedorID, int trabajadorID) {
        this.movimientoID = movimientoID;
        this.libroID = libroID;
        this.fechaMovimiento = fechaMovimiento;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
        this.proveedorID = proveedorID;
        this.trabajadorID = trabajadorID;
    }

    // Constructor para REGISTRAR un nuevo movimiento
    public MovimientoLibro(int libroID, String tipoMovimiento, int cantidad, String observaciones, Integer proveedorID, int trabajadorID) {
        this.libroID = libroID;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
        this.proveedorID = proveedorID;
        this.trabajadorID = trabajadorID;
    }

    public int getMovimientoID() {
        return movimientoID;
    }

    public void setMovimientoID(int movimientoID) {
        this.movimientoID = movimientoID;
    }

    public int getLibroID() {
        return libroID;
    }

    public void setLibroID(int libroID) {
        this.libroID = libroID;
    }

    public Timestamp getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Timestamp fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Integer getProveedorID() {
        return proveedorID;
    }

    public void setProveedorID(Integer proveedorID) {
        this.proveedorID = proveedorID;
    }

    public int getTrabajadorID() {
        return trabajadorID;
    }

    public void setTrabajadorID(int trabajadorID) {
        this.trabajadorID = trabajadorID;
    }
}
