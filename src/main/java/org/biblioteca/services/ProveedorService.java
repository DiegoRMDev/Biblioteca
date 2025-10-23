package org.biblioteca.services;

import org.biblioteca.dao.ProveedorDAO;
import org.biblioteca.dao.ProveedorDAOImpl;
import org.biblioteca.entities.Proveedor;

import java.util.List;

public class ProveedorService {

    private ProveedorDAO proveedorDAO;

    public ProveedorService() {
        this.proveedorDAO = new ProveedorDAOImpl();
    }

    public void registrarProveedor(String nombre, String direccion, String telefono, String email) throws IllegalArgumentException {

        Proveedor nuevoProveedor = new Proveedor(nombre, direccion, telefono, email);
        proveedorDAO.insertar(nuevoProveedor);
    }


    public void modificarProveedor(int proveedorID, String nombre, String direccion, String telefono, String email) throws IllegalArgumentException {

        Proveedor proveedor = new Proveedor();
        proveedor.setProveedorID(proveedorID);
        proveedor.setNombre(nombre);
        proveedor.setDireccion(direccion);
        proveedor.setTelefono(telefono);
        proveedor.setEmail(email);
        proveedorDAO.actualizar(proveedor);
    }

    public void eliminarProveedor(int id) {
        proveedorDAO.eliminar(id);
    }

    public Proveedor buscarProveedorPorId(int id) {
        return proveedorDAO.obtenerPorId(id);
    }

    public List<Proveedor> listarProveedores() {
        return proveedorDAO.obtenerTodos();
    }
}
