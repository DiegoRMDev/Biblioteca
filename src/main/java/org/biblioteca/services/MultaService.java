package org.biblioteca.services;

import org.biblioteca.dao.MultaDAO;
import org.biblioteca.dao.MultaDAOImpl;
import org.biblioteca.entities.Multa;
import java.util.List;

public class MultaService {
    private MultaDAO multaDAO;

    public MultaService() {
        this.multaDAO = new MultaDAOImpl();
    }

    public List<Multa> listarMultas() {
        return multaDAO.obtenerTodas();
    }
}
