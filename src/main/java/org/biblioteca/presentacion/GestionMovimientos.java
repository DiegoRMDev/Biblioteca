package org.biblioteca.presentacion;

import org.biblioteca.entities.MovimientoLibro;
import org.biblioteca.services.MovimientoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionMovimientos extends JPanel implements Actualizable {
    // --- Componentes vinculados al .form (UI Designer) ---
    private JPanel mainPanel;       // Panel principal
    private JTable tablaMovimientos;
    private JScrollPane centralPanel;
    private JPanel topPanel;
    private JLabel lblTitulo;

    // NUEVOS COMPONENTES
    private JTextField txtFiltroLibro;
    private JComboBox<String> cboFiltroTipo;
    private JTextField txtFiltroTrabajador;
    private JButton btnBuscar;
    private JButton btnLimpiar;

    // --- Lógica ---
    private DefaultTableModel modeloTabla;
    private final MovimientoService movimientoService;

    public GestionMovimientos() {
        this.movimientoService = new MovimientoService();

        // Configuración necesaria para que el panel se muestre
        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);
        cargarTiposMovimiento();
        // Eventos
        btnBuscar.addActionListener(e -> filtrarDatos());
        btnLimpiar.addActionListener(e -> limpiarFiltros());

        // Carga inicial
        actualizarDatos();
    }

    private void cargarTiposMovimiento() {
        DefaultComboBoxModel<String> modeloCombo = new DefaultComboBoxModel<>();
        modeloCombo.addElement("Todos");
        // Lista manual de tipos definidos en tu BD/Lógica
        modeloCombo.addElement("IngresoProveedor");
        modeloCombo.addElement("IngresoDonacion");
        modeloCombo.addElement("SalidaPrestamo");
        modeloCombo.addElement("EntradaDevolucion");
        // ... otros tipos
        cboFiltroTipo.setModel(modeloCombo);
    }

    private void filtrarDatos() {
        String libro = txtFiltroLibro.getText().trim();
        String tipo = (String) cboFiltroTipo.getSelectedItem();
        String trabajador = txtFiltroTrabajador.getText().trim();

        List<MovimientoLibro> resultados = movimientoService.filtrarMovimientos(libro, tipo, trabajador);
        llenarTabla(resultados);
    }

    private void limpiarFiltros() {
        txtFiltroLibro.setText("");
        txtFiltroTrabajador.setText("");
        cboFiltroTipo.setSelectedIndex(0); // "Todos"
        actualizarDatos(); // Recarga
    }
    @Override
    public void actualizarDatos() {


        List<MovimientoLibro> movimientos = movimientoService.listarHistorialCompleto();
        llenarTabla(movimientos);
    }

    private void llenarTabla(List<MovimientoLibro> lista) {
        modeloTabla.setRowCount(0);
        for (MovimientoLibro m : lista) {
            modeloTabla.addRow(new Object[]{
                    m.getMovimientoID(),
                    m.getTituloLibro(),
                    m.getTipoMovimiento(),
                    m.getCantidad(),
                    m.getFechaMovimiento(),
                    m.getNombreTrabajador(),
                    m.getObservaciones()
            });
        }
    }

    // Este método es llamado por el UI Designer al marcar "Custom Create" en la tabla
    private void createUIComponents() {
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Libro", "Tipo Movimiento", "Cant.", "Fecha", "Registrado Por", "Observaciones"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacemos la tabla de solo lectura
            }
        };
        tablaMovimientos = new JTable(modeloTabla);

        // Opcional: Ajustar anchos de columna
        tablaMovimientos.getColumnModel().getColumn(0).setPreferredWidth(30); // ID
        tablaMovimientos.getColumnModel().getColumn(3).setPreferredWidth(40); // Cantidad
        tablaMovimientos.getColumnModel().getColumn(4).setPreferredWidth(130); // Fecha
        tablaMovimientos.getColumnModel().getColumn(6).setPreferredWidth(200); // Observaciones
    }
}