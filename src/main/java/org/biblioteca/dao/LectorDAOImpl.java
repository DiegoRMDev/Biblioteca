package org.biblioteca.dao;

import org.biblioteca.entities.Lector;
import org.biblioteca.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LectorDAOImpl implements LectorDAO {

    private Connection conexion;

    public LectorDAOImpl() {
        this.conexion = DBConnection.getConnection();
    }
    @Override
    public void insertar(Lector lector) {

        String sql = "INSERT INTO Lectores (DNI, Nombre, Direccion, Telefono, Email) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, lector.getDni());
            stmt.setString(2, lector.getNombre());
            stmt.setString(3, lector.getDireccion());
            stmt.setString(4, lector.getTelefono());
            stmt.setString(5, lector.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void actualizar(Lector lector) {
        String sql = "UPDATE Lectores SET DNI = ?, Nombre = ?, Direccion = ?, Telefono = ?, Email = ? WHERE LectorID = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, lector.getDni());
            stmt.setString(2, lector.getNombre());
            stmt.setString(3, lector.getDireccion());
            stmt.setString(4, lector.getTelefono());
            stmt.setString(5, lector.getEmail());
            stmt.setInt(6, lector.getLectorID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM Lectores WHERE LectorID = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Lector obtenerPorId(int id) {
        String sql = "SELECT * FROM Lectores WHERE LectorID = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Lector(
                            rs.getInt("LectorID"),
                            rs.getString("DNI"),
                            rs.getString("Nombre"),
                            rs.getString("Direccion"),
                            rs.getString("Telefono"),
                            rs.getString("Email"),
                            rs.getTimestamp("FechaRegistro")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public List<Lector> obtenerTodos() {
        List<Lector> lectores = new ArrayList<>();
        String sql = "SELECT * FROM Lectores ORDER BY Nombre";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lectores.add(new Lector(
                        rs.getInt("LectorID"),
                        rs.getString("DNI"),
                        rs.getString("Nombre"),
                        rs.getString("Direccion"),
                        rs.getString("Telefono"),
                        rs.getString("Email"),
                        rs.getTimestamp("FechaRegistro")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lectores;
    }
}
