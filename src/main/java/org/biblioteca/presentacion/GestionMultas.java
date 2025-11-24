package org.biblioteca.presentacion;

import org.biblioteca.entities.Multa;
import org.biblioteca.entities.Prestamo;
import org.biblioteca.services.MultaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class GestionMultas extends JPanel {

    private JPanel mainPanel;
    private JTable tablaMultas;
    private JPanel panelInferior;
    private JButton btnVerDetalle;
    private JPanel panelFiltrados;
    private JTextField txtFiltroPrestamoID;
    private JTextField txtFiltroFechaInicio;
    private JTextField txtFiltroFechaFin;
    private JButton btnFiltrar;
    private JButton btnLimpiarFiltros;
    private JPanel panelFiltros;
    private JScrollPane centralPanel;




    private DefaultTableModel modeloTabla;
    private final MultaService multaService;

    public GestionMultas() {
        this.multaService = new MultaService();

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        configurarTabla();
        configurarEventos();
        cargarMultas();
    }

    /**
     * Configuración de la tabla (llamado por createUIComponents)
     */
    private void configurarTabla() {
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Préstamo", "Libro", "Días Retraso", "Monto", "Fecha Registro"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaMultas.setModel(modeloTabla);
        tablaMultas.setRowHeight(28);
        tablaMultas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    /**
     * Método autogenerado por IntelliJ para componentes personalizados.
     */
    private void createUIComponents() {
        tablaMultas = new JTable();
    }

    private void configurarEventos() {

        btnVerDetalle.addActionListener(e -> verDetalle());
        btnFiltrar.addActionListener(e -> aplicarFiltros());
        btnLimpiarFiltros.addActionListener(e -> limpiarFiltros());

    }

    /**
     * Carga las multas en la tabla desde el Servicio.
     */
    public void cargarMultas() {
        modeloTabla.setRowCount(0); // limpiar

        List<Multa> lista = multaService.listarMultas();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Multa m : lista) {
            modeloTabla.addRow(new Object[]{
                    m.getMultaID(),
                    m.getPrestamoID(),
                    m.getLibroID(),
                    m.getDiasRetraso(),
                    m.getMonto(),
                    m.getFechaRegistro() != null ? sdf.format(m.getFechaRegistro()) : ""
            });
        }
    }

    /**
     * Acción: Mostrar un cuadro con el detalle completo de la multa.
     */
    private void verDetalle() {
        int fila = tablaMultas.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una multa para ver los detalles.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idMulta = (int) modeloTabla.getValueAt(fila, 0);

        try {
            Multa multa = multaService.obtenerPorId(idMulta);

            if (multa == null) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró información de la multa.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Mostrar detalle en un JOptionPane
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String detalle =
                    "ID Multa: " + multa.getMultaID() +
                            "\nID Préstamo: " + multa.getPrestamoID() +
                            "\nID Libro: " + multa.getLibroID() +
                            "\nDías de Retraso: " + multa.getDiasRetraso() +
                            "\nMonto: " + multa.getMonto() +
                            "\nFecha Registro: " + (multa.getFechaRegistro() != null ? sdf.format(multa.getFechaRegistro()) : "-");

            JOptionPane.showMessageDialog(this, detalle, "Detalle de Multa", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener detalle: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aplicarFiltros() {

        String prestamoStr = txtFiltroPrestamoID.getText().trim();
        String fechaIniStr = txtFiltroFechaInicio.getText().trim();
        String fechaFinStr = txtFiltroFechaFin.getText().trim();

        Integer prestamoID = null;
        Timestamp tInicio = null;
        Timestamp tFin = null;

        try {
            // Filtrar por ID de préstamo
            if (!prestamoStr.isEmpty()) {
                prestamoID = Integer.parseInt(prestamoStr);
            }

            // Fecha desde
            if (!fechaIniStr.isEmpty()) {
                tInicio = Timestamp.valueOf(fechaIniStr + " 00:00:00");
            }

            // Fecha hasta
            if (!fechaFinStr.isEmpty()) {
                tFin = Timestamp.valueOf(fechaFinStr + " 23:59:59");
            }

            final Integer fPrestamoID = prestamoID;
            final Timestamp fInicio = tInicio;
            final Timestamp fFin = tFin;

            // Obtener todas las multas y luego filtrar en memoria
            List<Multa> todas = multaService.listarMultas();
            List<Multa> filtradas = todas.stream()
                    .filter(m -> (fPrestamoID == null || m.getPrestamoID() == fPrestamoID))
                    .filter(m -> (fInicio == null || m.getFechaRegistro().after(fInicio)))
                    .filter(m -> (fFin == null || m.getFechaRegistro().before(fFin)))
                    .toList();

            cargarMultasFiltradas(filtradas);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Verifique los valores ingresados",
                    "Error de filtros",
                    JOptionPane.WARNING_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void cargarMultasFiltradas(List<Multa> lista) {

        modeloTabla.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Multa m : lista) {
            modeloTabla.addRow(new Object[]{
                    m.getMultaID(),
                    m.getPrestamoID(),
                    m.getLibroID(),
                    m.getDiasRetraso(),
                    m.getMonto(),
                    m.getFechaRegistro() != null ? sdf.format(m.getFechaRegistro()) : ""
            });
        }
    }
    private void limpiarFiltros() {
        txtFiltroPrestamoID.setText("");
        txtFiltroFechaInicio.setText("");
        txtFiltroFechaFin.setText("");
        cargarMultas();
    }

}

