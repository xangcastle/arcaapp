package com.twine.arca_app.adapters;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Filter;

import com.twine.arca_app.models.Comercio_Categoria;
import com.twine.arca_app.models.Cupon;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by TWINE-DELL on 24/3/2017.
 */
@EBean
public class CategoriaComercioAdapter extends RecyclerViewAdapterBase<Comercio_Categoria, CategoriaComercioItemView>  {

    @RootContext
    Context context;

    public CategoriaComercioAdapter(Context context ) {
        this.context = context;
    }

    @Override
    protected CategoriaComercioItemView onCreateItemView(ViewGroup parent, int viewType) {
        return CategoriaComercioItemView_.build(context);
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
