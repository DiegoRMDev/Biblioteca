package org.biblioteca.presentacion;

import org.biblioteca.controller.TrabajadorController;
import org.biblioteca.entities.Trabajador;
import org.biblioteca.services.RolService;
import org.biblioteca.services.TrabajadorService;
import org.biblioteca.util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

public class GestionTrabajador extends JPanel {
    private JPanel mainPanel;
    private JTable tablaTrabajadores;
    private JScrollPane centralPanel;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;

    private DefaultTableModel modeloTabla;
    private final TrabajadorController trabajadorController;
    private final RolService rolService;

    public GestionTrabajador() {
        TrabajadorService trabajadorService = new TrabajadorService();
        this.rolService = new RolService();
        this.trabajadorController = new TrabajadorController(trabajadorService, rolService);
        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);

        aplicarPermisosCRUD();

        btnNuevo.addActionListener(e -> abrirDialogoEditor(null));
        btnEditar.addActionListener(e -> editarTrabajadorSeleccionado());
        btnEliminar.addActionListener(e -> eliminarTrabajadorSeleccionado());

        actualizarTabla();
    }

    private void aplicarPermisosCRUD() {
        if (!SessionManager.esAdministrador()) {
            btnNuevo.setEnabled(false);
            btnEditar.setEnabled(false);
            btnEliminar.setEnabled(false);
        }
    }

    public void actualizarTabla() {
        modeloTabla.setRowCount(0);

        List<Trabajador> trabajadores = trabajadorController.listarTrabajadores();

        for (Trabajador t : trabajadores) {
            modeloTabla.addRow(new Object[]{
                    t.getTrabajadorID(),
                    t.getNombre(),
                    t.getApellido(),
                    t.getDni(),
                    t.getUsuarioLogin(),
                    t.getEmail(),
                    t.getRolID(),
                    t.getEstado(),
                    t.getFechaRegistro()
            });
        }
    }

    private void abrirDialogoEditor(Trabajador trabajador) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        EditorTrabajador dialogo = new EditorTrabajador(parentFrame, this.trabajadorController, this.rolService, trabajador);
        dialogo.setVisible(true);
        actualizarTabla();
    }

    private void editarTrabajadorSeleccionado() {
        int fila = tablaTrabajadores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un trabajador para editar.", "Acción Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int trabajadorID = (int) modeloTabla.getValueAt(fila, 0);

        Trabajador trabajadorAEditar = trabajadorController.obtenerPorId(trabajadorID);

        if (trabajadorAEditar != null) {
            abrirDialogoEditor(trabajadorAEditar);
        } else {
            JOptionPane.showMessageDialog(this, "Error: No se pudo encontrar el trabajador con ID: " + trabajadorID, "Error de Edición", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarTrabajadorSeleccionado() {
        int fila = tablaTrabajadores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un trabajador para eliminar.", "Acción Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este trabajador?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            int trabajadorID = (int) modeloTabla.getValueAt(fila, 0);
            try {
                trabajadorController.eliminarTrabajador(trabajadorID);
                actualizarTabla();
                JOptionPane.showMessageDialog(this, "Trabajador eliminado con éxito.", "Eliminación", JOptionPane.INFORMATION_MESSAGE);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: El trabajador podría tener registros asociados.", "Error de Persistencia", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void createUIComponents() {
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombre", "Apellido", "DNI", "Usuario Login", "Email", "Rol ID", "Estado", "Fecha Registro"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaTrabajadores = new JTable(modeloTabla);
    }

}
