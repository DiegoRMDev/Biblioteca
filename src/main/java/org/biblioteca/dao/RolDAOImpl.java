package org.biblioteca.dao;

import org.biblioteca.entities.Rol;
import org.biblioteca.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolDAOImpl implements RolDAO {
    private final Connection conexion;

    public RolDAOImpl() {
        // Obtenemos la conexión singleton
        this.conexion = DBConnection.getConnection();
    }

    private Rol extraerRolDeResultSet(ResultSet rs) throws SQLException {
        return new Rol(
                rs.getInt("RolID"),
                rs.getString("NombreRol")
        );
    }

    @Override
    public void insertar(Rol rol) {
        // Asumiendo que la BD autogenera el RolID
        String sql = "INSERT INTO Roles (NombreRol) VALUES (?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            // El setter de la entidad ya validó el nombre
            stmt.setString(1, rol.getNombreRol());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al insertar Rol: " + e.getMessage());
            // En un sistema real, se lanzaría una excepción más específica (ej. RolDuplicadoException)
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Rol rol) {
        String sql = "UPDATE Roles SET NombreRol = ? WHERE RolID = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, rol.getNombreRol());
            stmt.setInt(2, rol.getRolID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar Rol: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM Roles WHERE RolID = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Manejar error de llave foránea (si el rol está asociado a un trabajador)
            System.err.println("Error al eliminar Rol: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Rol obtenerPorId(int id) {
        String sql = "SELECT RolID, NombreRol FROM Roles WHERE RolID = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extraerRolDeResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener Rol por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Rol> obtenerTodos() {
        List<Rol> roles = new ArrayList<>();
        String sql = "SELECT RolID, NombreRol FROM Roles ORDER BY NombreRol";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                roles.add(extraerRolDeResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los Roles: " + e.getMessage());
            e.printStackTrace();
        }
        return roles;
    }

    @Override
    public Rol obtenerPorNombre(String nombre) {
        String sql = "SELECT RolID, NombreRol FROM Roles WHERE NombreRol = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extraerRolDeResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener Rol por Nombre: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
