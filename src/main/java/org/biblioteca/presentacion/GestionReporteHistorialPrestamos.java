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

            cargarItemsComboBox();
            txtFechaFin.setText(LocalDate.now().format(DATE_FORMATTER));
            txtFechaInicio.setText(LocalDate.now().minusMonths(1).format(DATE_FORMATTER));

            btnGenerar.addActionListener(e -> generarReporte());
        }

        private void cargarItemsComboBox() {
            if (cbTipoEvento.getItemCount() == 0) {
                cbTipoEvento.addItem("Todos los Préstamos");
                cbTipoEvento.addItem("Solo Pendientes");
                cbTipoEvento.addItem("Solo Devueltos");
                cbTipoEvento.addItem("Solo Retrasados");
                cbTipoEvento.setSelectedIndex(0);
            }
        }
        private void generarReporte() {
            try {

                String fechaInicioStr = txtFechaInicio.getText();
                String fechaFinStr = txtFechaFin.getText();

                if (fechaInicioStr.trim().isEmpty() || fechaFinStr.trim().isEmpty()) {
                    throw new IllegalArgumentException("Debe especificar una Fecha de Inicio y una Fecha de Fin.");
                }

                String tipoEventoNombre = (String) cbTipoEvento.getSelectedItem();


                if (tipoEventoNombre == null) {
                    throw new IllegalStateException("El tipo de evento no fue seleccionado.");
                }


                LocalDate localDateInicio = LocalDate.parse(fechaInicioStr, DATE_FORMATTER);
                LocalDate localDateFin = LocalDate.parse(fechaFinStr, DATE_FORMATTER);


                if (localDateInicio.isAfter(localDateFin)) {
                    throw new IllegalArgumentException("La Fecha de Inicio no puede ser posterior a la Fecha de Fin.");
                }

                if (localDateFin.isAfter(LocalDate.now())) {
                    throw new IllegalArgumentException("La Fecha de Fin no puede ser una fecha futura.");
                }

                Timestamp fechaInicio = Timestamp.valueOf(LocalDateTime.of(localDateInicio, LocalTime.MIN));
                LocalDate diaSiguiente = localDateFin.plusDays(1);
                Timestamp fechaFin = Timestamp.valueOf(LocalDateTime.of(diaSiguiente, LocalTime.MIN));

                reporteService.generarReporteHistorialPrestamos(fechaInicio, fechaFin, tipoEventoNombre);

            } catch (IllegalArgumentException ex) {

                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Error de Validación de Entrada",
                        JOptionPane.WARNING_MESSAGE);
            } catch (DateTimeParseException ex) {

                JOptionPane.showMessageDialog(this,
                        "Error de formato de fecha. Use el formato AAAA-MM-DD (ej: 2025-01-15).",
                        "Error de Formato",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (Exception ex) {

                JOptionPane.showMessageDialog(this,
                        "Error al generar el reporte:\n" + ex.getMessage(),
                        "Error de Reporte",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
