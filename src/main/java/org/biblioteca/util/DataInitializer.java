package org.biblioteca.util;

import org.biblioteca.entities.Rol;
import org.biblioteca.services.RolService;
import org.biblioteca.services.TrabajadorService;

public class DataInitializer {

    private static final RolService rolService = new RolService();
    private static final TrabajadorService trabajadorService = new TrabajadorService();


    private static final String ROL_ADMIN = "Administrador";
    private static final String ROL_BIBLIOTECARIO = "Bibliotecario";

    private DataInitializer() {
        // Constructor privado para evitar instanciación (Clase de Utilidad)
    }

    /**
     * Metodo principal para inicializar los roles y el trabajador administrador por defecto.
     * Debe llamarse una sola vez al inicio de la aplicación.
     */
    public static void inicializarDatos() {
        System.out.println("--- Iniciando Data Initializer ---");
        try {
            // 1. Inicializar Roles
            inicializarRoles();

            // 2. Inicializar Trabajador Administrador por defecto
            inicializarTrabajadorAdmin();

        } catch (Exception e) {
            System.err.println("¡ERROR CRÍTICO! No se pudieron inicializar los datos de la aplicación.");
            e.printStackTrace();
        }
        System.out.println("--- Data Initializer Finalizado ---");
    }

    private static void inicializarRoles() {
        System.out.println("Verificando Roles...");

        // Usamos el SERVICE para verificar la existencia (evita duplicados).
        if (rolService.obtenerRolPorNombre(ROL_ADMIN) == null) {
            rolService.registrarRol(ROL_ADMIN);
            System.out.println("-> Rol '" + ROL_ADMIN + "' insertado.");
        } else {
            System.out.println("-> Rol '" + ROL_ADMIN + "' ya existe.");
        }

        if (rolService.obtenerRolPorNombre(ROL_BIBLIOTECARIO) == null) {
            rolService.registrarRol(ROL_BIBLIOTECARIO);
            System.out.println("-> Rol '" + ROL_BIBLIOTECARIO + "' insertado.");
        } else {
            System.out.println("-> Rol '" + ROL_BIBLIOTECARIO + "' ya existe.");
        }
    }

    private static void inicializarTrabajadorAdmin() {
        System.out.println("Verificando Trabajador Administrador...");

        // 1. Verificar si ya existe un trabajador con el usuario 'admin'.
        if (trabajadorService.obtenerTrabajadorPorUsuarioLogin("JuanAdmin") == null) {

            // 2. Obtener el RolID del Administrador (se garantiza su existencia en el paso anterior)
            Rol rolAdmin = rolService.obtenerRolPorNombre(ROL_ADMIN);

            if (rolAdmin != null) {
                // 3. Registrar el trabajador usando el SERVICE.
                trabajadorService.registrarTrabajador(
                        "Juan Perez", // Nombre
                        "JuanAdmin",                 // UsuarioLogin
                        "admin123",                // Contrasena
                        rolAdmin.getRolID()      // RolID
                );
                System.out.println("-> Trabajador 'JuanAdmin' creado con contraseña 'admin123'.");
            } else {
                System.err.println("ERROR: No se pudo obtener el Rol ID para Administrador. No se creó el usuario Admin.");
            }
        } else {
            System.out.println("-> Trabajador 'JuanAdmin' ya existe. Omitting initialization.");
        }
    }
}
