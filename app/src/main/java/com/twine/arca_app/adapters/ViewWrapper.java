package com.twine.arca_app.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by TWINE-DELL on 24/3/2017.
 */

public class ViewWrapper<T, V extends View & ViewWrapper.Binder<T>> extends RecyclerView.ViewHolder {

    private V view;

    public ViewWrapper(V itemView) {
        super(itemView);
        view = itemView;
    }

    public V getView() {
        return view;
    }

    public interface Binder<T> {
        void onBind(T data);
    }
}
