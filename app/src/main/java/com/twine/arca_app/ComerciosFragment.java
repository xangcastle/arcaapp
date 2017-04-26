package com.twine.arca_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.twine.arca_app.adapters.CategoriaComercioAdapter;
import com.twine.arca_app.adapters.CuponAdapter;
import com.twine.arca_app.adapters.DividerItemDecoration;
import com.twine.arca_app.general.Utilidades;
import com.twine.arca_app.models.Comercio;
import com.twine.arca_app.models.Comercio_Categoria;
import com.twine.arca_app.models.Cupon;
import com.twine.arca_app.models.Descuento;
import com.twine.arca_app.models.Empleado;
import com.twine.arca_app.models.Producto;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@EFragment
public class ComerciosFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    @RestService
    RestClient restClient;
    @Bean
    MyRestErrorHandler myErrorhandler;
    @ViewById(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bean
    CategoriaComercioAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        restClient.setRestErrorHandler(myErrorhandler);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comercios, container, false);
    }
    @AfterViews
    void cargaInicial(){
        sincronizar();
    }
    @AfterViews
    void bindAdapter() {
        adapter=new CategoriaComercioAdapter(getContext());
        List<Comercio_Categoria> categorias = Utilidades.db.getCategorias();
        adapter.addAll(categorias);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
    @UiThread
    void refrescarLista(){
        List<Comercio_Categoria> categorias = Utilidades.db.getCategorias();
        adapter.clear();
        adapter.addAll(categorias);
        adapter.notifyDataSetChanged();
    }

    @Click(R.id.readQR)
    void click_readQR(){
        IntentIntegrator integrator = new IntentIntegrator((Activity) getContext());
        integrator.setDesiredBarcodeFormats(integrator.QR_CODE_TYPES);
        Intent scanIntent=integrator.createScanIntent();
        startActivityForResult(scanIntent,IntentIntegrator.REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(IntentIntegrator.REQUEST_CODE==requestCode && (resultCode == Activity.RESULT_OK)){
            IntentResult scanningResult =
                    IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanningResult != null) {
                String scanContent = scanningResult.getContents();
                conversionQRCode(scanContent);
            }
        }
    }
    void conversionQRCode(String scanContent){
        try {
            JSONObject jbarcode=new JSONObject(scanContent);
            Descuento descuento = new Select().from(Descuento.class)
                    .where("id_descuento=?",jbarcode.getInt("e"))
                    .executeSingle();
            if (descuento==null)
                descuento=new Descuento();
            descuento.id_descuento=jbarcode.getInt("e");
            descuento.nombre=jbarcode.getString("f");
            descuento.porcentaje_descuento=jbarcode.getDouble("g");
            descuento.vigencia=jbarcode.getInt("h");

            if (jbarcode.has("i"))
                descuento.desc_dia_vigencia=jbarcode.getInt("i");
            if (jbarcode.has("j"))
                descuento.desc_dia_vigencia_porc_inf=jbarcode.getDouble("j");
            if (jbarcode.has("k"))
                descuento.desc_dia_vigencia_porc_sup=jbarcode.getDouble("k");
            if (jbarcode.has("l"))
                descuento.desc_compra_minima=jbarcode.getDouble("l");
            if (jbarcode.has("m"))
                descuento.desc_compra_minima_porc_inf=jbarcode.getDouble("m");
            if (jbarcode.has("n"))
                descuento.desc_compra_minima_porc_sup=jbarcode.getDouble("n");

            descuento.save();

            Empleado empleado = new Select().from(Empleado.class)
                    .where("id_empleado=?",jbarcode.getInt("b"))
                    .executeSingle();
            if (empleado==null)
                empleado=new Empleado();
            empleado.id_empleado=jbarcode.getInt("b");
            empleado.nombre=jbarcode.getString("c");
            empleado.save();

            Cupon cupon = new Select().from(Cupon.class)
                    .where("codigo=?", jbarcode.getString("a"))
                    .executeSingle();
            if (cupon==null)
                cupon=new Cupon();
            cupon.codigo=jbarcode.getString("a");
            cupon.descuento=descuento;
            cupon.empleado=empleado;
            cupon.canjeado=false;
            cupon.creado= new Date();
            cupon.save();

        } catch (JSONException e) {
            Toast.makeText(getContext(),"No fue posible obneter el cupon",Toast.LENGTH_LONG).show();
        }
    }

    @Background
    void sincronizar(){
        if(Utilidades.isConnected(getContext())){
            String respuesta=restClient.get_comercios();
            guardarComercios(respuesta);
            cargarCupones();
        }
    }
    private void guardarComercios(String respuesta){
        if(respuesta!=null){
            try {
                JSONArray jcomercios=new JSONArray(respuesta);
                for (int i = 0; i < jcomercios.length(); i++) {
                    JSONObject jcomercio=jcomercios.getJSONObject(i);


                    Comercio_Categoria categoria=new Select().from(Comercio_Categoria.class)
                            .where("id_categoria=?", jcomercio.getJSONObject("categoria").getInt("id"))
                            .executeSingle();

                    if(categoria==null)
                        categoria=new Comercio_Categoria();
                    categoria.id_categoria=jcomercio.getJSONObject("categoria").getInt("id");
                    categoria.nombre=jcomercio.getJSONObject("categoria").getString("nombre");
                    categoria.save();

                    Comercio comercio=new Select().from(Comercio.class)
                            .where("id_comercio=?", jcomercio.getInt("id"))
                            .executeSingle();
                    boolean esNuevo=false;
                    if(comercio==null) {
                        comercio = new Comercio();
                        esNuevo=true;
                    }
                    comercio.id_comercio=jcomercio.getInt("id");
                    comercio.nombre=jcomercio.getString("nombre");

                    if (jcomercio.has("direccion"))
                        comercio.direccion=jcomercio.getString("direccion");
                    if (jcomercio.has("telefono"))
                        comercio.telefono=jcomercio.getString("telefono");
                    if (jcomercio.has("latitude"))
                        comercio.latitude=jcomercio.getDouble("latitude");
                    if (jcomercio.has("longitude"))
                        comercio.longitude=jcomercio.getDouble("longitude");
                    if (jcomercio.has("logo"))
                        comercio.logo=jcomercio.getString("logo");
                    if (jcomercio.has("baner"))
                        comercio.baner=jcomercio.getString("baner");

                    comercio.categoria=categoria;
                    comercio.save();
                    if(esNuevo)
                        refrescarLista();

                    JSONArray jdescuentos=jcomercio.getJSONArray("descuentos");
                    for (int j = 0; j < jdescuentos.length(); j++) {
                        JSONObject jdescuento=jdescuentos.getJSONObject(j);
                        Descuento descuento=new Select().from(Descuento.class)
                                .where("id_descuento=?", jdescuento.getInt("id"))
                                .executeSingle();
                        if(descuento==null)
                            descuento=new Descuento();
                        descuento.id_descuento=jdescuento.getInt("id");
                        descuento.nombre=jdescuento.getString("nombre");
                        if(jdescuento.has("porcentaje_descuento") && !jdescuento.isNull("porcentaje_descuento"))
                            descuento.porcentaje_descuento=jdescuento.getDouble("porcentaje_descuento");
                        if(jdescuento.has("vigencia") && !jdescuento.isNull("vigencia"))
                            descuento.vigencia=jdescuento.getInt("vigencia");
                        if(jdescuento.has("desc_dia_vigencia") && !jdescuento.isNull("desc_dia_vigencia"))
                            descuento.desc_dia_vigencia=jdescuento.getInt("desc_dia_vigencia");
                        if(jdescuento.has("desc_dia_vigencia_porc_inf") && !jdescuento.isNull("desc_dia_vigencia_porc_inf"))
                            descuento.desc_dia_vigencia_porc_inf=jdescuento.getDouble("desc_dia_vigencia_porc_inf");
                        if(jdescuento.has("desc_dia_vigencia_porc_sup") && !jdescuento.isNull("desc_dia_vigencia_porc_sup"))
                            descuento.desc_dia_vigencia_porc_sup=jdescuento.getDouble("desc_dia_vigencia_porc_sup");
                        if(jdescuento.has("desc_compra_minima") && !jdescuento.isNull("desc_compra_minima"))
                            descuento.desc_compra_minima=jdescuento.getDouble("desc_compra_minima");
                        if(jdescuento.has("desc_compra_minima_porc_inf") && !jdescuento.isNull("desc_compra_minima_porc_inf"))
                            descuento.desc_compra_minima_porc_inf=jdescuento.getDouble("desc_compra_minima_porc_inf");
                        if(jdescuento.has("desc_compra_minima_porc_sup") && !jdescuento.isNull("desc_compra_minima_porc_sup"))
                            descuento.desc_compra_minima_porc_sup=jdescuento.getDouble("desc_compra_minima_porc_sup");
                        if(jdescuento.has("tipo_cambio") && !jdescuento.isNull("tipo_cambio"))
                            descuento.tipo_cambio=jdescuento.getDouble("tipo_cambio");
                        if(jdescuento.has("activo") && !jdescuento.isNull("activo"))
                            descuento.activo=jdescuento.getBoolean("activo");
                        descuento.comercio=comercio;
                        descuento.save();
                    }
                    JSONArray jproductos=jcomercio.getJSONArray("productos");
                    for (int j = 0; j < jproductos.length(); j++) {
                        JSONObject jproducto = jproductos.getJSONObject(j);
                        Producto producto=new Select().from(Producto.class)
                                .where("id_producto=?", jproducto.getInt("id"))
                                .executeSingle();
                        if(producto==null)
                            producto=new Producto();
                        producto.id_producto=jproducto.getInt("id");
                        producto.nombre=jproducto.getString("nombre");
                        producto.descripcion=jproducto.getString("descripcion");
                        producto.precio=jproducto.getDouble("precio");
                        producto.imagen=jproducto.getString("imagen");
                        producto.descuento=jproducto.getDouble("descuento");
                        producto.activo=jproducto.getBoolean("activo");
                        producto.comercio=comercio;
                        producto.save();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void cargarCupones(){
        List<Cupon> cupones =Utilidades.db.getCuponesPendientesCarga();
        for (Cupon cupon:cupones) {
            String respuesta=restClient.save_cupon(
                    String.valueOf(cupon.descuento.id_descuento),
                    String.valueOf(cupon.empleado.id_empleado),
                    String.valueOf(cupon.codigo),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cupon.creado));
            if(respuesta!=null){
                try {
                    JSONObject jrespuesta=new JSONObject(respuesta);
                    if(jrespuesta.getInt("code")==200){
                        cupon.id_cupon=jrespuesta.getInt("id_cupon");
                        cupon.save();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}