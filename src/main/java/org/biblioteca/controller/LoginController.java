package org.biblioteca.controller;

import org.biblioteca.entities.Trabajador;
import org.biblioteca.services.TrabajadorService;
import org.biblioteca.util.SessionManager;

public class LoginController {

    private final TrabajadorService trabajadorService;

    /**
     * Constructor con inyección de dependencias. Solo requiere el TrabajadorService.
     * @param trabajadorService Servicio para acceder a la lógica de autenticación.
     */
    public LoginController(TrabajadorService trabajadorService) {
        this.trabajadorService = trabajadorService;
    }

    /**
     * Intenta autenticar un trabajador y, si es exitoso, inicia la sesión global.
     * @param usuarioLogin El nombre de usuario ingresado.
     * @param passwordPlana La contraseña ingresada.
     * @return true si el login fue exitoso y la sesión se inició, false en caso contrario.
     */
    public boolean intentarLogin(String usuarioLogin, String passwordPlana) {

        if (usuarioLogin.isEmpty() || passwordPlana.isEmpty()) {
            return false;
        }

        Trabajador trabajador = trabajadorService.autenticarTrabajador(usuarioLogin, passwordPlana);

        if (trabajador != null) {
            SessionManager.iniciarSesion(trabajador);
            return true;
        } else {
            return false;
        }
    }
}
