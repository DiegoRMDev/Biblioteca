package org.biblioteca.entities;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

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

    // Constructor para REGISTRAR una nueva multa (Debe usar setters para validar)
    public Multa(int prestamoID, int libroID, int diasRetraso, BigDecimal monto) {
        // Al registrar, usamos los setters para activar las validaciones integradas
        this.setPrestamoID(prestamoID);
        this.setLibroID(libroID);
        this.setDiasRetraso(diasRetraso);
        this.setMonto(monto);

        // La fecha de registro se establece automáticamente al crear la multa
        this.setFechaRegistro(Timestamp.from(Instant.now()));
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

    /**
     * Valida y establece el ID del Préstamo. (NOT NULL, ID positivo)
     */
    public void setPrestamoID(int prestamoID) {
        if (prestamoID <= 0) {
            throw new IllegalArgumentException("El ID del Préstamo es inválido. Debe ser un valor positivo.");
        }
        this.prestamoID = prestamoID;
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

    public int getDiasRetraso() {
        return diasRetraso;
    }

    /**
     * Valida y establece los Días de Retraso. (NOT NULL, >= 1)
     */
    public void setDiasRetraso(int diasRetraso) {
        if (diasRetraso <= 0) {
            throw new IllegalArgumentException("Los Días de Retraso deben ser 1 o más.");
        }
        this.diasRetraso = diasRetraso;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    /**
     * Valida y establece el Monto de la Multa. (NOT NULL, > 0)
     */
    public void setMonto(BigDecimal monto) {
        if (monto == null) {
            throw new IllegalArgumentException("El Monto de la Multa es obligatorio.");
        }

        // El monto debe ser mayor que cero.
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El Monto de la Multa debe ser un valor positivo.");
        }

        this.monto = monto;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Valida y establece la Fecha de Registro. (NOT NULL, No futura)
     */
    public void setFechaRegistro(Timestamp fechaRegistro) {
        if (fechaRegistro == null) {
            throw new IllegalArgumentException("La Fecha de Registro es obligatoria.");
        }



        this.fechaRegistro = fechaRegistro;
    }
}
