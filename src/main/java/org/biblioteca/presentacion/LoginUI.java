package org.biblioteca.presentacion;

import org.biblioteca.controller.LoginController;
import org.biblioteca.services.TrabajadorService;
import org.biblioteca.entities.Trabajador;
import org.biblioteca.util.SessionManager;
import javax.swing.*;
import java.awt.*;

public class LoginUI extends JFrame {

    private final LoginController loginController;

    private JPanel mainPanel;
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnLogin;
    private JPanel formContainerPanel;


    private static final String BACKGROUND_IMAGE_PATH = "/img/FondoLogin.jpg";

    public LoginUI() {
        this.loginController = new LoginController(new TrabajadorService());

        setTitle("Biblioteca - Inicio de Sesión");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        mainPanel = new ImagePanel(BACKGROUND_IMAGE_PATH);
        mainPanel.setLayout(new GridBagLayout());
    }

    private void configurarListeners() {
        if (btnLogin != null) {
            btnLogin.addActionListener(e -> intentarLogin());
        }
        if (txtContrasena != null) {
            txtContrasena.addActionListener(e -> intentarLogin());
        }
    }

    private void intentarLogin() {
        String usuario = txtUsuario.getText();
        String contrasena = new String(txtContrasena.getPassword());

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese usuario y contraseña.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean loginExitoso = loginController.intentarLogin(usuario, contrasena);
            if (loginExitoso) {
                Trabajador t = SessionManager.getCurrentTrabajador();
                String nombre = (t != null) ? t.getNombre() : "Usuario";

                JOptionPane.showMessageDialog(this, "Bienvenido/a, " + nombre, "Login Exitoso", JOptionPane.INFORMATION_MESSAGE);

                dispose();
                new VentanaPrincipal().setVisible(true);

            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error de Credenciales", JOptionPane.ERROR_MESSAGE);
                txtContrasena.setText("");
                txtUsuario.requestFocus();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error del sistema: " + ex.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private static final String DEFAULT_USER_TEXT = "Ingrese su usuario";
    private static final String DEFAULT_PASS_TEXT = "••••••••";

    private void setupPlaceholders() {

        txtUsuario.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtUsuario.getText().equals(DEFAULT_USER_TEXT)) {
                    txtUsuario.setText("");
                    txtUsuario.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtUsuario.getText().isEmpty()) {
                    txtUsuario.setForeground(Color.GRAY);
                    txtUsuario.setText(DEFAULT_USER_TEXT);
                }
            }
        });

        txtContrasena.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                String currentText = new String(txtContrasena.getPassword());

                if (currentText.equals(DEFAULT_PASS_TEXT)) {
                    txtContrasena.setText("");
                    txtContrasena.setForeground(Color.BLACK);
                }
                txtContrasena.setEchoChar('•');
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                String currentText = new String(txtContrasena.getPassword());

                if (currentText.isEmpty()) {
                    txtContrasena.setForeground(Color.GRAY);
                    txtContrasena.setText(DEFAULT_PASS_TEXT);
                    txtContrasena.setEchoChar((char) 0);
                }
            }
        });
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }

}
