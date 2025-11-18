package org.biblioteca.dao;
import org.biblioteca.entities.Categoria;
import org.biblioteca.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CategoriaDAOImpl implements  CategoriaDAO {
    private Connection conexion;

    public CategoriaDAOImpl() {
        this.conexion = DBConnection.getConnection();
    }

    @Override
    public void insertar(Categoria categoria) {

        String sql = "INSERT INTO Categorias (Nombre, Descripcion) VALUES (?, ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, categoria.getNombre());
            stmt.setString(2, categoria.getDescripcion());
            stmt.executeUpdate();
            // CAMBIO 2: Recuperar y asignar la clave generada
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    // Asignar el ID de vuelta al objeto
                    categoria.setCategoriaID(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de errores
        }

    }

    @Override
    public void actualizar(Categoria categoria) {

        String sql = "UPDATE Categorias SET Nombre = ?, Descripcion = ? WHERE CategoriaID = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, categoria.getNombre());
            stmt.setString(2, categoria.getDescripcion());
            stmt.setInt(3, categoria.getCategoriaID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM Categorias WHERE CategoriaID = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Categoria obtenerPorId(int id) {
        String sql = "SELECT * FROM Categorias WHERE CategoriaID = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Categoria(
                            rs.getInt("CategoriaID"),
                            rs.getString("Nombre"),
                            rs.getString("Descripcion")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // No se encontró
    }

    @Override
    public List<Categoria> obtenerTodos() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM Categorias ORDER BY Nombre";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categorias.add(new Categoria(
                        rs.getInt("CategoriaID"),
                        rs.getString("Nombre"),
                        rs.getString("Descripcion")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorias;
    }
}
