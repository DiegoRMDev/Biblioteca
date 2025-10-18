package org.biblioteca.entities;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Multa {

    private int multaID;
    private int prestamoID;
    private int libroID;
    private int diasRetraso;
    private BigDecimal monto;
    private Timestamp fechaRegistro;

    public Multa() {
    }

    // Constructor para CARGAR datos desde la BD
    public Multa(int multaID, int prestamoID, int libroID, int diasRetraso, BigDecimal monto, Timestamp fechaRegistro) {
        this.multaID = multaID;
        this.prestamoID = prestamoID;
        this.libroID = libroID;
        this.diasRetraso = diasRetraso;
        this.monto = monto;
        this.fechaRegistro = fechaRegistro;
    }

    // Constructor para REGISTRAR una nueva multa
    public Multa(int prestamoID, int libroID, int diasRetraso, BigDecimal monto) {
        this.prestamoID = prestamoID;
        this.libroID = libroID;
        this.diasRetraso = diasRetraso;
        this.monto = monto;
    }

    public int getMultaID() {
        return multaID;
    }

    public void setMultaID(int multaID) {
        this.multaID = multaID;
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

    public int getDiasRetraso() {
        return diasRetraso;
    }

    public void setDiasRetraso(int diasRetraso) {
        this.diasRetraso = diasRetraso;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
