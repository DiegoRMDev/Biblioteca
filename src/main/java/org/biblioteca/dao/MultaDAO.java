package org.biblioteca.dao;

import org.biblioteca.entities.Multa;
import org.biblioteca.entities.MultaResumen;

import java.util.List;

public interface MultaDAO {
    void agregarMulta(Multa multa) throws Exception;
    List<Multa> listarTodas() throws Exception;
    List<Multa> listarMultasPorPrestamo(int prestamoId) throws Exception;
    List<Multa> listarMultasPorLector(int lectorId) throws Exception;
    List<MultaResumen> listarResumenPorLectores() throws Exception;
    Multa obtenerPorId(int multaId) throws Exception;
}
