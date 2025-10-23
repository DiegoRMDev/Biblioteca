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


        crearBarraDeMenu();


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

        barraMenu = new JMenuBar();


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


        menuSistema = new JMenu("Sistema");
        menuItemSalir = new JMenuItem("Salir");
        menuSistema.add(menuItemSalir);


        barraMenu.add(menuMantenimiento);
        barraMenu.add(menuSistema);


        this.setJMenuBar(barraMenu);
    }

    public static void main(String[] args) {
        // (Tu código main para iniciar la aplicación)
        SwingUtilities.invokeLater(() -> {
            new MenuPrincipal().setVisible(true);
        });
    }
}
