package org.biblioteca.dao;
import org.biblioteca.entities.MovimientoLibro;

import java.util.List;

public interface MovimientoLibroDAO {

    void insertar(MovimientoLibro movimiento);
    List<MovimientoLibro> obtenerTodosConDetalles();
    List<MovimientoLibro> obtenerPorFiltros(String tituloLibro, String tipoMovimiento, String nombreTrabajador);

}
