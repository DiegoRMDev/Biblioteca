package org.biblioteca.services;

import org.biblioteca.dao.*;
import org.biblioteca.entities.Autor;
import org.biblioteca.entities.Donante;
import org.biblioteca.entities.Libro;
import org.biblioteca.entities.MovimientoLibro;
import org.biblioteca.util.SessionManager;

import java.util.List;

public class LibroService {
    private LibroDAO libroDAO;
    private MovimientoLibroDAO movimientoDAO;
    private DonanteDAO donanteDAO;

    public LibroService() {
        this.libroDAO = new LibroDAOImpl();
        this.movimientoDAO = new MovimientoLibroDAOImpl();
        this.donanteDAO = new DonanteDAOImpl();
    }

    public void registrarLibro(Libro libro, List<Autor> autores, Integer proveedorID, Donante datosDonante) throws IllegalArgumentException {

        // 1. Validar ISBN (Existente)
        Libro libroExistente = libroDAO.obtenerPorIsbn(libro.getIsbn());
        if (libroExistente != null) {
            throw new IllegalArgumentException("El ISBN '" + libro.getIsbn() + "' ya está registrado.");
        }

        // Guardamos el stock real que ingresó el usuario
        int stockReal = libro.getStock();

        // ==============================================================================
        // 2. VALIDACIÓN ANTICIPADA (CORRECCIÓN)
        // Validamos Donante/Proveedor ANTES de guardar el libro en la BD.
        // ==============================================================================
        if (stockReal > 0) {
            boolean tieneProveedor = (proveedorID != null && proveedorID > 0);
            boolean tieneDatosDonante = (datosDonante != null && datosDonante.getNombre() != null && !datosDonante.getNombre().trim().isEmpty());

            if (!tieneProveedor && !tieneDatosDonante) {
                // Si lanzamos el error aquí, el código se detiene y NO se guarda el libro.
                throw new IllegalArgumentException("Para registrar stock inicial: Si no selecciona un proveedor, debe ingresar los datos del donante.");
            }
        }

        // 3. Insertamos el libro con Stock 0 (Solo si pasó la validación anterior)
        libro.setStock(0);
        libroDAO.insertar(libro, autores);

        // 4. Registrar el Movimiento de Stock (Si aplica)
        if (stockReal > 0) {
            try {
                int trabajadorID = SessionManager.getCurrentTrabajador().getTrabajadorID();
                String tipoMovimiento;
                String observacion;
                Integer donanteID = null;

                // Lógica de Negocio: Proveedor vs Donante
                if (proveedorID != null && proveedorID > 0) {
                    tipoMovimiento = "IngresoProveedor";
                    observacion = "Compra Inicial - Nuevo Libro";
                } else {
                    // ES UNA DONACIÓN (Ya validamos arriba que datosDonante no es null)

                    // Registramos al donante en este momento
                    donanteDAO.insertar(datosDonante);
                    donanteID = datosDonante.getDonanteID();

                    tipoMovimiento = "IngresoDonacion";
                    observacion = "Donación Inicial - " + datosDonante.getNombre();
                }

                MovimientoLibro movimiento = new MovimientoLibro(
                        libro.getLibroID(),
                        tipoMovimiento,
                        stockReal,
                        observacion,
                        proveedorID,
                        donanteID,
                        trabajadorID
                );

                movimientoDAO.insertar(movimiento);

            } catch (Exception e) {
                e.printStackTrace();
                // Opcional: Si falla el movimiento, podríamos borrar el libro manualmente para no dejar datos huérfanos.
                // libroDAO.eliminar(libro.getLibroID());
                throw new RuntimeException("El libro se guardó, pero hubo un error al registrar el movimiento: " + e.getMessage());
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
