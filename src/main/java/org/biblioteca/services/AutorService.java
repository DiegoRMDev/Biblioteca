package org.biblioteca.services;

import org.biblioteca.dao.AutorDAO;
import org.biblioteca.dao.AutorDAOImpl;
import org.biblioteca.entities.Autor;

import java.util.List;

public class AutorService {

    private AutorDAO autorDAO;


    public AutorService() {
        this.autorDAO = new AutorDAOImpl();
    }

    /**
     * Registra un nuevo autor, aprovechando las validaciones del modelo.
     * @throws IllegalArgumentException si los datos no son válidos.
     */
    public void registrarAutor(String nombre, String apellido, String nacionalidad) throws IllegalArgumentException {
        // Al usar este constructor, se activan tus validaciones en los setters
        Autor nuevoAutor = new Autor(nombre, apellido, nacionalidad);
        autorDAO.insertar(nuevoAutor);
    }

    /**
     * Modifica un autor existente, aprovechando las validaciones del modelo.
     * @throws IllegalArgumentException si los datos no son válidos.
     */
    public void modificarAutor(int id, String nombre, String apellido, String nacionalidad) throws IllegalArgumentException {
        // Creamos un objeto Autor y usamos los setters para validar los nuevos datos
        Autor autorExistente = new Autor();
        autorExistente.setAutorID(id);
        autorExistente.setNombre(nombre);
        autorExistente.setApellido(apellido);
        autorExistente.setNacionalidad(nacionalidad);

        autorDAO.actualizar(autorExistente);
    }

    public void eliminarAutor(int id) {
        autorDAO.eliminar(id);
    }

    public Autor buscarAutorPorId(int id) {
        return autorDAO.obtenerPorId(id);
    }

    public List<Autor> listarAutores() {
        return autorDAO.obtenerTodos();
    }
}
