package org.biblioteca.presentacion;

import org.biblioteca.entities.Proveedor;
import org.biblioteca.services.ProveedorService;

import javax.swing.*;

public class EditorProveedor extends JDialog {
    private JPanel mainPanel;
    private JPanel centralPanel;
    private JTextField txtNombres;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JButton btnGuardar;
    private JButton btnCancelar;


    private ProveedorService proveedorService;
    private Proveedor proveedorAEditar;

    public EditorProveedor(JFrame parent, ProveedorService service, Proveedor proveedor) {
        super(parent, true);
        this.proveedorService = service;
        this.proveedorAEditar = proveedor;

        setTitle(proveedor == null ? "Nuevo Proveedor" : "Editar Proveedor");
        setContentPane(mainPanel);
        setSize(400, 300);
        setLocationRelativeTo(parent);

        if (proveedor != null) {
            poblarFormulario();
        }

        btnGuardar.addActionListener(e -> guardarProveedor());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void poblarFormulario() {
        txtNombres.setText(proveedorAEditar.getNombre());
        txtDireccion.setText(proveedorAEditar.getDireccion());
        txtTelefono.setText(proveedorAEditar.getTelefono());
        txtEmail.setText(proveedorAEditar.getEmail());
    }

    private void guardarProveedor() {
        // Recoger datos de la UI
        String nombre = txtNombres.getText();
        String direccion = txtDireccion.getText();
        String telefono = txtTelefono.getText();
        String email = txtEmail.getText();

        if (direccion.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "La dirección del Proveedor es obligatorio.",
                    "Campo Vacío",
                    JOptionPane.WARNING_MESSAGE);
            txtDireccion.requestFocus();
            return;
        }
        if (telefono.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El telefono del Proveedor es obligatorio.",
                    "Campo Vacío",
                    JOptionPane.WARNING_MESSAGE);
            txtDireccion.requestFocus();
            return;
        }

        if (email.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El email del Proveedor es obligatorio.",
                    "Campo Vacío",
                    JOptionPane.WARNING_MESSAGE);
            txtDireccion.requestFocus();
            return;
        }

        try {

            if (proveedorAEditar == null) {
                proveedorService.registrarProveedor(nombre, direccion, telefono, email);
            } else {
                proveedorService.modificarProveedor(proveedorAEditar.getProveedorID(), nombre, direccion, telefono, email);
            }
            dispose();
        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
        }
    }
}
