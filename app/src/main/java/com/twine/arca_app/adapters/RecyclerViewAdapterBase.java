package com.twine.arca_app.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.twine.arca_app.models.Cupon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by TWINE-DELL on 24/3/2017.
 */

public abstract class RecyclerViewAdapterBase<T, V extends View & ViewWrapper.Binder<T>>
        extends RecyclerView.Adapter<ViewWrapper<T, V>>
        implements Filterable{

    protected List<T> items = new ArrayList<T>();
    protected List<T> items_bk = new ArrayList<T>();

    @Override
    public final ViewWrapper<T, V> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewWrapper<T, V>(onCreateItemView(parent, viewType));
    }

    protected abstract V onCreateItemView(ViewGroup parent, int viewType);

    @Override
    public final void onBindViewHolder(ViewWrapper<T, V> viewHolder, int position) {
        V view = viewHolder.getView();
        T data = items.get(position);
        view.onBind(data);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void add(T item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void addAll(Collection<T> collection) {
        items.addAll(collection);
        items_bk.addAll(collection);
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
    }

}