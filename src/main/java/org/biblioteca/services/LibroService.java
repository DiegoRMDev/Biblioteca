package org.biblioteca.services;

import org.biblioteca.dao.LibroDAO;
import org.biblioteca.dao.LibroDAOImpl;
import org.biblioteca.entities.Autor;
import org.biblioteca.entities.Libro;

import java.util.List;

public class LibroService {
    private LibroDAO libroDAO;
    public LibroService() {
        this.libroDAO = new LibroDAOImpl();
    }

    public void registrarLibro(Libro libro, List<Autor> autores) throws IllegalArgumentException {
        // Las validaciones de los campos del libro ya están en tu clase Libro
        libroDAO.insertar(libro, autores);
    }

    public void modificarLibro(Libro libro, List<Autor> autores) throws IllegalArgumentException {
        // Las validaciones también se aplican aquí
        libroDAO.actualizar(libro, autores);
    }

    public void eliminarLibro(int id) {
        libroDAO.eliminar(id);
    }

    public Libro buscarLibroPorId(int id) {
        return libroDAO.obtenerPorId(id);
    }

    public List<Libro> listarLibros() {
        return libroDAO.obtenerTodos();
    }
}
