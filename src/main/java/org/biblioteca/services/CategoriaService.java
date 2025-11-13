package org.biblioteca.services;

import org.biblioteca.dao.CategoriaDAO;
import org.biblioteca.dao.CategoriaDAOImpl;
import org.biblioteca.entities.Categoria;

import java.util.List;

public class CategoriaService {

    private CategoriaDAO categoriaDAO;

    public CategoriaService() {

        this.categoriaDAO = new CategoriaDAOImpl();
    }

    public void registrarCategoria(String nombre, String descripcion) {

        try {
            Categoria nuevaCategoria = new Categoria(nombre, descripcion);
            categoriaDAO.insertar(nuevaCategoria);
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }


    public void modificarCategoria(int id, String nuevoNombre, String nuevaDescripcion) {
        try {
            Categoria categoriaExistente = new Categoria(id, nuevoNombre, nuevaDescripcion);
            // Re-usamos los setters para validar la nueva información
            categoriaExistente.setNombre(nuevoNombre);
            categoriaExistente.setDescripcion(nuevaDescripcion);
            categoriaDAO.actualizar(categoriaExistente);
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    public void eliminarCategoria(int id) {
        categoriaDAO.eliminar(id);
    }

    public Categoria buscarCategoriaPorId(int id) {
        return categoriaDAO.obtenerPorId(id);
    }

    public List<Categoria> listarCategorias() {
        return categoriaDAO.obtenerTodos();
    }
}
