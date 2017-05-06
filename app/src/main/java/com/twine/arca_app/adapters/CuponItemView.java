package com.twine.arca_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twine.arca_app.CuponActivity_;
import com.twine.arca_app.R;
import com.twine.arca_app.general.Utilidades;
import com.twine.arca_app.models.Cupon;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by TWINE-DELL on 24/3/2017.
 */
@EViewGroup(R.layout.item_cupon)
public class CuponItemView extends LinearLayout implements ViewWrapper.Binder<Cupon> {

    @ViewById(R.id.comercio_nombre)
    TextView comercio_nombre;

    @ViewById(R.id.cupon_vencimiento)
    TextView cupon_vencimiento;

    @ViewById(R.id.porcentaje_descuento)
    TextView porcentaje_descuento;

    @ViewById(R.id.imageView)
    ImageView imageView;

    @ViewById(R.id.cardView)
    CardView cardView;

    @ViewById(R.id.condiciones)
    TextView condiciones;

    public CuponItemView(Context context) {
        super(context);
    }

    @Override
    public void onBind(Cupon cupon) {
        final Cupon m=cupon;
        if(cupon.descuento.comercio!=null){
            comercio_nombre.setText(cupon.descuento.comercio.nombre);
        }
        cupon_vencimiento.setText(cupon.label_vence_en());
        condiciones.setText(cupon.descuento.condiciones());

        porcentaje_descuento.setText("-" +
                String.valueOf(cupon.descuento.porcentaje_descuento) +
                " %");
        Utilidades.cargarImageView(imageView,
                Utilidades.BASE_URL + cupon.descuento.comercio.logo,
                R.drawable.shop);

        cardView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CuponActivity_.class);
                intent.putExtra("id_cupon", m.getId());
                v.getContext().startActivity(intent);
            }
        });
    }
}