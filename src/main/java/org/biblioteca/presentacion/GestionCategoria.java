package org.biblioteca.presentacion;

import org.biblioteca.entities.Categoria;
import org.biblioteca.services.CategoriaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class GestionCategoria  extends JFrame{
    private JPanel mainPanel;
    private JTable tableCategoria;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JScrollPane centralPanel;
    private JPanel butonPanel;


    private CategoriaService servicio;
    private DefaultTableModel modeloTabla;
    public GestionCategoria() {
        this.servicio = new CategoriaService();


        setTitle("Gestión de Categorías con UI Designer");

        setContentPane(mainPanel);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Cargar los datos iniciales
        actualizarTabla();

        // --- Lógica de los Eventos ---
        btnNuevo.addActionListener(e -> abrirDialogoEditor(null));
        btnEditar.addActionListener(e -> editarCategoriaSeleccionada());
        btnEliminar.addActionListener(e -> eliminarCategoriaSeleccionada());
    }





    public void actualizarTabla() {
        modeloTabla.setRowCount(0);
        List<Categoria> categorias = servicio.listarCategorias();
        for (Categoria cat : categorias) {
            modeloTabla.addRow(new Object[]{cat.getCategoriaID(), cat.getNombre(), cat.getDescripcion()});
        }
    }

    private void abrirDialogoEditor(Categoria categoria) {
        EditorCategoria dialogo = new EditorCategoria(this, servicio, categoria);
        dialogo.setVisible(true);
        actualizarTabla();
    }

    private void editarCategoriaSeleccionada() {
        int filaSeleccionada = tableCategoria.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una categoría para editar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int idCategoria = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        Categoria categoriaAEditar = servicio.buscarCategoriaPorId(idCategoria);
        abrirDialogoEditor(categoriaAEditar);
    }

    private void eliminarCategoriaSeleccionada() {
        int filaSeleccionada = tableCategoria.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una categoría para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            int idCategoria = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
            servicio.eliminarCategoria(idCategoria); // Corregido el nombre de la variable
            actualizarTabla();
            JOptionPane.showMessageDialog(this, "Categoría eliminada.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GestionCategoria().setVisible(true);
        });
    }

    private void createUIComponents() {


            // Inicializamos el modelo de la tabla aquí
            modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombre", "Descripción"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            // Creamos la JTable y le asignamos el modelo
            tableCategoria = new JTable(modeloTabla);


    }
}
