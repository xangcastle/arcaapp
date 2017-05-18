package com.twine.arca_app.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.okhttp.internal.Util;
import com.twine.arca_app.R;
import com.twine.arca_app.general.Utilidades;
import com.twine.arca_app.models.Comercio;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose Williams Garcia on 30/4/2017.
 */

public class GridComercioAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private List data = new ArrayList();

    public GridComercioAdapter(Context context, int layoutResourceId, List data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = (scanForActivity(context).getLayoutInflater());
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.comercio_nombre);
            holder.direccion = (TextView) row.findViewById(R.id.comercio_direccion);
            holder.telefono = (TextView) row.findViewById(R.id.comercio_telefono);
            holder.image = (ImageView) row.findViewById(R.id.imageView);
            holder.baner=(ImageView) row.findViewById(R.id.baner);
            holder.ratingBar=(RatingBar) row.findViewById(R.id.ratingBar);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Comercio item = (Comercio) data.get(position);
        holder.imageTitle.setText(item.nombre);
        holder.direccion.setText(item.direccion);
        holder.telefono.setText(item.telefono);
        Utilidades.cargarImageView(holder.image,
                Utilidades.BASE_URL+item.logo,
                R.drawable.shop);
        Utilidades.cargarImageView(holder.baner,
                Utilidades.BASE_URL+item.baner,
                R.drawable.backgroundbaner);

        if(item.rating()==0)
            holder.ratingBar.setRating(1);
        else
            holder.ratingBar.setRating(item.rating());

        return row;
    }
    private static Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity)cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper)cont).getBaseContext());
        return null;
    }
    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
        ImageView baner;
        TextView direccion;
        TextView telefono;
        RatingBar ratingBar;
    }
}