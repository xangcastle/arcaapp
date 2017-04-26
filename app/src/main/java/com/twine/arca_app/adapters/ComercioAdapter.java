package com.twine.arca_app.adapters;

import android.content.Context;
import android.view.ViewGroup;

import com.twine.arca_app.models.Comercio;
import com.twine.arca_app.models.Comercio_Categoria;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by TWINE-DELL on 24/3/2017.
 */
@EBean
public class ComercioAdapter extends RecyclerViewAdapterBase<Comercio, ComercioItemView>  {

    @RootContext
    Context context;

    public ComercioAdapter(Context context ) {
        this.context = context;
    }

    @Override
    protected ComercioItemView onCreateItemView(ViewGroup parent, int viewType) {
        return ComercioItemView_.build(context);
    }

}
