package org.biblioteca.presentacion;

import org.biblioteca.services.ReporteService;

import javax.swing.*;

import java.awt.*;
import java.time.Year;
public class GestionReporteTopLibPrest extends JPanel {
    private JPanel mainPanel;
    private JSpinner spinnerMes;
    private JSpinner spinnerAnio;
    private JButton btnGenerar;
    private JPanel panelFiltros;
    private JButton btnGenerarGrafico;

    private ReporteService reporteService;

    public GestionReporteTopLibPrest() {
        this.reporteService = new ReporteService();

        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);

        spinnerMes.setModel(new SpinnerNumberModel(1, 1, 12, 1));
        spinnerAnio.setModel(new SpinnerNumberModel(Year.now().getValue(), 2020, 2050, 1));
        spinnerAnio.setEditor(new JSpinner.NumberEditor(spinnerAnio, "#"));


        btnGenerar.addActionListener(e -> generarReporte());

        btnGenerarGrafico.addActionListener(e -> generarGrafico());
    }

    private void generarGrafico() {
        try {

            int mes = (Integer) spinnerMes.getValue();
            int anio = (Integer) spinnerAnio.getValue();

            reporteService.generarGraficoTopLibros(mes, anio);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al generar el gráfico:\n" + ex.getMessage(),
                    "Error de Reporte",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    private void generarReporte() {
        try {

            int mes = (Integer) spinnerMes.getValue();
            int anio = (Integer) spinnerAnio.getValue();

            reporteService.generarReporteTopLibros(mes, anio);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al generar el reporte:\n" + ex.getMessage(),
                    "Error de Reporte",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


}
