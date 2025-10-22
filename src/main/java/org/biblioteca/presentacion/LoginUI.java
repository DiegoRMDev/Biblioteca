package org.biblioteca.presentacion;

import org.biblioteca.entities.Trabajador;
import org.biblioteca.services.TrabajadorService;
import org.biblioteca.util.SessionManager;
import javax.swing.*;
import java.awt.*;

public class LoginUI extends JFrame {

    private final TrabajadorService trabajadorService;

    private JPanel mainPanel;
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnLogin;


    private static final String BACKGROUND_IMAGE_PATH = "/img/FondoBiblioteca.jpg";

    public LoginUI() {
        this.trabajadorService = new TrabajadorService();

        // 1. Configuración de la ventana
        setTitle("Biblioteca - Inicio de Sesión");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // 2. Agregar la lógica de eventos
        configurarListeners();

        pack(); // Usa el PreferredSize del ImagePanel (500x350)
        setLocationRelativeTo(null); // Centrar
    }

    /**
     * Reemplaza el JPanel normal del diseñador por nuestro ImagePanel.
     * Este metodo se llama automáticamente por el diseñador antes de cargar los componentes.
     */
    private void createUIComponents() {
        // 1. Reemplazamos el JPanel normal por nuestra clase ImagePanel
        mainPanel = new ImagePanel(BACKGROUND_IMAGE_PATH);

        // 2. Usamos GridBagLayout en el mainPanel para que el formulario (que diseñaste en el .form)
        // se centre en la pantalla.
        mainPanel.setLayout(new GridBagLayout());
    }

    private void configurarListeners() {
        // Agregamos el listener al botón de login
        if (btnLogin != null) {
            btnLogin.addActionListener(e -> intentarLogin());
        }

        // Opcional: Permite iniciar sesión al presionar ENTER en el campo de contraseña
        if (txtContrasena != null) {
            txtContrasena.addActionListener(e -> intentarLogin());
        }
    }

    private void intentarLogin() {
        String usuario = txtUsuario.getText();
        // IMPORTANTE: Se debe obtener el String para la verificación con el Hash/Salt
        String contrasena = new String(txtContrasena.getPassword());

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese usuario y contraseña.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Lógica de Autenticación
            Trabajador trabajador = trabajadorService.autenticarTrabajador(usuario, contrasena);

            if (trabajador != null) {
                // Login Exitoso: Iniciar Sesión Global
                SessionManager.iniciarSesion(trabajador);

                JOptionPane.showMessageDialog(this, "Bienvenido/a, " + trabajador.getNombre(), "Login Exitoso", JOptionPane.INFORMATION_MESSAGE);

                // CERRAR LOGIN Y ABRIR VENTANA PRINCIPAL (siguiente paso)
                dispose();
                // new VentanaPrincipal().setVisible(true); // <-- Descomentar/Usar en el siguiente paso

            } else {
                // Login Fallido
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error de Credenciales", JOptionPane.ERROR_MESSAGE);
                txtContrasena.setText("");
                txtUsuario.requestFocus();
            }
        } catch (Exception ex) {
            // Captura errores de conexión a BD o del sistema
            JOptionPane.showMessageDialog(this, "Error del sistema: " + ex.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }

}
