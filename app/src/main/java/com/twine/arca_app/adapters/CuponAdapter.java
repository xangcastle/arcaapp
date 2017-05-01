package com.twine.arca_app.adapters;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.twine.arca_app.models.Cupon;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TWINE-DELL on 24/3/2017.
 */
@EBean
public class CuponAdapter extends RecyclerViewAdapterBase<Cupon, CuponItemView>  {

    @RootContext
    Context context;

    public CuponAdapter( Context context ) {
        this.context = context;
    }

    @Override
    protected CuponItemView onCreateItemView(ViewGroup parent, int viewType) {
        return CuponItemView_.build(context);
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
