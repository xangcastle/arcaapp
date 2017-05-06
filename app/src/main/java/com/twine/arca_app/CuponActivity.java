package com.twine.arca_app;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.google.zxing.WriterException;
import com.twine.arca_app.general.Utilidades;
import com.twine.arca_app.models.Cupon;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity
public class CuponActivity extends AppCompatActivity {
    @Extra("id_cupon")
    long id_cupon;
    @ViewById(R.id.imageView)
    ImageView imageQR;
    @ViewById(R.id.imageViewComercio)
    ImageView imageComercio;
    @ViewById(R.id.comercio_nombre)
    TextView comercio_nombre;
    @ViewById(R.id.porcentaje_descuento)
    TextView porcentaje_descuento;
    @ViewById(R.id.cupon_vencimiento)
    TextView cupon_vencimiento;
    @ViewById(R.id.condiciones)
    TextView condiciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cupon);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }
    @AfterViews
    void renderCupon(){
        if(id_cupon!=0){


            Cupon cupon =new Select().from(Cupon.class)
                    .where("id=?",id_cupon).executeSingle();
            if(cupon!=null){
                try {
                    Utilidades.cargarImageView(imageQR,
                            Utilidades.encodeAsBitmap(cupon.codigo));
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                Utilidades.cargarImageView(imageComercio,
                        Utilidades.BASE_URL + cupon.descuento.comercio.logo,
                        R.drawable.shop);
                comercio_nombre.setText(cupon.descuento.comercio.nombre);
                porcentaje_descuento.setText("-" +
                        String.valueOf(cupon.descuento.porcentaje_descuento) + "%");
                cupon_vencimiento.setText(cupon.label_vence_en());
                condiciones.setText(cupon.descuento.condiciones());
            }
        }
    }

}
