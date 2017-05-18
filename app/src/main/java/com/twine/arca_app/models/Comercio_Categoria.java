package com.twine.arca_app.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.androidannotations.annotations.Click;

/**
 * Created by TWINE-DELL on 26/3/2017.
 */
@Table(name = "comercio_categorias")
public class Comercio_Categoria extends Model {
    @Column(name = "id_categoria")
    public int id_categoria;
    @Column(name = "nombre")
    public String nombre;
    @Column(name = "icono")
    public String icono;


}
