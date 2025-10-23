package org.biblioteca.presentacion;

import javax.swing.*;
import java.awt.*;

public class GestionAdminDashboard extends JPanel {
    public GestionAdminDashboard() {
        this.setLayout(new BorderLayout());

        JTabbedPane tabbedPaneInterno = new JTabbedPane();

        // Pestaña 1: Proveedores (Usando la vista corregida)
        tabbedPaneInterno.addTab("Proveedores", null, new GestionProveedor(), "Gestionar proveedores");

        // Pestaña 2: Trabajadores (Placeholder para futuro desarrollo)
        tabbedPaneInterno.addTab("Trabajadores", null, new JPanel(), "Gestionar usuarios y roles del sistema");

        // Pestaña 3: Roles (Placeholder para futuro desarrollo)
        tabbedPaneInterno.addTab("Roles", null, new JPanel(), "Gestionar roles y permisos");


        this.add(tabbedPaneInterno, BorderLayout.CENTER);
        tabbedPaneInterno.setSelectedIndex(0);
    }
}
