package org.biblioteca.presentacion;

import org.biblioteca.entities.Trabajador;
import org.biblioteca.services.RolService;
import org.biblioteca.services.TrabajadorService;
import org.biblioteca.util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.event.ListSelectionListener;

public class GestionTrabajador extends JPanel {
    private JPanel mainPanel;
    private JTable tablaTrabajadores;
    private JScrollPane centralPanel;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;

    private DefaultTableModel modeloTabla;
    private TrabajadorService trabajadorService;

    public GestionTrabajador() {
        this.trabajadorService = new TrabajadorService();


        // 1. Configuración de Layout
        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);

        // 2. Aplicar permisos CRUD
        aplicarPermisosCRUD();

        // 3. Configurar Listeners
        btnNuevo.addActionListener(e -> abrirDialogoEditor(null));
        btnEditar.addActionListener(e -> editarTrabajadorSeleccionado());
        btnEliminar.addActionListener(e -> eliminarTrabajadorSeleccionado());

        tablaTrabajadores.getSelectionModel().addListSelectionListener(e -> verificarSeleccion());

        // 4. Cargar datos
        actualizarTabla();
    }

    private void aplicarPermisosCRUD() {
        // Solo el Administrador puede crear, editar o eliminar trabajadores
        if (!SessionManager.esAdministrador()) {
            btnNuevo.setEnabled(false);
            btnEditar.setEnabled(false);
            btnEliminar.setEnabled(false);
        }
    }

    public void actualizarTabla() {
        modeloTabla.setRowCount(0); // Limpiar la tabla
        List<Trabajador> trabajadores = trabajadorService.listarTrabajadores();
        for (Trabajador t : trabajadores) {
            // **MODIFICACIÓN AQUÍ:** Incluyendo Apellido, DNI, Email y Estado.
            modeloTabla.addRow(new Object[]{
                    t.getTrabajadorID(),
                    t.getNombre(),
                    t.getApellido(),       // NUEVO
                    t.getDni(),            // NUEVO
                    t.getUsuarioLogin(),
                    t.getNombreRol(),
                    t.getTelefono(),
                    t.getEmail(),          // NUEVO
                    t.getEstado(),         // NUEVO
                    t.getFechaRegistro(),

            });
        }
    }

    private void abrirDialogoEditor(Trabajador trabajador) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // El diálogo necesita el servicio de Trabajador y el servicio de Rol (para el ComboBox)
        EditorTrabajador dialogo = new EditorTrabajador(parentFrame, trabajadorService, new RolService(), trabajador);
        dialogo.setVisible(true);
        actualizarTabla();
    }

    private void editarTrabajadorSeleccionado() {
        int fila = tablaTrabajadores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un trabajador para editar.", "Acción Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int trabajadorID = (int) modeloTabla.getValueAt(fila, 0);


        Trabajador trabajadorAEditar = trabajadorService.obtenerPorId(trabajadorID);

        if (trabajadorAEditar != null) {
            abrirDialogoEditor(trabajadorAEditar);
        } else {
            JOptionPane.showMessageDialog(this, "Error: No se pudo encontrar el trabajador con ID: " + trabajadorID, "Error de Edición", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verificarSeleccion() {
        // Solo aplica la lógica avanzada si el usuario en sesión es Administrador
        if (!SessionManager.esAdministrador()) {
            return;
        }

        int fila = tablaTrabajadores.getSelectedRow();
        boolean habilitarEditar = true;
        boolean habilitarEliminar = true;

        if (fila != -1) {
            int trabajadorID = (int) modeloTabla.getValueAt(fila, 0);
            String nombreRol = (String) modeloTabla.getValueAt(fila, 5); // La columna 5 es "Rol"

            // Obtener ID del usuario en sesión
            int idSesion = SessionManager.getCurrentTrabajador().getTrabajadorID();

            // REGLAS AVANZADAS:
            boolean esAdmin = "Administrador".equalsIgnoreCase(nombreRol);
            boolean esMismaCuenta = (trabajadorID == idSesion);

            if (esAdmin) {
                // Si selecciona a un Admin (incluyéndose a sí mismo)
                habilitarEliminar = false; // No puede eliminar a ningún Admin.
                if (esMismaCuenta) {
                    // Si se edita a sí mismo, SI puede editar (Edición de datos propios está permitida)
                    habilitarEditar = true;
                } else {
                    // Si edita a otro Admin, la edición solo permite cambiar el estado (el EditorTrabajador lo restringirá)
                    habilitarEditar = true;
                }
            }
        }

        // Aplicar la restricción al botón Eliminar
        btnEliminar.setEnabled(habilitarEliminar);

    }

    private void eliminarTrabajadorSeleccionado() {
        int fila = tablaTrabajadores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un trabajador para eliminar.", "Acción Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int trabajadorID = (int) modeloTabla.getValueAt(fila, 0);
        String nombreRol = (String) modeloTabla.getValueAt(fila, 5);
        int idSesion = SessionManager.getCurrentTrabajador().getTrabajadorID();

        boolean esAdmin = "Administrador".equalsIgnoreCase(nombreRol);
        boolean esMismaCuenta = (trabajadorID == idSesion);

        if (SessionManager.esAdministrador() && (esAdmin || esMismaCuenta)) {
            JOptionPane.showMessageDialog(this, "Operación Prohibida: No puedes eliminar un Administrador ni tu propia cuenta.", "Error de Seguridad", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este trabajador?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {

            try {
                // Llama al servicio, que contiene la lógica de seguridad
                trabajadorService.eliminarTrabajador(trabajadorID);

                actualizarTabla();
                JOptionPane.showMessageDialog(this, "Trabajador eliminado con éxito.", "Eliminación", JOptionPane.INFORMATION_MESSAGE);

            } catch (SecurityException ex) {
                // CAPTURA LA EXCEPCIÓN DE SEGURIDAD (Admin eliminando Admin, o auto-eliminación)
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Seguridad", JOptionPane.ERROR_MESSAGE);

            } catch (RuntimeException ex) {
                // Capturar errores de BD (clave foránea, etc.)
                JOptionPane.showMessageDialog(this, "Error al eliminar: El trabajador podría tener registros asociados o hubo un error de persistencia.", "Error de Persistencia", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();

            } catch (Exception ex) {
                // Capturar la Exception genérica (como "El trabajador a eliminar no existe")
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void createUIComponents() {
        // **MODIFICACIÓN AQUÍ:**
        // 1. Inicializamos el modelo con las nuevas columnas
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombre", "Apellido", "DNI", "Usuario","Rol","Telefono", "Email", "Estado", "Fecha Registro"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        // 2. Creamos la JTable y le asignamos el modelo
        tablaTrabajadores = new JTable(modeloTabla);
    }

}
