package org.biblioteca.presentacion;

import org.biblioteca.entities.Autor;
import org.biblioteca.services.AutorService;

import javax.swing.*;

public class EditorAutor extends JDialog {
    private JPanel mainPanel;
    private JPanel centralPanel;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtNacionalidad;
    private JButton btnGuardar;
    private JButton btnCancelar;

    private AutorService servicio;
    private Autor autorActual;


    public EditorAutor(JFrame parent, AutorService servicio, Autor autor) {
        super(parent, true);
        this.servicio = servicio;
        this.autorActual = autor;

        setTitle(autor == null ? "Nuevo Autor" : "Editar Autor");
        setContentPane(mainPanel);
        setSize(400, 250);
        setLocationRelativeTo(parent);

        // Llenar campos si es una edición
        if (autor != null) {
            txtNombre.setText(autor.getNombre());
            txtApellido.setText(autor.getApellido());
            txtNacionalidad.setText(autor.getNacionalidad());
        }

        btnGuardar.addActionListener(e -> guardarAutor());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void guardarAutor() {
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String nacionalidad = txtNacionalidad.getText();



        try {
            if (autorActual == null) {
                // Se registra el nuevo autor (Activa validaciones de la entidad)
                servicio.registrarAutor(nombre, apellido, nacionalidad);
            } else {
                // Se modifica el autor existente (Activa validaciones de la entidad)
                servicio.modificarAutor(autorActual.getAutorID(), nombre, apellido, nacionalidad);
            }
            dispose(); // Cierra el diálogo solo si el guardado fue exitoso
        } catch (IllegalArgumentException ex) {
            // Capturamos el error de validación y lo mostramos al usuario.
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
        }
    }
}
