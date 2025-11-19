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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRowToMulta(rs));
            }

        } catch (Exception e) {
            System.err.println("Error en obtenerTodas(): " + e.getMessage());
        }

        return lista;
    }

    public Multa obtenerPorId(int multaId) throws Exception {
        String sql = "SELECT MultaID, PrestamoID, LibroID, DiasRetraso, Monto, FechaRegistro FROM Multas WHERE MultaID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, multaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMulta(rs);
                }
            }
        }
        return null;
    }
    // mapeo
    private Multa mapRowToMulta(ResultSet rs) throws SQLException {
        Multa m = new Multa();
        m.setMultaID(rs.getInt("MultaID"));
        m.setPrestamoID(rs.getInt("PrestamoID"));
        m.setLibroID(rs.getInt("LibroID"));
        m.setDiasRetraso(rs.getInt("DiasRetraso"));
        m.setMonto(rs.getBigDecimal("Monto"));
        Timestamp ts = rs.getTimestamp("FechaRegistro");
        if (ts != null) {
            m.setFechaRegistro(ts);
        }
        return m;
    }


}

