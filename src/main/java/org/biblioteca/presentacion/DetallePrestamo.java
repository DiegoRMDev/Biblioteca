package org.biblioteca.presentacion;

import org.biblioteca.entities.Libro;
import org.biblioteca.entities.Prestamo;
import org.biblioteca.entities.PrestamoDetalle;
import org.biblioteca.services.LibroService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DetallePrestamo extends JDialog {
    private JPanel mainPanel;
    private JLabel lblLectorNombre;
    private JLabel lblTrabajadorNombre;
    private JLabel lblFechaPrestamo;
    private JLabel lblFechaPrevista;
    private JScrollPane scrollDetalles;
    private JTable tablaDetallesLibros;
    private JButton btnCerrar;


    private DefaultTableModel modeloDetalles;
    private LibroService libroService;
    private Prestamo prestamo;

    public DetallePrestamo(JFrame parent, Prestamo prestamo, LibroService libroService) {
        super(parent, true); // true = es un diálogo modal

        this.prestamo = prestamo;
        this.libroService = libroService;

        setTitle("Detalle del Préstamo ID: " + prestamo.getPrestamoID());
        setContentPane(mainPanel);

        // Asignar el listener al botón cerrar
        btnCerrar.addActionListener(e -> dispose());

        // Cargar los datos en la UI
        poblarDatos();

        pack(); // Ajusta el tamaño al contenido
        setSize(600, 400); // O un tamaño que prefieras
        setLocationRelativeTo(parent);
    }
    private void createUIComponents() {
        modeloDetalles = new DefaultTableModel(new Object[]{"ID Libro", "Título del Libro", "Cantidad"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaDetallesLibros = new JTable(modeloDetalles);
    }

    private void poblarDatos() {



        lblLectorNombre.setText(prestamo.getLectorNombre());
        lblTrabajadorNombre.setText(prestamo.getTrabajadorNombre());
        lblFechaPrestamo.setText(prestamo.getFechaPrestamo().toString());
        lblFechaPrevista.setText(prestamo.getFechaDevolucionPrevista().toString());



        //Llenar la tabla de libros (detalles)
        modeloDetalles.setRowCount(0); // Limpiar por si acaso

        if (prestamo.getDetalles() != null) {
            for (PrestamoDetalle detalle : prestamo.getDetalles()) {

                // Usamos LibroService para obtener el título del libro
                Libro libro = libroService.buscarLibroPorId(detalle.getLibroID());
                String titulo = (libro != null) ? libro.getTitulo() : "Libro no encontrado";

                modeloDetalles.addRow(new Object[]{
                        detalle.getLibroID(),
                        titulo,
                        detalle.getCantidad()
                });
            }
        }}
}
