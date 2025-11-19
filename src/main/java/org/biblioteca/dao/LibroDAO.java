package org.biblioteca.dao;

import org.biblioteca.entities.Autor;
import org.biblioteca.entities.Libro;

import java.util.List;

public interface LibroDAO {
    Libro obtenerPorId(int id);
    List<Libro> obtenerTodos();
    void insertar(Libro libro, List<Autor> autores);
    void actualizar(Libro libro, List<Autor> autores);
    void eliminar(int id);
    Libro obtenerPorIsbn(String isbn);

}
