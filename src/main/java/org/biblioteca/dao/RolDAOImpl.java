package org.biblioteca.dao;

import org.biblioteca.entities.Rol;
import org.biblioteca.util.DBConnection;
import java.sql.Statement;

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
        String sql = "INSERT INTO Roles (NombreRol) VALUES (?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, rol.getNombreRol());
            stmt.executeUpdate();

            // RECUPERAR EL ID GENERADO POR LA BASE DE DATOS
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    // Asignamos el ID autogenerado al objeto Rol
                    rol.setRolID(idGenerado);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar Rol: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error de persistencia al insertar Rol.", e);
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
