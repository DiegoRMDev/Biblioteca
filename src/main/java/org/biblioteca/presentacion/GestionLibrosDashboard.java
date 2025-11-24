package org.biblioteca.presentacion;

import javax.swing.*;
import java.awt.*;

/**
 * Contenedor principal que agrupa las gestiones de Autor, Categoria y Libro
 * dentro de un JTabbedPane interno.
 * Esta clase se carga en el contentPanel (JTabbedPane principal) de VentanaPrincipal.
 */

public class GestionLibrosDashboard extends JPanel implements Actualizable {

    // Almacenar las instancias de las vistas internas
    private final GestionLibro gestionLibro;
    private final JTabbedPane tabbedPaneInterno;

    public GestionLibrosDashboard() {
        // Inicializar las vistas internas
        this.gestionLibro = new GestionLibro(); // <-- Vista clave con la tabla de stock

        // Establecemos el layout principal para que ocupe el espacio.
        this.setLayout(new BorderLayout());

        // Crear el JTabbedPane interno
        this.tabbedPaneInterno = new JTabbedPane();

        // Usamos la instancia creada para añadirla
        tabbedPaneInterno.addTab("Libros", null, gestionLibro, "Gestionar información de los libros");
        tabbedPaneInterno.addTab("Movimientos (Kardex)", null, new GestionMovimientos(), "Historial de entradas y salidas");

        // Las otras vistas no necesitan ser almacenadas si no las vamos a refrescar
        tabbedPaneInterno.addTab("Autores", null, new GestionAutor(), "Gestionar los autores de los libros");
        tabbedPaneInterno.addTab("Categorías", null, new GestionCategoria(), "Gestionar las categorías de los libros");

        // Añadir el JTabbedPane al centro de este Dashboard
        this.add(tabbedPaneInterno, BorderLayout.CENTER);

        tabbedPaneInterno.setSelectedIndex(0);
    }

    @Override
    public void actualizarDatos() {
        // Cuando la pestaña principal "Gestión de Recursos de Libros" es seleccionada:

        // Primero, verificamos qué pestaña INTERNA está seleccionada
        if (tabbedPaneInterno.getSelectedComponent() == gestionLibro) {

            gestionLibro.actualizarDatos();
        }

        // Nota: Si quieres actualizar siempre la tabla de libros independientemente de la pestaña interna,
        // simplemente llama a gestionLibro.actualizarDatos(); sin el 'if'. Por ahora, mantenemos la verificación.
    }
}