package org.biblioteca.dao;

import org.biblioteca.entities.Trabajador;

import java.util.List;

public interface TrabajadorDAO {

    void insertar(Trabajador trabajador);
    void actualizar(Trabajador trabajador);
    void eliminar(int id);
    Trabajador obtenerPorId(int id);
    List<Trabajador> obtenerTodos();
    Trabajador obtenerPorUsuarioLogin(String usuarioLogin);
    void actualizarContrasena(int trabajadorID, String nuevaContrasena);
}
