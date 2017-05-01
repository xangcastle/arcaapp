package com.twine.arca_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.twine.arca_app.ComercioActivity_;
import com.twine.arca_app.CuponActivity_;
import com.twine.arca_app.R;
import com.twine.arca_app.general.Utilidades;
import com.twine.arca_app.models.Comercio;
import com.twine.arca_app.models.Comercio_Categoria;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by TWINE-DELL on 24/3/2017.
 */
@EViewGroup(R.layout.item_comercio)
public class ComercioItemView extends LinearLayout implements ViewWrapper.Binder<Comercio> {

    @ViewById(R.id.comercio_nombre)
    TextView comercio_nombre;
    @ViewById(R.id.comercio_direccion)
    TextView comercio_direccion;
    @ViewById(R.id.comercio_telefono)
    TextView comercio_telefono;
    @ViewById(R.id.imageView)
    ImageView imageView;
    @ViewById(R.id.baner)
    ImageView baner;
    @ViewById(R.id.ratingBar)
    RatingBar ratingBar;

    @ViewById(R.id.cardView)
    CardView cardView;


    public ComercioItemView(Context context) {
        super(context);
    }

    @Override
    public void onBind(Comercio model) {
        final Comercio m=model;
        if(model.nombre!=null){
            comercio_nombre.setText(model.nombre);
        }
        if(model.direccion!=null){
            comercio_direccion.setText(model.direccion);
        }
        if(model.telefono!=null){
            comercio_telefono.setText(model.telefono);
        }
        Utilidades.cargarImageView(imageView,
                Utilidades.BASE_URL+model.logo,
                R.drawable.shop);
        Utilidades.cargarImageView(baner,
                Utilidades.BASE_URL+model.baner,
                R.drawable.backgroundbaner);

        if(model.rating()==0)
            ratingBar.setRating(1);
        else
            ratingBar.setRating(model.rating());

        cardView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ComercioActivity_.class);
                intent.putExtra("id_comercio", m.getId());
                v.getContext().startActivity(intent);
            }
        });
    }
}