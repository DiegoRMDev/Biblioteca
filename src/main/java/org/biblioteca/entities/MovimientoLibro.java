package org.biblioteca.entities;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

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

    // Constructor para REGISTRAR un nuevo movimiento (Debe usar setters para validar)
    public MovimientoLibro(int libroID, String tipoMovimiento, int cantidad, String observaciones, Integer proveedorID, int trabajadorID) {
        // Al registrar, usamos los setters
        this.setLibroID(libroID);
        this.setTipoMovimiento(tipoMovimiento);
        this.setCantidad(cantidad);
        this.setObservaciones(observaciones);
        this.setProveedorID(proveedorID);
        this.setTrabajadorID(trabajadorID);

        // La fecha de movimiento se establece automáticamente al crear el objeto
        this.setFechaMovimiento(Timestamp.from(Instant.now()));
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

    /**
     * Valida y establece el ID del Libro. (NOT NULL, ID positivo)
     */
    public void setLibroID(int libroID) {
        if (libroID <= 0) {
            throw new IllegalArgumentException("El ID del Libro es inválido. Debe ser un valor positivo.");
        }
        this.libroID = libroID;
    }

    public Timestamp getFechaMovimiento() {

        return fechaMovimiento;
    }

    /**
     * Valida y establece la Fecha de Movimiento. (NOT NULL, No futura)
     */
    public void setFechaMovimiento(Timestamp fechaMovimiento) {
        if (fechaMovimiento == null) {
            throw new IllegalArgumentException("La Fecha de Movimiento es obligatoria.");
        }

        // Verificamos que la fecha no sea posterior al momento actual.
        if (fechaMovimiento.after(Timestamp.from(Instant.now()))) {
            throw new IllegalArgumentException("La Fecha de Movimiento no puede ser una fecha futura.");
        }

        this.fechaMovimiento = fechaMovimiento;
    }

    public String getTipoMovimiento() {

        return tipoMovimiento;
    }

    /**
     * Valida y establece el Tipo de Movimiento. (NOT NULL y solo valores permitidos)
     */
    public void setTipoMovimiento(String tipoMovimiento) {
        if (tipoMovimiento == null || tipoMovimiento.trim().isEmpty()) {
            throw new IllegalArgumentException("El Tipo de Movimiento es obligatorio.");
        }

        String cleanTipo = tipoMovimiento.trim();

        // Regla de Negocio (replicando el CHECK constraint)
        final List<String> TIPOS_PERMITIDOS = List.of("Entrada", "Salida", "Ajuste");

        if (!TIPOS_PERMITIDOS.contains(cleanTipo)) {
            throw new IllegalArgumentException("El Tipo de Movimiento debe ser 'Entrada', 'Salida' o 'Ajuste'.");
        }

        this.tipoMovimiento = cleanTipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    /**
     * Valida y establece la Cantidad. (NOT NULL, Cantidad >= 1)
     */
    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La Cantidad de libros en el movimiento debe ser 1 o más.");
        }
        this.cantidad = cantidad;
    }

    public String getObservaciones() {

        return observaciones;
    }

    /**
     * Establece las Observaciones. (Opcional y Límite Realista de 200)
     */
    public void setObservaciones(String observaciones) {
        if (observaciones != null && !observaciones.trim().isEmpty()) {
            String cleanObs = observaciones.trim();
            final int MAX_LENGHT = 200; // Límite realista, menor al NVARCHAR(255) de la BD

            if (cleanObs.length() > MAX_LENGHT) {
                throw new IllegalArgumentException("Las Observaciones no pueden exceder los " + MAX_LENGHT + " caracteres.");
            }
            this.observaciones = cleanObs;
        } else {
            this.observaciones = null; // Permite nulo o vacío
        }
    }

    public Integer getProveedorID() {

        return proveedorID;
    }

    /**
     * Valida y establece el ID del Proveedor. (Opcional/NULLable, ID positivo si no es NULL)
     */
    public void setProveedorID(Integer proveedorID) {
        if (proveedorID != null && proveedorID <= 0) {
            throw new IllegalArgumentException("El ID del Proveedor es inválido. Debe ser un valor positivo o nulo.");
        }
        this.proveedorID = proveedorID;
    }

    public int getTrabajadorID() {

        return trabajadorID;
    }

    /**
     * Valida y establece el ID del Trabajador. (NOT NULL, ID positivo)
     */
    public void setTrabajadorID(int trabajadorID) {
        if (trabajadorID <= 0) {
            throw new IllegalArgumentException("El ID del Trabajador es inválido. Debe ser un valor positivo.");
        }
        this.trabajadorID = trabajadorID;
    }
}
