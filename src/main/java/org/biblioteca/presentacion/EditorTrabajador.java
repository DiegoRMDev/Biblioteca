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
    private JTextField txtApellido;
    private JTextField txtDni;
    private JTextField txtEmail;
    private JTextField txtTelefono;
    private JComboBox<String> cboEstado;

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
        cargarEstados(); // Cargar el ComboBox de estado

        if (!esCreacion) {
            poblarFormulario();
            // En modo edición
            txtUsuarioLogin.setEnabled(false); // No se edita el usuario
            txtDni.setEnabled(false);          // No se edita el DNI
            txtContrasena.setText("");
        } else {
            // En modo creación
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
            if (esCreacion) {
                if (contrasena.isEmpty()) {
                    throw new IllegalArgumentException("La contraseña es obligatoria al crear un nuevo trabajador.");
                }

                trabajadorService.registrarTrabajador(
                        nombre, apellido, dni, usuarioLogin, contrasena,
                        rolSeleccionado.getRolID(), email, telefono
                );
                JOptionPane.showMessageDialog(this, "Trabajador registrado con éxito.", "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);


            } else {
                // 1. Aplicar datos al objeto (incluyendo el posible cambio de RolID)
                trabajadorAEditar.setNombre(nombre);
                trabajadorAEditar.setApellido(apellido);
                trabajadorAEditar.setEmail(email);
                trabajadorAEditar.setTelefono(telefono);
                trabajadorAEditar.setRolID(rolSeleccionado.getRolID()); // <-- RolID que se valida en el servicio
                trabajadorAEditar.setEstado(estado);

                // 2. Intentar actualizar los datos (Valida la regla del cambio de Rol de Admin)
                // Si hay SecurityException, se lanzará AQUÍ.
                trabajadorService.actualizarDatos(trabajadorAEditar);

                // 3. Si la actualización de datos tuvo éxito, actualizamos la contraseña si se cambió.
                if (!contrasena.isEmpty()) {
                    trabajadorService.actualizarContrasena(trabajadorAEditar.getTrabajadorID(), contrasena);
                }

                JOptionPane.showMessageDialog(this, "Datos del trabajador actualizados con éxito.", "Edición Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }

            dispose();

        } catch (SecurityException ex) {
            // CAPTURA la excepción del Servicio que prohíbe el cambio de Rol de Admin.
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Permiso", JOptionPane.ERROR_MESSAGE);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Error de Validación: " + ex.getMessage(), "Datos Inválidos", JOptionPane.ERROR_MESSAGE);

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Error de Persistencia: El DNI o Usuario ya existe, o hubo un error en la base de datos.", "Error de Guardado", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}

