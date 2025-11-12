package org.biblioteca.presentacion;

import org.biblioteca.entities.Lector;
import org.biblioteca.exception.LectorConPrestamoException;
import org.biblioteca.services.LectorService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionLector extends JPanel{
    private JPanel mainPanel;
    private JTable tablaLectores;
    private JPanel butoPanel;
    private JScrollPane centralPanel;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;

    private DefaultTableModel modeloTabla;
    private LectorService lectorService;

    public GestionLector(){

        this.lectorService = new LectorService();

        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);


        actualizarTabla();

        btnNuevo.addActionListener(e -> abrirDialogoEditor(null));
        btnEditar.addActionListener(e -> editarLectorSeleccionado());
        btnEliminar.addActionListener(e -> eliminarLectorSeleccionado());

    }

    public void actualizarTabla() {
        modeloTabla.setRowCount(0);
        List<Lector> lectores = lectorService.listarLectores();
        for (Lector lector : lectores) {
            modeloTabla.addRow(new Object[]{
                    lector.getLectorID(),
                    lector.getDni(),
                    lector.getNombre(),
                    lector.getDireccion(),
                    lector.getTelefono(),
                    lector.getEmail()
            });
        }
    }

    private void abrirDialogoEditor(Lector lector) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        EditorLector dialogo = new EditorLector(parentFrame, lectorService, lector);
        dialogo.setVisible(true);
        actualizarTabla();

    }

    private void editarLectorSeleccionado() {
        int fila = tablaLectores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un lector para editar.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int lectorId = (int) modeloTabla.getValueAt(fila, 0);
        Lector lectorAEditar = lectorService.buscarLectorPorId(lectorId);
        abrirDialogoEditor(lectorAEditar);
    }
    private void eliminarLectorSeleccionado() {
        int fila = tablaLectores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un lector para eliminar.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar a este lector?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int lectorId = (int) modeloTabla.getValueAt(fila, 0);

            try {
                // Intenta la eliminación. Si tiene historial, el DAO lanzará la LectorConPrestamoException.
                lectorService.eliminarLector(lectorId);

                // Si tiene éxito
                actualizarTabla();
                JOptionPane.showMessageDialog(this, "Lector eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            } catch (LectorConPrestamoException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(), // Muestra el mensaje detallado
                        "Error de Eliminación",
                        JOptionPane.ERROR_MESSAGE);

            } catch (Exception ex) {
                // Captura cualquier otro error inesperado (ej. problema de conexión a la BD)
                JOptionPane.showMessageDialog(this,
                        "Ocurrió un error inesperado al intentar eliminar el lector.",
                        "Error del Sistema",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Imprime el error para depuración
            }
        }
    }



    private void createUIComponents() {
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "DNI", "Nombre", "Dirección","Teléfono", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaLectores = new JTable(modeloTabla);
    }
}
