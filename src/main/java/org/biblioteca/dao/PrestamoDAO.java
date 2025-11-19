package org.biblioteca.dao;

import org.biblioteca.entities.Prestamo;
import org.biblioteca.entities.PrestamoDetalle;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public interface PrestamoDAO {

    void insertar(Prestamo prestamo, List<PrestamoDetalle> detalles) throws SQLException;

    // Actualiza el préstamo (usado para marcar devoluciones)
    void actualizar(Prestamo prestamo) throws SQLException;

    // Lista todos los préstamos para la tabla principal
    List<Prestamo> obtenerTodos();

    // Obtiene un préstamo y sus detalles
    Prestamo obtenerPorId(int id);

    List<Prestamo> obtenerPorFiltros(String dni, Timestamp fechaInicio, Timestamp fechaFin);

}
