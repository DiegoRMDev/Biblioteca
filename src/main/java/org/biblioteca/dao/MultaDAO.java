package org.biblioteca.dao;

import org.biblioteca.entities.Multa;
import java.util.List;

public interface MultaDAO {
    List<Multa> obtenerTodas();
    // Nuevos métodos para la gestión
    List<Multa> obtenerPorDniLector(String dni);
    void eliminar(int idMulta); // Usaremos eliminar para simular el "Pago"
    boolean tieneMultaPendiente(int lectorID);

}