package org.biblioteca.services;

import org.biblioteca.dao.TrabajadorDAO;
import org.biblioteca.dao.TrabajadorDAOImpl;
import org.biblioteca.entities.Trabajador;
import org.biblioteca.util.PasswordUtil;
import org.biblioteca.util.SessionManager;

import java.util.List;

public class TrabajadorService {

    private final TrabajadorDAO trabajadorDAO;

    public TrabajadorService() {
        this.trabajadorDAO = new TrabajadorDAOImpl();
    }

    private boolean sonCadenasIguales(String s1, String s2) {
        String limpio1 = (s1 == null || s1.trim().isEmpty()) ? null : s1.trim();
        String limpio2 = (s2 == null || s2.trim().isEmpty()) ? null : s2.trim();

        if (limpio1 == null && limpio2 == null) {
            return true;
        }
        if (limpio1 != null && limpio2 != null) {
            return limpio1.equals(limpio2);
        }
        return false;
    }


    public void registrarTrabajador(String nombre, String apellido, String dni, String usuarioLogin, String contrasena, int rolID, String email, String telefono) throws IllegalArgumentException {
        // Validar Usuario Único
        if (trabajadorDAO.obtenerPorUsuarioLogin(usuarioLogin) != null) {
            throw new IllegalArgumentException("El nombre de usuario '" + usuarioLogin + "' ya no está disponible.");
        }

        // Validar DNI Único
        if (trabajadorDAO.obtenerPorDni(dni) != null) { // Asumiendo que implementaste el método
            throw new IllegalArgumentException("El DNI '" + dni + "' ya está asociado a otro trabajador.");
        }
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

    public void actualizarDatos(Trabajador trabajadorModificado) throws IllegalArgumentException, SecurityException {

        Trabajador trabajadorOriginal = trabajadorDAO.obtenerPorId(trabajadorModificado.getTrabajadorID());

        if (trabajadorOriginal == null) {
            throw new IllegalArgumentException("El trabajador a modificar no existe.");
        }

        // Validaciones de Usuario Login
        Trabajador otroConMismoUsuario = trabajadorDAO.obtenerPorUsuarioLogin(trabajadorModificado.getUsuarioLogin());
        if (otroConMismoUsuario != null && otroConMismoUsuario.getTrabajadorID() != trabajadorModificado.getTrabajadorID()) {
            throw new IllegalArgumentException("El nombre de usuario '" + trabajadorModificado.getUsuarioLogin() + "' ya está ocupado por otro trabajador.");
        }

        // Validación de DNI Único
        Trabajador otroConMismoDni = trabajadorDAO.obtenerPorDni(trabajadorModificado.getDni());
        if (otroConMismoDni != null && otroConMismoDni.getTrabajadorID() != trabajadorModificado.getTrabajadorID()) {
            throw new IllegalArgumentException("El DNI '" + trabajadorModificado.getDni() + "' ya pertenece a otro trabajador.");
        }

        int idTrabajadorSesion = SessionManager.getCurrentTrabajador().getTrabajadorID();
        int idTrabajadorAEditar = trabajadorModificado.getTrabajadorID();
        boolean esTrabajadorOriginalAdmin = "Administrador".equalsIgnoreCase(trabajadorOriginal.getNombreRol());


        // =========================================================================
        // REGLA DE SEGURIDAD REFORZADA CON EXCEPCIÓN DE ESTADO
        // =========================================================================

        // 1. Si el usuario en sesión es Administrador y no se está auto-editando, Y el original es Administrador
        if (SessionManager.esAdministrador() &&
                idTrabajadorSesion != idTrabajadorAEditar &&
                esTrabajadorOriginalAdmin)
        {
            // 2. VERIFICAR SI HAY OTROS CAMBIOS ADEMÁS DEL ESTADO

            // [INICIO CORRECCIÓN: Uso de sonCadenasIguales]
            boolean soloSeCambioElEstado =
                    // El RolID sigue siendo el mismo
                    trabajadorOriginal.getRolID() == trabajadorModificado.getRolID() &&

                            // Nombre y Apellido no han cambiado (usando el método auxiliar)
                            sonCadenasIguales(trabajadorOriginal.getNombre(), trabajadorModificado.getNombre()) &&
                            sonCadenasIguales(trabajadorOriginal.getApellido(), trabajadorModificado.getApellido()) &&

                            // Email y Teléfono no han cambiado (usando el método auxiliar)
                            sonCadenasIguales(trabajadorOriginal.getEmail(), trabajadorModificado.getEmail()) &&
                            sonCadenasIguales(trabajadorOriginal.getTelefono(), trabajadorModificado.getTelefono()) &&

                            // El estado SÍ ha cambiado
                            !trabajadorOriginal.getEstado().equals(trabajadorModificado.getEstado());
            // [FIN CORRECCIÓN]

            // 3. Si SÓLO se cambió el estado, permitimos el paso
            if (soloSeCambioElEstado) {
                // No hacemos nada, el código continuará y llamará al DAO.
                System.out.println("ADVERTENCIA DE SEGURIDAD: Administrador '" +
                        SessionManager.getCurrentTrabajador().getUsuarioLogin() +
                        "' cambió el estado a '" + trabajadorModificado.getEstado() +
                        "' al Administrador '" + trabajadorOriginal.getUsuarioLogin() + "'.");
            } else {
                // 4. Si hay CUALQUIER otro cambio (rol, nombre, etc.) además del estado...
                throw new SecurityException("Operación Prohibida: Un Administrador solo puede modificar el estado de otro Administrador.");
            }
        }

        // Si pasa todas las validaciones (o es auto-edición), se procede.
        trabajadorDAO.actualizar(trabajadorModificado);
    }

    public void actualizarContrasena(int trabajadorID, String nuevaContrasena) throws IllegalArgumentException {
        Trabajador trabajadorOriginal = trabajadorDAO.obtenerPorId(trabajadorID);

        if (trabajadorOriginal == null) {
            throw new IllegalArgumentException("El trabajador a modificar no existe.");
        }

        int idTrabajadorSesion = SessionManager.getCurrentTrabajador().getTrabajadorID();

        // REGLA DE SEGURIDAD: Admin NO puede cambiar la contraseña de OTRO Admin
        if (SessionManager.esAdministrador()) {
            boolean esTrabajadorOriginalAdmin = "Administrador".equalsIgnoreCase(trabajadorOriginal.getNombreRol());

            if (idTrabajadorSesion != trabajadorID && esTrabajadorOriginalAdmin) {
                // Bloqueo total para la contraseña de otros Admins.
                throw new SecurityException("Operación Prohibida: Un Administrador no puede cambiar la contraseña de otro Administrador.");
            }
        }

        // Se podría validar la nuevaContrasena aquí antes de llamar al DAO
        trabajadorDAO.actualizarContrasena(trabajadorID, nuevaContrasena);
    }

    public void eliminarTrabajador(int idTrabajadorAEliminar) throws SecurityException, Exception {
        Trabajador trabajadorAEliminar = trabajadorDAO.obtenerPorId(idTrabajadorAEliminar);

        if (trabajadorAEliminar == null) {
            throw new Exception("El trabajador a eliminar no existe en la base de datos.");
        }

        // 1. REGLA: Un usuario no puede eliminarse a sí mismo.
        if (SessionManager.isSesionActiva() && SessionManager.getCurrentTrabajador().getTrabajadorID() == idTrabajadorAEliminar) {
            throw new SecurityException("Operación Prohibida: No puedes eliminar tu propia cuenta de sesión.");
        }

        // 2. REGLA: Un Administrador no puede eliminar a otro Administrador.
        if (SessionManager.esAdministrador() && "Administrador".equalsIgnoreCase(trabajadorAEliminar.getNombreRol())) {
            throw new SecurityException("Operación Prohibida: Un Administrador no puede eliminar a otro Administrador.");
        }

        // Si todas las validaciones pasan, se procede con la eliminación.
        trabajadorDAO.eliminar(idTrabajadorAEliminar);
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
