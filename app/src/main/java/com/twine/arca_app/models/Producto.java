package com.twine.arca_app.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by TWINE-DELL on 28/3/2017.
 */
@Table(name = "productos")
public class Producto extends Model {
    @Column(name = "id_producto")
    public int id_producto;
    @Column(name = "nombre")
    public String nombre;
    @Column(name = "descripcion")
    public String descripcion;
    @Column(name = "precio")
    public Double precio;
    @Column(name = "descuento")
    public Double descuento;
    @Column(name = "imagen")
    public String imagen;
    @Column(name = "pk_comercio")
    public Comercio comercio;
    @Column(name = "activo")
    public Boolean activo;
}
