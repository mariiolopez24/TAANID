
package com.example.tfg_junio_java;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

public class PeliculaTest {

    @Test
    public void testGettersAndSetters() {
        Map<String, String> sinopsis = new HashMap<>();
        sinopsis.put("es", "Una película de ejemplo.");
        sinopsis.put("en", "An example movie.");

        Pelicula pelicula = new Pelicula("1", "https://example.com/image.jpg", "Example Movie", sinopsis, "https://example.com/trailer", "https://example.com/movie");

        assertEquals("1", pelicula.getId());
        assertEquals("https://example.com/image.jpg", pelicula.getImagenUrl());
        assertEquals("Example Movie", pelicula.getNombrePeli());
        assertEquals(sinopsis, pelicula.getSinopsis());
        assertEquals("https://example.com/trailer", pelicula.getUrlTrailer());
        assertEquals("https://example.com/movie", pelicula.getUrlPelicula());

        pelicula.setId("2");
        pelicula.setImagenUrl("https://example.com/new_image.jpg");
        pelicula.setNombrePeli("New Example Movie");
        Map<String, String> newSinopsis = new HashMap<>();
        newSinopsis.put("es", "Una nueva película de ejemplo.");
        newSinopsis.put("en", "A new example movie.");
        pelicula.setSinopsis(newSinopsis);
        pelicula.setUrlTrailer("https://example.com/new_trailer");
        pelicula.setUrlPelicula("https://example.com/new_movie");

        assertEquals("2", pelicula.getId());
        assertEquals("https://example.com/new_image.jpg", pelicula.getImagenUrl());
        assertEquals("New Example Movie", pelicula.getNombrePeli());
        assertEquals(newSinopsis, pelicula.getSinopsis());
        assertEquals("https://example.com/new_trailer", pelicula.getUrlTrailer());
        assertEquals("https://example.com/new_movie", pelicula.getUrlPelicula());
    }
}
