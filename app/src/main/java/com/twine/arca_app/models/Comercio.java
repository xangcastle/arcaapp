package com.twine.arca_app.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by TWINE-DELL on 22/3/2017.
 */

@Table(name = "comercios")
public class Comercio extends Model {
    @Column(name = "id_comercio")
    public int id_comercio;
    @Column(name = "nombre")
    public String nombre;
    @Column(name = "direccion")
    public String direccion;
    @Column(name = "telefono")
    public String telefono;
    @Column(name = "pk_categoria")
    public Comercio_Categoria categoria;
    @Column(name = "logo")
    public String logo;
    @Column(name = "baner")
    public String baner;
    @Column(name = "latitude")
    public Double latitude;
    @Column(name = "longitude")
    public Double longitude;

    public List<Producto> productos(){
        return new Select().from(Producto.class)
                .where("pk_comercio=?", this.getId())
                .where("activo=?",true)
                .execute();
    }
    public List<Descuento> descuentos(){
        return new Select().from(Descuento.class)
                .where("pk_comercio=?", this.getId())
                .where("activo=?",true)
                .execute();
    }
}
