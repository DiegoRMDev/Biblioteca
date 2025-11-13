package org.biblioteca.dao;
import org.biblioteca.entities.Multa;
import org.biblioteca.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MultaDAOImpl implements MultaDAO {

    private Connection conexion;

    public MultaDAOImpl() {
        this.conexion = DBConnection.getConnection();
    }

    @Override
    public List<Multa> obtenerTodas() {
        List<Multa> multas = new ArrayList<>();
        // Traemos también el nombre del lector para el reporte
        String sql = "SELECT m.*, l.Nombre AS LectorNombre " +
                "FROM Multas m " +
                "JOIN Prestamos p ON m.PrestamoID = p.PrestamoID " +
                "JOIN Lectores l ON p.LectorID = l.LectorID " +
                "ORDER BY m.FechaRegistro DESC";

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Multa multa = new Multa(
                        rs.getInt("MultaID"),
                        rs.getInt("PrestamoID"),
                        rs.getInt("LibroID"),
                        rs.getInt("DiasRetraso"),
                        rs.getBigDecimal("Monto"),
                        rs.getTimestamp("FechaRegistro")
                );
                // NOTA: La entidad Multa no tiene un campo 'LectorNombre',
                // pero lo usaremos en un DTO o en la capa de servicio.
                // Por ahora, solo cargamos la entidad Multa.
                multas.add(multa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return multas;
    }
}
