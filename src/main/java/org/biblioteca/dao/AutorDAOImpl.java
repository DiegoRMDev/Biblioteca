package org.biblioteca.dao;

import org.biblioteca.entities.Autor;
import org.biblioteca.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AutorDAOImpl implements  AutorDAO{

    private Connection conexion;

    public AutorDAOImpl() {
        // Asume que tienes una clase para manejar la conexión
        this.conexion = DBConnection.getConnection();
    }

    @Override
    public void insertar(Autor autor) {
        String sql = "INSERT INTO Autores (Nombre, Apellido, Nacionalidad) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, autor.getNombre());
            stmt.setString(2, autor.getApellido());
            stmt.setString(3, autor.getNacionalidad());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    autor.setAutorID(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void actualizar(Autor autor) {
        String sql = "UPDATE Autores SET Nombre = ?, Apellido = ?, Nacionalidad = ? WHERE AutorID = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, autor.getNombre());
            stmt.setString(2, autor.getApellido());
            stmt.setString(3, autor.getNacionalidad());
            stmt.setInt(4, autor.getAutorID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM Autores WHERE AutorID = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Autor obtenerPorId(int id) {
        String sql = "SELECT * FROM Autores WHERE AutorID = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Autor(
                            rs.getInt("AutorID"),
                            rs.getString("Nombre"),
                            rs.getString("Apellido"),
                            rs.getString("Nacionalidad")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Autor> obtenerTodos() {
        List<Autor> autores = new ArrayList<>();
        String sql = "SELECT * FROM Autores ORDER BY Apellido, Nombre";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                autores.add(new Autor(
                        rs.getInt("AutorID"),
                        rs.getString("Nombre"),
                        rs.getString("Apellido"),
                        rs.getString("Nacionalidad")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return autores;
    }
}
