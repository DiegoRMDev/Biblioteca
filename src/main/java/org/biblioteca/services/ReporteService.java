package org.biblioteca.services;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import org.biblioteca.dao.ReporteDAO;
import org.biblioteca.dao.ReporteDAOImpl;
import org.biblioteca.util.DBConnection;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReporteService {
    private ReporteDAO reporteDAO;

    public ReporteService() {
        this.reporteDAO = new ReporteDAOImpl();
    }

    public List<Map<String, Object>> getTopLibros(int limite) {
        return reporteDAO.obtenerTopLibrosPrestados(limite);
    }

    public List<Map<String, Object>> getTopLectores(int limite) {
        return reporteDAO.obtenerTopLectoresActivos(limite);
    }


    public void generarReporteTopLibros(int mes, int anio) throws Exception {
        try {
            //  Obtener la conexión a la BD
            Connection conexion = DBConnection.getConnection();
            if (conexion == null) {
                throw new RuntimeException("No se pudo obtener la conexión a la base de datos.");
            }

            // Cargar el archivo .jasper desde la carpeta 'resources/reportes'
            InputStream reporteStream = getClass().getResourceAsStream("/reportes/ReporteTopLibros.jasper");
            if (reporteStream == null) {
                throw new RuntimeException("No se pudo encontrar el archivo del reporte. ¿Está en 'resources/reportes'?");
            }

            //  Calcular las fechas de inicio y fin (para los parámetros)

            // FECHA_INICIO: Primer día de ese mes (ej. 01/10/2024 00:00:00)
            LocalDate primerDia = LocalDate.of(anio, mes, 1);
            Timestamp fechaInicio = Timestamp.valueOf(primerDia.atStartOfDay());

            // FECHA_FIN: Primer día del SIGUIENTE mes (ej. 01/11/2024 00:00:00)
            YearMonth yearMonth = YearMonth.of(anio, mes);
            LocalDate primerDiaSiguienteMes = yearMonth.atEndOfMonth().plusDays(1);
            Timestamp fechaFin = Timestamp.valueOf(primerDiaSiguienteMes.atStartOfDay());

            //  Preparar los parámetros que la consulta SQL espera
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("FECHA_INICIO", fechaInicio);
            parametros.put("FECHA_FIN", fechaFin);

            //  Llenar el reporte
            // Jasper usará la conexión, los parámetros y la consulta SQL
            // interna del reporte para generar la vista.
            JasperPrint jasperPrint = JasperFillManager.fillReport(reporteStream, parametros, conexion);

            //  Mostrar el visor de reporte
            // (El 'false' es para que no cierre la aplicación entera al cerrar el visor)
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setTitle("Reporte: Libros más prestados (" + mes + "/" + anio + ")");
            viewer.setVisible(true);

        } catch (JRException e) {
            e.printStackTrace();
            throw new Exception("Error al generar el reporte Jasper: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error inesperado: " + e.getMessage());
        }
    }
}
