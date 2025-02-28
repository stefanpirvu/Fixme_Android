package com.example.fixme_android;

public class Servicio {
    private final int idServicio;
    private final String titulo;
    private final String categoria;
    private final float precio;
    private final String descripcion;
    private final int idUsuario;

    public Servicio(int idServicio, String titulo, String categoria, float precio, String descripcion, int idUsuario) {
        this.idServicio = idServicio;
        this.titulo = titulo;
        this.categoria = categoria;
        this.precio = precio;
        this.descripcion = descripcion;
        this.idUsuario = idUsuario;
    }

    public int getIdServicio() {
        return idServicio;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getCategoria() {
        return categoria;
    }

    public float getPrecio() {
        return precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }
}