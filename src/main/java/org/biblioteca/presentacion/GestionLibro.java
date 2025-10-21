package org.biblioteca.presentacion;

import org.biblioteca.entities.Libro;
import org.biblioteca.services.LibroService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.stream.Collectors;

public class GestionLibro extends  JFrame{
    private JPanel mainPanel;
    private JScrollPane centralPanel;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JTable tablaLibros;

    private DefaultTableModel modeloTabla;
    private LibroService libroService;

    public GestionLibro() {
        this.libroService = new LibroService();

        setTitle("Gestión de Libros");
        setContentPane(mainPanel);
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        btnNuevo.addActionListener(e -> abrirDialogoEditor(null));
        btnEditar.addActionListener(e -> editarLibroSeleccionado());
        btnEliminar.addActionListener(e -> eliminarLibroSeleccionado());

        actualizarTabla();
    }

    public void actualizarTabla() {
        modeloTabla.setRowCount(0); // Limpiar la tabla
        List<Libro> libros = libroService.listarLibros();
        for (Libro libro : libros) {
            // Unimos los nombres de los autores en un solo String, ej: "Autor A, Autor B"
            String autoresStr = libro.getAutores().stream()
                    .map(autor -> autor.getNombre() + " " + autor.getApellido())
                    .collect(Collectors.joining(", "));

            modeloTabla.addRow(new Object[]{
                    libro.getLibroID(),
                    libro.getIsbn(),
                    libro.getTitulo(),
                    autoresStr,
                    libro.getEditorial(),
                    libro.getAnioPublicacion(),
                    libro.getCategoriaID(),
                    libro.getIdioma(),
                    libro.getUbicacionFisica(),
                    libro.getRutaImagen(),
                    libro.getEstado(),
                    libro.getStock()

            });
        }
    }

    private void abrirDialogoEditor(Libro libro) {
        // Creamos y mostramos el diálogo, pasándole el libro a editar (o null si es nuevo)
        EditorLibro dialogo = new EditorLibro(this, libroService, libro);
        dialogo.setVisible(true);
        actualizarTabla(); // Actualizamos la tabla por si hubo cambios
    }

    private void editarLibroSeleccionado() {
        int fila = tablaLibros.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un libro para editar.", "Acción Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int libroId = (int) modeloTabla.getValueAt(fila, 0);
        Libro libroAEditar = libroService.buscarLibroPorId(libroId);
        abrirDialogoEditor(libroAEditar);
    }

    private void eliminarLibroSeleccionado() {
        int fila = tablaLibros.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un libro para eliminar.", "Acción Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar este libro?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            int libroId = (int) modeloTabla.getValueAt(fila, 0);
            libroService.eliminarLibro(libroId);
            actualizarTabla();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GestionLibro().setVisible(true));
    }

    private void createUIComponents() {
        // Creamos el modelo de la tabla de forma personalizada
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "ISBN","Título", "Autores",  "Editorial","AnioPubli","Categoria","Idioma","Ubi. Fisica","Ruta Img",
        "Estado","Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaLibros = new JTable(modeloTabla);
    }
}
