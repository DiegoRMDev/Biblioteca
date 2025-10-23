package org.biblioteca.entities;

public class Proveedor {

    private int proveedorID;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;

    public Proveedor() {
    }

    // Constructor para CARGAR datos desde la BD
    public Proveedor(int proveedorID, String nombre, String direccion, String telefono, String email) {
        this.proveedorID = proveedorID;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
    }

    // Constructor para REGISTRAR un nuevo proveedor (Debe usar setters para validar)
    public Proveedor(String nombre, String direccion, String telefono, String email) {
        // Al registrar, usamos los setters para activar las validaciones integradas
        this.setNombre(nombre);
        this.setDireccion(direccion);
        this.setTelefono(telefono);
        this.setEmail(email);
    }

    public int getProveedorID() {
        return proveedorID;
    }

    public void setProveedorID(int proveedorID) {
        this.proveedorID = proveedorID;
    }

    public String getNombre() {
        return nombre;
    }

    /**
     * Valida y establece el Nombre del Proveedor. (NOT NULL y Límite Realista de 80)
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El Nombre del Proveedor es obligatorio.");
        }

        String nombreLimpio = nombre.trim();
        final int MAX_LENGHT = 80; // Límite realista, menor al NVARCHAR(150) de la BD

        if (nombreLimpio.length() > MAX_LENGHT) {
            throw new IllegalArgumentException("El Nombre no puede exceder los " + MAX_LENGHT + " caracteres.");
        }

        this.nombre = nombreLimpio;
    }

    public String getDireccion() {
        return direccion;
    }

    /**
     * Establece la Dirección. (Opcional y Límite Realista de 150)
     */
    public void setDireccion(String direccion) {
        if (direccion != null && !direccion.trim().isEmpty()) {
            String direccionLimpia = direccion.trim();
            final int MAX_LENGHT = 150; // Límite realista, menor al NVARCHAR(200) de la BD

            if (direccionLimpia.length() > MAX_LENGHT) {
                throw new IllegalArgumentException("La Dirección no puede exceder los " + MAX_LENGHT + " caracteres.");
            }
            this.direccion = direccionLimpia;
        } else {
            this.direccion = null; // Permite nulo o vacío
        }
    }

    public String getTelefono() {
        return telefono;
    }

    /**
     * Valida y establece el Teléfono. (Opcional, 6 a 12 dígitos numéricos)
     */
    public void setTelefono(String telefono) {
        String cleanTelefono = (telefono != null) ? telefono.trim() : null;

        if (cleanTelefono != null && !cleanTelefono.isEmpty()) {
            // Validación: Entre 6 y 12 dígitos numéricos (^[\\d]{6,12}$)
            if (!cleanTelefono.matches("^[\\d]{6,12}$")) {
                throw new IllegalArgumentException("El Teléfono debe contener entre 6 y 12 dígitos numéricos.");
            }
            this.telefono = cleanTelefono;
        } else {
            this.telefono = null;
        }
    }

    public String getEmail() {
        return email;
    }

    /**
     * Valida y establece el Email. (Opcional, formato básico de email)
     */
    public void setEmail(String email) {
        String cleanEmail = (email != null) ? email.trim() : null;

        if (cleanEmail != null && !cleanEmail.isEmpty()) {
            // Validación de formato: Patrón básico (x@y.z)
            if (!cleanEmail.matches(".*@.*\\..*")) {
                throw new IllegalArgumentException("El formato del Email es inválido (ej: correo@dominio.com).");
            }
            this.email = cleanEmail;
        } else {
            this.email = null; // Permite que sea nulo o vacío
        }
    }

    @Override
    public String toString() {
        return this.nombre;
    }
}
