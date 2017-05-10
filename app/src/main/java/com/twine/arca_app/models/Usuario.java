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
    @Column(name = "telefono")
    public String telefono;
    @Column(name = "edad")
    public String edad;
    @Column(name = "genero")
    public String genero;
    @Column(name = "activo")
    public boolean activo;
    @Column(name = "codigo")
    public String codigo;

    public  String getFullname(){
        return nombre + " " + apellido;
    }
}
