package org.biblioteca.presentacion;

import org.biblioteca.entities.Lector;
import org.biblioteca.entities.Libro;
import org.biblioteca.entities.Prestamo;
import org.biblioteca.entities.PrestamoDetalle;
import org.biblioteca.services.LectorService;
import org.biblioteca.services.LibroService;
import org.biblioteca.services.PrestamoService;
import org.biblioteca.util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class EditorPrestamo extends JDialog {
    private JPanel mainPanel;
    private JComboBox cboLectores;
    private JSpinner spinnerFechaDevolucion;
    private JPanel panelSuperior;
    private JPanel panelAgregarLibro;
    private JScrollPane scrollDetalles;
    private JTable tablaDetalles;
    private JComboBox cboLibros;
    private JSpinner spinnerCantidad;
    private JPanel panelInferior;
    private JButton btnCancelar;
    private JButton btnGuardar;
    private JButton btnQuitarLibro;
    private JLabel Libro;
    private JButton btnAgregarLibro;
    private JLabel lblLector;
    private JLabel lblCantidad;
    private JPanel panelQuitar;
    private JLabel lblFechaDev;
    private JPanel panelCentral;

    private PrestamoService prestamoService;
    private LectorService lectorService;
    private LibroService libroService;

    private DefaultTableModel modeloDetalles;

    private Map<Integer, Libro> mapaLibrosDisponibles;

    public EditorPrestamo(JFrame parent, PrestamoService prestamoService) {
        super(parent, true);
        this.prestamoService = prestamoService;

        // Instanciamos los servicios que necesitamos para poblar los combos
        this.lectorService = new LectorService();
        this.libroService = new LibroService();
        this.mapaLibrosDisponibles = new HashMap<>();

        setTitle("Registrar Nuevo Préstamo");
        setContentPane(mainPanel);

        configurarSpinners();
        cargarDatosIniciales();
        configurarListeners();

        pack(); // Ajusta el tamaño al contenido
        setLocationRelativeTo(parent);
    }



    private void configurarSpinners() {
        // Configurar spinner de cantidad (para libros)
        // Valor inicial 1, mínimo 1, máximo 10 (límite por préstamo), paso 1
        SpinnerNumberModel cantidadModel = new SpinnerNumberModel(1, 1, 10, 1);
        spinnerCantidad.setModel(cantidadModel);

        // Configurar spinner de fecha
        // Valor inicial: 7 días desde hoy
        Instant hoy = Instant.now();
        Date fechaDefault = Date.from(hoy.plus(7, ChronoUnit.DAYS));
        Date fechaMin = Date.from(hoy.plus(1, ChronoUnit.DAYS)); // Mínimo mañana

        // Modelo de fecha: valor inicial, fecha mínima, sin fecha máxima, paso (día)
        SpinnerDateModel fechaModel = new SpinnerDateModel(fechaDefault, fechaMin, null, Calendar.DAY_OF_MONTH);
        spinnerFechaDevolucion.setModel(fechaModel);

        // Formato visual para la fecha
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerFechaDevolucion, "dd/MM/yyyy");
        spinnerFechaDevolucion.setEditor(dateEditor);
    }

    private void cargarDatosIniciales() {
        // Cargar Lectores
        try {
            List<Lector> lectores = lectorService.listarLectores();
            DefaultComboBoxModel<Lector> lectorModel = new DefaultComboBoxModel<>();
            lectores.forEach(lectorModel::addElement);
            cboLectores.setModel(lectorModel);
            cboLectores.setSelectedItem(null); // Empezar sin selección
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar lectores: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Cargar Libros (sólo los que están 'Activo' y tienen stock > 0)
        try {
            List<Libro> librosDisponibles = libroService.listarLibros().stream()
                    .filter(libro -> libro.getEstado().equals("Activo") && libro.getStock() > 0)
                    .collect(Collectors.toList());

            DefaultComboBoxModel<Libro> libroModel = new DefaultComboBoxModel<>();
            mapaLibrosDisponibles.clear(); // Limpiar el mapa

            for (Libro libro : librosDisponibles) {
                libroModel.addElement(libro); // El 'toString()' de Libro mostrará el título
                mapaLibrosDisponibles.put(libro.getLibroID(), libro); // Guardar en el mapa para consultar stock
            }

            cboLibros.setModel(libroModel);
            cboLibros.setSelectedItem(null);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar libros: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configurarListeners() {
        btnAgregarLibro.addActionListener(this::agregarLibro);
        btnQuitarLibro.addActionListener(this::quitarLibro);
        btnGuardar.addActionListener(this::guardarPrestamo);
        btnCancelar.addActionListener(e -> dispose());
    }

    private void agregarLibro(ActionEvent e) {
        Libro libroSeleccionado = (Libro) cboLibros.getSelectedItem();
        if (libroSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un libro.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cantidadDeseada = (Integer) spinnerCantidad.getValue();
        int libroID = libroSeleccionado.getLibroID();

        //  Validar si el libro ya fue agregado a la tabla
        for (int i = 0; i < modeloDetalles.getRowCount(); i++) {
            int idEnTabla = (int) modeloDetalles.getValueAt(i, 0); // Columna 0 = ID Libro
            if (idEnTabla == libroID) {
                JOptionPane.showMessageDialog(this, "El libro '" + libroSeleccionado.getTitulo() + "' ya está en la lista.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Validar stock (usando nuestro mapa)
        Libro libroCompleto = mapaLibrosDisponibles.get(libroID);
        if (cantidadDeseada > libroCompleto.getStock()) {
            JOptionPane.showMessageDialog(this,
                    "Stock insuficiente para '" + libroCompleto.getTitulo() + "'.\nStock disponible: " + libroCompleto.getStock(),
                    "Validación de Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }


        modeloDetalles.addRow(new Object[]{
                libroCompleto.getLibroID(),
                libroCompleto.getTitulo(),
                cantidadDeseada
        });
    }
    private void quitarLibro(ActionEvent e) {
        int filaSeleccionada = tablaDetalles.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un libro de la tabla para quitarlo.", "Acción Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convertir la fila de la vista a la fila del modelo (por si la tabla está ordenada)
        int modelRow = tablaDetalles.convertRowIndexToModel(filaSeleccionada);
        modeloDetalles.removeRow(modelRow);
    }

    private void guardarPrestamo(ActionEvent e) {
        try {
            //  Recolección de Datos ---
            Lector lector = (Lector) cboLectores.getSelectedItem();
            Date fechaSpinner = (Date) spinnerFechaDevolucion.getValue();
            Timestamp fechaDevolucion = new Timestamp(fechaSpinner.getTime());
            Timestamp fechaPrestamo = Timestamp.from(Instant.now());

            //. Validaciones de UI ---
            if (lector == null) {
                throw new IllegalArgumentException("Debe seleccionar un Lector.");
            }
            if (modeloDetalles.getRowCount() == 0) {
                throw new IllegalArgumentException("Debe agregar al menos un libro al préstamo.");
            }
            if (fechaDevolucion.before(fechaPrestamo)) {
                throw new IllegalArgumentException("La fecha de devolución no puede ser anterior a la fecha actual.");
            }

            //  Crear Objeto Prestamo (Maestro) ---
            Prestamo nuevoPrestamo = new Prestamo();
            nuevoPrestamo.setLectorID(lector.getLectorID());
            nuevoPrestamo.setTrabajadorID(SessionManager.getCurrentTrabajador().getTrabajadorID()); //
            nuevoPrestamo.setFechaPrestamo(fechaPrestamo);
            nuevoPrestamo.setFechaDevolucionPrevista(fechaDevolucion);
            nuevoPrestamo.setEstado("Pendiente"); // Usamos el estado 'Pendiente' del script de BD

            //Crear Lista de Detalles (Hijos) ---
            List<PrestamoDetalle> detalles = new ArrayList<>();
            for (int i = 0; i < modeloDetalles.getRowCount(); i++) {
                int libroID = (int) modeloDetalles.getValueAt(i, 0); // Columna 0 = ID
                int cantidad = (int) modeloDetalles.getValueAt(i, 2); // Columna 2 = Cantidad

                // Usamos el constructor simple de PrestamoDetalle
                detalles.add(new PrestamoDetalle(0, libroID, cantidad)); // 0 es placeholder para PrestamoID
            }

            // --- 5. Llamar al Servicio (Aquí ocurre la transacción) ---
            prestamoService.registrarPrestamo(nuevoPrestamo, detalles);

            // --- 6. Éxito ---
            JOptionPane.showMessageDialog(this,
                    "Préstamo registrado exitosamente.\nEl stock de los libros ha sido actualizado.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            dispose(); // Cerrar la ventana

        } catch (IllegalArgumentException ex) {
            // Captura errores de validación de la UI
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Datos Inválidos", JOptionPane.WARNING_MESSAGE);

        } catch (Exception ex) {
            // Captura errores del backend (ej. "Sin stock" del Trigger SQL)
            JOptionPane.showMessageDialog(this, "Error al guardar el préstamo: \n" + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Imprime el stack trace completo en la consola
        }
    }

    private void createUIComponents() {
        modeloDetalles = new DefaultTableModel(new Object[]{"ID Libro", "Título", "Cantidad"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacemos la tabla no editable
            }
        };
        tablaDetalles = new JTable(modeloDetalles);

        // Opcional: Ocultar la columna de ID (es útil tenerla pero no visible)
        tablaDetalles.removeColumn(tablaDetalles.getColumnModel().getColumn(0));
    }
}
