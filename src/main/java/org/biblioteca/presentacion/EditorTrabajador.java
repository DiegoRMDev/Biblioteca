package org.biblioteca.presentacion;

import org.biblioteca.entities.Rol;
import org.biblioteca.entities.Trabajador;
import org.biblioteca.services.RolService;
import org.biblioteca.services.TrabajadorService;
import org.biblioteca.controller.TrabajadorController;

import javax.swing.*;
import java.util.List;

public class EditorTrabajador extends JDialog {
    private JPanel mainPanel;
    private JPanel centralPanel;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JTextField txtNombre;
    private JTextField txtUsuarioLogin;
    private JPasswordField txtContrasena;
    private JComboBox<Rol> cboRol;
    private JTextField txtApellido;
    private JTextField txtDni;
    private JTextField txtEmail;
    private JTextField txtTelefono;
    private JComboBox<String> cboEstado;

    private final TrabajadorController trabajadorController;
    private final RolService rolService;
    private final Trabajador trabajadorAEditar;
    private final boolean esCreacion;


    public EditorTrabajador(JFrame parent, TrabajadorController trabajadorController, RolService rolService, Trabajador trabajador) {
        super(parent, true);

        this.trabajadorController = trabajadorController;
        this.rolService = rolService; // Mantenemos el RolService para cargar el combo de roles (lógica de UI)

        this.trabajadorAEditar = trabajador;
        this.esCreacion = (trabajador == null);

        setTitle(esCreacion ? "Registrar Nuevo Trabajador" : "Editar Trabajador");
        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(parent);

        cargarRoles();
        cargarEstados();

        if (!esCreacion) {
            poblarFormulario();
            txtUsuarioLogin.setEnabled(false);
            txtDni.setEnabled(false);
            txtContrasena.setText("");
        } else {
            txtContrasena.setText("");
        }

        btnGuardar.addActionListener(e -> guardarTrabajador());
        btnCancelar.addActionListener(e -> dispose());
    }


    private void cargarEstados() {
        DefaultComboBoxModel<String> estadoModel = new DefaultComboBoxModel<>();
        estadoModel.addElement("Activo");
        estadoModel.addElement("Inactivo");
        cboEstado.setModel(estadoModel);
    }

    private void cargarRoles() {
        DefaultComboBoxModel<Rol> rolModel = new DefaultComboBoxModel<>();
        List<Rol> roles = rolService.listarRoles();
        roles.forEach(rolModel::addElement);
        cboRol.setModel(rolModel);
    }

    private void poblarFormulario() {
        txtNombre.setText(trabajadorAEditar.getNombre());
        txtApellido.setText(trabajadorAEditar.getApellido());
        txtDni.setText(trabajadorAEditar.getDni());
        txtUsuarioLogin.setText(trabajadorAEditar.getUsuarioLogin());
        txtEmail.setText(trabajadorAEditar.getEmail());
        txtTelefono.setText(trabajadorAEditar.getTelefono());

        cboEstado.setSelectedItem(trabajadorAEditar.getEstado());

        for (int i = 0; i < cboRol.getItemCount(); i++) {
            Rol rol = cboRol.getItemAt(i);
            if (rol.getRolID() == trabajadorAEditar.getRolID()) {
                cboRol.setSelectedIndex(i);
                break;
            }
        }
    }

    private void guardarTrabajador() {
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String dni = txtDni.getText();
        String usuarioLogin = txtUsuarioLogin.getText();
        String contrasena = new String(txtContrasena.getPassword());
        String email = txtEmail.getText();
        String telefono = txtTelefono.getText();
        String estado = (String) cboEstado.getSelectedItem();

        Rol rolSeleccionado = (Rol) cboRol.getSelectedItem();

        if (rolSeleccionado == null || estado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un rol y un estado.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String mensajeExito = trabajadorController.procesarGuardado(
                    trabajadorAEditar, nombre, apellido, dni, usuarioLogin,
                    contrasena, rolSeleccionado.getRolID(), email,
                    telefono, estado
            );

            JOptionPane.showMessageDialog(this, mensajeExito,
                    esCreacion ? "Registro Exitoso" : "Edición Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Error de Validación: " + ex.getMessage(), "Datos Inválidos", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Error de Persistencia: El DNI o Usuario ya existe, o hubo un error en la base de datos.", "Error de Guardado", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}

