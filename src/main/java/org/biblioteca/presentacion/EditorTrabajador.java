package org.biblioteca.presentacion;

import org.biblioteca.entities.Rol;
import org.biblioteca.entities.Trabajador;
import org.biblioteca.services.RolService;
import org.biblioteca.services.TrabajadorService;

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

    private TrabajadorService trabajadorService;
    private RolService rolService;
    private Trabajador trabajadorAEditar;


    private final boolean esCreacion;


    public EditorTrabajador(JFrame parent, TrabajadorService trabajadorService, RolService rolService, Trabajador trabajador) {
        super(parent, true);
        this.trabajadorService = trabajadorService;
        this.rolService = rolService;
        this.trabajadorAEditar = trabajador;
        this.esCreacion = (trabajador == null);

        setTitle(esCreacion ? "Registrar Nuevo Trabajador" : "Editar Trabajador");
        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(parent);

        cargarRoles();

        if (!esCreacion) {
            poblarFormulario();
            // En modo edición, no se permite cambiar el usuario o rol fácilmente
            txtUsuarioLogin.setEnabled(false);
            // La contraseña solo se actualiza si el usuario la escribe
            txtContrasena.setText(""); // Limpiamos para que el usuario sepa que no se va a cambiar
        } else {
            // En modo creación, la contraseña es obligatoria
            txtContrasena.setText("");
        }

        btnGuardar.addActionListener(e -> guardarTrabajador());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void cargarRoles() {
        DefaultComboBoxModel<Rol> rolModel = new DefaultComboBoxModel<>();
        List<Rol> roles = rolService.listarRoles();
        roles.forEach(rolModel::addElement);
        cboRol.setModel(rolModel);
    }

    private void poblarFormulario() {
        txtNombre.setText(trabajadorAEditar.getNombre());
        txtUsuarioLogin.setText(trabajadorAEditar.getUsuarioLogin());

        // Seleccionar el rol actual
        for (int i = 0; i < cboRol.getItemCount(); i++) {
            Rol rol = cboRol.getItemAt(i);
            if (rol.getRolID() == trabajadorAEditar.getRolID()) {
                cboRol.setSelectedIndex(i);
                break;
            }
        }
    }

    private void guardarTrabajador() {
        // 1. Recoger datos
        String nombre = txtNombre.getText();
        String usuarioLogin = txtUsuarioLogin.getText();
        String contrasena = new String(txtContrasena.getPassword());
        Rol rolSeleccionado = (Rol) cboRol.getSelectedItem();

        // Validaciones básicas de interfaz
        if (rolSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un rol.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (esCreacion) {
                // Modo CREACIÓN
                // Contraseña es obligatoria
                if (contrasena.isEmpty()) {
                    throw new IllegalArgumentException("La contraseña es obligatoria al crear un nuevo trabajador.");
                }

                trabajadorService.registrarTrabajador(nombre, usuarioLogin, contrasena, rolSeleccionado.getRolID());

            } else {
                // Modo EDICIÓN
                trabajadorAEditar.setNombre(nombre);
                trabajadorAEditar.setRolID(rolSeleccionado.getRolID());

                // Actualizar la contraseña SOLO si el usuario ha escrito algo
                if (!contrasena.isEmpty()) {
                    trabajadorService.actualizarContrasena(trabajadorAEditar.getTrabajadorID(), contrasena);
                }

                // Actualizar los datos básicos (Nombre y RolID)
                trabajadorService.actualizarDatos(trabajadorAEditar);
            }

            dispose();

        } catch (IllegalArgumentException ex) {
            // Captura errores de validación (Nombre vacío, Usuario inválido, Contraseña corta)
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            // Captura errores de persistencia (ej: UsuarioLogin ya existe - UNIQUE constraint)
            JOptionPane.showMessageDialog(this, "Error: El usuario de login ya está registrado o hubo un error en la BD.", "Error de Persistencia", JOptionPane.ERROR_MESSAGE);
        }
    }
}
