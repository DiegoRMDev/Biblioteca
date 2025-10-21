package org.biblioteca.presentacion;

import org.biblioteca.entities.Categoria;
import org.biblioteca.services.CategoriaService;

import javax.swing.*;

public class EditorCategoria extends JDialog{
    private JPanel mainPanel;
    private JPanel centralPanel;
    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JButton btnCancelar;
    private JButton btnGuardar;

    private CategoriaService servicio;
    private Categoria categoriaActual;


    public EditorCategoria(JFrame parent, CategoriaService servicio, Categoria categoria) {
        super(parent, true); // true = es un diálogo modal

        this.servicio = servicio;
        this.categoriaActual = categoria;

        // --- Configuración del Diálogo ---
        setTitle(categoria == null ? "Nueva Categoría" : "Editar Categoría");
        setContentPane(mainPanel);
        setSize(400, 200);
        setLocationRelativeTo(parent);

        // Si estamos editando, llenamos los campos con los datos existentes
        if (categoria != null) {
            txtNombre.setText(categoria.getNombre());
            txtDescripcion.setText(categoria.getDescripcion());
        }

        // --- Lógica de Eventos ---
        btnGuardar.addActionListener(e -> guardarCategoria());
        btnCancelar.addActionListener(e -> dispose()); // dispose() simplemente cierra el diálogo
    }


    private void guardarCategoria() {
        String nombre = txtNombre.getText();
        String descripcion = txtDescripcion.getText();



        try {
            if (categoriaActual == null) { // Modo: Crear
                servicio.registrarCategoria(nombre, descripcion);
            } else { // Modo: Editar
                servicio.modificarCategoria(categoriaActual.getCategoriaID(), nombre, descripcion);
            }
            dispose();
        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
        }
    }

}
