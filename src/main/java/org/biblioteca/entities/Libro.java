package org.biblioteca.entities;

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
    private List<LibroAutor> autoresAsociados;

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

    // Constructor para REGISTRAR un nuevo libro
    public Libro(String isbn, String titulo, String editorial, int anioPublicacion, int categoriaID, String idioma, String ubicacionFisica, String rutaImagen, String estado, int stock) {
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

    public int getLibroID() {
        return libroID;
    }

    public void setLibroID(int libroID) {
        this.libroID = libroID;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(int anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public int getCategoriaID() {
        return categoriaID;
    }

    public void setCategoriaID(int categoriaID) {
        this.categoriaID = categoriaID;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getUbicacionFisica() {
        return ubicacionFisica;
    }

    public void setUbicacionFisica(String ubicacionFisica) {
        this.ubicacionFisica = ubicacionFisica;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public List<LibroAutor> getAutoresAsociados() {
        return autoresAsociados;
    }

    public void setAutoresAsociados(List<LibroAutor> autoresAsociados) {
        this.autoresAsociados = autoresAsociados;
    }
}
