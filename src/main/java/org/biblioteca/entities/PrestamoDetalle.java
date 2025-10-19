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

    // Constructor para REGISTRAR un nuevo detalle (Debe usar setters para validar)
    public PrestamoDetalle(int prestamoID, int libroID, int cantidad) {
        // Al registrar, usamos los setters para activar las validaciones integradas
        this.setPrestamoID(prestamoID);
        this.setLibroID(libroID);
        this.setCantidad(cantidad);
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

    public int getCantidad() {
        return cantidad;
    }

    /**
     * Valida y establece la Cantidad de Libros. (NOT NULL, Cantidad >= 1)
     */
    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            // Un detalle de préstamo debe ser para al menos 1 unidad.
            throw new IllegalArgumentException("La Cantidad de libros prestados debe ser 1 o más.");
        }
        this.cantidad = cantidad;
    }
}
