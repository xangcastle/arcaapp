package com.twine.arca_app;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.twine.arca_app.adapters.DescuentoGridAdapter;
import com.twine.arca_app.adapters.ProductoGridAdapter;
import com.twine.arca_app.general.ExpandableHeightGridView;
import com.twine.arca_app.general.Utilidades;
import com.twine.arca_app.models.Comercio;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
@EActivity
public class ComercioActivity extends AppCompatActivity {
    @Extra("id_comercio")
    long id_comercio;
    @ViewById(R.id.imageComercio)
    ImageView imageComercio;
    @ViewById(R.id.imagePortada)
    ImageView imagePortada;
    @ViewById(R.id.nombre_comercio)
    TextView nombre_comercio;
    @ViewById(R.id.direccion_comercio)
    TextView direccion_comercio;
    @ViewById(R.id.telefono_comercio)
    TextView telefono_comercio;
    @ViewById(R.id.viewMap)
    CardView viewMap;

    @ViewById(R.id.gridview)
    ExpandableHeightGridView gridview;
    private ProductoGridAdapter gridAdapter;

    @ViewById(R.id.gridviewDescuentos)
    ExpandableHeightGridView gridviewDescuentos;
    private DescuentoGridAdapter gridDescuentoAdapter;

    Comercio comercio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comercio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @AfterViews
    void renderComercio(){
        if(id_comercio!=0){
            comercio=new Select().from(Comercio.class)
                    .where("id=?",id_comercio)
                    .executeSingle();
            if(comercio!=null){
                direccion_comercio.setText(comercio.direccion);
                telefono_comercio.setText(comercio.telefono);
                nombre_comercio.setText(comercio.nombre);
                Utilidades.cargarImageView(imageComercio,
                        Utilidades.BASE_URL + comercio.logo,
                        R.drawable.shop);
                Utilidades.cargarImageView(imagePortada,
                        Utilidades.BASE_URL+comercio.baner,
                        R.drawable.backgroundbaner);
                if(comercio.latitude==null || comercio.latitude==0)
                    viewMap.setVisibility(View.GONE);
                else
                    viewMap.setVisibility(View.VISIBLE);

                gridAdapter = new ProductoGridAdapter(this, R.layout.item_grid_producto, comercio.productos());
                gridview.setExpanded(true);
                gridview.setAdapter(gridAdapter);

                gridDescuentoAdapter = new DescuentoGridAdapter(this, R.layout.cupon, comercio.descuentos());
                gridviewDescuentos.setExpanded(true);
                gridviewDescuentos.setAdapter(gridDescuentoAdapter);
            }

        }
    }


}
