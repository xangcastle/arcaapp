package com.twine.arca_app.adapters;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twine.arca_app.R;
import com.twine.arca_app.general.Utilidades;
import com.twine.arca_app.models.Comercio;
import com.twine.arca_app.models.Comercio_Categoria;
import com.twine.arca_app.models.Cupon;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by TWINE-DELL on 24/3/2017.
 */
@EViewGroup(R.layout.item_categoria_comercios)
public class CategoriaComercioItemView extends LinearLayout implements ViewWrapper.Binder<Comercio_Categoria> {

    //@ViewById(R.id.NombreCategoria)
    //TextView NombreCategoria;

    @ViewById(R.id.recyclerView)
    RecyclerView recyclerView;

    @Bean
    ComercioAdapter adapter;

    public CategoriaComercioItemView(Context context) {
        super(context);
    }

    @Override
    public void onBind(Comercio_Categoria model) {
        if(model.nombre!=null){
            //NombreCategoria.setText(model.nombre);
        }
        adapter=new ComercioAdapter(getContext());
        List<Comercio> comercios = Utilidades.db.getComerciobyCategoria(model);
        adapter.addAll(comercios);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(
                        getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}