package org.biblioteca.presentacion;

import org.biblioteca.entities.Autor;
import org.biblioteca.services.AutorService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class GestionAutor extends JFrame {
    private JPanel mainPanel;
    private JScrollPane centralPanel;
    private JTable tablaAutores;
    private JPanel butonPanel;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private DefaultTableModel modeloTabla;
    private AutorService servicio;

    public GestionAutor() {
        this.servicio = new AutorService();

        // Configuración de la ventana
        setTitle("Gestión de Autores");
        setContentPane(mainPanel); // Vinculado desde el UI Designer
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Lógica de los botones
        btnNuevo.addActionListener(e -> abrirDialogoEditor(null));
        btnEditar.addActionListener(e -> editarAutorSeleccionado());
        btnEliminar.addActionListener(e -> eliminarAutorSeleccionado());

        // Cargar datos
        actualizarTabla();
    }

    public void actualizarTabla() {
        modeloTabla.setRowCount(0);
        List<Autor> autores = servicio.listarAutores();
        for (Autor autor : autores) {
            modeloTabla.addRow(new Object[]{
                    autor.getAutorID(),
                    autor.getNombre(),
                    autor.getApellido(),
                    autor.getNacionalidad()
            });
        }
    }

    private void abrirDialogoEditor(Autor autor) {
        EditorAutor dialogo = new EditorAutor(this, servicio, autor);
        dialogo.setVisible(true);
        actualizarTabla();
    }

    private void editarAutorSeleccionado() {
        int fila = tablaAutores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un autor para editar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        Autor autor = servicio.buscarAutorPorId(id);
        abrirDialogoEditor(autor);
    }

    private void eliminarAutorSeleccionado() {
        int fila = tablaAutores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un autor para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "¿Está seguro?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            int id = (int) modeloTabla.getValueAt(fila, 0);
            servicio.eliminarAutor(id);
            actualizarTabla();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GestionAutor().setVisible(true));
    }

    private void createUIComponents() {
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombre", "Apellido", "Nacionalidad"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaAutores = new JTable(modeloTabla);
    }
}
