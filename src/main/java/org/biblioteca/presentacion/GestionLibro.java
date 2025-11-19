package org.biblioteca.presentacion;

import org.biblioteca.entities.Libro;
import org.biblioteca.services.LibroService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class GestionLibro extends JPanel implements Actualizable {

    private JPanel mainPanel;
    private JScrollPane centralPanel;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JTable tablaLibros;

    // NUEVOS CAMPOS PARA BUSQUEDA
    private JTextField txtBuscar;
    private JPanel panelBuscador;
    private TableRowSorter<DefaultTableModel> sorter;

    private DefaultTableModel modeloTabla;
    private LibroService libroService;

    @Override
    public void actualizarDatos() {
        this.actualizarTabla();
    }

    public GestionLibro() {
        this.libroService = new LibroService();

        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);

        btnNuevo.addActionListener(e -> abrirDialogoEditor(null));
        btnEditar.addActionListener(e -> editarLibroSeleccionado());
        btnEliminar.addActionListener(e -> eliminarLibroSeleccionado());

        // Inicializar el sorter para filtrado
        sorter = new TableRowSorter<>(modeloTabla);
        tablaLibros.setRowSorter(sorter);

        // Listener del cuadro de texto para filtrar en tiempo real
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrar(); }

            private void filtrar() {
                String texto = txtBuscar.getText();
                if (texto.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                }
            }
        });

        actualizarTabla();
    }

    public void actualizarTabla() {
        modeloTabla.setRowCount(0);
        List<Libro> libros = libroService.listarLibros();

        for (Libro libro : libros) {
            String autoresStr = libro.getAutores().stream()
                    .map(a -> a.getNombre() + " " + a.getApellido())
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
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        EditorLibro dialogo = new EditorLibro(parentFrame, libroService, libro);
        dialogo.setVisible(true);
        actualizarTabla();
    }

    private void editarLibroSeleccionado() {
        int fila = tablaLibros.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un libro para editar.");
            return;
        }
        int libroId = (int) modeloTabla.getValueAt(fila, 0);
        Libro libro = libroService.buscarLibroPorId(libroId);
        abrirDialogoEditor(libro);
    }

    private void eliminarLibroSeleccionado() {
        int fila = tablaLibros.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un libro para eliminar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar este libro?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int libroId = (int) modeloTabla.getValueAt(fila, 0);
            libroService.eliminarLibro(libroId);
            actualizarTabla();
        }
    }

    private void createUIComponents() {
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "ISBN", "Título", "Autores", "Editorial", "AnioPubli",
                        "Categoria", "Idioma", "Ubi. Fisica", "Ruta Img", "Estado", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaLibros = new JTable(modeloTabla);
    }
}
