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
        System.out.println("Sesión iniciada: " + trabajador.getUsuarioLogin() + " (Rol: " + trabajador.getNombreRol() + ")");
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
     * MÉTODOS DE PERMISOS BASADOS EN EL ROL (Usando el Nombre del Rol)
     */

    /**
     * Verifica si el trabajador actual es un Administrador.
     */
    public static boolean esAdministrador() {
        // Usamos equalsIgnoreCase para una comparación segura, en caso de que la DB devuelva mayúsculas/minúsculas diferentes.
        return currentTrabajador != null && "Administrador".equalsIgnoreCase(currentTrabajador.getNombreRol());
    }

    /**
     * Verifica si el trabajador actual es un Bibliotecario.
     */
    public static boolean esBibliotecario() {
        return currentTrabajador != null && "Bibliotecario".equalsIgnoreCase(currentTrabajador.getNombreRol());
    }

    /**
     * Verifica si el trabajador actual tiene un rol específico por nombre.
     */
    public static boolean tieneRol(String nombreRol) {
        if (nombreRol == null) {
            return false;
        }
        return currentTrabajador != null && nombreRol.equalsIgnoreCase(currentTrabajador.getNombreRol());
    }
}
