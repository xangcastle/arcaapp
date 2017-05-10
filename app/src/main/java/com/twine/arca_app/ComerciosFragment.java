package com.twine.arca_app;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.twine.arca_app.adapters.ComercioAdapter;
import com.twine.arca_app.adapters.DividerItemDecoration;
import com.twine.arca_app.general.SessionManager;
import com.twine.arca_app.general.Utilidades;
import com.twine.arca_app.models.Comercio;
import com.twine.arca_app.models.Comercio_Categoria;
import com.twine.arca_app.models.Comercio_Rating;
import com.twine.arca_app.models.Cupon;
import com.twine.arca_app.models.Descuento;
import com.twine.arca_app.models.Empleado;
import com.twine.arca_app.models.Producto;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    RecyclerView lista;
    @ViewById(R.id.btnCategorias)
    Button btnCategorias;
    @ViewById(R.id.swipeRefresh)
    SwipeRefreshLayout refreshLayout;

    ComercioAdapter adapter;

    SessionManager session;
    List<Comercio> comercios;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        restClient.setRestErrorHandler(myErrorhandler);
        session=new SessionManager(getContext());
        setHasOptionsMenu(true);


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comercios, container, false);
    }

    @AfterViews
    void cargaInicial(){
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        sincronizar();
                    }
                }
        );
        refreshLayout.setColorSchemeResources(
                R.color.blue_800,
                R.color.purple_600,
                R.color.orange_800,
                R.color.lime_A800
        );
        sincronizar();

    }
    @AfterViews
    void bindAdapter() {
        String categoria_id=session.getSharedValue("categoria_id");
        if(categoria_id==null) {
            session.saveSharedValue("categoria_id", "0");
            categoria_id="0";
        }


        Comercio_Categoria categoria = new Select().from(Comercio_Categoria.class)
                .where("id_categoria=?", Long.parseLong(categoria_id)).executeSingle();

        if(categoria_id.equals("0"))
            btnCategorias.setText("CATEGORIA: TODAS");
        else
            btnCategorias.setText("CATEGORIA: " + categoria.nombre.toUpperCase());

        if(categoria==null || categoria.id_categoria==0){
            comercios = Utilidades.db.getComercios();
        }else
            comercios=Utilidades.db.getComerciobyCategoria(categoria);


        adapter=new ComercioAdapter(getContext());
        adapter.addAll(comercios);
        lista.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                                                        getContext(),
                                                        LinearLayoutManager.VERTICAL,
                                                        false);
        lista.setLayoutManager(layoutManager);
        lista.setItemAnimator(new DefaultItemAnimator());
        lista.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        lista.setAdapter(adapter);

    }
    @UiThread
    void refrescarLista(){
        String categoria_id=session.getSharedValue("categoria_id");
        if(categoria_id==null)
            session.saveSharedValue("categoria_id","0");

        Comercio_Categoria categoria = new Select().from(Comercio_Categoria.class)
                .where("id_categoria=?", Long.parseLong(categoria_id)).executeSingle();

        if(categoria_id.equals("0"))
            btnCategorias.setText("CATEGORIA: TODAS");
        else
            btnCategorias.setText("CATEGORIA: " + categoria.nombre.toUpperCase());

        comercios.clear();
        List<Comercio> comerciotmp;
        if(categoria==null || categoria.id_categoria==0){
            comerciotmp = Utilidades.db.getComercios();
        }else
            comerciotmp=Utilidades.db.getComerciobyCategoria(categoria);
        for (Comercio comercio:comerciotmp) {
            comercios.add(comercio);
        }
        adapter.clear();
        adapter.addAll(comercios);
        if(refreshLayout!=null)
            refreshLayout.setRefreshing(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_main_tab, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                refrescarLista();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                List<Comercio> comerciotmp = Utilidades.db.getComercios();
                adapter.clear();
                adapter.addAll(comerciotmp);
                adapter.getFilter().filter(newText);
                return true;
            }

        });
        super.onCreateOptionsMenu(menu,inflater);
    }


    @ItemClick(R.id.gridview)
    void gridItemClick(int position){
        Comercio comercio=comercios.get(position);
        Intent intent = new Intent(getContext(), ComercioActivity_.class);
        intent.putExtra("id_comercio", comercio.getId());
        getContext().startActivity(intent);
    }

    @Click(R.id.btnCategorias)
    void btnCategorias_Click(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final List<Comercio_Categoria> categorias=Utilidades.db.getCategorias();
        List<String> strCategorias =new ArrayList<>();
        for (Comercio_Categoria catetoria:categorias) {
            if(catetoria.id_categoria==0)
                strCategorias.add("TODAS");
            else
                strCategorias.add(catetoria.nombre.toUpperCase());
        }

        CharSequence[] items= strCategorias.toArray(new CharSequence[strCategorias.size()]);
        builder.setTitle(R.string.slect_categoria)
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(categorias.get(which).id_categoria==0)
                            btnCategorias.setText("CATEGORIA: TODAS");
                        else
                            btnCategorias.setText("CATEGORIA: " + categorias.get(which).nombre.toUpperCase());
                        session.saveSharedValue("categoria_id",
                                String.valueOf(categorias.get(which).id_categoria));
                        refrescarLista();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    /*@Click(R.id.readQR)
    void click_readQR(){
        IntentIntegrator integrator = new IntentIntegrator((Activity) getContext());
        integrator.setDesiredBarcodeFormats(integrator.QR_CODE_TYPES);
        Intent scanIntent=integrator.createScanIntent();
        startActivityForResult(scanIntent,IntentIntegrator.REQUEST_CODE);
    }*/
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
            refrescarLista();
        }
    }

    private void guardarComercios(String respuesta){
        if(respuesta!=null){
            try {
                JSONArray jcomercios=new JSONArray(respuesta);
                for (int i = 0; i < jcomercios.length(); i++) {
                    JSONObject jcomercio=jcomercios.getJSONObject(i);


                    Comercio_Categoria categoria_todas=new Select().from(Comercio_Categoria.class)
                            .where("id_categoria=?", 0)
                            .executeSingle();

                    if(categoria_todas==null){
                        categoria_todas=new Comercio_Categoria();
                        categoria_todas.id_categoria=0;
                        categoria_todas.nombre="Sin categoria";
                        categoria_todas.save();
                    }


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

                    Comercio_Rating rating=new Select().from(Comercio_Rating.class)
                            .where("pk_comercio").executeSingle();

                    if(rating==null)
                        rating=new Comercio_Rating();
                    rating.comercio=comercio;

                    if(jcomercio.has("rating")&&!jcomercio.isNull("rating"))
                        rating.rating=jcomercio.getInt("rating");
                    else
                        rating.rating=1;

                    rating.save();

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
                        producto.precio=jproducto.isNull("precio")?0:jproducto.getDouble("precio");
                        producto.imagen=jproducto.getString("imagen");
                        producto.descuento=jproducto.isNull("descuento")?0:jproducto.getDouble("descuento");
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
                    String.valueOf(Utilidades.db.getUsuario().codigo),
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
