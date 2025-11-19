package org.biblioteca.presentacion;

import org.biblioteca.entities.Prestamo;
import org.biblioteca.services.LibroService;
import org.biblioteca.services.PrestamoService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class GestionPrestamo extends JPanel {
    private JPanel mainPanel;
    private JScrollPane centralPanel;
    private JButton btnNuevoPrestamo;
    private JButton btnRegistrarDevol;
    private JTable tablaPrestamos;
    private JPanel panelInferio;
    private JButton btnVerDetalle;
    private JPanel panelFiltrados;
    private JTextField txtFiltroDni;
    private JTextField txtFiltroFechaInicio;
    private JTextField txtFiltroFechaFin;
    private JButton btnFiltrar;
    private JButton btnLimpiarFiltros;


    private DefaultTableModel modeloTabla;
    private PrestamoService prestamoService;
    private LibroService libroService;
    public GestionPrestamo() {
        this.prestamoService = new PrestamoService();
        this.libroService = new LibroService();
        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER); // Asumiendo que mainPanel es tu panel raíz en el .form

        // Configurar botones
        btnNuevoPrestamo.addActionListener(e -> abrirEditor());
        btnRegistrarDevol.addActionListener(e -> registrarDevolucion());
        btnFiltrar.addActionListener(e -> aplicarFiltros());
        btnLimpiarFiltros.addActionListener(e -> limpiarFiltros());
        btnVerDetalle.addActionListener(this::abrirDialogoDetalle);

        actualizarTabla();
    }

    private void createUIComponents() {
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Lector", "Trabajador", "Fecha Préstamo", "Fecha Devolución", "Estado"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaPrestamos = new JTable(modeloTabla);
    }

    private void abrirDialogoDetalle(ActionEvent e) {
        int filaSeleccionada = tablaPrestamos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un préstamo de la tabla para ver su detalle.", "Acción Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtenemos el ID del Préstamo de la fila seleccionada
        int prestamoId = (int) modeloTabla.getValueAt(filaSeleccionada, 0);

        // Buscamos el objeto Préstamo completo
        // (Gracias a nuestro DAO, este objeto ya incluye la lista de sus detalles)
        Prestamo prestamo = prestamoService.buscarPrestamoPorId(prestamoId);

        if (prestamo == null) {
            JOptionPane.showMessageDialog(this, "Error: No se pudo encontrar el préstamo seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Creamos y mostramos el nuevo diálogo
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        DetallePrestamo dialogo = new DetallePrestamo(parentFrame, prestamo, libroService);
        dialogo.setVisible(true);
    }
    private void aplicarFiltros() {
        String dni = txtFiltroDni.getText().trim();
        String fechaIniStr = txtFiltroFechaInicio.getText().trim();
        String fechaFinStr = txtFiltroFechaFin.getText().trim();

        Timestamp tInicio = null;
        Timestamp tFin = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            // Parsear Fecha Inicio
            if (!fechaIniStr.isEmpty()) {
                LocalDate ldInicio = LocalDate.parse(fechaIniStr, formatter);
                tInicio = Timestamp.valueOf(ldInicio.atStartOfDay());
            }

            // Parsear Fecha Fin
            if (!fechaFinStr.isEmpty()) {
                LocalDate ldFin = LocalDate.parse(fechaFinStr, formatter);
                // Ajustamos al final del día para incluir préstamos de ese día completo
                tFin = Timestamp.valueOf(ldFin.atTime(23, 59, 59));
            }

            // Llamar al servicio
            List<Prestamo> prestamosFiltrados = prestamoService.filtrarPrestamos(
                    dni.isEmpty() ? null : dni,
                    tInicio,
                    tFin
            );

            actualizarTablaConDatos(prestamosFiltrados);

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha incorrecto. Use: yyyy-MM-dd (ej. 2025-10-30)",
                    "Error de Formato", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al filtrar: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void limpiarFiltros() {
        txtFiltroDni.setText("");
        txtFiltroFechaInicio.setText("");
        txtFiltroFechaFin.setText("");
        actualizarTabla(); // Recarga todos sin filtros
    }

    private void actualizarTabla() {
        // Carga por defecto (todos)
        List<Prestamo> prestamos = prestamoService.listarPrestamos();
        actualizarTablaConDatos(prestamos);
    }

    // Método auxiliar para llenar la JTable con una lista específica
    private void actualizarTablaConDatos(List<Prestamo> listaPrestamos) {
        modeloTabla.setRowCount(0);
        for (Prestamo p : listaPrestamos) {
            modeloTabla.addRow(new Object[]{
                    p.getPrestamoID(),
                    p.getLectorNombre(),
                    p.getTrabajadorNombre(),
                    p.getFechaPrestamo(),
                    p.getFechaDevolucionPrevista(),
                    p.getEstado()
            });
        }
    }

    private void abrirEditor() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        EditorPrestamo dialogo = new EditorPrestamo(parentFrame, prestamoService);
         dialogo.setVisible(true);
        actualizarTabla();

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        if (parentWindow instanceof VentanaPrincipal) {
            ((VentanaPrincipal) parentWindow).refrescarVista("Inicio");
        }
    }

    private void registrarDevolucion() {
        int fila = tablaPrestamos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un préstamo para marcar su devolución.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int prestamoId = (int) modeloTabla.getValueAt(fila, 0);
        String estadoActual = (String) modeloTabla.getValueAt(fila, 5);

        if (!estadoActual.equalsIgnoreCase("Pendiente") && !estadoActual.equalsIgnoreCase("Activo")) {
            JOptionPane.showMessageDialog(this, "Este préstamo ya fue procesado (Devuelto o Retrasado).", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Confirma la devolución de este préstamo (ID: " + prestamoId + ")?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Registramos la devolución con la fecha y hora actual
                prestamoService.registrarDevolucion(prestamoId, Timestamp.from(Instant.now()));
                JOptionPane.showMessageDialog(this, "Devolución registrada exitosamente. El stock ha sido actualizado.");
                actualizarTabla(); // La tabla mostrará el nuevo estado (Devuelto o Retrasado) y la multa si aplica

                Window parentWindow = SwingUtilities.getWindowAncestor(this);
                if (parentWindow instanceof VentanaPrincipal) {
                    ((VentanaPrincipal) parentWindow).refrescarVista("Inicio");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al registrar la devolución: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
