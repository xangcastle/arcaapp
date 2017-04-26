package com.twine.arca_app.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.twine.arca_app.R;
import com.twine.arca_app.general.Utilidades;
import com.twine.arca_app.models.Producto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TWINE-DELL on 28/3/2017.
 */

public class ProductoGridAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private List<Producto> data = new ArrayList();

    public ProductoGridAdapter(Context context, int layoutResourceId, List<Producto> data) {
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
            holder.producto_nombre = (TextView) row.findViewById(R.id.producto_nombre);
            holder.producto_precio = (TextView) row.findViewById(R.id.producto_precio);
            holder.producto_anterior = (TextView) row.findViewById(R.id.producto_anterior);
            holder.imageView = (ImageView) row.findViewById(R.id.imageView);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Producto item =  data.get(position);
        holder.producto_nombre.setText(item.nombre);
        if(item.descuento>0){
            holder.producto_precio.setText(String.valueOf(item.descuento) + " C$");
            holder.producto_anterior.setText(String.valueOf(item.precio)+ " C$");
            holder.producto_anterior.setPaintFlags(
                    holder.producto_anterior.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            holder.producto_precio.setText(String.valueOf(item.descuento));
            holder.producto_anterior.setVisibility(View.GONE);
        }
        Utilidades.cargarImageView(holder.imageView,Utilidades.BASE_URL+item.imagen);
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
        TextView producto_nombre;
        TextView producto_precio;
        TextView producto_anterior;
        ImageView imageView;
    }
}