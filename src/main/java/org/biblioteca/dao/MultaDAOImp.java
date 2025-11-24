package org.biblioteca.dao;

import org.biblioteca.entities.Multa;
import org.biblioteca.util.DBConnection;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MultaDAOImp implements MultaDAO {


    @Override
    public List<Multa> obtenerTodas() {
        List<Multa> lista = new ArrayList<>();
        String sql = "SELECT MultaID, PrestamoID, LibroID, DiasRetraso, Monto, FechaRegistro FROM Multas";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql);

             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRowToMulta(rs));
            }

        } catch (Exception e) {
            System.err.println("Error en obtenerTodas(): " + e.getMessage());
        }

        return lista;
    }

    @Override
    public List<Multa> obtenerPorDniLector(String dni) {
        List<Multa> multas = new ArrayList<>();
        // Join necesario para llegar desde Multa -> Prestamo -> Lector -> DNI
        String sql = "SELECT m.* FROM Multas m " +
                "JOIN Prestamos p ON m.PrestamoID = p.PrestamoID " +
                "JOIN Lectores l ON p.LectorID = l.LectorID " +
                "WHERE l.DNI LIKE ?"; // Búsqueda parcial

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, dni + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Reutilizamos tu lógica de mapeo existente (puedes refactorizar mapRowToMulta si lo deseas)
                    multas.add(mapRowToMulta(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return multas;
    }

    @Override
    public void eliminar(int idMulta) {
        String sql = "DELETE FROM Multas WHERE MultaID = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idMulta);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean tieneMultaPendiente(int lectorID) {
        // Consultamos si existe AL MENOS UNA multa asociada a los préstamos de este lector
        String sql = "SELECT COUNT(*) FROM Multas m " +
                "JOIN Prestamos p ON m.PrestamoID = p.PrestamoID " +
                "WHERE p.LectorID = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, lectorID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Si el conteo es mayor a 0, tiene multas
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar multas del lector: " + e.getMessage());
            e.printStackTrace();
        }
        return false; // En caso de error o si no hay multas
    }

    private Multa mapRowToMulta(ResultSet rs) throws SQLException {
        Multa m = new Multa();
        m.setMultaID(rs.getInt("MultaID"));
        m.setPrestamoID(rs.getInt("PrestamoID"));
        m.setLibroID(rs.getInt("LibroID"));
        m.setDiasRetraso(rs.getInt("DiasRetraso"));
        m.setMonto(rs.getBigDecimal("Monto"));
        m.setFechaRegistro(rs.getTimestamp("FechaRegistro"));
        return m;
    }
}

