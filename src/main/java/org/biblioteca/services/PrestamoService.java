package org.biblioteca.services;

import org.biblioteca.dao.*;
import org.biblioteca.entities.MovimientoLibro;
import org.biblioteca.entities.Prestamo;
import org.biblioteca.entities.PrestamoDetalle;
import org.biblioteca.util.SessionManager;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class PrestamoService {

    private PrestamoDAO prestamoDAO;
    private MovimientoLibroDAO movimientoDAO;
    private MultaDAO multaDAO;
    public PrestamoService() {
        this.prestamoDAO = new PrestamoDAOImpl();
        this.movimientoDAO = new MovimientoLibroDAOImpl();;
        this.multaDAO = new MultaDAOImp();
    }

    public void registrarPrestamo(Prestamo prestamo, List<PrestamoDetalle> detalles) throws Exception {

        if (prestamo.getLectorID() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un lector.");
        }

        //  REGLA DE NEGOCIO ---
        if (prestamoDAO.tienePrestamoPendiente(prestamo.getLectorID())) {
            throw new IllegalStateException("El lector ya tiene un préstamo PENDIENTE. Debe realizar la devolución antes de solicitar nuevos libros.");
        }
        // ------------------------------

        // Regla 2: Multas pendientes (NUEVA)
        if (multaDAO.tieneMultaPendiente(prestamo.getLectorID())) {
            throw new IllegalStateException("El lector tiene MULTAS pendientes de pago. No puede realizar nuevos préstamos hasta regularizar su situación.");
        }

        if (prestamo.getTrabajadorID() <= 0) {
            throw new IllegalArgumentException("Error de sesión, no se encontró al trabajador.");
        }
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("El préstamo debe tener al menos un libro.");
        }
        int trabajadorID = prestamo.getTrabajadorID();
        try {
            prestamoDAO.insertar(prestamo, detalles);
            for (PrestamoDetalle detalle : detalles) {
                MovimientoLibro movimiento = new MovimientoLibro(
                        detalle.getLibroID(),
                        "SalidaPrestamo", // Tipo Movimiento
                        detalle.getCantidad(),
                        "Préstamo ID: " + prestamo.getPrestamoID(), // Observación útil
                        null,
                        null,
                        trabajadorID
                );
                movimientoDAO.insertar(movimiento);
            }
        } catch (SQLException e) {
            // Capturamos el error de la BD (ej. el trigger de "sin stock")
            if (e.getMessage().contains("No hay suficiente stock")) {
                throw new Exception("No hay suficiente stock para uno de los libros seleccionados.");
            } else {
                throw new Exception("Error de base de datos al registrar el préstamo: " + e.getMessage());
            }
        }
    }

    public void registrarDevolucion(int prestamoID, Timestamp fechaDevolucion) throws Exception {
        // Ahora esto funciona gracias al DAO completo
        Prestamo prestamo = prestamoDAO.obtenerPorId(prestamoID);
        if (prestamo == null) {
            throw new Exception("No se encontró el préstamo con ID: " + prestamoID);
        }
        if (prestamo.getFechaDevolucionReal() != null) {
            throw new Exception("Este préstamo ya fue devuelto anteriormente.");
        }

        prestamo.setFechaDevolucionReal(fechaDevolucion);

        try {
            // El DAO solo necesita la fecha real, el trigger se encarga del resto
            prestamoDAO.actualizar(prestamo);
            if (prestamo.getDetalles() != null) {
                int trabajadorActualID = SessionManager.getCurrentTrabajador().getTrabajadorID();

                for (PrestamoDetalle detalle : prestamo.getDetalles()) {
                    MovimientoLibro movimiento = new MovimientoLibro(
                            detalle.getLibroID(),
                            "EntradaDevolucion", // Tipo Movimiento (vuelven al inventario)
                            detalle.getCantidad(),
                            "Devolución Préstamo ID: " + prestamoID,
                            null,
                            null,
                            trabajadorActualID
                    );
                    movimientoDAO.insertar(movimiento);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error de base de datos al registrar la devolución: " + e.getMessage());
        }
    }

    public List<Prestamo> listarPrestamos() {

        return prestamoDAO.obtenerTodos();
    }

    public Prestamo buscarPrestamoPorId(int id) {

        return prestamoDAO.obtenerPorId(id);
    }



    public List<Prestamo> filtrarPrestamos(String dni, Timestamp fechaInicio, Timestamp fechaFin) {

        return prestamoDAO.obtenerPorFiltros(dni, fechaInicio, fechaFin);
    }


}
