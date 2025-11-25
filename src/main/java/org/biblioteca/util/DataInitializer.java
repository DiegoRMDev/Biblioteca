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
        System.out.println("Verificando Trabajador...");

        if (trabajadorService.obtenerTrabajadorPorUsuarioLogin("Juan15") == null) {

            Rol rolAdmin = rolService.obtenerRolPorNombre(ROL_ADMIN);

            if (rolAdmin != null) {

                trabajadorService.registrarTrabajador(
                        "Juan",
                        "Perez",
                        "72345678",
                        "Juan15",
                        "admin123",
                        rolAdmin.getRolID(),
                        "juan20@gmail.com",
                        "988800999"
                );
                System.out.println("-> Trabajador 'Juan15' creado con exito y datos completos.");
            } else {
                System.err.println("ERROR: No se pudo obtener el Rol ID para Administrador. No se creó el usuario Admin.");
            }
        } else {
            System.out.println("-> Trabajador 'Juan15' ya existe. Omitting initialization.");
        }
    }
}
