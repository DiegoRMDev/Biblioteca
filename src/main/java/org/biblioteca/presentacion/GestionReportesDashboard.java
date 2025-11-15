package org.biblioteca.presentacion;
import org.biblioteca.util.SessionManager;
import javax.swing.*;
import java.awt.*;

/**
 * Contenedor del módulo de Reportes. Utiliza JTabbedPane para organizar
 * los distintos reportes analíticos disponibles (Top Libros, Multas, etc.).
 *
 * El contenido de las pestañas internas se limita según el rol del usuario.
 */

public class GestionReportesDashboard extends JPanel {

    public GestionReportesDashboard() {
        this.setLayout(new BorderLayout());
        JTabbedPane tabbedPaneInterno = new JTabbedPane();

        // Determinar si el usuario actual es Administrador
        boolean esAdmin = SessionManager.esAdministrador();

        // ====================================================
        // 1. REPORTE EXCLUSIVO DEL ADMINISTRADOR (RF-009 con filtros de fecha)
        // ====================================================

        if (esAdmin) {
            // Solo el Administrador puede ver y usar este reporte con rango de fechas
            tabbedPaneInterno.addTab("Top Libros (Rango de Fecha)", null,
                    new GestionReporteTopLibPrest(), "Reporte analítico de libros más solicitados con filtros de fecha.");
        }


        // Pestaña 3: Historial de prestamos con filtros de rango de fechas y opcion de devolucion, prestamo o ambos (Ejemplo de otro reporte futuro)

        // Si no hay pestañas añadidas, se podría mostrar un mensaje.
        if (tabbedPaneInterno.getTabCount() > 0) {
            this.add(tabbedPaneInterno, BorderLayout.CENTER);
            tabbedPaneInterno.setSelectedIndex(0);
        } else {
            this.add(new JLabel("No hay reportes analíticos disponibles para su rol.", SwingConstants.CENTER), BorderLayout.CENTER);
        }
    }
}

