package org.biblioteca.dao;

import org.biblioteca.entities.Donante;
import org.biblioteca.util.DBConnection;
import java.sql.*;

public class DonanteDAOImpl implements DonanteDAO {
    private Connection conexion;

    public DonanteDAOImpl() {
        this.conexion = DBConnection.getConnection();
    }

    @Override
    public int insertar(Donante donante) {
        String sql = "INSERT INTO Donantes (Nombre) VALUES (?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, donante.getNombre());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    donante.setDonanteID(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al registrar el donante.", e);
        }
        return 0;
    }
}