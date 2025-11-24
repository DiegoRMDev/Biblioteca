package org.biblioteca.dao;

import org.biblioteca.entities.MovimientoLibro;
import org.biblioteca.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class MovimientoLibroDAOImpl implements  MovimientoLibroDAO{

    private Connection conexion;

    public MovimientoLibroDAOImpl() {
        this.conexion = DBConnection.getConnection();
    }
    @Override
    public void insertar(MovimientoLibro movimiento) {
        // Agregamos DonanteID a la consulta
        String sql = "INSERT INTO MovimientosLibros (LibroID, FechaMovimiento, TipoMovimiento, Cantidad, Observaciones, ProveedorID, DonanteID, TrabajadorID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // ... set 1 al 5
            stmt.setInt(1, movimiento.getLibroID());
            stmt.setTimestamp(2, movimiento.getFechaMovimiento());
            stmt.setString(3, movimiento.getTipoMovimiento());
            stmt.setInt(4, movimiento.getCantidad());
            stmt.setString(5, movimiento.getObservaciones());

            // ProveedorID (Param 6)
            if (movimiento.getProveedorID() != null) {
                stmt.setInt(6, movimiento.getProveedorID());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }

            // DonanteID (Param 7 )
            if (movimiento.getDonanteID() != null) {
                stmt.setInt(7, movimiento.getDonanteID());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }

            // TrabajadorID (Param 8)
            stmt.setInt(8, movimiento.getTrabajadorID());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al registrar el movimiento del libro.", e);
        }
    }

    @Override
    public java.util.List<MovimientoLibro> obtenerTodosConDetalles() {
        java.util.List<MovimientoLibro> lista = new java.util.ArrayList<>();

        // SQL con JOINs:
        // T = Trabajadores (para obtener Nombre y Apellido)
        // L = Libros (para obtener el Titulo)
        String sql = "SELECT m.*, " +
                "t.Nombre AS NomTrab, t.Apellido AS ApeTrab, " +
                "l.Titulo AS TituloLibro " +
                "FROM MovimientosLibros m " +
                "INNER JOIN Trabajadores t ON m.TrabajadorID = t.TrabajadorID " +
                "INNER JOIN Libros l ON m.LibroID = l.LibroID " +
                "ORDER BY m.FechaMovimiento DESC";

        try (java.sql.Statement stmt = conexion.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Construimos el objeto base
                MovimientoLibro mov = new MovimientoLibro(
                        rs.getInt("MovimientoID"),
                        rs.getInt("LibroID"),
                        rs.getTimestamp("FechaMovimiento"),
                        rs.getString("TipoMovimiento"),
                        rs.getInt("Cantidad"),
                        rs.getString("Observaciones"),
                        (Integer) rs.getObject("ProveedorID"), // Puede ser NULL
                        (Integer) rs.getObject("DonanteID"),   // Puede ser NULL
                        rs.getInt("TrabajadorID")
                );

                // Llenamos los campos extras para la vista
                String nombreCompleto = rs.getString("NomTrab") + " " + rs.getString("ApeTrab");
                mov.setNombreTrabajador(nombreCompleto);
                mov.setTituloLibro(rs.getString("TituloLibro"));

                lista.add(mov);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public java.util.List<MovimientoLibro> obtenerPorFiltros(String tituloLibro, String tipoMovimiento, String nombreTrabajador) {
        java.util.List<MovimientoLibro> lista = new java.util.ArrayList<>();

        // Consulta Base con JOINs
        StringBuilder sql = new StringBuilder(
                "SELECT m.*, " +
                        "t.Nombre AS NomTrab, t.Apellido AS ApeTrab, " +
                        "l.Titulo AS TituloLibro " +
                        "FROM MovimientosLibros m " +
                        "JOIN Trabajadores t ON m.TrabajadorID = t.TrabajadorID " +
                        "JOIN Libros l ON m.LibroID = l.LibroID " +
                        "WHERE 1=1 "); // Truco para concatenar ANDs fácilmente

        java.util.List<Object> parametros = new java.util.ArrayList<>();

        // Filtro por Libro
        if (tituloLibro != null && !tituloLibro.trim().isEmpty()) {
            sql.append(" AND l.Titulo LIKE ? ");
            parametros.add("%" + tituloLibro + "%");
        }

        // Filtro por Tipo de Movimiento (Búsqueda exacta o "Todos")
        if (tipoMovimiento != null && !tipoMovimiento.trim().isEmpty() && !tipoMovimiento.equals("Todos")) {
            sql.append(" AND m.TipoMovimiento = ? ");
            parametros.add(tipoMovimiento);
        }

        // Filtro por Trabajador (Busca en Nombre O Apellido)
        if (nombreTrabajador != null && !nombreTrabajador.trim().isEmpty()) {
            sql.append(" AND (t.Nombre LIKE ? OR t.Apellido LIKE ?) ");
            parametros.add("%" + nombreTrabajador + "%");
            parametros.add("%" + nombreTrabajador + "%");
        }

        sql.append(" ORDER BY m.FechaMovimiento DESC");

        try (java.sql.PreparedStatement stmt = conexion.prepareStatement(sql.toString())) {
            // Asignar parámetros
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }

            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Reutilizamos la lógica de construcción (puedes extraer esto a un método privado helper)
                    MovimientoLibro mov = new MovimientoLibro(
                            rs.getInt("MovimientoID"),
                            rs.getInt("LibroID"),
                            rs.getTimestamp("FechaMovimiento"),
                            rs.getString("TipoMovimiento"),
                            rs.getInt("Cantidad"),
                            rs.getString("Observaciones"),
                            (Integer) rs.getObject("ProveedorID"),
                            (Integer) rs.getObject("DonanteID"),
                            rs.getInt("TrabajadorID")
                    );

                    String nombreCompleto = rs.getString("NomTrab") + " " + rs.getString("ApeTrab");
                    mov.setNombreTrabajador(nombreCompleto);
                    mov.setTituloLibro(rs.getString("TituloLibro"));

                    lista.add(mov);
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }


}
