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


    public void registrarTrabajador(String nombre, String usuarioLogin, String contrasena, int rolID) throws IllegalArgumentException {
        // La entidad Trabajador valida nombre, usuario, contraseña (longitud), y rolID.
        Trabajador nuevoTrabajador = new Trabajador(nombre, usuarioLogin, contrasena, rolID);
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
            // Usuario no encontrado
            return null;
        }

        // 3. Obtener el Hash y Salt almacenados
        byte[] storedHash = trabajadorAlmacenado.getContrasenaHash();
        byte[] storedSalt = trabajadorAlmacenado.getSalt();

        // Manejo de caso extremo donde los datos de seguridad estén corruptos (debería ser NOT NULL en BD)
        if (storedHash == null || storedSalt == null) {
            System.err.println("Error de seguridad: Hash o Salt nulo para el usuario: " + usuarioLogin);
            return null;
        }

        // 4. USAR PasswordUtil para verificar la contraseña
        boolean esAutentico = PasswordUtil.verifyPassword(passwordPlana, storedHash, storedSalt);

        if (esAutentico) {

            trabajadorAlmacenado.setContrasenaHash(null);
            trabajadorAlmacenado.setSalt(null);

            return trabajadorAlmacenado;
        } else {
            // Contraseña incorrecta
            return null;
        }
    }

    // Métodos CRUD estándar para la gestión de trabajadores

    public List<Trabajador> listarTrabajadores() {
        return trabajadorDAO.obtenerTodos();
    }

    public void actualizarDatos(Trabajador trabajador) throws IllegalArgumentException {
        // Se asume que este metodo solo actualiza Nombre, RolID, etc., no la contraseña.
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
}
