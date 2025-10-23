package org.biblioteca.dao;

import org.biblioteca.entities.Rol;

import java.util.List;

public interface RolDAO {

    void insertar(Rol rol);

    void actualizar(Rol rol);

    void eliminar(int id);

    Rol obtenerPorId(int id);

    List<Rol> obtenerTodos();

    Rol obtenerPorNombre(String nombre);
}
