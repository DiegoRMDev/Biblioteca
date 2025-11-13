package org.biblioteca.dao;
import java.util.List;
import java.util.Map;

public interface ReporteDAO {

    // Devuelve una lista de Mapas. Cada Mapa tendrá "Titulo" (String) y "Total" (Integer)
    List<Map<String, Object>> obtenerTopLibrosPrestados(int limite);

    // Devuelve una lista de Mapas. Cada Mapa tendrá "Nombre" (String) y "Total" (Integer)
    List<Map<String, Object>> obtenerTopLectoresActivos(int limite);
}
