package org.biblioteca.dao;
import org.biblioteca.entities.Proveedor;
import java.util.List;

public interface ProveedorDAO {

    void insertar(Proveedor proveedor);
    void actualizar(Proveedor proveedor);
    void eliminar(int id);
    Proveedor obtenerPorId(int id);
    List<Proveedor> obtenerTodos();
}

