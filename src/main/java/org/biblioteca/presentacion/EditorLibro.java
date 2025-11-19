package org.biblioteca.presentacion;

import org.biblioteca.entities.Autor;
import org.biblioteca.entities.Categoria;
import org.biblioteca.entities.Libro;
import org.biblioteca.entities.Proveedor;
import org.biblioteca.services.AutorService;
import org.biblioteca.services.CategoriaService;
import org.biblioteca.services.LibroService;
import org.biblioteca.services.ProveedorService;

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
    private JTextField txtStock;
    private JComboBox<Categoria> cboCategoria;
    private JList<Autor> lstAutores;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JComboBox cboEstado;
    private JComboBox cboProveedor;


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
            if (cboProveedor != null) {
                cboProveedor.setEnabled(false);
                // Opcional: cboProveedor.setVisible(false);
            }
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
        ProveedorService proveedorService = new ProveedorService();
        DefaultComboBoxModel<Proveedor> proveedorModel = new DefaultComboBoxModel<>();

        proveedorModel.addElement(new Proveedor(0, "Seleccione...", "", "", ""));
        proveedorService.listarProveedores().forEach(proveedorModel::addElement);
        cboProveedor.setModel(proveedorModel);
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
        // 1. RECOGER DATOS DE TEXTO (y limpiar espacios)
        String titulo = txtTitulo.getText().trim();
        String isbn = txtIsbn.getText().trim();
        String editorial = txtEditorial.getText().trim();
        String anioPubStr = txtAnioPub.getText().trim();
        String idioma = txtIdioma.getText().trim();
        String ubicFisica = txtUbFisc.getText().trim();
        String rutImg = txtRutImg.getText().trim();
        String stockStr = txtStock.getText().trim();
        String estado = (String) cboEstado.getSelectedItem();

        // 2. VALIDACIONES DE CAMPOS VACÍOS (Obligatorios)
        if (titulo.isEmpty()) {
            mostrarError("El Título es obligatorio.", txtTitulo);
            return;
        }
        if (isbn.isEmpty()) {
            mostrarError("El ISBN es obligatorio.", txtIsbn);
            return;
        }
        if (anioPubStr.isEmpty()) {
            mostrarError("El Año de Publicación es obligatorio.", txtAnioPub);
            return;
        }
        if (stockStr.isEmpty()) {
            mostrarError("El Stock es obligatorio.", txtStock);
            return;
        }

        // 3. VALIDACIÓN DE SELECCIONES (Combos y Listas)
        Categoria categoriaSeleccionada = (Categoria) cboCategoria.getSelectedItem();
        if (categoriaSeleccionada == null) {
            mostrarError("Debe seleccionar una Categoría.", cboCategoria);
            return;
        }

        List<Autor> autoresSeleccionados = lstAutores.getSelectedValuesList();
        if (autoresSeleccionados.isEmpty()) {
            mostrarError("Debe seleccionar al menos un Autor (Use Ctrl+Clic para varios).", lstAutores);
            return;
        }

        Proveedor proveedorSeleccionado = (Proveedor) cboProveedor.getSelectedItem();

        // 4. VALIDACIÓN DE FORMATOS NUMÉRICOS
        int anioPub;
        int stock;

        try {
            anioPub = Integer.parseInt(anioPubStr);
        } catch (NumberFormatException e) {
            mostrarError("El Año de Publicación debe ser un número entero válido.", txtAnioPub);
            return;
        }

        try {
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            mostrarError("El Stock debe ser un número entero válido.", txtStock);
            return;
        }

        // 5. PROCESO DE GUARDADO (Aquí saltarán las validaciones de Entidad y Servicio)
        try {
            Libro libro = new Libro();

            // Seteamos los valores (Las validaciones internas de Libro.java se ejecutan aquí)
            libro.setTitulo(titulo);
            libro.setIsbn(isbn); // Valida formato ISBN y longitud
            libro.setEditorial(editorial);
            libro.setAnioPublicacion(anioPub); // Valida que no sea año futuro
            libro.setIdioma(idioma);
            libro.setUbicacionFisica(ubicFisica);
            libro.setRutaImagen(rutImg);
            libro.setEstado(estado);
            libro.setStock(stock); // Valida que no sea negativo
            libro.setCategoriaID(categoriaSeleccionada.getCategoriaID());

            // Llamada al servicio (Valida ISBN duplicado)
            if (libroAEditar == null) {

                if (stock > 0 && proveedorSeleccionado == null) {
                    JOptionPane.showMessageDialog(this, "Debe seleccionar un Proveedor para registrar el stock inicial.", "Falta Proveedor", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Integer idProv = null;
                if (proveedorSeleccionado != null && proveedorSeleccionado.getProveedorID() > 0) {
                    idProv = proveedorSeleccionado.getProveedorID();
                }

                libroService.registrarLibro(libro, autoresSeleccionados, idProv); // Ahora enviará null en vez de 0
                JOptionPane.showMessageDialog(this, "Libro registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);


            } else {
                libro.setLibroID(libroAEditar.getLibroID());
                libroService.modificarLibro(libro, autoresSeleccionados);
                JOptionPane.showMessageDialog(this, "Libro actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

            dispose(); // Cerrar ventana si todo salió bien

        } catch (IllegalArgumentException ex) {
            // Captura: ISBN duplicado, Año futuro, Stock negativo, Textos muy largos
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            // Captura: Errores de base de datos inesperados
            JOptionPane.showMessageDialog(this, "Ocurrió un error en el sistema: " + ex.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void mostrarError(String mensaje, JComponent campo) {
        JOptionPane.showMessageDialog(this, mensaje, "Dato Incorrecto", JOptionPane.WARNING_MESSAGE);
        campo.requestFocus();
    }

    @Override
    public String toString() {

        return "EditorLibroDialog";
    }
}
