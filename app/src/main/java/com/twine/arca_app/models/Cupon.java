package com.twine.arca_app.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by TWINE-DELL on 22/3/2017.
 */

@Table(name = "cupones")
public class Cupon extends Model {
    @Column(name = "id_cupon")
    public int id_cupon;
    @Column(name = "pk_descuento")
    public Descuento descuento;
    @Column(name = "codigo")
    public String codigo;
    @Column(name = "canjeado")
    public boolean canjeado;
    @Column(name = "pkEmpleadoCreacion")
    public Empleado empleado;
    @Column(name = "creado")
    public Date creado;
    @Column(name = "actualizado")
    public Date actualizado;

    public long vence_en(){
        try{
            Calendar day = Calendar.getInstance();
            long diff = day.getTime().getTime() -this.creado.getTime() ;
            long segundos = diff / 1000;
            long minutos = segundos / 60;
            long horas = minutos / 60;
            long dias = horas / 24;
            return this.descuento.vigencia- dias;
        }catch (NullPointerException ex){
            return 0;
        }

    }
    public String label_vence_en(){
        long vencimiento=vence_en();
        if(vencimiento<0)
            return "Cupon vencido";
        else if (vencimiento==0)
            return "Su cupon vence hoy";
        else
            return "Su cupon vence en " + String.valueOf(vencimiento) + " días";
    }
}
