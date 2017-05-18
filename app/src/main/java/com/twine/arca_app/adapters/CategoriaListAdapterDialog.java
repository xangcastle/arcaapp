package com.twine.arca_app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.widget.IconTextView;
import com.twine.arca_app.R;
import com.twine.arca_app.general.Utilidades;
import com.twine.arca_app.models.Comercio_Categoria;

import java.util.List;

/**
 * Created by Jose Williams Garcia on 18/5/2017.
 */

public class CategoriaListAdapterDialog extends BaseAdapter {

    private static final String TAG = "CategoriaListAdapter";
    private List<Comercio_Categoria> listData;
    private Context context;
    private LayoutInflater layoutInflater;

    public CategoriaListAdapterDialog(Context context, List<Comercio_Categoria> listData) {
        this.listData = listData;
        this.context=context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_categoria, null);
            holder = new ViewHolder();
            holder.categoria = (TextView) convertView.findViewById(R.id.nombre_categoria);
            holder.icon_imagen=(ImageView)convertView.findViewById(R.id.icon_imagen);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.categoria.setText(listData.get(position).id_categoria==0?"TODAS":
                listData.get(position).nombre.toString().toUpperCase());
        if (listData.get(position).id_categoria!=0){
            if(listData.get(position).icono!=null&&listData.get(position).icono.length()>1){
                try{
                    FontAwesomeIcons ficon = FontAwesomeIcons.valueOf("fa_" + listData.get(position).icono.replace("-","_"));
                    IconDrawable drawableicon = new IconDrawable(this.context,ficon.key());
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.icon_imagen.setBackgroundDrawable(drawableicon.color(Color.GRAY));
                    }else {
                        holder.icon_imagen.setBackground(drawableicon.color(Color.GRAY));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                FontAwesomeIcons ficon = FontAwesomeIcons.valueOf("fa_chevron_down");
                IconDrawable drawableicon = new IconDrawable(this.context,ficon.key());
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.icon_imagen.setBackgroundDrawable(drawableicon.color(Color.GRAY));
                }else {
                    holder.icon_imagen.setBackground(drawableicon.color(Color.GRAY));
                }
            }
        }else {
            FontAwesomeIcons ficon = FontAwesomeIcons.valueOf("fa_share_square_o");
            IconDrawable drawableicon = new IconDrawable(this.context,ficon.key());
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.icon_imagen.setBackgroundDrawable(drawableicon.color(Color.GRAY));
            }else {
                holder.icon_imagen.setBackground(drawableicon.color(Color.GRAY));
            }
        }

        return convertView;
    }

    static class ViewHolder {
        TextView categoria;
        ImageView icon_imagen;
    }

}