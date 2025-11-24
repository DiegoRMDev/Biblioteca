package org.biblioteca.presentacion;

import org.biblioteca.entities.Rol;
import org.biblioteca.entities.Trabajador;
import org.biblioteca.services.RolService;
import org.biblioteca.services.TrabajadorService;
import org.biblioteca.util.SessionManager;

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
            cboRol.setEnabled(false);
            txtContrasena.setText("");

            aplicarRestriccionesAdmin();
        } else {
            // En modo creación
            txtContrasena.setText("");
        }

        btnGuardar.addActionListener(e -> guardarTrabajador());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void aplicarRestriccionesAdmin() {
        // 1. Condición: El usuario en sesión es Admin, y NO se está auto-editando.
        boolean esEdicionDeOtro = SessionManager.esAdministrador() &&
                SessionManager.getCurrentTrabajador().getTrabajadorID() != trabajadorAEditar.getTrabajadorID();

        // El rol del trabajador a editar viene cargado gracias a la corrección en el DAO.
        boolean esTrabajadorAEditarAdmin = "Administrador".equalsIgnoreCase(trabajadorAEditar.getNombreRol());

        if (esEdicionDeOtro && esTrabajadorAEditarAdmin) {
            // 2. Si un Admin edita a otro Admin, bloqueamos todos los campos excepto el estado.
            txtNombre.setEnabled(false);
            txtApellido.setEnabled(false);
            txtEmail.setEnabled(false);
            txtTelefono.setEnabled(false);

            // Bloqueamos la edición de contraseña
            txtContrasena.setEnabled(false);

            // 3. Informar visualmente al usuario
            setTitle("Editar Trabajador (Solo Estado Disponible)");
            JOptionPane.showMessageDialog(this,
                    "El sistema solo permite a un Administrador modificar el estado de otro Administrador.",
                    "Restricción de Seguridad", JOptionPane.INFORMATION_MESSAGE);
        }
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
        // --- 1. RECOLECCIÓN Y LIMPIEZA DE DATOS ---
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String dni = txtDni.getText().trim();
        String usuarioLogin = txtUsuarioLogin.getText().trim();
        String contrasena = new String(txtContrasena.getPassword()).trim();
        String email = txtEmail.getText().trim();
        String telefono = txtTelefono.getText().trim();

        Rol rolSeleccionado = (Rol) cboRol.getSelectedItem();
        String estado = (String) cboEstado.getSelectedItem();

        // --- 2. VALIDACIONES VISUALES (UI) ---

        // A) Campos Obligatorios Básicos
        if (nombre.isEmpty()) {
            mostrarError("El Nombre es obligatorio.", txtNombre);
            return;
        }
        if (apellido.isEmpty()) {
            mostrarError("El Apellido es obligatorio.", txtApellido);
            return;
        }
        if (dni.isEmpty()) {
            mostrarError("El DNI es obligatorio.", txtDni);
            return;
        }
        if (usuarioLogin.isEmpty()) {
            mostrarError("El Usuario es obligatorio.", txtUsuarioLogin);
            return;
        }

        // B) Validación de Formato de DNI (8 dígitos numéricos)
        if (!dni.matches("\\d{8}")) {
            mostrarError("El DNI debe contener exactamente 8 números.", txtDni);
            return;
        }

        // C) Validaciones de Contraseña
        if (esCreacion) {
            // Si es nuevo, la contraseña es obligatoria
            if (contrasena.isEmpty()) {
                mostrarError("Debe asignar una contraseña al nuevo trabajador.", txtContrasena);
                return;
            }
        }
        // Si escribió algo en la contraseña (sea nuevo o edición), validar longitud
        if (!contrasena.isEmpty() && contrasena.length() < 6) {
            mostrarError("La contraseña debe tener al menos 6 caracteres.", txtContrasena);
            return;
        }

        // D) Validación de Email (Opcional, pero si escribe algo, que sea válido)
        if (!email.isEmpty() && !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,4}$")) {
            mostrarError("El formato del correo electrónico no es válido (ej: usuario@dominio.com).", txtEmail);
            return;
        }

        // E) Validación de Combos
        if (rolSeleccionado == null) {
            mostrarError("Debe seleccionar un Rol para el trabajador.", cboRol);
            return;
        }
        if (estado == null) {
            mostrarError("Debe seleccionar un Estado.", cboEstado);
            return;
        }

        // --- 3. LLAMADA AL SERVICIO (BACKEND) ---
        try {
            if (esCreacion) {
                trabajadorService.registrarTrabajador(
                        nombre, apellido, dni, usuarioLogin, contrasena,
                        rolSeleccionado.getRolID(), email, telefono
                );
                JOptionPane.showMessageDialog(this, "Trabajador registrado con éxito.", "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);

            } else {
                // Aplicar cambios al objeto en memoria
                trabajadorAEditar.setNombre(nombre);
                trabajadorAEditar.setApellido(apellido);
                trabajadorAEditar.setDni(dni); // Permitimos intentar cambiar DNI (el servicio validará duplicados)
                trabajadorAEditar.setEmail(email);
                trabajadorAEditar.setTelefono(telefono);
                trabajadorAEditar.setRolID(rolSeleccionado.getRolID());
                trabajadorAEditar.setEstado(estado);

                // Intentar guardar (El servicio validará reglas de negocio como "Admin no puede degradarse a sí mismo")
                trabajadorService.actualizarDatos(trabajadorAEditar);

                // Si cambió la contraseña, actualizarla
                if (!contrasena.isEmpty()) {
                    trabajadorService.actualizarContrasena(trabajadorAEditar.getTrabajadorID(), contrasena);
                }

                JOptionPane.showMessageDialog(this, "Datos del trabajador actualizados con éxito.", "Edición Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }

            dispose(); // Cerrar ventana si todo salió bien

        } catch (SecurityException ex) {
            // Captura reglas de seguridad (ej: Admin intentando borrar a otro Admin)
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Acceso Denegado", JOptionPane.ERROR_MESSAGE);

        } catch (IllegalArgumentException ex) {
            // Captura validaciones de negocio (ej: DNI duplicado, Usuario duplicado)
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dato Inválido", JOptionPane.WARNING_MESSAGE);

        } catch (Exception ex) {
            // Captura errores inesperados
            JOptionPane.showMessageDialog(this, "Ocurrió un error al guardar: " + ex.getMessage(), "Error del Sistema", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void mostrarError(String mensaje, JComponent campo) {
        JOptionPane.showMessageDialog(this, mensaje, "Validación", JOptionPane.WARNING_MESSAGE);
        campo.requestFocus();
    }
}

