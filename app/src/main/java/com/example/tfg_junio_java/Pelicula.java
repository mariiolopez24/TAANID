package com.example.tfg_junio_java;

import java.io.Serializable;

public class Pelicula implements Serializable {

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

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public String getNombrePeli() { return nombrePeli; }
    public void setNombrePeli(String nombrePeli) { this.nombrePeli = nombrePeli; }

    public String getSinopsis() { return sinopsis; }
    public void setSinopsis(String sinopsis) { this.sinopsis = sinopsis; }

    public String getUrlTrailer() { return urlTrailer; }
    public void setUrlTrailer(String urlTrailer) { this.urlTrailer = urlTrailer; }
}
