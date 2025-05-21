package com.example.tfg_junio_java;

import java.io.Serializable;

public class Pelicula implements Serializable {

    private String id; // ← ID del documento en Firestore
    private String imagenUrl;
    private String nombrePeli;
    private String sinopsis;
    private String urlTrailer;

    public Pelicula() {}

    public Pelicula(String imagenUrl, String nombrePeli, String sinopsis, String urlTrailer) {
        this.imagenUrl = imagenUrl;
        this.nombrePeli = nombrePeli;
        this.sinopsis = sinopsis;
        this.urlTrailer = urlTrailer;
    }

    // Nuevo constructor con ID
    public Pelicula(String id, String imagenUrl, String nombrePeli, String sinopsis, String urlTrailer) {
        this.id = id;
        this.imagenUrl = imagenUrl;
        this.nombrePeli = nombrePeli;
        this.sinopsis = sinopsis;
        this.urlTrailer = urlTrailer;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public String getNombrePeli() { return nombrePeli; }
    public void setNombrePeli(String nombrePeli) { this.nombrePeli = nombrePeli; }

    public String getSinopsis() { return sinopsis; }
    public void setSinopsis(String sinopsis) { this.sinopsis = sinopsis; }

    public String getUrlTrailer() { return urlTrailer; }
    public void setUrlTrailer(String urlTrailer) { this.urlTrailer = urlTrailer; }
}
