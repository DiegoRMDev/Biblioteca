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

        // Configuración básica (Tu .form ya hace esto, pero es bueno tenerlo)
        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);

        // --- Configuración de los Spinners ---

        // Modelo para Mes: (valor inicial, min, max, paso)
        // Usamos 1 como valor inicial, min 1, max 12, paso 1
        spinnerMes.setModel(new SpinnerNumberModel(1, 1, 12, 1));

        // Modelo para Año: (valor inicial, min, max, paso)
        // Usamos el año actual como inicial, min 2020, max 2050, paso 1
        spinnerAnio.setModel(new SpinnerNumberModel(Year.now().getValue(), 2020, 2050, 1));
        // Evita que el spinner de año use comas (ej. "2,024")
        spinnerAnio.setEditor(new JSpinner.NumberEditor(spinnerAnio, "#"));


        // --- Listener del Botón ---
        btnGenerar.addActionListener(e -> generarReporte());

        btnGenerarGrafico.addActionListener(e -> generarGrafico());
    }

    // NUEVO MeTODO PARA GENERAR EL GRÁFICO
    private void generarGrafico() {
        try {
            //  Obtenemos los valores de la UI
            int mes = (Integer) spinnerMes.getValue();
            int anio = (Integer) spinnerAnio.getValue();

            //  Llamamos al nuevo metodo del servicio
            reporteService.generarGraficoTopLibros(mes, anio);

        } catch (Exception ex) {
            //  Mostramos cualquier error
            JOptionPane.showMessageDialog(this,
                    "Error al generar el gráfico:\n" + ex.getMessage(),
                    "Error de Reporte",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    private void generarReporte() {
        try {
            //  Obtenemos los valores de la UI
            int mes = (Integer) spinnerMes.getValue();
            int anio = (Integer) spinnerAnio.getValue();

            //  Llamamos a nuestro servicio de reportes
            reporteService.generarReporteTopLibros(mes, anio);

        } catch (Exception ex) {
            //  Mostramos cualquier error
            JOptionPane.showMessageDialog(this,
                    "Error al generar el reporte:\n" + ex.getMessage(),
                    "Error de Reporte",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


}
