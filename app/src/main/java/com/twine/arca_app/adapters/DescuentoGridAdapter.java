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
import com.twine.arca_app.models.Descuento;
import com.twine.arca_app.models.Producto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TWINE-DELL on 28/3/2017.
 */

public class DescuentoGridAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private List<Descuento> data = new ArrayList();

    public DescuentoGridAdapter(Context context, int layoutResourceId, List<Descuento> data) {
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
            holder.porcentaje = (TextView) row.findViewById(R.id.textView2);
            holder.descuento_obten_hasta = (TextView) row.findViewById(R.id.textView5);
            holder.condiciones=(TextView) row.findViewById(R.id.condiciones);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Descuento item =  data.get(position);
        holder.porcentaje.setText(String.valueOf(item.porcentaje_descuento)  + " %");
        if ((item.desc_compra_minima!=null && item.desc_compra_minima>0)
                || item.desc_dia_vigencia>0)
            holder.descuento_obten_hasta.setText("HASTA");
        else
            holder.descuento_obten_hasta.setText("OBTEN");
        holder.condiciones.setText(item.condiciones());

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
        TextView porcentaje;
        TextView descuento_obten_hasta;
        TextView condiciones;
    }
}