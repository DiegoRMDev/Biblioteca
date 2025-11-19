package org.biblioteca.dao;

import org.biblioteca.entities.MovimientoLibro;
import org.biblioteca.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
public class MovimientoLibroDAOImpl implements  MovimientoLibroDAO{

    private Connection conexion;

    public MovimientoLibroDAOImpl() {
        this.conexion = DBConnection.getConnection();
    }
    @Override
    public void insertar(MovimientoLibro movimiento) {
        String sql = "INSERT INTO MovimientosLibros (LibroID, FechaMovimiento, TipoMovimiento, Cantidad, Observaciones, ProveedorID, TrabajadorID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, movimiento.getLibroID());
            stmt.setTimestamp(2, movimiento.getFechaMovimiento());
            stmt.setString(3, movimiento.getTipoMovimiento());
            stmt.setInt(4, movimiento.getCantidad());
            stmt.setString(5, movimiento.getObservaciones());

            // ProveedorID puede ser nulo
            if (movimiento.getProveedorID() != null) {
                stmt.setInt(6, movimiento.getProveedorID());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }

            stmt.setInt(7, movimiento.getTrabajadorID());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al registrar el movimiento del libro.", e);
        }


    }
}
