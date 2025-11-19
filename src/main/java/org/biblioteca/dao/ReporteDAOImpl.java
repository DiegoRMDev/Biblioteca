package org.biblioteca.dao;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.biblioteca.util.DBConnection;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReporteDAOImpl implements ReporteDAO{
    private Connection conexion;

    public ReporteDAOImpl() {
        this.conexion = DBConnection.getConnection();
    }
    @Override
    public List<Map<String, Object>> obtenerTopLibrosPrestados(int limite) {
        List<Map<String, Object>> resultado = new ArrayList<>();
        String sql = "SELECT TOP (?) l.Titulo, SUM(pd.Cantidad) AS TotalPrestado " +
                "FROM PrestamoDetalle pd " +
                "JOIN Libros l ON pd.LibroID = l.LibroID " +
                "GROUP BY l.Titulo " +
                "ORDER BY TotalPrestado DESC";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("Titulo", rs.getString("Titulo"));
                    fila.put("Total", rs.getInt("TotalPrestado"));
                    resultado.add(fila);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    @Override
    public List<Map<String, Object>> obtenerTopLectoresActivos(int limite) {
        List<Map<String, Object>> resultado = new ArrayList<>();
        String sql = "SELECT TOP (?) l.Nombre, COUNT(p.PrestamoID) AS TotalPrestamos " +
                "FROM Lectores l " +
                "JOIN Prestamos p ON l.LectorID = p.LectorID " +
                "GROUP BY l.Nombre " +
                "ORDER BY TotalPrestamos DESC";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("Nombre", rs.getString("Nombre"));
                    fila.put("Total", rs.getInt("TotalPrestamos"));
                    resultado.add(fila);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultado;
    }
    // MeTODO UNIFICADO
    @Override
    public JasperPrint generarReporte(InputStream reporteStream, Map<String, Object> parametros) throws Exception {
        Connection conexion = null;
        try {
            // Obtener la conexión a la BD a través de la clase de utilidad
            conexion = DBConnection.getConnection();
            if (conexion == null || conexion.isClosed()) {
                throw new RuntimeException("No se pudo obtener la conexión a la base de datos.");
            }

            // JasperReports utiliza esta conexión, el InputStream del reporte y los parámetros
            return JasperFillManager.fillReport(reporteStream, parametros, conexion);
        } catch (SQLException e) {
            // Manejo específico de errores de base de datos
            throw new SQLException("Error de DB al generar reporte: " + e.getMessage(), e);
        } catch (Exception e) {
            // Manejo de otros errores (ej. JRException)
            throw new Exception("Error al llenar el reporte: " + e.getMessage(), e);
        }
    }
}
