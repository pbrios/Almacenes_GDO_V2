package com.example.almacenesgdov2.objetos;

import java.io.Serializable;

public class Almacenes implements Serializable {
    private String id;
    private String almacen;

    public Almacenes(){}

    public Almacenes(String id, String almacen){
        this.setId(id);
        this.setName(almacen);
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getAlmacen() {
        return almacen;
    }
    public void setName(String almacen) {
        this.almacen = almacen;
    }

    @Override
    public String toString() {
        return "Almacenes{" +
                "id='" + id + '\'' +
                ", almacen='" + almacen + '\'' +
                '}';
    }
}
