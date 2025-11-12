package org.biblioteca.entities;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;

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


    private String lectorNombre;
    private String trabajadorNombre;

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

    // Constructor para REGISTRAR un nuevo préstamo (Debe usar setters para validar)
    public Prestamo(int lectorID, int trabajadorID, Timestamp fechaPrestamo, Timestamp fechaDevolucionPrevista) {
        // Asignamos el estado inicial por defecto
        final String ESTADO_INICIAL = "Activo";

        this.setLectorID(lectorID);
        this.setTrabajadorID(trabajadorID);
        this.setFechaPrestamo(fechaPrestamo);
        // La fecha de devolución prevista se valida aquí y en setFechaPrestamo
        this.setFechaDevolucionPrevista(fechaDevolucionPrevista);
        this.setEstado(ESTADO_INICIAL); // Establece el estado por defecto validado

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



    /**
     * Valida y establece el ID del Lector. (NOT NULL, ID positivo)
     */
    public void setLectorID(int lectorID) {
        if (lectorID <= 0) {
            throw new IllegalArgumentException("El ID del Lector es inválido. Debe ser un valor positivo.");
        }
        this.lectorID = lectorID;
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

    public Timestamp getFechaPrestamo() {
        return fechaPrestamo;
    }

    /**
     * Valida y establece la Fecha de Préstamo. (NOT NULL)
     * También verifica si existe una FechaDevolucionPrevista y si es posterior a esta.
     */
    public void setFechaPrestamo(Timestamp fechaPrestamo) {
        if (fechaPrestamo == null) {
            throw new IllegalArgumentException("La Fecha de Préstamo es obligatoria.");
        }

        // Regla de Negocio: La fecha de préstamo no debe ser futura
        if (fechaPrestamo.after(new Timestamp(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1)))) {
            throw new IllegalArgumentException("La Fecha de Préstamo no puede ser una fecha futura.");
        }

        this.fechaPrestamo = fechaPrestamo;

        // Validación cruzada: Si ya existe una fecha prevista, debe ser posterior a esta nueva fecha de préstamo
        if (this.fechaDevolucionPrevista != null && this.fechaDevolucionPrevista.before(fechaPrestamo)) {
            throw new IllegalArgumentException("La Fecha Prevista de Devolución debe ser posterior a la Fecha de Préstamo.");
        }
    }

    public Timestamp getFechaDevolucionPrevista() {
        return fechaDevolucionPrevista;
    }

    /**
     * Valida y establece la Fecha Prevista de Devolución. (NOT NULL y Lógica de Fechas)
     */
    public void setFechaDevolucionPrevista(Timestamp fechaDevolucionPrevista) {
        if (fechaDevolucionPrevista == null) {
            throw new IllegalArgumentException("La Fecha Prevista de Devolución es obligatoria.");
        }

        // Regla de Negocio: Debe ser posterior a la Fecha de Préstamo (si esta ya existe)
        if (this.fechaPrestamo != null && fechaDevolucionPrevista.before(this.fechaPrestamo)) {
            throw new IllegalArgumentException("La Fecha Prevista de Devolución debe ser posterior a la Fecha de Préstamo.");
        }

        this.fechaDevolucionPrevista = fechaDevolucionPrevista;
    }

    public Timestamp getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }

    /**
     * Establece la Fecha Real de Devolución. (Opcional, pero si se establece, debe ser válida)
     */
    public void setFechaDevolucionReal(Timestamp fechaDevolucionReal) {
        if (fechaDevolucionReal != null) {
            // Lógica de Negocio: La fecha real de devolución no puede ser anterior a la fecha de préstamo.
            if (this.fechaPrestamo != null && fechaDevolucionReal.before(this.fechaPrestamo)) {
                throw new IllegalArgumentException("La Fecha Real de Devolución no puede ser anterior a la Fecha de Préstamo.");
            }
        }
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    public String getEstado() {
        return estado;
    }

    /**
     * Valida y establece el Estado. (NOT NULL y Lógica de Negocio: solo valores permitidos)
     */
    public void setEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El Estado del Préstamo es obligatorio.");
        }

        String cleanEstado = estado.trim();

        // Regla de Negocio (replicando el CHECK constraint de la BD)
        // Antes: List.of("Activo", "Completado", "Vencido")
        // Ahora (Correcto):
        final List<String> ESTADOS_PERMITIDOS = List.of("Pendiente", "Devuelto", "Retrasado");

        if (!ESTADOS_PERMITIDOS.contains(cleanEstado)) {
            // Actualizamos también el mensaje de error
            throw new IllegalArgumentException("El Estado debe ser 'Pendiente', 'Devuelto' o 'Retrasado'.");
        }

        this.estado = cleanEstado;
    }

    public List<PrestamoDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<PrestamoDetalle> detalles) { this.detalles = detalles; }

    public String getLectorNombre() { return lectorNombre; }
    public void setLectorNombre(String lectorNombre) { this.lectorNombre = lectorNombre; }

    public String getTrabajadorNombre() { return trabajadorNombre; }
    public void setTrabajadorNombre(String trabajadorNombre) { this.trabajadorNombre = trabajadorNombre; }
}
