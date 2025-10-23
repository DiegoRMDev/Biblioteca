package org.biblioteca.presentacion;

import org.biblioteca.entities.Lector;
import org.biblioteca.services.LectorService;

import javax.swing.*;

public class EditorLector extends  JDialog {
    private JPanel mainPanel;
    private JPanel centralPanel;
    private JTextField txtNombres;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JTextField txtDni;
    private JButton btnGuardar;
    private JButton btnCancelar;

    private LectorService lectorService;
    private Lector lectorAEditar;

    public EditorLector(JFrame parent, LectorService service, Lector lector) {
        super(parent, true);
        this.lectorService = service;
        this.lectorAEditar = lector;

        setTitle(lector == null ? "Nuevo Lector" : "Editar Lector");
        setContentPane(mainPanel);
        setSize(400, 300);
        setLocationRelativeTo(parent);

        if (lector != null) {
            poblarFormulario();
        }

        btnGuardar.addActionListener(e -> guardarLector());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void poblarFormulario() {
        txtDni.setText(lectorAEditar.getDni());
        txtNombres.setText(lectorAEditar.getNombre());
        txtDireccion.setText(lectorAEditar.getDireccion());
        txtTelefono.setText(lectorAEditar.getTelefono());
        txtEmail.setText(lectorAEditar.getEmail());
    }

    private void guardarLector() {

        String dni = txtDni.getText();
        String nombre = txtNombres.getText();
        String direccion = txtDireccion.getText();
        String telefono = txtTelefono.getText();
        String email = txtEmail.getText();

        if (direccion.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "La dirección del Lector es obligatorio.",
                    "Campo Vacío",
                    JOptionPane.WARNING_MESSAGE);
            txtDireccion.requestFocus();
            return;
        }
        if (telefono.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El telefono del Lector es obligatorio.",
                    "Campo Vacío",
                    JOptionPane.WARNING_MESSAGE);
            txtTelefono.requestFocus();
            return;
        }

        try {

            if (lectorAEditar == null) {
                lectorService.registrarLector(dni, nombre, direccion, telefono, email);
            } else {
                lectorService.modificarLector(lectorAEditar.getLectorID(), dni, nombre, direccion, telefono, email);
            }
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
        }
    }
}
