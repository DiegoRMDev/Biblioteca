package org.biblioteca.presentacion;

import org.biblioteca.entities.Autor;
import org.biblioteca.entities.Categoria;
import org.biblioteca.entities.Libro;
import org.biblioteca.services.AutorService;
import org.biblioteca.services.CategoriaService;
import org.biblioteca.services.LibroService;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EditorLibro extends JDialog {
    private JPanel mainPanel;
    private JPanel centralPanel;
    private JTextField txtTitulo;
    private JTextField txtIsbn;
    private JTextField txtEditorial;
    private JTextField txtAnioPub;
    private JTextField txtIdioma;
    private JTextField txtUbFisc;
    private JTextField txtRutImg;
    private JTextField txtEstado;
    private JTextField txtStock;
    private JComboBox<Categoria> cboCategoria;
    private JList<Autor> lstAutores;
    private JButton btnGuardar;
    private JButton btnCancelar;


    private LibroService libroService;
    private Libro libroAEditar;

    public EditorLibro(JFrame parent, LibroService libroService, Libro libro) {
        super(parent, true);
        this.libroService = libroService;
        this.libroAEditar = libro;

        setTitle(libro == null ? "Registrar Nuevo Libro" : "Editar Libro");
        setContentPane(mainPanel);
        pack(); // Ajusta el tamaño automáticamente
        setLocationRelativeTo(parent);

        cargarDatosParaSeleccion();

        if (libro != null) {
            poblarFormulario();
        }

        btnGuardar.addActionListener(e -> guardarLibro());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void cargarDatosParaSeleccion() {
        // Cargar todas las categorías disponibles en el ComboBox
        CategoriaService categoriaService = new CategoriaService();
        DefaultComboBoxModel<Categoria> categoriaModel = new DefaultComboBoxModel<>();
        categoriaService.listarCategorias().forEach(categoriaModel::addElement);
        cboCategoria.setModel(categoriaModel);

        // Cargar todos los autores disponibles en la JList
        AutorService autorService = new AutorService();
        DefaultListModel<Autor> autorModel = new DefaultListModel<>();
        autorService.listarAutores().forEach(autorModel::addElement);
        lstAutores.setModel(autorModel);
    }

    private void poblarFormulario() {
        // Llenar los campos de texto con los datos del libro
        txtTitulo.setText(libroAEditar.getTitulo());
        txtIsbn.setText(libroAEditar.getIsbn());
        txtEditorial.setText(libroAEditar.getEditorial());
        txtAnioPub.setText(String.valueOf(libroAEditar.getAnioPublicacion()));
        txtIdioma.setText(libroAEditar.getIdioma());
        txtUbFisc.setText(libroAEditar.getUbicacionFisica());
        txtRutImg.setText(libroAEditar.getRutaImagen());
        txtEstado.setText(libroAEditar.getEstado());
        txtStock.setText(String.valueOf(libroAEditar.getStock()));

        // Seleccionar la categoría correcta en el ComboBox
        for (int i = 0; i < cboCategoria.getModel().getSize(); i++) {
            if (cboCategoria.getModel().getElementAt(i).getCategoriaID() == libroAEditar.getCategoriaID()) {
                cboCategoria.setSelectedIndex(i);
                break;
            }
        }

        // Pre-seleccionar los autores asociados al libro en la JList
        List<Integer> idsAutoresDelLibro = libroAEditar.getAutores().stream()
                .map(Autor::getAutorID)
                .collect(Collectors.toList());
        List<Integer> indicesParaSeleccionar = new ArrayList<>();
        DefaultListModel<Autor> model = (DefaultListModel<Autor>) lstAutores.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (idsAutoresDelLibro.contains(model.getElementAt(i).getAutorID())) {
                indicesParaSeleccionar.add(i);
            }
        }

        lstAutores.setSelectedIndices(indicesParaSeleccionar.stream().mapToInt(i -> i).toArray());
    }

    private void guardarLibro() {
        // Recoger datos de los campos
        String titulo = txtTitulo.getText();
        String isbn = txtIsbn.getText();
        String editorial=txtEditorial.getText();
        Integer anioPub= Integer.valueOf(txtAnioPub.getText());
        String idioma=txtIdioma.getText();
        String ubicFisica= txtUbFisc.getText();
        String rutImg=txtRutImg.getText();
        String estado=txtEstado.getText();
        Integer stock=Integer.valueOf(txtStock.getText());
        Categoria categoriaSeleccionada = (Categoria) cboCategoria.getSelectedItem();
        List<Autor> autoresSeleccionados = lstAutores.getSelectedValuesList();

        try {
            Libro libro = new Libro();
            // Usar los setters que tienen validaciones
            libro.setTitulo(titulo);
            libro.setIsbn(isbn);
            libro.setEditorial(editorial);
            libro.setAnioPublicacion(anioPub);
            libro.setIdioma(idioma);
            libro.setUbicacionFisica(ubicFisica);
            libro.setRutaImagen(rutImg);
            libro.setEstado(estado);
            libro.setStock(stock);


            libro.setCategoriaID(categoriaSeleccionada.getCategoriaID());

            if (libroAEditar == null) { // Modo Creación
                libroService.registrarLibro(libro, autoresSeleccionados);
            } else { // Modo Edición
                libro.setLibroID(libroAEditar.getLibroID());
                libroService.modificarLibro(libro, autoresSeleccionados);
            }
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public String toString() {

        return "EditorLibroDialog";
    }
}
