package org.biblioteca.controller;

import org.biblioteca.entities.Trabajador;
import org.biblioteca.services.RolService;
import org.biblioteca.services.TrabajadorService;

import java.util.List;

public class TrabajadorController {

    private final TrabajadorService trabajadorService;
    private final RolService rolService;

    public TrabajadorController(TrabajadorService trabajadorService, RolService rolService) {
        this.trabajadorService = trabajadorService;
        this.rolService = rolService;
    }


    public String procesarGuardado(
            Trabajador trabajadorAEditar, String nombre, String apellido, String dni,
            String usuarioLogin, String contrasena, int rolID, String email,
            String telefono, String estado) throws IllegalArgumentException, RuntimeException {

        if (trabajadorAEditar == null) {
            if (contrasena.isEmpty()) {
                throw new IllegalArgumentException("La contraseña es obligatoria al crear un nuevo trabajador.");
            }

            trabajadorService.registrarTrabajador(
                    nombre, apellido, dni, usuarioLogin, contrasena,
                    rolID, email, telefono);

            return "Trabajador registrado con éxito.";

        }

        else {
            trabajadorAEditar.setNombre(nombre);
            trabajadorAEditar.setApellido(apellido);
            trabajadorAEditar.setEmail(email);
            trabajadorAEditar.setTelefono(telefono);
            trabajadorAEditar.setRolID(rolID);
            trabajadorAEditar.setEstado(estado);
            trabajadorService.actualizarDatos(trabajadorAEditar);

            if (!contrasena.isEmpty()) {
                trabajadorService.actualizarContrasena(trabajadorAEditar.getTrabajadorID(), contrasena);
            }

            return "Datos del trabajador actualizados con éxito.";
        }
    }



    /**
     * Obtiene la lista completa de trabajadores desde el servicio.
     * @return Lista de todos los trabajadores.
     */
    public List<Trabajador> listarTrabajadores() {
        return trabajadorService.listarTrabajadores();
    }

    /**
     * Obtiene un trabajador por su ID. Utilizado típicamente antes de la edición.
     * @param id ID del trabajador.
     * @return Objeto Trabajador, o null si no se encuentra.
     */
    public Trabajador obtenerPorId(int id) {
        return trabajadorService.obtenerPorId(id);
    }

    /**
     * Elimina un trabajador por su ID.
     * @param id ID del trabajador a eliminar.
     */
    public void eliminarTrabajador(int id) {
        // La lógica de eliminación permanece simple en el Service
        trabajadorService.eliminarTrabajador(id);
    }
}
