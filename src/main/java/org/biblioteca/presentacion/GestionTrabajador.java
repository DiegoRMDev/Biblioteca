package org.biblioteca.presentacion;

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
    private TrabajadorService trabajadorService;

    public GestionTrabajador() {
        this.trabajadorService = new TrabajadorService();


        // 1. Configuración de Layout
        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);

        // 2. Aplicar permisos CRUD (Deshabilitar si no es Admin)
        aplicarPermisosCRUD();

        // 3. Configurar Listeners
        btnNuevo.addActionListener(e -> abrirDialogoEditor(null));
        btnEditar.addActionListener(e -> editarTrabajadorSeleccionado());
        btnEliminar.addActionListener(e -> eliminarTrabajadorSeleccionado());

        // 4. Cargar datos
        actualizarTabla();
    }

    private void aplicarPermisosCRUD() {
        // Solo el Administrador puede crear, editar o eliminar trabajadores
        if (!SessionManager.esAdministrador()) {
            btnNuevo.setEnabled(false);
            btnEditar.setEnabled(false);
            btnEliminar.setEnabled(false);
        }
    }

    public void actualizarTabla() {
        modeloTabla.setRowCount(0); // Limpiar la tabla
        List<Trabajador> trabajadores = trabajadorService.listarTrabajadores();
        for (Trabajador t : trabajadores) {
            modeloTabla.addRow(new Object[]{
                    t.getTrabajadorID(),
                    t.getNombre(),
                    t.getUsuarioLogin(),
                    t.getRolID(),
                    t.getFechaRegistro()
            });
        }
    }

    private void abrirDialogoEditor(Trabajador trabajador) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // El diálogo necesita el servicio de Trabajador y el servicio de Rol (para el ComboBox)
        EditorTrabajador dialogo = new EditorTrabajador(parentFrame, trabajadorService, new RolService(), trabajador);
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


        Trabajador trabajadorAEditar = trabajadorService.obtenerPorId(trabajadorID);

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
            trabajadorService.eliminarTrabajador(trabajadorID);
            actualizarTabla();
        }
    }

    private void createUIComponents() {
        // 1. Inicializamos el modelo (la cabecera)
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombre", "Usuario Login", "Rol ID", "Fecha Registro"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        // 2. Creamos la JTable y le asignamos el modelo
        tablaTrabajadores = new JTable(modeloTabla);
    }

}
