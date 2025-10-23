package org.biblioteca.presentacion;

import javax.swing.*;
import java.awt.*;

/**
 * Contenedor principal que agrupa las gestiones de Autor, Categoria y Libro
 * dentro de un JTabbedPane interno.
 * Esta clase se carga en el contentPanel (JTabbedPane principal) de VentanaPrincipal.
 */

public class GestionLibrosDashboard extends JPanel {
    public GestionLibrosDashboard() {
        // Establecemos el layout principal para que ocupe el espacio.
        this.setLayout(new BorderLayout());

        //  Crear el JTabbedPane interno
        JTabbedPane tabbedPaneInterno = new JTabbedPane();


        tabbedPaneInterno.addTab("Libros", null, new GestionLibro(), "Gestionar información de los libros");


        tabbedPaneInterno.addTab("Autores", null, new GestionAutor(), "Gestionar los autores de los libros");


        tabbedPaneInterno.addTab("Categorías", null, new GestionCategoria(), "Gestionar las categorías de los libros");

        //  Añadir el JTabbedPane al centro de este Dashboard
        this.add(tabbedPaneInterno, BorderLayout.CENTER);

        tabbedPaneInterno.setSelectedIndex(0);
    }
}
