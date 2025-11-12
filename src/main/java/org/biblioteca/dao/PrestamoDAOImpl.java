package org.biblioteca.dao;

import org.biblioteca.entities.Libro; // Necesario para cargar detalles
import org.biblioteca.entities.Prestamo;
import org.biblioteca.entities.PrestamoDetalle;
import org.biblioteca.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.List;

public class PrestamoDAOImpl implements PrestamoDAO {

    private Connection conexion;

    public PrestamoDAOImpl() {
        this.conexion = DBConnection.getConnection();
    }

    private Prestamo extraerPrestamoDeResultSet(ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo(
                rs.getInt("PrestamoID"),
                rs.getInt("LectorID"),
                rs.getInt("TrabajadorID"),
                rs.getTimestamp("FechaPrestamo"),
                rs.getTimestamp("FechaDevolucionPrevista"),
                rs.getTimestamp("FechaDevolucionReal"),
                rs.getString("Estado")
        );
        // Cargar los campos adicionales del JOIN
        prestamo.setLectorNombre(rs.getString("LectorNombre"));
        prestamo.setTrabajadorNombre(rs.getString("TrabajadorNombre"));
        return prestamo;
    }


    private List<PrestamoDetalle> obtenerDetallesParaPrestamo(int prestamoID) throws SQLException {
        List<PrestamoDetalle> detalles = new ArrayList<>();
        String sql = "SELECT * FROM PrestamoDetalle WHERE PrestamoID = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, prestamoID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    detalles.add(new PrestamoDetalle(
                            rs.getInt("DetalleID"),
                            rs.getInt("PrestamoID"),
                            rs.getInt("LibroID"),
                            rs.getInt("Cantidad")
                    ));
                }
            }
        }
        return detalles;
    }

    @Override
    public void insertar(Prestamo prestamo, List<PrestamoDetalle> detalles) throws SQLException {

        // El estado en la BD por defecto es 'Pendiente', pero tu entidad usa 'Activo'
        // Asegurémonos de usar el estado correcto de tu script SQL.
        String estadoDB = "Pendiente"; // Según tu script: DEFAULT ('Pendiente')
        if (prestamo.getEstado() != null && !prestamo.getEstado().isEmpty()) {
            // Permitimos que la entidad defina el estado si lo trae
            estadoDB = prestamo.getEstado();
        }

        String sqlPrestamo = "INSERT INTO Prestamos (LectorID, TrabajadorID, FechaPrestamo, FechaDevolucionPrevista, Estado) VALUES (?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO PrestamoDetalle (PrestamoID, LibroID, Cantidad) VALUES (?, ?, ?)";

        try {
            conexion.setAutoCommit(false);

            int prestamoID;
            try (PreparedStatement stmt = conexion.prepareStatement(sqlPrestamo, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, prestamo.getLectorID());
                stmt.setInt(2, prestamo.getTrabajadorID());
                stmt.setTimestamp(3, prestamo.getFechaPrestamo());
                stmt.setTimestamp(4, prestamo.getFechaDevolucionPrevista());
                stmt.setString(5, estadoDB);
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        prestamoID = rs.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID del préstamo.");
                    }
                }
            }

            try (PreparedStatement stmtDetalle = conexion.prepareStatement(sqlDetalle)) {
                for (PrestamoDetalle detalle : detalles) {
                    stmtDetalle.setInt(1, prestamoID);
                    stmtDetalle.setInt(2, detalle.getLibroID());
                    stmtDetalle.setInt(3, detalle.getCantidad());
                    stmtDetalle.addBatch();
                }
                stmtDetalle.executeBatch();
            }

            // El Trigger 'trg_PrestamoNuevo' se dispara aquí
            conexion.commit();

        } catch (SQLException e) {
            conexion.rollback();
            throw e;
        } finally {
            conexion.setAutoCommit(true);
        }
    }

    @Override
    public void actualizar(Prestamo prestamo) throws SQLException {

        String sql = "UPDATE Prestamos SET FechaDevolucionReal = ? WHERE PrestamoID = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setTimestamp(1, prestamo.getFechaDevolucionReal());
            stmt.setInt(2, prestamo.getPrestamoID());
            stmt.executeUpdate(); // El trigger se dispara aquí
        }
    }

    @Override
    public List<Prestamo> obtenerTodos() {
        List<Prestamo> prestamos = new ArrayList<>();
        // SQL con JOINs para traer los nombres
        String sql = "SELECT p.*, l.Nombre AS LectorNombre, t.Nombre AS TrabajadorNombre " +
                "FROM Prestamos p " +
                "JOIN Lectores l ON p.LectorID = l.LectorID " +
                "JOIN Trabajadores t ON p.TrabajadorID = t.TrabajadorID " +
                "ORDER BY p.FechaPrestamo DESC";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Prestamo prestamo = extraerPrestamoDeResultSet(rs);
                // Cargamos los detalles (libros) para este préstamo
                prestamo.setDetalles(obtenerDetallesParaPrestamo(prestamo.getPrestamoID()));
                prestamos.add(prestamo);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los préstamos: " + e.getMessage());
            e.printStackTrace();
        }
        return prestamos;
    }

    @Override
    public Prestamo obtenerPorId(int id) {
        Prestamo prestamo = null;
        String sql = "SELECT p.*, l.Nombre AS LectorNombre, t.Nombre AS TrabajadorNombre " +
                "FROM Prestamos p " +
                "JOIN Lectores l ON p.LectorID = l.LectorID " +
                "JOIN Trabajadores t ON p.TrabajadorID = t.TrabajadorID " +
                "WHERE p.PrestamoID = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    prestamo = extraerPrestamoDeResultSet(rs);
                    // Cargamos los detalles (libros) para este préstamo
                    prestamo.setDetalles(obtenerDetallesParaPrestamo(prestamo.getPrestamoID()));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener préstamo por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return prestamo;
    }
}
