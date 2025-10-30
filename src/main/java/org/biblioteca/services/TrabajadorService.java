package org.biblioteca.services;

import org.biblioteca.dao.TrabajadorDAO;
import org.biblioteca.dao.TrabajadorDAOImpl;
import org.biblioteca.entities.Trabajador;
import org.biblioteca.util.PasswordUtil;

import java.util.List;

public class TrabajadorService {

    private final TrabajadorDAO trabajadorDAO;

    public TrabajadorService() {
        this.trabajadorDAO = new TrabajadorDAOImpl();
    }


    public void registrarTrabajador(String nombre, String apellido, String dni, String usuarioLogin, String contrasena, int rolID, String email, String telefono) throws IllegalArgumentException {

        // El constructor de Trabajador ahora recibe todos los campos nuevos
        Trabajador nuevoTrabajador = new Trabajador(nombre, apellido, dni, usuarioLogin, contrasena, rolID, email, telefono);

        trabajadorDAO.insertar(nuevoTrabajador);
    }


    public Trabajador autenticarTrabajador(String usuarioLogin, String passwordPlana) {
        if (usuarioLogin == null || usuarioLogin.trim().isEmpty() || passwordPlana == null || passwordPlana.isEmpty()) {
            return null;
        }

        // 1. Buscar al trabajador por su nombre de usuario.
        Trabajador trabajadorAlmacenado = trabajadorDAO.obtenerPorUsuarioLogin(usuarioLogin);

        // 2. Verificar si se encontró el usuario
        if (trabajadorAlmacenado == null) {
            return null;
        }

        if ("Inactivo".equalsIgnoreCase(trabajadorAlmacenado.getEstado())) {

            return null;
        }
        byte[] storedHash = trabajadorAlmacenado.getContrasenaHash();
        byte[] storedSalt = trabajadorAlmacenado.getSalt();

        if (storedHash == null || storedSalt == null) {
            System.err.println("Error de seguridad: Hash o Salt nulo para el usuario: " + usuarioLogin);
            return null;
        }


        boolean esAutentico = PasswordUtil.verifyPassword(passwordPlana, storedHash, storedSalt);

        if (esAutentico) {
            trabajadorAlmacenado.setContrasenaHash(null);
            trabajadorAlmacenado.setSalt(null);

            return trabajadorAlmacenado;
        } else {
            return null;
        }
    }

    // Métodos CRUD estándar para la gestión de trabajadores

    public List<Trabajador> listarTrabajadores() {
        return trabajadorDAO.obtenerTodos();
    }

    public void actualizarDatos(Trabajador trabajador) throws IllegalArgumentException {
        trabajadorDAO.actualizar(trabajador);
    }

    public void actualizarContrasena(int trabajadorID, String nuevaContrasena) throws IllegalArgumentException {
        // Se podría validar la nuevaContrasena aquí antes de llamar al DAO
        trabajadorDAO.actualizarContrasena(trabajadorID, nuevaContrasena);
    }

    public void eliminarTrabajador(int id) {
        trabajadorDAO.eliminar(id);
    }

    public Trabajador obtenerTrabajadorPorUsuarioLogin(String usuarioLogin) {
        if (usuarioLogin == null || usuarioLogin.trim().isEmpty()) {
            return null;
        }
        return trabajadorDAO.obtenerPorUsuarioLogin(usuarioLogin);
    }

    public Trabajador obtenerPorId(int id) {
        return trabajadorDAO.obtenerPorId(id);
    }
}
