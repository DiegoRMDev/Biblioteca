package org.biblioteca.entities;

import java.time.Year;
import java.util.List;

public class Libro {

    private int libroID;
    private String isbn;
    private String titulo;
    private String editorial;
    private int anioPublicacion;
    private int categoriaID; // Clave foránea a Categorias
    private String idioma;
    private String ubicacionFisica;
    private String rutaImagen;
    private String estado;
    private int stock;

    // Nuevo campo para la relación N:M
    private List<Autor> autores;

    public Libro() {
    }

    // Constructor para CARGAR datos desde la BD
    public Libro(int libroID, String isbn, String titulo, String editorial, int anioPublicacion, int categoriaID, String idioma, String ubicacionFisica, String rutaImagen, String estado, int stock) {
        this.libroID = libroID;
        this.isbn = isbn;
        this.titulo = titulo;
        this.editorial = editorial;
        this.anioPublicacion = anioPublicacion;
        this.categoriaID = categoriaID;
        this.idioma = idioma;
        this.ubicacionFisica = ubicacionFisica;
        this.rutaImagen = rutaImagen;
        this.estado = estado;
        this.stock = stock;
    }

    // Constructor para REGISTRAR un nuevo libro (Debe usar setters para validar)
    public Libro(String isbn, String titulo, String editorial, int anioPublicacion, int categoriaID, String idioma, String ubicacionFisica, String rutaImagen, String estado, int stock) {
        this.setIsbn(isbn);
        this.setTitulo(titulo);
        this.setEditorial(editorial);
        this.setAnioPublicacion(anioPublicacion);
        this.setCategoriaID(categoriaID);
        this.setIdioma(idioma);
        this.setUbicacionFisica(ubicacionFisica);
        this.setRutaImagen(rutaImagen);
        this.setEstado(estado);
        this.setStock(stock);
    }

    public int getLibroID() {
        return libroID;
    }

    public void setLibroID(int libroID) {
        this.libroID = libroID;
    }

    public String getIsbn() {
        return isbn;
    }

    /**
     * Valida y establece el ISBN. (NOT NULL, formato ISBN-10 o ISBN-13)
     */
    public void setIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("El ISBN es obligatorio.");
        }

        String cleanIsbn = isbn.trim().toUpperCase();

        // Validación de formato ISBN-10 o ISBN-13
        // El formato ISBN permite guiones, espacios y la letra 'X'.
        // Aquí usamos una Regex que acepta el formato sin guiones o con el estándar más común:
        final String ISBN_REGEX =
                "^(?:ISBN(?:-13)?:?\\s)?(?=[0-9X]{10,13}$|(?=(?:[0-9]+[-\\s]){4})[-\\s0-9X]{17}|97[89][0-9]{10}|(?=(?:[0-9]+[-\\s]){3})[-\\s0-9X]{13})(?:97[89][ -]?)?[0-9]{1,5}[ -]?[0-9]+[ -]?[0-9]+[ -]?[0-9X]$";


        if (!cleanIsbn.matches("^([0-9]{10}|[0-9]{13})$")) {
            // Se puede relajar esta regla si la aplicación maneja ISBNs con guiones.
            throw new IllegalArgumentException("El ISBN debe ser de 10 o 13 dígitos numéricos.");
        }

        this.isbn = cleanIsbn;
    }

    public String getTitulo() {
        return titulo;
    }

    /**
     * Valida y establece el Título. (NOT NULL y Límite Realista de 150)
     */
    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El Título del libro es obligatorio.");
        }

        String cleanTitulo = titulo.trim();
        final int MAX_LENGHT = 150; // Límite realista, menor al NVARCHAR(255) de la BD

        if (cleanTitulo.length() > MAX_LENGHT) {
            throw new IllegalArgumentException("El Título no puede exceder los " + MAX_LENGHT + " caracteres.");
        }

        this.titulo = cleanTitulo;
    }

    public String getEditorial() {
        return editorial;
    }

    /**
     * Establece la Editorial. (Opcional y Límite Realista de 80)
     */
    public void setEditorial(String editorial) {
        if (editorial != null && !editorial.trim().isEmpty()) {
            String cleanEditorial = editorial.trim();
            final int MAX_LENGHT = 80; // Límite realista, menor al NVARCHAR(100) de la BD

            if (cleanEditorial.length() > MAX_LENGHT) {
                throw new IllegalArgumentException("La Editorial no puede exceder los " + MAX_LENGHT + " caracteres.");
            }
            this.editorial = cleanEditorial;
        } else {
            this.editorial = null;
        }
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    /**
     * Valida y establece el Año de Publicación. (NOT NULL y Lógica de Negocio: No puede ser futuro)
     */
    public void setAnioPublicacion(int anioPublicacion) {
        final int ANIO_ACTUAL = Year.now().getValue();

        if (anioPublicacion <= 0) {
            throw new IllegalArgumentException("El Año de Publicación debe ser un valor válido.");
        }

        if (anioPublicacion > ANIO_ACTUAL) {
            throw new IllegalArgumentException("El Año de Publicación no puede ser posterior al año actual (" + ANIO_ACTUAL + ").");
        }

        this.anioPublicacion = anioPublicacion;
    }

    public int getCategoriaID() {
        return categoriaID;
    }

    /**
     * Valida y establece la Clave Foránea CategoriaID. (NOT NULL, ID positivo)
     */
    public void setCategoriaID(int categoriaID) {
        if (categoriaID <= 0) {
            throw new IllegalArgumentException("El ID de la Categoría es inválido. Debe ser un valor positivo.");
        }
        this.categoriaID = categoriaID;
    }

    public String getIdioma() {
        return idioma;
    }

    /**
     * Valida y establece el Idioma. (NOT NULL y Límite Realista de 30)
     */
    public void setIdioma(String idioma) {
        if (idioma == null || idioma.trim().isEmpty()) {
            throw new IllegalArgumentException("El Idioma es obligatorio.");
        }

        String cleanIdioma = idioma.trim();
        final int MAX_LENGHT = 30; // Límite realista, menor al NVARCHAR(50) de la BD

        if (cleanIdioma.length() > MAX_LENGHT) {
            throw new IllegalArgumentException("El Idioma no puede exceder los " + MAX_LENGHT + " caracteres.");
        }

        this.idioma = cleanIdioma;
    }

    public String getUbicacionFisica() {
        return ubicacionFisica;
    }

    /**
     * Establece la Ubicación Física. (Opcional y Límite Realista de 30)
     */
    public void setUbicacionFisica(String ubicacionFisica) {
        if (ubicacionFisica != null && !ubicacionFisica.trim().isEmpty()) {
            String cleanUbicacion = ubicacionFisica.trim();
            final int MAX_LENGHT = 30; // Límite realista, menor al NVARCHAR(50) de la BD

            if (cleanUbicacion.length() > MAX_LENGHT) {
                throw new IllegalArgumentException("La Ubicación Física no puede exceder los " + MAX_LENGHT + " caracteres.");
            }
            this.ubicacionFisica = cleanUbicacion;
        } else {
            this.ubicacionFisica = null;
        }
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    /**
     * Establece la Ruta de la Imagen.
     */
    public void setRutaImagen(String rutaImagen) {
        // Validación básica de longitud, aunque las rutas pueden ser largas
        if (rutaImagen != null && rutaImagen.trim().length() > 255) {
            throw new IllegalArgumentException("La Ruta de Imagen no puede exceder los 255 caracteres.");
        }
        this.rutaImagen = (rutaImagen != null) ? rutaImagen.trim() : null;
    }

    public String getEstado() {
        return estado;
    }

    /**
     * Valida y establece el Estado. (NOT NULL y Lógica de Negocio: solo valores permitidos)
     */
    public void setEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El Estado del libro es obligatorio.");
        }

        String cleanEstado = estado.trim();

        // Regla de Negocio (replicando el CHECK constraint de la BD)
        final List<String> ESTADOS_PERMITIDOS = List.of("Activo", "Inactivo", "Retirado");

        if (!ESTADOS_PERMITIDOS.contains(cleanEstado)) {
            throw new IllegalArgumentException("El Estado debe ser 'Activo', 'Inactivo' o 'Retirado'.");
        }

        this.estado = cleanEstado;
    }

    public int getStock() {
        return stock;
    }

    /**
     * Valida y establece el Stock. (NOT NULL y Stock no negativo)
     */
    public void setStock(int stock) {
        // Regla de Negocio (replicando el CHECK constraint de la BD)
        if (stock < 0) {
            throw new IllegalArgumentException("El Stock no puede ser negativo.");
        }
        this.stock = stock;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }
}
