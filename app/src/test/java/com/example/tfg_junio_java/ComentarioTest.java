
package com.example.tfg_junio_java;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ComentarioTest {

    @Test
    public void testConstructorAndGetters() {
        String usuarioId = "user123";
        String nombreUsuario = "John Doe";
        String comentario = "Great movie!";
        int puntuacion = 5;
        long timestamp = System.currentTimeMillis();

        Comentario comentarioObj = new Comentario(usuarioId, nombreUsuario, comentario, puntuacion, timestamp);

        assertEquals(usuarioId, comentarioObj.getUsuarioId());
        assertEquals(nombreUsuario, comentarioObj.getNombreUsuario());
        assertEquals(comentario, comentarioObj.getComentario());
        assertEquals(puntuacion, comentarioObj.getPuntuacion());
        assertEquals(timestamp, comentarioObj.getTimestamp());
    }

    @Test
    public void testSetters() {
        Comentario comentarioObj = new Comentario();

        String usuarioId = "user123";
        String nombreUsuario = "John Doe";
        String comentario = "Great movie!";
        int puntuacion = 5;
        long timestamp = System.currentTimeMillis();

        comentarioObj.setUsuarioId(usuarioId);
        comentarioObj.setNombreUsuario(nombreUsuario);
        comentarioObj.setComentario(comentario);
        comentarioObj.setPuntuacion(puntuacion);
        comentarioObj.setTimestamp(timestamp);

        assertEquals(usuarioId, comentarioObj.getUsuarioId());
        assertEquals(nombreUsuario, comentarioObj.getNombreUsuario());
        assertEquals(comentario, comentarioObj.getComentario());
        assertEquals(puntuacion, comentarioObj.getPuntuacion());
        assertEquals(timestamp, comentarioObj.getTimestamp());
    }
}
