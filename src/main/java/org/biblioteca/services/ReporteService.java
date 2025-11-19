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
import java.util.Date;
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

            String nombreMes = primerDia.getMonth().getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("es", "ES"));
            String periodoTexto = "Período: " + nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1).toLowerCase() + " de " + anio;

            //  Preparar los parámetros que la consulta SQL espera
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("FECHA_INICIO", fechaInicio);
            parametros.put("FECHA_FIN", fechaFin);
            parametros.put("PERIODO_TEXTO", periodoTexto);

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

    public void generarReporteHistorialPrestamos(Timestamp fechaInicio, Timestamp fechaFin, String tipoEventoNombre) throws Exception {
        try {
            // 1. Cargar el recurso (Responsabilidad del Servicio/Capa superior)
            InputStream reporteStream = getClass().getResourceAsStream("/reportes/ReporteHistorialPrestamos.jasper");
            if (reporteStream == null) {
                throw new RuntimeException("No se pudo encontrar el archivo del reporte de Historial.");
            }

            // 2. Lógica de filtro y parámetros (Responsabilidad del Servicio)
            String tipoFiltroSQL;
            if (tipoEventoNombre.contains("Todos")) {
                tipoFiltroSQL = "A";
            } else if (tipoEventoNombre.contains("Pendientes")) {
                tipoFiltroSQL = "P";
            } else if (tipoEventoNombre.contains("Devueltos")) {
                tipoFiltroSQL = "D";
            } else if (tipoEventoNombre.contains("Retrasados")) {
                tipoFiltroSQL = "R";
            } else {
                tipoFiltroSQL = "A";
            }

            String periodoTexto = "Periodo de: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(fechaInicio) +
                    " a " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date(fechaFin.getTime() - 1000));

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("FECHA_INICIO", fechaInicio);
            parametros.put("FECHA_FIN", fechaFin);
            parametros.put("TIPO_EVENTO", tipoFiltroSQL);
            parametros.put("PERIODO_TEXTO", periodoTexto);

            // 🚨 3. Llamar al DAO para obtener el JasperPrint 🚨
            JasperPrint jasperPrint = reporteDAO.generarHistorialPrestamos(reporteStream, parametros);

            // 4. Mostrar el reporte (Responsabilidad del Servicio/UI)
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setTitle("Reporte: Historial de Préstamos y Devoluciones");
            viewer.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error al generar el reporte Historial: " + e.getMessage());
        }
    }

    public void generarGraficoTopLibros(int mes, int anio) throws Exception {
        try {
            // 1. Obtener la conexión a la BD
            Connection conexion = DBConnection.getConnection();
            if (conexion == null) {
                throw new RuntimeException("No se pudo obtener la conexión a la base de datos.");
            }

            // 2. Cargar el archivo .jasper del gráfico (Asumimos que el archivo se llamará ReporteTopLibrosGrafico.jasper)
            InputStream reporteStream = getClass().getResourceAsStream("/reportes/ReporteTopLibrosGrafico.jasper");
            if (reporteStream == null) {
                throw new RuntimeException("No se pudo encontrar el archivo del gráfico. Asegúrate de que ReporteTopLibrosGrafico.jasper esté en 'resources/reportes'.");
            }

            // 3. Calcular las fechas de inicio y fin (Lógica de período)
            // FECHA_INICIO: Primer día de ese mes
            LocalDate primerDia = LocalDate.of(anio, mes, 1);
            Timestamp fechaInicio = Timestamp.valueOf(primerDia.atStartOfDay());

            // FECHA_FIN: Primer día del SIGUIENTE mes
            YearMonth yearMonth = YearMonth.of(anio, mes);
            LocalDate primerDiaSiguienteMes = yearMonth.atEndOfMonth().plusDays(1);
            Timestamp fechaFin = Timestamp.valueOf(primerDiaSiguienteMes.atStartOfDay());

            String nombreMes = primerDia.getMonth().getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("es", "ES"));
            String periodoTexto = "Período: " + nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1).toLowerCase() + " de " + anio;

            // 4. Preparar los parámetros
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("FECHA_INICIO", fechaInicio);
            parametros.put("FECHA_FIN", fechaFin);
            parametros.put("PERIODO_TEXTO", periodoTexto);

            // 5. Llenar el reporte (Generar el JasperPrint)
            JasperPrint jasperPrint = JasperFillManager.fillReport(reporteStream, parametros, conexion);

            // 6. Mostrar el visor del reporte
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setTitle("Gráfico: Libros más prestados (" + mes + "/" + anio + ")");
            viewer.setVisible(true);

        } catch (JRException e) {
            e.printStackTrace();
            throw new Exception("Error al generar el gráfico Jasper: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error inesperado: " + e.getMessage());
        }
    }
}
