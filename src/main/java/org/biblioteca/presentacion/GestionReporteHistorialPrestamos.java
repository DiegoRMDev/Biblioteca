    package org.biblioteca.presentacion;

    import org.biblioteca.services.ReporteService;

    import javax.swing.*;
    import java.awt.*;
    import java.sql.Timestamp;
    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.LocalTime;
    import java.time.format.DateTimeFormatter;
    import java.time.format.DateTimeParseException;

    public class GestionReporteHistorialPrestamos extends JPanel {
        private JPanel mainPanel;
        private JPanel panelFiltros;
        private JTextField txtFechaInicio;
        private JTextField txtFechaFin;
        private JComboBox<String> cbTipoEvento;
        private JButton btnGenerar;

        private final ReporteService reporteService;
        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        public GestionReporteHistorialPrestamos() {
            this.reporteService = new ReporteService();

            this.setLayout(new BorderLayout());
            this.add(mainPanel, BorderLayout.CENTER);

            // --- Configuración inicial de las fechas ---
            cargarItemsComboBox();
            txtFechaFin.setText(LocalDate.now().format(DATE_FORMATTER));
            txtFechaInicio.setText(LocalDate.now().minusMonths(1).format(DATE_FORMATTER));

            // --- Listener del Botón ---
            btnGenerar.addActionListener(e -> generarReporte());
        }

        private void cargarItemsComboBox() {
            // Los ítems deben ser: Todos, Pendiente, Devuelto (que incluye Retrasado en el filtro SQL)
            if (cbTipoEvento.getItemCount() == 0) {
                cbTipoEvento.addItem("Todos los Préstamos");
                cbTipoEvento.addItem("Solo Pendientes");
                cbTipoEvento.addItem("Solo Devueltos");
                cbTipoEvento.addItem("Solo Retrasados"); // Opcional, pero útil si quieres el filtro exacto
                cbTipoEvento.setSelectedIndex(0);
            }
        }
        private void generarReporte() {
            try {
                // 1. Obtener valores de la UI y validar que no estén vacíos.
                String fechaInicioStr = txtFechaInicio.getText();
                String fechaFinStr = txtFechaFin.getText();

                if (fechaInicioStr.trim().isEmpty() || fechaFinStr.trim().isEmpty()) {
                    throw new IllegalArgumentException("Debe especificar una Fecha de Inicio y una Fecha de Fin.");
                }

                String tipoEventoNombre = (String) cbTipoEvento.getSelectedItem();

                // VALIDACIÓN: El tipo de evento no debe ser nulo.
                if (tipoEventoNombre == null) {
                    throw new IllegalStateException("El tipo de evento no fue seleccionado.");
                }

                // 2. Parsear y validar formato (ya cubierto por DateTimeParseException)
                LocalDate localDateInicio = LocalDate.parse(fechaInicioStr, DATE_FORMATTER);
                LocalDate localDateFin = LocalDate.parse(fechaFinStr, DATE_FORMATTER);

                //  VALIDACIONES LÓGICAS

                // 3. Validar que la Fecha de Inicio no sea posterior a la Fecha de Fin
                if (localDateInicio.isAfter(localDateFin)) {
                    throw new IllegalArgumentException("La Fecha de Inicio no puede ser posterior a la Fecha de Fin.");
                }

                // 4. Validar que la Fecha de Fin no sea futura (opcional, pero útil)
                if (localDateFin.isAfter(LocalDate.now())) {
                    throw new IllegalArgumentException("La Fecha de Fin no puede ser una fecha futura.");
                }

                // 5. Convertir a Timestamp, asegurando que abarque el rango
                Timestamp fechaInicio = Timestamp.valueOf(LocalDateTime.of(localDateInicio, LocalTime.MIN));
                LocalDate diaSiguiente = localDateFin.plusDays(1);
                Timestamp fechaFin = Timestamp.valueOf(LocalDateTime.of(diaSiguiente, LocalTime.MIN));

                // 6. Llamamos a nuestro servicio de reportes
                reporteService.generarReporteHistorialPrestamos(fechaInicio, fechaFin, tipoEventoNombre);

            } catch (IllegalArgumentException ex) {
                // Captura de errores de validación lógica
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Error de Validación de Entrada",
                        JOptionPane.WARNING_MESSAGE);
            } catch (DateTimeParseException ex) {
                // Captura de errores de formato de fecha
                JOptionPane.showMessageDialog(this,
                        "Error de formato de fecha. Use el formato AAAA-MM-DD (ej: 2025-01-15).",
                        "Error de Formato",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (Exception ex) {
                // Captura de errores del Servicio o DAO (conexión, JRException, etc.)
                JOptionPane.showMessageDialog(this,
                        "Error al generar el reporte:\n" + ex.getMessage(),
                        "Error de Reporte",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
