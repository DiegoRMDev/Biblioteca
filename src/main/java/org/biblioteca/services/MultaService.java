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

    public List<Multa> buscarMultasPorDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            return listarMultas();
        }
        return multaDAO.obtenerPorDniLector(dni);
    }

    public void pagarMulta(int multaID) {
        // Aquí podrías agregar lógica de auditoría o caja si quisieras
        multaDAO.eliminar(multaID);
    }

}
