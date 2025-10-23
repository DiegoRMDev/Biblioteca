package org.biblioteca.presentacion;

import org.biblioteca.util.SessionManager;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;


public class VentanaPrincipal extends JFrame {
    private JPanel mainPanel;
    private JPanel panelIzquierdo;
    private JPanel panelNorte;
    private JButton btnInicio;
    private JButton btnTrabajadores;
    private JButton btnLibros;
    private JButton btnPrestamos;
    private JButton btnDevoluciones;
    private JButton btnReportes;
    private JButton btnLectores;
    private JLabel logo;
    private JButton cerrarSesionButton;
    private JTabbedPane contentPanel;

    // Mapa para almacenar instancias de vistas (para no recargar)
    private final Map<String, JPanel> vistasAbiertas = new HashMap<>();

    public VentanaPrincipal() {
        // 1. Configuración básica de la ventana
        setTitle("Biblioteca - Panel Principal (" + SessionManager.getCurrentTrabajador().getUsuarioLogin() + ")");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Evitar cierre accidental

        // 2. Control de Acceso (Autorización)
        configurarPermisos();

        // 3. Configurar Listeners y Navegación
        configurarListeners();

        // 4. Asegurar el tamaño inicial y centrar
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Abrir la vista de inicio por defecto
        abrirVista("Inicio", new JPanel()); // Reemplaza JPanel() con tu panel de inicio si lo tienes
    }

    /**
     * Aplica la lógica de control de acceso basada en el rol.
     */
    private void configurarPermisos() {
        // Ocultar botones que solo debe ver el Administrador
        if (!SessionManager.esAdministrador()) {
            btnTrabajadores.setVisible(false);
            btnReportes.setVisible(false);
        }

        // Aquí puedes añadir más lógica de permisos si el Bibliotecario tiene restricciones
    }

    private void configurarListeners() {
        // 1. Cierre de Sesión
        if (cerrarSesionButton != null) {
            cerrarSesionButton.addActionListener(e -> intentarCerrarSesion());
        }

        // 2. Navegación (Conexión de botones)

        // Botón Inicio
        btnInicio.addActionListener(e -> abrirVista("Inicio", new JPanel()));

        // Botón TRABAJADORES (Placeholder - Reemplazar con new GestionTrabajadores() cuando esté lista)
        //btnTrabajadores.addActionListener(e -> abrirVista("Gestión Trabajadores", new JPanel()));

        // Usamos el nuevo panel contenedor que creamos: GestionLibrosDashboard
        btnLibros.addActionListener(e -> abrirVista("Gestión de Recursos de Libros", new GestionLibrosDashboard()));

        // Botón LECTORES (Placeholder - Reemplazar con new GestionLectores() cuando esté lista)
        //btnLectores.addActionListener(e -> abrirVista("Gestión Lectores", new JPanel()));

        // Botón PRÉSTAMOS (Placeholder)
        //btnPrestamos.addActionListener(e -> abrirVista("Gestión de Préstamos", new JPanel()));

        // Botón DEVOLUCIONES (Placeholder)
        //btnDevoluciones.addActionListener(e -> abrirVista("Gestión de Devoluciones", new JPanel()));

        // Botón REPORTES (Placeholder)
        //btnReportes.addActionListener(e -> abrirVista("Reportes", new JPanel()));


        // 3. Manejar el cierre de la ventana (para forzar el cierre de sesión)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                intentarCerrarSesion();
            }
        });
    }

    /**
     * Gestiona la apertura de una vista en el JTabbedPane.
     */
    private void abrirVista(String titulo, JPanel vista) {
        // 1. Verificar si la pestaña ya está abierta
        if (vistasAbiertas.containsKey(titulo)) {
            // Si ya existe, simplemente enfocarla (seleccionar la pestaña)
            contentPanel.setSelectedComponent(vistasAbiertas.get(titulo));
            return;
        }

        // 2. Si no existe, añadirla
        contentPanel.addTab(titulo, vista);
        vistasAbiertas.put(titulo, vista);

        // 3. Enfocar la nueva pestaña
        contentPanel.setSelectedComponent(vista);
    }

    private void intentarCerrarSesion() {
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea cerrar la sesión?", "Confirmar Cierre",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (respuesta == JOptionPane.YES_OPTION) {
            // Cierre de Sesión (Lógica del backend)
            SessionManager.cerrarSesion();

            // Cerrar Ventana Principal y reabrir Login
            dispose();
            new LoginUI().setVisible(true);
        }
    }
}
