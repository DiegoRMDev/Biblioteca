package org.biblioteca.util;

import org.biblioteca.entities.Trabajador;

public class SessionManager {
    // Variable estática que guarda la información del trabajador logueado
    private static Trabajador currentTrabajador;

    private SessionManager() {

    }

    /**
     * Inicia una nueva sesión guardando el objeto Trabajador autenticado.
     * @param trabajador El Trabajador que ha iniciado sesión exitosamente.
     */
    public static void iniciarSesion(Trabajador trabajador) {
        if (trabajador == null) {
            throw new IllegalArgumentException("El objeto Trabajador no puede ser nulo al iniciar sesión.");
        }
        currentTrabajador = trabajador;
        // Opcionalmente, imprimir un mensaje de inicio de sesión
        System.out.println("Sesión iniciada: " + trabajador.getUsuarioLogin() + " (Rol ID: " + trabajador.getRolID() + ")");
    }

    /**
     * Cierra la sesión activa.
     */
    public static void cerrarSesion() {
        if (currentTrabajador != null) {
            System.out.println("Sesión cerrada: " + currentTrabajador.getUsuarioLogin());
        }
        currentTrabajador = null;
    }

    /**
     * Obtiene el Trabajador actualmente logueado.
     * @return El objeto Trabajador, o null si no hay sesión activa.
     */
    public static Trabajador getCurrentTrabajador() {
        return currentTrabajador;
    }

    /**
     * Verifica si hay una sesión activa.
     * @return true si hay un trabajador logueado, false en caso contrario.
     */
    public static boolean isSesionActiva() {
        return currentTrabajador != null;
    }

    /**
     * ==========================================================
     * MÉTODOS DE PERMISOS BASADOS EN EL ROL (Según la discusión previa)
     * ==========================================================
     */

    /**
     * Verifica si el usuario logueado es Administrador.
     * (Asumimos RolID = 1 para Administrador)
     * @return true si el RolID es 1.
     */
    public static boolean esAdministrador() {
        return currentTrabajador != null && currentTrabajador.getRolID() == 1;
    }

    /**
     * Verifica si el usuario logueado es Bibliotecario (o cualquier otro rol operativo).
     * (Asumimos RolID = 2 para Bibliotecario)
     * @return true si el RolID es 2.
     */
    public static boolean esBibliotecario() {
        return currentTrabajador != null && currentTrabajador.getRolID() == 2;
    }

    /**
     * Metodo genérico para verificar si el usuario tiene un RolID específico.
     * @param rolId ID del rol a verificar.
     * @return true si el usuario tiene ese rol.
     */
    public static boolean tieneRol(int rolId) {
        return currentTrabajador != null && currentTrabajador.getRolID() == rolId;
    }
}
