package com.twine.arca_app.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Jose Williams Garcia on 30/4/2017.
 */
@Table(name = "comercio_ratings")
public class Comercio_Rating extends Model {
    @Column(name = "pk_comercio")
    public Comercio comercio;
    @Column(name = "pk_usuario")
    public Usuario usuario;
    @Column(name = "rating")
    public int rating;
    @Column(name = "cargado")
    public boolean cargado;
}
