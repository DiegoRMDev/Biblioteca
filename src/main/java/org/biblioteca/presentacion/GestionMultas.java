package org.biblioteca.presentacion;

import org.biblioteca.entities.Libro;
import org.biblioteca.entities.Multa;
import org.biblioteca.entities.Prestamo;
import org.biblioteca.services.LibroService;
import org.biblioteca.services.MultaService;
import org.biblioteca.services.PrestamoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionMultas extends JPanel implements Actualizable {
    // --- Componentes para el UI Designer ---
    // Estos nombres deben coincidir EXACTAMENTE con el campo "field name" en el diseñador
    private JPanel mainPanel;
    private JTextField txtBuscarDni;
    private JButton btnBuscar;
    private JButton btnRefrescar;
    private JTable tablaMultas;
    private JButton btnPagar;
    private JScrollPane centralPanel;
    private JPanel topPanel;
    private JPanel bottomPanel;

    // --- Modelos y Servicios ---
    private DefaultTableModel modeloTabla;
    private final MultaService multaService;
    private final PrestamoService prestamoService;
    private final LibroService libroService;

    public GestionMultas() {
        this.multaService = new MultaService();
        this.prestamoService = new PrestamoService();
        this.libroService = new LibroService();

        // IMPORTANTE: Esta línea permite que el JPanel se muestre correctamente
        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);

        // Configurar Listeners (Eventos)
        btnBuscar.addActionListener(e -> filtrarMultas());

        btnRefrescar.addActionListener(e -> {
            txtBuscarDni.setText("");
            actualizarDatos();
        });

        btnPagar.addActionListener(e -> pagarMultaSeleccionada());

        // Carga inicial de datos
        actualizarDatos();
    }

    // --- Lógica de Negocio ---

    @Override
    public void actualizarDatos() {
        List<Multa> lista = multaService.listarMultas();
        llenarTabla(lista);
    }

    private void filtrarMultas() {
        String dni = txtBuscarDni.getText().trim();
        List<Multa> lista = multaService.buscarMultasPorDni(dni);
        llenarTabla(lista);
    }

    private void llenarTabla(List<Multa> multas) {
        modeloTabla.setRowCount(0);
        for (Multa m : multas) {
            // Obtenemos datos legibles usando los servicios
            String nombreLector = "---";
            String tituloLibro = "---";

            try {
                Prestamo p = prestamoService.buscarPrestamoPorId(m.getPrestamoID());
                if (p != null) nombreLector = p.getLectorNombre();

                Libro l = libroService.buscarLibroPorId(m.getLibroID());
                if (l != null) tituloLibro = l.getTitulo();

            } catch (Exception e) {
                System.err.println("Error cargando detalles de multa: " + e.getMessage());
            }

            modeloTabla.addRow(new Object[]{
                    m.getMultaID(),
                    nombreLector,
                    tituloLibro,
                    m.getDiasRetraso(),
                    m.getMonto(),
                    m.getFechaRegistro()
            });
        }
    }

    private void pagarMultaSeleccionada() {
        int fila = tablaMultas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una multa para registrar el pago.");
            return;
        }

        int idMulta = (int) modeloTabla.getValueAt(fila, 0); // Columna 0 es ID
        String lector = (String) modeloTabla.getValueAt(fila, 1);
        Object monto = modeloTabla.getValueAt(fila, 4);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Confirmar pago de S/ " + monto + " del lector " + lector + "?\nLa multa será eliminada.",
                "Confirmar Pago", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            multaService.pagarMulta(idMulta);
            actualizarDatos();
            JOptionPane.showMessageDialog(this, "Pago registrado correctamente.");
        }
    }

    // --- Configuración Personalizada para el UI Designer ---
    // Este método es llamado automáticamente por el .form cuando marcas "Custom Create" en la tabla
    private void createUIComponents() {
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Lector", "Libro", "Días Retraso", "Monto (S/)", "Fecha"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable
            }
        };
        tablaMultas = new JTable(modeloTabla);
    }
}