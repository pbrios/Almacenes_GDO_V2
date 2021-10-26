package com.example.almacenesgdov2.objetos;

import java.io.Serializable;

public class Productos implements Serializable {
    private String codigo;
    private String descripcion;
    private String unidad;
    private String cantidad;

    public Productos(){}

    public Productos(String codigo, String descripcion, String unidades, String cantidad) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.unidad = unidades;
        this.cantidad = cantidad;
    }

    public Productos(String codigo, String descripcion, String unidades) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.unidad = unidades;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getUnidades() {
        return unidad;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setUnidades(String unidades) {
        this.unidad = unidades;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "Productos{" +
                "codigo='" + codigo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", unidad='" + unidad + '\'' +
                ", cantidad='" + cantidad + '\'' +
                '}';
    }
}
