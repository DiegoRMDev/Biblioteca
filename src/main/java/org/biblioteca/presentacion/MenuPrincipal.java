package org.biblioteca.presentacion;

import javax.swing.*;

public class MenuPrincipal extends JFrame{
    private JPanel mainPanel;

    // --- Componentes del Menú (Añadidos Manualmente) ---
    private JMenuBar barraMenu;
    private JMenu menuMantenimiento;
    private JMenu menuSistema;
    private JMenuItem menuItemLibros;
    private JMenuItem menuItemAutores;
    private JMenuItem menuItemLectores;
    private JMenuItem menuItemCategorias;
    private JMenuItem menuItemProveedores;
    private JMenuItem menuItemSalir;

    public MenuPrincipal() {
        setTitle("Sistema de Gestión de Biblioteca");
        setContentPane(mainPanel); // mainPanel es tu JPanel del .form
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. Llamamos al método que crea nuestro menú
        crearBarraDeMenu();

        // 2. Asignamos los eventos a los ítems del menú
        menuItemLibros.addActionListener(e -> {
            GestionLibro v = new GestionLibro();
            v.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            v.setVisible(true);
        });

        menuItemAutores.addActionListener(e -> {
            GestionAutor v = new GestionAutor();
            v.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            v.setVisible(true);
        });

        menuItemLectores.addActionListener(e -> {
            GestionLector v = new GestionLector();
            v.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            v.setVisible(true);
        });

        menuItemCategorias.addActionListener(e -> {
            GestionCategoria v = new GestionCategoria();
            v.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            v.setVisible(true);
        });

        menuItemProveedores.addActionListener(e -> {
            GestionProveedor v = new GestionProveedor();
            v.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            v.setVisible(true);
        });

        menuItemSalir.addActionListener(e -> {
            int opcion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de que desea salir?",
                    "Confirmar Salida",
                    JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }


    private void crearBarraDeMenu() {
        // Crear la barra principal
        barraMenu = new JMenuBar();

        // --- Menú "Mantenimiento" ---
        menuMantenimiento = new JMenu("Mantenimiento");

        menuItemLibros = new JMenuItem("Gestionar Libros");
        menuItemAutores = new JMenuItem("Gestionar Autores");
        menuItemLectores = new JMenuItem("Gestionar Lectores");
        menuItemCategorias = new JMenuItem("Gestionar Categorías");
        menuItemProveedores = new JMenuItem("Gestionar Proveedores");

        menuMantenimiento.add(menuItemLibros);
        menuMantenimiento.add(menuItemAutores);
        menuMantenimiento.add(menuItemLectores);
        menuMantenimiento.add(menuItemCategorias);
        menuMantenimiento.add(menuItemProveedores);

        // --- Menú "Sistema" ---
        menuSistema = new JMenu("Sistema");
        menuItemSalir = new JMenuItem("Salir");
        menuSistema.add(menuItemSalir);

        // Añadir los menús a la barra
        barraMenu.add(menuMantenimiento);
        barraMenu.add(menuSistema);

        // 3. ¡El paso más importante! Asignar la barra de menú a la ventana
        this.setJMenuBar(barraMenu);
    }

    public static void main(String[] args) {
        // (Tu código main para iniciar la aplicación)
        SwingUtilities.invokeLater(() -> {
            new MenuPrincipal().setVisible(true);
        });
    }
}
