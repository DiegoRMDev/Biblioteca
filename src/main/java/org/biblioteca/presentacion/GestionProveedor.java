package org.biblioteca.presentacion;

import org.biblioteca.entities.Proveedor;
import org.biblioteca.services.ProveedorService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class GestionProveedor  extends JFrame{
    private JPanel mainPanel;
    private JScrollPane centralPanel;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JTable tablaProveedores;

    private DefaultTableModel modeloTabla;
    private ProveedorService proveedorService;
    public GestionProveedor() {
        this.proveedorService = new ProveedorService();

        setTitle("Gestión de Proveedores");
        setContentPane(mainPanel);
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        actualizarTabla();

        btnNuevo.addActionListener(e -> abrirDialogoEditor(null));
        btnEditar.addActionListener(e -> editarProveedorSeleccionado());
        btnEliminar.addActionListener(e -> eliminarProveedorSeleccionado());
    }



    public void actualizarTabla() {
        modeloTabla.setRowCount(0);
        List<Proveedor> proveedores = proveedorService.listarProveedores();
        for (Proveedor proveedor : proveedores) {
            modeloTabla.addRow(new Object[]{
                    proveedor.getProveedorID(),
                    proveedor.getNombre(),
                    proveedor.getDireccion(),
                    proveedor.getTelefono(),
                    proveedor.getEmail()
            });
        }
    }

    private void abrirDialogoEditor(Proveedor proveedor) {
        EditorProveedor dialogo = new EditorProveedor(this, proveedorService, proveedor);
        dialogo.setVisible(true);
        actualizarTabla();
    }

    private void editarProveedorSeleccionado() {
        int fila = tablaProveedores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor para editar.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int proveedorId = (int) modeloTabla.getValueAt(fila, 0);
        Proveedor proveedorAEditar = proveedorService.buscarProveedorPorId(proveedorId);
        abrirDialogoEditor(proveedorAEditar);
    }

    private void eliminarProveedorSeleccionado() {
        int fila = tablaProveedores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor para eliminar.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este proveedor?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int proveedorId = (int) modeloTabla.getValueAt(fila, 0);
            proveedorService.eliminarProveedor(proveedorId);
            actualizarTabla();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GestionProveedor().setVisible(true));
    }

    private void createUIComponents() {
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombre", "Dirección", "Teléfono", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProveedores = new JTable(modeloTabla);
    }
}
