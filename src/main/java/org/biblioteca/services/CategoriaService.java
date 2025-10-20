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
            System.out.println("Categoría registrada exitosamente.");
        } catch (IllegalArgumentException e) {
            // Captura los errores de validación de tu clase Categoria
            System.err.println("Error de validación: " + e.getMessage());
        }
    }


    public void modificarCategoria(int id, String nuevoNombre, String nuevaDescripcion) {
        try {
            Categoria categoriaExistente = new Categoria(id, nuevoNombre, nuevaDescripcion);
            // Re-usamos los setters para validar la nueva información
            categoriaExistente.setNombre(nuevoNombre);
            categoriaExistente.setDescripcion(nuevaDescripcion);
            categoriaDAO.actualizar(categoriaExistente);
            System.out.println("Categoría actualizada exitosamente.");
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación: " + e.getMessage());
        }
    }

    public void eliminarCategoria(int id) {
        categoriaDAO.eliminar(id);
        System.out.println("Categoría eliminada.");
    }

    public Categoria buscarCategoriaPorId(int id) {
        return categoriaDAO.obtenerPorId(id);
    }

    public List<Categoria> listarCategorias() {
        return categoriaDAO.obtenerTodos();
    }
}
