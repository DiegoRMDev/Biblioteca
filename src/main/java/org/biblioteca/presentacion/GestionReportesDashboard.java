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
        boolean puedeVerReportes = SessionManager.esAdministrador() || SessionManager.esBibliotecario(); // Ya definido en VentanaPrincipal

        // ====================================================
        // 1. REPORTE EXCLUSIVO DEL ADMINISTRADOR (RF-009 con filtros de fecha)
        // ====================================================

        if (esAdmin) {
            // Solo el Administrador puede ver y usar este reporte con rango de fechas
            tabbedPaneInterno.addTab("Top Libros (Mes)", null,
                    new GestionReporteTopLibPrest(), "Reporte analítico de libros más solicitados con filtros de fecha.");
        }

        // ====================================================
        // 2. REPORTE ACCESIBLE PARA AMBOS ROLES (Historial de Préstamos)
        // ====================================================

        if (puedeVerReportes) {
            // Tanto Administrador como Bibliotecario pueden generar este reporte
            tabbedPaneInterno.addTab("Historial de Préstamos", null,
                    new GestionReporteHistorialPrestamos(), "Historial detallado de préstamos y devoluciones por rango de fechas y tipo de evento.");
        }

        if (puedeVerReportes) {
            // Añadir el nuevo reporte de Lectores Activos con rango de fechas
            tabbedPaneInterno.addTab("Lectores Activos (Meses)", null,
                    new GestionReporteLectoresActivos(), "Reporte de tendencia de lectores únicos activos por rango de fechas.");
        }


        // Si no hay pestañas añadidas, se podría mostrar un mensaje.
        if (tabbedPaneInterno.getTabCount() > 0) {
            this.add(tabbedPaneInterno, BorderLayout.CENTER);
            tabbedPaneInterno.setSelectedIndex(0);
        } else {
            this.add(new JLabel("No hay reportes analíticos disponibles para su rol.", SwingConstants.CENTER), BorderLayout.CENTER);
        }
    }
}

