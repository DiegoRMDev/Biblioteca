package org.biblioteca.services;

import org.biblioteca.dao.LectorDAO;
import org.biblioteca.dao.LectorDAOImpl;
import org.biblioteca.entities.Lector;
import org.biblioteca.exception.LectorConPrestamoException;

import java.util.List;

public class LectorService {
    private LectorDAO lectorDAO;

    public LectorService() {
        this.lectorDAO = new LectorDAOImpl();
    }


    public void registrarLector(String dni, String nombre, String direccion, String telefono, String email) throws IllegalArgumentException {

        Lector nuevoLector = new Lector(dni, nombre, direccion, telefono, email);
        lectorDAO.insertar(nuevoLector);
    }

    public void modificarLector(int lectorID, String dni, String nombre, String direccion, String telefono, String email) throws IllegalArgumentException {

        Lector lector = new Lector();
        lector.setLectorID(lectorID);
        lector.setDni(dni);
        lector.setNombre(nombre);
        lector.setDireccion(direccion);
        lector.setTelefono(telefono);
        lector.setEmail(email);
        lectorDAO.actualizar(lector);
    }

    public void eliminarLector(int id) throws LectorConPrestamoException {
        try {
            lectorDAO.eliminar(id);
        } catch (LectorConPrestamoException e) {
            throw e;
        }
    }

    public Lector buscarLectorPorId(int id) {
        return lectorDAO.obtenerPorId(id);
    }

    public List<Lector> listarLectores() {
        return lectorDAO.obtenerTodos();
    }
}
