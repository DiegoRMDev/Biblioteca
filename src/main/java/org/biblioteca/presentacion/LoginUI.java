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
    private JPanel formContainerPanel;


    private static final String BACKGROUND_IMAGE_PATH = "/img/FondoLogin.jpg";

    public LoginUI() {
        this.trabajadorService = new TrabajadorService();

        // 1. Configuración de la ventana
        setTitle("Biblioteca - Inicio de Sesión");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // 2. Agregar la lógica de eventos
        configurarListeners();

        setupPlaceholders();


        pack();
        setLocationRelativeTo(null);
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
                 new VentanaPrincipal().setVisible(true);

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

    // Dentro de la clase LoginUI
    private static final String DEFAULT_USER_TEXT = "Ingrese su usuario";
    private static final String DEFAULT_PASS_TEXT = "••••••••";

    // Dentro de LoginUI.java
    private void setupPlaceholders() {

        // --- Configuración para txtUsuario (JTextField) ---
        txtUsuario.addFocusListener(new java.awt.event.FocusAdapter() {

            // Cuando el campo GANA el foco (el usuario hace clic)
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                // Verifica si el texto actual ES el texto del placeholder
                if (txtUsuario.getText().equals(DEFAULT_USER_TEXT)) {
                    txtUsuario.setText(""); // 1. Limpia el texto
                    txtUsuario.setForeground(Color.BLACK); // 2. Cambia a color de texto normal
                }
            }

            // Cuando el campo PIERDE el foco (el usuario hace clic fuera)
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                // Si el campo queda vacío, restauramos el placeholder
                if (txtUsuario.getText().isEmpty()) {
                    txtUsuario.setForeground(Color.GRAY); // 1. Restaura el color gris
                    txtUsuario.setText(DEFAULT_USER_TEXT); // 2. Restaura el texto
                }
            }
        });

        // --- Configuración para txtContrasena (JPasswordField) ---
        txtContrasena.addFocusListener(new java.awt.event.FocusAdapter() {

            // Cuando el campo GANA el foco
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                String currentText = new String(txtContrasena.getPassword());

                // Verifica si el texto actual ES el texto del placeholder
                if (currentText.equals(DEFAULT_PASS_TEXT)) {
                    txtContrasena.setText(""); // 1. Limpia el texto
                    txtContrasena.setForeground(Color.BLACK); // 2. Cambia a color normal
                }
                // 3. Importante: Activa la ocultación de caracteres
                txtContrasena.setEchoChar('•');
            }

            // Cuando el campo PIERDE el foco
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                String currentText = new String(txtContrasena.getPassword());

                if (currentText.isEmpty()) {
                    txtContrasena.setForeground(Color.GRAY); // 1. Restaura el color gris
                    txtContrasena.setText(DEFAULT_PASS_TEXT); // 2. Restaura el texto

                    // 3. Importante: Desactiva la ocultación para que se vea el placeholder
                    txtContrasena.setEchoChar((char) 0);
                }
            }
        });
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }

}
