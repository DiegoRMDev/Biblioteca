package org.biblioteca.presentacion;

import org.biblioteca.entities.Multa;
import org.biblioteca.entities.Prestamo;
import org.biblioteca.services.MultaService;
import org.biblioteca.services.PrestamoService;
import org.biblioteca.services.ReporteService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardReportes extends JPanel {
    private JTable tablaTopLibros;
    private JTable tablaTopLectores;
    private JTable tablaHistorialPrestamos;
    private JTable tablaMultas;
    private JPanel mainPanel;

    // Modelos para las 4 tablas
    private DefaultTableModel modeloTopLibros;
    private DefaultTableModel modeloTopLectores;
    private DefaultTableModel modeloHistorial;
    private DefaultTableModel modeloMultas;

    // Servicios
    private ReporteService reporteService;
    private PrestamoService prestamoService;
    private MultaService multaService;

    public DashboardReportes() {
        this.reporteService = new ReporteService();
        this.prestamoService = new PrestamoService();
        this.multaService = new MultaService();

        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);

        // Cargar todos los datos al iniciar
        cargarDatosDashboard();
    }

    private void createUIComponents() {
        // Inicializar los modelos de las 4 tablas

        // Tabla 1: Top Libros
        modeloTopLibros = new DefaultTableModel(new Object[]{"Libro", "Préstamos"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaTopLibros = new JTable(modeloTopLibros);

        // Tabla 2: Top Lectores
        modeloTopLectores = new DefaultTableModel(new Object[]{"Lector", "Préstamos"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaTopLectores = new JTable(modeloTopLectores);

        // Tabla 3: Historial Préstamos
        modeloHistorial = new DefaultTableModel(new Object[]{"ID", "Lector", "Fecha", "Estado"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaHistorialPrestamos = new JTable(modeloHistorial);

        // Tabla 4: Multas
        modeloMultas = new DefaultTableModel(new Object[]{"ID Multa", "ID Préstamo", "Monto", "Fecha"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaMultas = new JTable(modeloMultas);
    }

    public void cargarDatosDashboard() {
        cargarTopLibros();
        cargarTopLectores();
        cargarHistorialPrestamos();
        cargarMultas();
    }

    private void cargarTopLibros() {
        modeloTopLibros.setRowCount(0);
        List<Map<String, Object>> topLibros = reporteService.getTopLibros(10);
        for (Map<String, Object> fila : topLibros) {
            modeloTopLibros.addRow(new Object[]{
                    fila.get("Titulo"),
                    fila.get("Total")
            });
        }
    }

    private void cargarTopLectores() {
        modeloTopLectores.setRowCount(0);
        List<Map<String, Object>> topLectores = reporteService.getTopLectores(10);
        for (Map<String, Object> fila : topLectores) {
            modeloTopLectores.addRow(new Object[]{
                    fila.get("Nombre"),
                    fila.get("Total")
            });
        }
    }

    private void cargarHistorialPrestamos() {
        modeloHistorial.setRowCount(0);
        // Usamos el servicio existente y tomamos solo los 20 más recientes
        List<Prestamo> historial = prestamoService.listarPrestamos().stream()
                .limit(10)
                .collect(Collectors.toList());

        for (Prestamo p : historial) {
            modeloHistorial.addRow(new Object[]{
                    p.getPrestamoID(),
                    p.getLectorNombre(),
                    p.getFechaPrestamo(),
                    p.getEstado()
            });
        }
    }

    private void cargarMultas() {
        modeloMultas.setRowCount(0);
        // Tomamos solo las 10 multas más recientes
        List<Multa> multas = multaService.listarMultas().stream()
                .limit(10)
                .collect(Collectors.toList());

        for (Multa m : multas) {
            modeloMultas.addRow(new Object[]{
                    m.getMultaID(),
                    m.getPrestamoID(),
                    m.getMonto(),
                    m.getFechaRegistro()
            });
        }
    }
}
