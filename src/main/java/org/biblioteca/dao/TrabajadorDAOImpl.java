package org.biblioteca.dao;

import org.biblioteca.entities.Trabajador;
import org.biblioteca.util.DBConnection;
import org.biblioteca.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrabajadorDAOImpl implements TrabajadorDAO {
    private final Connection conexion;

    public TrabajadorDAOImpl() {
        this.conexion = DBConnection.getConnection();
    }

    private Trabajador extraerTrabajadorDeResultSet(ResultSet rs) throws SQLException {
        // ACTUALIZADO: extrae AHORA todos los campos del trabajador
        return new Trabajador(
                rs.getInt("TrabajadorID"),
                rs.getString("Nombre"),
                rs.getString("Apellido"),  // NUEVO
                rs.getString("DNI"),       // NUEVO
                rs.getString("UsuarioLogin"),
                rs.getInt("RolID"),
                rs.getString("Email"),     // NUEVO
                rs.getString("Telefono"),  // NUEVO
                rs.getString("Estado"),    // NUEVO
                rs.getTimestamp("FechaRegistro")
        );
    }

    @Override
    public void insertar(Trabajador trabajador) {
        // 1. GENERAR HASH Y SALT antes de la inserción
        byte[] salt = PasswordUtil.getNewSalt();
        byte[] contrasenaHash = PasswordUtil.hashPassword(trabajador.getContrasena(), salt);

        if (contrasenaHash == null) {
            throw new RuntimeException("Error al generar el hash de la contraseña.");
        }

        // 2. QUERY ACTUALIZADA: Incluye Apellido, DNI, Email, Telefono, Estado
        String sql = "INSERT INTO Trabajadores (Nombre, Apellido, DNI, UsuarioLogin, ContrasenaHash, Salt, RolID, Email, Telefono, Estado, FechaRegistro) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, trabajador.getNombre());
            stmt.setString(2, trabajador.getApellido());    // NUEVO
            stmt.setString(3, trabajador.getDni());         // NUEVO
            stmt.setString(4, trabajador.getUsuarioLogin());

            // Usamos setBytes para los datos de seguridad (VARBINARY)
            stmt.setBytes(5, contrasenaHash);
            stmt.setBytes(6, salt);

            stmt.setInt(7, trabajador.getRolID());
            stmt.setString(8, trabajador.getEmail());       // NUEVO
            stmt.setString(9, trabajador.getTelefono());    // NUEVO
            stmt.setString(10, trabajador.getEstado());     // NUEVO

            // FechaRegistro se maneja en la DB con DEFAULT, pero aquí la insertamos explícitamente como buena práctica
            stmt.setTimestamp(11, new Timestamp(System.currentTimeMillis()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al insertar Trabajador: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error de persistencia al registrar trabajador.", e);
        }
    }


    // METODO PARA EL LOGIN

    @Override
    public Trabajador obtenerPorUsuarioLogin(String usuarioLogin) {
        // CORRECCIÓN: Agregamos el JOIN para traer también el NombreRol
        String sql = "SELECT t.*, r.NombreRol " +
                "FROM Trabajadores t " +
                "INNER JOIN Roles r ON t.RolID = r.RolID " +
                "WHERE t.UsuarioLogin = ? COLLATE Latin1_General_CS_AS";

        Trabajador trabajador = null;

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, usuarioLogin);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // 1. Extraer datos básicos
                    trabajador = extraerTrabajadorDeResultSet(rs);

                    // 2. Extraer datos de seguridad
                    trabajador.setContrasenaHash(rs.getBytes("ContrasenaHash"));
                    trabajador.setSalt(rs.getBytes("Salt"));

                    // 3. ¡LO MÁS IMPORTANTE! Asignar el nombre del rol
                    trabajador.setNombreRol(rs.getString("NombreRol"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener Trabajador por UsuarioLogin: " + e.getMessage());
            e.printStackTrace();
        }
        return trabajador;
    }


    // Métodos CRUD requeridos por la interfaz

    @Override
    public void actualizar(Trabajador trabajador) {
        String sql = "UPDATE Trabajadores SET Nombre = ?, Apellido = ?, DNI = ?, RolID = ?, Email = ?, Telefono = ?, Estado = ? WHERE TrabajadorID = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, trabajador.getNombre());
            stmt.setString(2, trabajador.getApellido());
            stmt.setString(3, trabajador.getDni());
            stmt.setInt(4, trabajador.getRolID());
            stmt.setString(5, trabajador.getEmail());
            stmt.setString(6, trabajador.getTelefono());
            stmt.setString(7, trabajador.getEstado());
            stmt.setInt(8, trabajador.getTrabajadorID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar Trabajador: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void actualizarContrasena(int trabajadorID, String nuevaContrasena) {
        // 1. GENERAR nuevo HASH y SALT
        byte[] salt = PasswordUtil.getNewSalt();
        byte[] contrasenaHash = PasswordUtil.hashPassword(nuevaContrasena, salt);

        if (contrasenaHash == null) {
            throw new RuntimeException("Error al generar el nuevo hash de la contraseña.");
        }

        String sql = "UPDATE Trabajadores SET ContrasenaHash = ?, Salt = ? WHERE TrabajadorID = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setBytes(1, contrasenaHash);
            stmt.setBytes(2, salt);
            stmt.setInt(3, trabajadorID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar la contraseña del Trabajador: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error de persistencia al actualizar contraseña.", e);
        }
    }

    @Override
    public Trabajador obtenerPorDni(String dni) {
        // 1. LA CONSULTA SQL (Con el JOIN clave)
        // Seleccionamos todo de Trabajadores (t.*) Y el nombre del rol de la tabla Roles (r.NombreRol)
        // Unimos las tablas donde coincidan los IDs (t.RolID = r.RolID)
        String sql = "SELECT t.*, r.NombreRol FROM Trabajadores t " +
                "INNER JOIN Roles r ON t.RolID = r.RolID " +
                "WHERE t.DNI = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, dni);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // 2. CREAR EL OBJETO BASE
                    // Este método (que ya tienes) lee los datos de la tabla 'Trabajadores'
                    Trabajador trabajador = extraerTrabajadorDeResultSet(rs);

                    // 3. LLENAR EL DATO FALTANTE (Del JOIN)
                    // Aquí leemos la columna extra 'NombreRol' que trajimos gracias al INNER JOIN
                    trabajador.setNombreRol(rs.getString("NombreRol"));

                    return trabajador;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener Trabajador por DNI: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM Trabajadores WHERE TrabajadorID = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar Trabajador: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Trabajador obtenerPorId(int id) {
        String sql = "SELECT * FROM Trabajadores WHERE TrabajadorID = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extraerTrabajadorDeResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener Trabajador por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Trabajador> obtenerTodos() {
        List<Trabajador> trabajadores = new ArrayList<>();

        // CORRECCIÓN: Añadimos el JOIN para traer el NombreRol
        String sql = "SELECT t.*, r.NombreRol " +
                "FROM Trabajadores t " +
                "INNER JOIN Roles r ON t.RolID = r.RolID " +
                "ORDER BY t.Nombre";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // 1. Extraemos los datos básicos (ID, Nombre, etc.)
                Trabajador trabajador = extraerTrabajadorDeResultSet(rs);

                // 2. ¡AQUÍ ESTABA EL FALTANTE! Asignamos el nombre del rol
                trabajador.setNombreRol(rs.getString("NombreRol"));

                trabajadores.add(trabajador);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los Trabajadores: " + e.getMessage());
            e.printStackTrace();
        }
        return trabajadores;
    }

}
