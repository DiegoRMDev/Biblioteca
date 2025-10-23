package org.biblioteca.dao;
import org.biblioteca.entities.Lector;
import java.util.List;

public interface LectorDAO {

    void insertar(Lector lector);
    void actualizar(Lector lector);
    void eliminar(int id);
    Lector obtenerPorId(int id);
    List<Lector> obtenerTodos();
}
