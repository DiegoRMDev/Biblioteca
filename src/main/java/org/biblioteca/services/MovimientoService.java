package org.biblioteca.services;

import org.biblioteca.dao.MovimientoLibroDAO;
import org.biblioteca.dao.MovimientoLibroDAOImpl;
import org.biblioteca.entities.MovimientoLibro;

import java.util.List;

public class MovimientoService {
    private final MovimientoLibroDAO movimientoDAO;

    public MovimientoService() {
        this.movimientoDAO = new MovimientoLibroDAOImpl();
    }

    public List<MovimientoLibro> listarHistorialCompleto() {
        return movimientoDAO.obtenerTodosConDetalles();
    }



    public java.util.List<MovimientoLibro> filtrarMovimientos(String libro, String tipo, String trabajador) {
        return movimientoDAO.obtenerPorFiltros(libro, tipo, trabajador);
    }
}
