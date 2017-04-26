package com.twine.arca_app.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by TWINE-DELL on 23/3/2017.
 */

@Table(name = "empleados")
public class Empleado extends Model {
    @Column(name = "id_empleado")
    public int id_empleado;
    @Column(name = "nombre")
    public String nombre;
}