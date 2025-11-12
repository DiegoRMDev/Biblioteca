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
import java.util.List;

public class GestionPrestamo extends JPanel {
    private JPanel mainPanel;
    private JScrollPane centralPanel;
    private JButton btnNuevoPrestamo;
    private JButton btnRegistrarDevol;
    private JTable tablaPrestamos;
    private JPanel panelInferio;
    private JButton btnVerDetalle;


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

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        List<Prestamo> prestamos = prestamoService.listarPrestamos();

        for (Prestamo p : prestamos) {
            modeloTabla.addRow(new Object[]{
                    p.getPrestamoID(),
                    p.getLectorNombre(),    // <-- Usamos el nombre del JOIN
                    p.getTrabajadorNombre(),// <-- Usamos el nombre del JOIN
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
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al registrar la devolución: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
