package com.twine.arca_app.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by TWINE-DELL on 22/3/2017.
 */

@Table(name = "usuarios")
public class Usuario extends Model {
    @Column(name = "id_usuario")
    public int id_usuario;
    @Column(name = "nombre")
    public String nombre;
    @Column(name = "apellido")
    public String apellido;
    @Column(name = "username")
    public String username;
    @Column(name = "email")
    public String email;
    @Column(name = "foto")
    public String foto;
    @Column(name = "direccion")
    public String direccion;
    @Column(name = "telefono")
    public String telefono;
    @Column(name = "activo")
    public boolean activo;

    public  String getFullname(){
        return nombre + " " + apellido;
    }
}
