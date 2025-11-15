package org.biblioteca.presentacion;

import org.biblioteca.util.SessionManager;

import javax.swing.*;
import java.awt.*;

public class GestionAdminDashboard extends JPanel {
    public GestionAdminDashboard() {
        this.setLayout(new BorderLayout());

        JTabbedPane tabbedPaneInterno = new JTabbedPane();


        tabbedPaneInterno.addTab("Proveedores", null, new GestionProveedor(), "Gestionar proveedores");

        if (SessionManager.esAdministrador()) {
            tabbedPaneInterno.addTab("Trabajadores", null, new GestionTrabajador(), "Gestionar usuarios del sistema");
        }



        this.add(tabbedPaneInterno, BorderLayout.CENTER);
        tabbedPaneInterno.setSelectedIndex(0);
    }
}
