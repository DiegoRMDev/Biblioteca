package org.biblioteca.dao;

import org.biblioteca.entities.Autor;
import org.biblioteca.entities.Libro;
import org.biblioteca.entities.MovimientoLibro;
import org.biblioteca.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAOImpl implements  LibroDAO{

    private Connection conexion;

    public LibroDAOImpl() {
        this.conexion = DBConnection.getConnection();
    }

    @Override
    public Libro obtenerPorId(int id) {
        Libro libro = null;
        String sqlLibro = "SELECT * FROM Libros WHERE LibroID = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sqlLibro)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // 1. Crea el objeto Libro con sus datos básicos
                    libro = new Libro(
                            rs.getInt("LibroID"),
                            rs.getString("ISBN"),
                            rs.getString("Titulo"),
                            rs.getString("Editorial"),
                            rs.getInt("AnioPublicacion"),
                            rs.getInt("CategoriaID"),
                            rs.getString("Idioma"),
                            rs.getString("UbicacionFisica"),
                            rs.getString("Estado"),
                            rs.getInt("Stock")
                    );

                    // 2. Ahora, busca y asigna su lista de autores
                    List<Autor> autores = obtenerAutoresParaLibro(libro.getLibroID());
                    libro.setAutores(autores);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return libro;
    }

    @Override
    public List<Libro> obtenerTodos() {
        List<Libro> libros = new ArrayList<>();

        // MODIFICACIÓN: Agregamos el JOIN con Categorias y seleccionamos el nombre
        String sql = "SELECT l.*, c.Nombre AS NombreCategoria " +
                "FROM Libros l " +
                "JOIN Categorias c ON l.CategoriaID = c.CategoriaID";

        try (Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Crea el objeto Libro (asegúrate de usar los argumentos correctos según tu constructor actual)
                Libro libro = new Libro(
                        rs.getInt("LibroID"),
                        rs.getString("ISBN"),
                        rs.getString("Titulo"),
                        rs.getString("Editorial"),
                        rs.getInt("AnioPublicacion"),
                        rs.getInt("CategoriaID"),
                        rs.getString("Idioma"),
                        rs.getString("UbicacionFisica"),
                        rs.getString("Estado"),
                        rs.getInt("Stock")
                );

                //  Asignamos el nombre de la categoría recuperado del JOIN
                libro.setCategoriaNombre(rs.getString("NombreCategoria"));

                // Busca y asigna su lista de autores
                libro.setAutores(obtenerAutoresParaLibro(libro.getLibroID()));

                libros.add(libro);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return libros;
    }

    @Override
    public void insertar(Libro libro, List<Autor> autores) {

        String sqlLibro = "INSERT INTO Libros (ISBN, Titulo, Editorial, AnioPublicacion, CategoriaID, Idioma, UbicacionFisica, Estado, Stock) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlLibroAutor = "INSERT INTO LibroAutores (LibroID, AutorID, OrdenAutor) VALUES (?, ?, ?)";

        try {
            conexion.setAutoCommit(false); // Iniciar transacción

            // Insertar el libro y obtener su ID generado
            try (PreparedStatement stmtLibro = conexion.prepareStatement(sqlLibro, Statement.RETURN_GENERATED_KEYS)) {
                // ... setear todos los parámetros del libro ...
                stmtLibro.setString(1, libro.getIsbn());
                stmtLibro.setString(2, libro.getTitulo());
                stmtLibro.setString(3, libro.getEditorial());
                stmtLibro.setInt(4, libro.getAnioPublicacion());
                stmtLibro.setInt(5, libro.getCategoriaID());
                stmtLibro.setString(6, libro.getIdioma());
                stmtLibro.setString(7, libro.getUbicacionFisica());
                stmtLibro.setString(8, libro.getEstado());
                stmtLibro.setInt(9, libro.getStock());
                stmtLibro.executeUpdate();

                try (ResultSet generatedKeys = stmtLibro.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        libro.setLibroID(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Fallo al crear libro, no se obtuvo ID.");
                    }
                }
            }

            // Insertar las relaciones en la tabla intermedia
            try (PreparedStatement stmtLibroAutor = conexion.prepareStatement(sqlLibroAutor)) {
                // Usamos un bucle con índice para saber el orden
                for (int i = 0; i < autores.size(); i++) {
                    Autor autor = autores.get(i);
                    stmtLibroAutor.setInt(1, libro.getLibroID());
                    stmtLibroAutor.setInt(2, autor.getAutorID());

                    stmtLibroAutor.setShort(3, (short) (i + 1));
                    stmtLibroAutor.addBatch();
                }
                stmtLibroAutor.executeBatch();
            }

            conexion.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            try { conexion.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try { conexion.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }

    }

    @Override
    public void actualizar(Libro libro, List<Autor> autores) {
        String sqlLibro = "UPDATE Libros SET ISBN = ?, Titulo = ?, Editorial = ?, AnioPublicacion = ?, " +
                "CategoriaID = ?, Idioma = ?, UbicacionFisica = ?,  " +
                "Estado = ?, Stock = ? WHERE LibroID = ?";
        String sqlDeleteRelaciones = "DELETE FROM LibroAutores WHERE LibroID = ?";
        String sqlInsertRelaciones = "INSERT INTO LibroAutores (LibroID, AutorID, OrdenAutor) VALUES (?, ?, ?)";

        try {
            conexion.setAutoCommit(false);


            try (PreparedStatement stmtLibro = conexion.prepareStatement(sqlLibro)) {
                stmtLibro.setString(1, libro.getIsbn());
                stmtLibro.setString(2, libro.getTitulo());
                stmtLibro.setString(3, libro.getEditorial());
                stmtLibro.setInt(4, libro.getAnioPublicacion());
                stmtLibro.setInt(5, libro.getCategoriaID());
                stmtLibro.setString(6, libro.getIdioma());
                stmtLibro.setString(7, libro.getUbicacionFisica());
                stmtLibro.setString(8, libro.getEstado());
                stmtLibro.setInt(9, libro.getStock());
                stmtLibro.setInt(10, libro.getLibroID());
                stmtLibro.executeUpdate();
            }

            //  Borrar todas las relaciones de autores existentes para este libro
            try (PreparedStatement stmtDelete = conexion.prepareStatement(sqlDeleteRelaciones)) {
                stmtDelete.setInt(1, libro.getLibroID());
                stmtDelete.executeUpdate();
            }

            //  Insertar las nuevas relaciones
            try (PreparedStatement stmtInsert = conexion.prepareStatement(sqlInsertRelaciones)) {
                // Y aplica el mismo bucle con índice
                for (int i = 0; i < autores.size(); i++) {
                    Autor autor = autores.get(i);
                    stmtInsert.setInt(1, libro.getLibroID());
                    stmtInsert.setInt(2, autor.getAutorID());
                    stmtInsert.setShort(3, (short) (i + 1));
                    stmtInsert.addBatch();
                }
                stmtInsert.executeBatch();
            }

            conexion.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            try { conexion.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try { conexion.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public void eliminar(int id) {
        String sqlDeleteRelaciones = "DELETE FROM LibroAutores WHERE LibroID = ?";
        String sqlDeleteLibro = "DELETE FROM Libros WHERE LibroID = ?";

        try {
            conexion.setAutoCommit(false);

            // Primero eliminar las relaciones en la tabla intermedia
            try (PreparedStatement stmtRelaciones = conexion.prepareStatement(sqlDeleteRelaciones)) {
                stmtRelaciones.setInt(1, id);
                stmtRelaciones.executeUpdate();
            }

            //  Luego eliminar el libro de la tabla principal
            try (PreparedStatement stmtLibro = conexion.prepareStatement(sqlDeleteLibro)) {
                stmtLibro.setInt(1, id);
                stmtLibro.executeUpdate();
            }

            conexion.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            try { conexion.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try { conexion.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public Libro obtenerPorIsbn(String isbn) {
        String sql = "SELECT * FROM Libros WHERE ISBN = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Reutilizamos la lógica de creación del objeto
                    Libro libro = new Libro(
                            rs.getInt("LibroID"),
                            rs.getString("ISBN"),
                            rs.getString("Titulo"),
                            rs.getString("Editorial"),
                            rs.getInt("AnioPublicacion"),
                            rs.getInt("CategoriaID"),
                            rs.getString("Idioma"),
                            rs.getString("UbicacionFisica"),
                            rs.getString("Estado"),
                            rs.getInt("Stock")
                    );
                    return libro;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    private List<Autor> obtenerAutoresParaLibro(int libroId) throws SQLException {
        List<Autor> autores = new ArrayList<>();
        String sqlAutores = "SELECT a.* FROM Autores a " +
                "JOIN LibroAutores la ON a.AutorID = la.AutorID " +
                "WHERE la.LibroID = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sqlAutores)) {
            stmt.setInt(1, libroId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    autores.add(new Autor(
                            rs.getInt("AutorID"),
                            rs.getString("Nombre"),
                            rs.getString("Apellido"),
                            rs.getString("Nacionalidad")
                    ));
                }
            }
        }
        return autores;
    }
}
