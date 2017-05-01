package com.twine.arca_app.adapters;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Filter;

import com.twine.arca_app.models.Comercio;
import com.twine.arca_app.models.Comercio_Categoria;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                items = (List<Comercio>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Comercio> filteredResults = null;
                if (constraint.length() == 0) {
                    filteredResults = items_bk;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }

            private List<Comercio> getFilteredResults(String s) {
                List<Comercio> results = new ArrayList<>();

                for (Comercio item : items) {
                    if (item.nombre.toLowerCase().contains(s)
                            || item.direccion.toLowerCase().contains(s)) {
                        results.add(item);
                    }
                }
                return results;
            }
        };
    }
}
