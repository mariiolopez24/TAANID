package com.example.tfg_junio_java;

import java.io.Serializable;
import java.util.Map;

public class Pelicula implements Serializable {
    private String id;
    private String imagenUrl;
    private String nombrePeli;
    private Map<String, String> sinopsis; // Cambiado aquí
    private String urlTrailer;
    private String urlPelicula;

    public Pelicula() {}

    public Pelicula(String id, String imagenUrl, String nombrePeli, Map<String, String> sinopsis, String urlTrailer, String urlPelicula) {
        this.id = id;
        this.imagenUrl = imagenUrl;
        this.nombrePeli = nombrePeli;
        this.sinopsis = sinopsis;
        this.urlTrailer = urlTrailer;
        this.urlPelicula = urlPelicula;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public String getNombrePeli() { return nombrePeli; }
    public void setNombrePeli(String nombrePeli) { this.nombrePeli = nombrePeli; }

    public Map<String, String> getSinopsis() { return sinopsis; }
    public void setSinopsis(Map<String, String> sinopsis) { this.sinopsis = sinopsis; }

    public String getUrlTrailer() { return urlTrailer; }
    public void setUrlTrailer(String urlTrailer) { this.urlTrailer = urlTrailer; }

    public String getUrlPelicula() { return urlPelicula; }
    public void setUrlPelicula(String urlPelicula) { this.urlPelicula = urlPelicula; }
}
