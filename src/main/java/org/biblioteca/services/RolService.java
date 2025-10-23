package org.biblioteca.services;

import org.biblioteca.dao.RolDAO;
import org.biblioteca.dao.RolDAOImpl;
import org.biblioteca.entities.Rol;

import java.util.List;

public class RolService {
    private final RolDAO rolDAO;


    public RolService() {
        this.rolDAO = new RolDAOImpl();
    }

    /**
     * Registra un nuevo rol.
     * @param nombreRol El nombre del rol.
     * @return El objeto Rol insertado con su ID autogenerado.
     */
    public Rol registrarRol(String nombreRol) throws IllegalArgumentException {

        Rol nuevoRol = new Rol(0, nombreRol);
        rolDAO.insertar(nuevoRol);

        return nuevoRol;
    }


    public Rol obtenerRolPorId(int id) {
        return rolDAO.obtenerPorId(id);
    }


    public Rol obtenerRolPorNombre(String nombre) {
        return rolDAO.obtenerPorNombre(nombre);
    }


    public List<Rol> listarRoles() {
        return rolDAO.obtenerTodos();
    }


}

