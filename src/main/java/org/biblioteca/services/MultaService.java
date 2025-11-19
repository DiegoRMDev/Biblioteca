package org.biblioteca.services;

import org.biblioteca.dao.MultaDAO;
import org.biblioteca.dao.MultaDAOImp;
import org.biblioteca.entities.Multa;
import java.util.List;

public class MultaService {

    private final MultaDAO multaDAO;

    public MultaService() {
        this.multaDAO = new MultaDAOImp();
    }

    public List<Multa> listarMultas() {
        return multaDAO.obtenerTodas();
    }

    public Multa obtenerPorId(int id) throws Exception {
        return ((MultaDAOImp) multaDAO).obtenerPorId(id);
    }

}
