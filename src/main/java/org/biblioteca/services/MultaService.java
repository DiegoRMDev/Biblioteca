package org.biblioteca.services;

import org.biblioteca.dao.MultaDAO;
import org.biblioteca.dao.MultaDAOImpl;
import org.biblioteca.entities.Multa;
import org.biblioteca.entities.MultaResumen;

import java.util.List;

public class MultaService {
    private final MultaDAO multaDAO;

    public MultaService() {
        this.multaDAO = new MultaDAOImpl();
    }

    public void agregarMulta(Multa multa) throws Exception {
        // aquí podrías agregar validaciones (ej: verificar que prestamo existe)
        multaDAO.agregarMulta(multa);
    }

    public List<Multa> listarTodas() throws Exception {
        return multaDAO.listarTodas();
    }

    public List<Multa> listarPorPrestamo(int prestamoId) throws Exception {
        return multaDAO.listarMultasPorPrestamo(prestamoId);
    }

    public List<Multa> listarPorLector(int lectorId) throws Exception {
        return multaDAO.listarMultasPorLector(lectorId);
    }

    public List<MultaResumen> listarResumenLectores() throws Exception {
        return multaDAO.listarResumenPorLectores();
    }

    public Multa obtenerPorId(int id) throws Exception {
        return multaDAO.obtenerPorId(id);
    }
}

