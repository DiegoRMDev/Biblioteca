package org.biblioteca.entities;

import java.sql.Timestamp;

public class Trabajador {

    private int trabajadorID;
    private String nombre;
    private String apellido;    // NUEVO
    private String dni;         // NUEVO
    private String usuarioLogin;
    private String contrasena; // Contraseña plana, temporalmente usada para hashear en el DAO
    private int rolID;
    private String email;       // NUEVO
    private String telefono;    // NUEVO
    private String estado;      // NUEVO
    private Timestamp fechaRegistro;

    private byte[] contrasenaHash; // Almacena el HASH (byte[]) de la BD
    private byte[] salt;           // Almacena el SALT (byte[]) de la BD

    public Trabajador() {
        this.estado = "Activo";
    }

    // Constructor 1: Para CARGAR datos COMPLETOS desde la BD
    public Trabajador(int trabajadorID, String nombre, String apellido, String dni, String usuarioLogin,
                      int rolID, String email, String telefono, String estado, Timestamp fechaRegistro) {
        this.trabajadorID = trabajadorID;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.usuarioLogin = usuarioLogin;
        this.rolID = rolID;
        this.email = email;
        this.telefono = telefono;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
    }

    // Constructor 2: Para REGISTRAR un nuevo trabajador (Debe usar setters para validar)
    public Trabajador(String nombre, String apellido, String dni, String usuarioLogin, String contrasena,
                      int rolID, String email, String telefono) {
        this.setNombre(nombre);
        this.setApellido(apellido);
        this.setDni(dni);
        this.setUsuarioLogin(usuarioLogin);
        this.setContrasena(contrasena);
        this.setRolID(rolID);
        this.setEmail(email);
        this.setTelefono(telefono);
        this.setEstado("Activo"); // Estado inicial al crear
    }

    public int getTrabajadorID() {
        return trabajadorID;
    }

    public void setTrabajadorID(int trabajadorID) {
        this.trabajadorID = trabajadorID;
    }

    public String getNombre() {
        return nombre;
    }

    /**
     * Valida y establece el nombre. (NOT NULL y NVARCHAR(100))
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del trabajador es obligatorio.");
        }

        String nombreLimpio = nombre.trim();

        if (nombreLimpio.length() > 100) { // Límite de la BD
            throw new IllegalArgumentException("El nombre no puede exceder los 100 caracteres.");
        }

        this.nombre = nombreLimpio;
    }



    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido del trabajador es obligatorio.");
        }
        if (apellido.trim().length() > 100) {
            throw new IllegalArgumentException("El apellido no puede exceder los 100 caracteres.");
        }
        this.apellido = apellido.trim();
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI es obligatorio.");
        }
        String dniLimpio = dni.trim();
        // Validación de BD: 8 dígitos numéricos
        if (dniLimpio.length() != 8 || !dniLimpio.matches("\\d+")) {
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos numéricos.");
        }
        this.dni = dniLimpio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && email.trim().length() > 100) {
            throw new IllegalArgumentException("El email no puede exceder los 100 caracteres.");
        }
        // Validación básica de formato (permite null/vacío)
        if (email != null && !email.trim().isEmpty() && !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,4}$")) {
            throw new IllegalArgumentException("El formato del email es inválido.");
        }
        this.email = (email != null) ? email.trim() : null;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        if (telefono != null && telefono.trim().length() > 15) {
            throw new IllegalArgumentException("El teléfono no puede exceder los 15 caracteres.");
        }
        this.telefono = (telefono != null) ? telefono.trim() : null;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }
        // Validación contra los valores definidos en BD: Activo, Inactivo, etc.
        String estadoLimpio = estado.trim();
        if (!estadoLimpio.equals("Activo") && !estadoLimpio.equals("Inactivo")) {
            // Se puede expandir esta lista si hay más estados
            throw new IllegalArgumentException("Estado inválido. Solo se permite 'Activo' o 'Inactivo'.");
        }
        this.estado = estadoLimpio;
    }


    public String getUsuarioLogin() {
        return usuarioLogin;
    }

    public void setUsuarioLogin(String usuarioLogin) {
        if (usuarioLogin == null || usuarioLogin.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario para login es obligatorio.");
        }

        String usuarioLimpio = usuarioLogin.trim();

        if (usuarioLimpio.length() > 50) { // Límite de la BD
            throw new IllegalArgumentException("El usuario de login no puede exceder los 50 caracteres.");
        }


        if (!usuarioLimpio.matches("^[a-zA-Z0-9]+$")) {
        throw new IllegalArgumentException("El usuario solo puede contener letras y números.");
        }

        this.usuarioLogin = usuarioLimpio;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }

        String contrasenaLimpia = contrasena.trim();


        if (contrasenaLimpia.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }

        this.contrasena = contrasenaLimpia;
    }

    public int getRolID() {
        return rolID;
    }

    /**
     * Valida y establece el ID del Rol. (Debe ser un ID válido)
     */
    public void setRolID(int rolID) {
        // Se valida que el ID sea un valor positivo (regla básica para una FK)
        if (rolID <= 0) {
            throw new IllegalArgumentException("El ID del Rol es inválido. Debe ser un valor positivo.");
        }
        this.rolID = rolID;
    }
    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public byte[] getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(byte[] contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }
}
