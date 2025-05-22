package com.example.tfg_junio_java;

public class Comentario {
    private String usuarioId;
    private String nombreUsuario;
    private String comentario;
    private int puntuacion;
    private long timestamp;

    public Comentario() {}

    public Comentario(String usuarioId, String nombreUsuario, String comentario, int puntuacion, long timestamp) {
        this.usuarioId = usuarioId;
        this.nombreUsuario = nombreUsuario;
        this.comentario = comentario;
        this.puntuacion = puntuacion;
        this.timestamp = timestamp;
    }

    // Getters y setters...
    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
