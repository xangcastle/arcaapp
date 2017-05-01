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
import com.activeandroid.query.Update;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.twine.arca_app.adapters.CuponAdapter;
import com.twine.arca_app.adapters.CuponAdapter_;
import com.twine.arca_app.adapters.DividerItemDecoration;
import com.twine.arca_app.general.Utilidades;
import com.twine.arca_app.models.Cupon;
import com.twine.arca_app.models.Descuento;
import com.twine.arca_app.models.Empleado;
import com.twine.arca_app.models.Usuario;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@EFragment
public class DescuentosFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    @ViewById(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bean
    CuponAdapter adapter;
    @RestService
    RestClient restClient;
    @Bean
    MyRestErrorHandler myErrorhandler;
    List<Cupon> cupones;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        restClient.setRestErrorHandler(myErrorhandler);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_descuentos, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        descargarCupones();
    }

    @AfterViews
    void bindAdapter() {
        adapter=new CuponAdapter(getContext());
        cupones = Utilidades.db.getCupones();
        adapter.addAll(cupones);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
    }
    @UiThread
    void recargarLista(){
        adapter.notifyDataSetChanged();
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

            cargarCupon(cupon);
        } catch (JSONException e) {
            Toast.makeText(getContext(),"No fue posible obneter el cupon",Toast.LENGTH_LONG).show();
        }
    }

    @Background
    void descargarCupones(){
        Usuario usuario=new Select().from(Usuario.class).where("activo=?",true).executeSingle();
        if (usuario!=null)
            if (Utilidades.isConnected(getContext())) {
                String respuesta = restClient.get_cupones(usuario.username);
                if(respuesta!=null){
                    try {
                        JSONObject jrespuesta=new JSONObject(respuesta);
                        if(jrespuesta.getInt("code")==200){
                            JSONArray jcupones = jrespuesta.getJSONArray("cupones");
                            for (int i = 0; i < jcupones.length(); i++) {
                                JSONObject jcupon = jcupones.getJSONObject(i);
                                Empleado empleado = new Select().from(Empleado.class)
                                        .where("id_empleado=?",jcupon.getJSONObject("creado_por").getInt("id"))
                                        .executeSingle();
                                if (empleado==null)
                                    empleado=new Empleado();
                                empleado.id_empleado=jcupon.getJSONObject("creado_por").getInt("id");
                                empleado.nombre=jcupon.getJSONObject("creado_por").getString("nombre");
                                empleado.save();

                                Descuento descuento = new Select().from(Descuento.class)
                                        .where("id_descuento=?",jcupon.getInt("id_descuento"))
                                        .executeSingle();

                                if(descuento!=null && empleado!=null){
                                    Cupon cupon = new Select().from(Cupon.class)
                                            .where("id_cupon=?",jcupon.getInt("id"))
                                            .executeSingle();
                                    Boolean isnew=false;
                                    if (cupon==null) {
                                        cupon = new Cupon();
                                        isnew=true;
                                    }
                                    cupon.id_cupon=jcupon.getInt("id");
                                    cupon.codigo=jcupon.getString("codigo");
                                    cupon.canjeado=jcupon.getBoolean("canjeado");
                                    try {
                                        cupon.creado=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                                                .parse(jcupon.getString("creado")) ;
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    cupon.empleado=empleado;
                                    cupon.descuento=descuento;
                                    cupon.save();
                                    if(isnew)
                                        cupones.add(cupon);
                                        recargarLista();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
    }

    @Background
    void cargarCupon(Cupon cupon){
        if(Utilidades.isConnected(getContext())){
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
