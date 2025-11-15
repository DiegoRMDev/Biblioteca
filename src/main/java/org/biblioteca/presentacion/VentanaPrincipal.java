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
        abrirVista("Inicio", new DashboardReportes()); // Reemplaza JPanel() con tu panel de inicio si lo tienes
    }

    /**
     * Aplica la lógica de control de acceso basada en el rol.
     */
    private void configurarPermisos() {
        btnTrabajadores.setVisible(SessionManager.esAdministrador());

        boolean esBibliotecario = SessionManager.esBibliotecario();
        btnPrestamos.setVisible(esBibliotecario);

        boolean puedeVerReportes = SessionManager.esAdministrador() || SessionManager.esBibliotecario();
        btnReportes.setVisible(puedeVerReportes);


        if (!puedeVerReportes && !SessionManager.esAdministrador()) {
            btnLibros.setVisible(false);
            btnLectores.setVisible(false);
            btnPrestamos.setVisible(false);
        }

    }

    private void configurarListeners() {
        // 1. Cierre de Sesión
        if (cerrarSesionButton != null) {
            cerrarSesionButton.addActionListener(e -> intentarCerrarSesion());
        }

        // 2. Navegación (Conexión de botones)

        // Botón Inicio
        btnInicio.addActionListener(e -> abrirVista("Inicio", new DashboardReportes()));

        // Botón TRABAJADORES (Placeholder - Reemplazar con new GestionTrabajadores() cuando esté lista)
        btnTrabajadores.addActionListener(e -> abrirVista("Gestión Administrativa", new GestionAdminDashboard()));

        // Usamos el nuevo panel contenedor que creamos: GestionLibrosDashboard
        btnLibros.addActionListener(e -> abrirVista("Gestión de Recursos de Libros", new GestionLibrosDashboard()));

        // Botón LECTORES (Placeholder - Reemplazar con new GestionLectores() cuando esté lista)
        btnLectores.addActionListener(e -> abrirVista("Gestión de Lectores", new GestionLector()));

        // Botón PRÉSTAMOS (Placeholder)
        btnPrestamos.addActionListener(e -> abrirVista("Gestión de Préstamos", new GestionPrestamo()));

        btnInicio.addActionListener(e -> abrirVista("Dashboard de Reportes", new DashboardReportes()));


        // Botón REPORTES
        btnReportes.addActionListener(e -> abrirVista("Módulo de Reportes Analíticos", new GestionReportesDashboard()));


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

        int currentIndex = contentPanel.indexOfTab(titulo);
        if (currentIndex != -1 && contentPanel.getSelectedIndex() == currentIndex) {
            return;
        }
        contentPanel.removeAll();

        if (vista instanceof Actualizable) {
            System.out.println("DEBUG: Refrescando vista: " + titulo);
            ((Actualizable) vista).actualizarDatos();
        }

        // 4. Añadir la nueva vista
        contentPanel.addTab(titulo, vista);

        // 5. Enfocar la nueva pestaña (será la única que hay)
        contentPanel.setSelectedComponent(vista);
    }
    public void refrescarVista(String titulo) {
        //   Buscamos el panel en nuestro mapa de vistas abiertas
        JPanel vista = vistasAbiertas.get(titulo);

        //  . Verificamos que exista y que sea el tipo que queremos refrescar
        if (vista instanceof DashboardReportes) {
            System.out.println("Refrescando el Dashboard de 'Inicio'...");
            // 3. Llamamos a su metodo público de recarga de datos
            ((DashboardReportes) vista).cargarDatosDashboard();
        }

        // (Aquí podríamos añadir más 'else if' si otras vistas
        // necesitaran refrescarse en el futuro)
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
