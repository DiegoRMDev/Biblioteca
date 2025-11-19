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
        String nombre = txtNombre.getText().trim(); // Es bueno añadir .trim()
        String descripcion = txtDescripcion.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El nombre de la categoría es obligatorio.",
                    "Campo Vacío",
                    JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        try {
            if (categoriaActual == null) { // Modo: Crear
                servicio.registrarCategoria(nombre, descripcion);
                JOptionPane.showMessageDialog(this, "Categoría registrada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else { // Modo: Editar
                servicio.modificarCategoria(categoriaActual.getCategoriaID(), nombre, descripcion);
                JOptionPane.showMessageDialog(this, "Categoría actualizada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            dispose();

        } catch (IllegalArgumentException ex) {
            // Errores de validación de la entidad (ej. nombre muy largo)
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);

        } catch (RuntimeException ex) {
            // ¡AQUÍ CAPTURAMOS EL ERROR DE DUPLICADO DEL DAO!
            // El mensaje será: "Ya existe una categoría con el nombre '...'"
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

}
