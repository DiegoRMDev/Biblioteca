package org.biblioteca.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestiona la conexión a la base de datos (Singleton) leyendo credenciales
 * desde el archivo config.properties.
 */

public class DBConnection {
    private static Properties properties = new Properties();
    private static Connection connection = null;

    // Bloque estático: Lee el archivo config.properties
    static {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("FATAL ERROR: No se encontró el archivo 'config.properties' en el classpath.");
            } else {
                properties.load(input);
            }
        } catch (Exception e) {
            System.err.println("FATAL ERROR al cargar las propiedades: " + e.getMessage());
        }
    }

    /**
     * Obtiene una instancia única (singleton) de la conexión a la BD.
     */
    public static Connection getConnection() {
        try {
            // Verificamos si es null O si está cerrada
            if (connection == null || connection.isClosed()) {
                // 1. Construye la URL de conexión
                String server = properties.getProperty("db.server");
                String port = properties.getProperty("db.port");
                String dbName = properties.getProperty("db.name");
                String url = "jdbc:sqlserver://" + server + ":" + port + ";databaseName=" + dbName + ";encrypt=false;trustServerCertificate=true;";

                Class.forName(properties.getProperty("db.driver"));

                // 2. Establece la conexión
                connection = DriverManager.getConnection(
                        url,
                        properties.getProperty("db.user"),
                        properties.getProperty("db.password")
                );
                System.out.println("Conexión a la base de datos establecida (o reconectada).");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver de SQL Server no encontrado. Revisa el pom.xml.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error al conectar. Verifica las credenciales en config.properties: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Cierra la conexión si está abierta.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Conexión cerrada.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
