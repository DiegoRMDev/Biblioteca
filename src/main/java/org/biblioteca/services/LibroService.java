package org.biblioteca.services;

import org.biblioteca.dao.LibroDAO;
import org.biblioteca.dao.LibroDAOImpl;
import org.biblioteca.dao.MovimientoLibroDAO;
import org.biblioteca.dao.MovimientoLibroDAOImpl;
import org.biblioteca.entities.Autor;
import org.biblioteca.entities.Libro;
import org.biblioteca.entities.MovimientoLibro;
import org.biblioteca.util.SessionManager;

import java.util.List;

public class LibroService {
    private LibroDAO libroDAO;
    private MovimientoLibroDAO movimientoDAO;
    public LibroService() {
        this.libroDAO = new LibroDAOImpl();
        this.movimientoDAO = new MovimientoLibroDAOImpl();
    }

    public void registrarLibro(Libro libro, List<Autor> autores, Integer proveedorID) throws IllegalArgumentException {
        // Las validaciones de los campos del libro ya están en tu clase Libro
        Libro libroExistente = libroDAO.obtenerPorIsbn(libro.getIsbn());
        if (libroExistente != null) {
            throw new IllegalArgumentException("El ISBN '" + libro.getIsbn() + "' ya está registrado en otro libro.");
        }

        // --- CORRECCIÓN PARA EVITAR DOBLE STOCK ---
        // 1. Guardamos el stock real que ingresó el usuario en una variable temporal
        int stockReal = libro.getStock();

        // 2. Insertamos el libro con Stock 0.
        // Así, cuando el trigger del movimiento se ejecute, sumará: 0 + 25 = 25 (Correcto)
        libro.setStock(0);
        libroDAO.insertar(libro, autores);

        // 3. Usamos la variable 'stockReal' para decidir si crear el movimiento
        if (stockReal > 0) {
            try {
                int trabajadorID = SessionManager.getCurrentTrabajador().getTrabajadorID();

                // --- LÓGICA DE DECISIÓN ---
                String tipoMovimiento;
                String observacion;

                if (proveedorID != null) {
                    tipoMovimiento = "IngresoProveedor";
                    observacion = "Compra Inicial - Nuevo Libro";
                } else {
                    tipoMovimiento = "IngresoDonacion";
                    observacion = "Donación / Inicial - Nuevo Libro";
                }
                // --------------------------

                MovimientoLibro movimiento = new MovimientoLibro(
                        libro.getLibroID(),
                        tipoMovimiento,
                        stockReal, // 4. Usamos el 'stockReal' para el movimiento
                        observacion,
                        proveedorID,
                        trabajadorID
                );

                movimientoDAO.insertar(movimiento);
                // Aquí se dispara el Trigger en la BD y actualiza el stock de 0 a 'stockReal'

            } catch (Exception e) {
                e.printStackTrace();
                // Opcional: Podrías revertir la inserción del libro si el movimiento falla
            }
        }
    }

    public void modificarLibro(Libro libro, List<Autor> autores) throws IllegalArgumentException {
        Libro libroExistente = libroDAO.obtenerPorIsbn(libro.getIsbn());

        if (libroExistente != null && libroExistente.getLibroID() != libro.getLibroID()) {
            throw new IllegalArgumentException("El ISBN '" + libro.getIsbn() + "' ya pertenece a otro libro.");
        }
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
