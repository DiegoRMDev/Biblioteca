package org.biblioteca.presentacion;

import org.biblioteca.services.ReporteService;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class GestionReporteLectoresActivos extends JPanel {
    private JPanel mainPanel;
    private JPanel panelFiltros;
    private JButton btnGenerarTabla;
    private JButton btnGenerarGrafico;

    private JSpinner spinnerMesInicio;
    private JSpinner spinnerAnioInicio;
    private JSpinner spinnerMesFin;
    private JSpinner spinnerAnioFin;

    private final ReporteService reporteService;

    public GestionReporteLectoresActivos() {
        this.reporteService = new ReporteService();

        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);

        LocalDate hoy = LocalDate.now();

        LocalDate haceTresMeses = hoy.minusMonths(3);
        spinnerMesInicio.setModel(new SpinnerNumberModel(haceTresMeses.getMonthValue(), 1, 12, 1));
        spinnerAnioInicio.setModel(new SpinnerNumberModel(haceTresMeses.getYear(), 2020, 2050, 1));
        spinnerAnioInicio.setEditor(new JSpinner.NumberEditor(spinnerAnioInicio, "#"));

        spinnerMesFin.setModel(new SpinnerNumberModel(hoy.getMonthValue(), 1, 12, 1));
        spinnerAnioFin.setModel(new SpinnerNumberModel(hoy.getYear(), 2020, 2050, 1));
        spinnerAnioFin.setEditor(new JSpinner.NumberEditor(spinnerAnioFin, "#"));


        btnGenerarTabla.addActionListener(e -> generarReporte(false));
        btnGenerarGrafico.addActionListener(e -> generarReporte(true));
    }

    private void generarReporte(boolean esGrafico) {
        try {

            int mesInicio = (Integer) spinnerMesInicio.getValue();
            int anioInicio = (Integer) spinnerAnioInicio.getValue();
            int mesFin = (Integer) spinnerMesFin.getValue();
            int anioFin = (Integer) spinnerAnioFin.getValue();

            LocalDate localDateInicio = LocalDate.of(anioInicio, mesInicio, 1);
            Timestamp fechaInicio = Timestamp.valueOf(localDateInicio.atStartOfDay());

            YearMonth yearMonthFin = YearMonth.of(anioFin, mesFin);
            LocalDate localDateFinExclusivo = yearMonthFin.atEndOfMonth().plusDays(1); // Esto es el 1er día del mes siguiente
            Timestamp fechaFin = Timestamp.valueOf(localDateFinExclusivo.atStartOfDay());

            YearMonth inicioYM = YearMonth.of(anioInicio, mesInicio);
            YearMonth finYM = YearMonth.of(anioFin, mesFin);

            if (inicioYM.isAfter(finYM)) {
                throw new IllegalArgumentException("El período de inicio no puede ser posterior al período de fin.");
            }
            if (finYM.isAfter(YearMonth.now())) {
                throw new IllegalArgumentException("El período de fin no puede ser futuro.");
            }

            String periodoTexto = String.format("Período: %02d/%d al %02d/%d",
                    mesInicio, anioInicio, mesFin, anioFin);

            String nombreReporte = esGrafico ? "ReporteLectoresActivos_Grafico.jasper" : "ReporteLectoresActivos_Tabla.jasper";

            reporteService.generarReporteLectoresActivos(fechaInicio, fechaFin, periodoTexto, nombreReporte);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al generar el reporte:\n" + ex.getMessage(), "Error de Reporte", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
