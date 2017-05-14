package com.twine.arca_app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.twine.arca_app.general.Utilidades;
import com.twine.arca_app.models.Usuario;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;

@EFragment(R.layout.fragment_perfil)
public class PerfilFragment extends Fragment {
    private static final String TAG = "PerfilFragment";
    private OnFragmentInteractionListener mListener;
    @ViewById(R.id.imageViewAvatar)
    ImageView avatar;
    @ViewById(R.id.nombre)
    TextView nombre;
    @ViewById(R.id.edad)
    TextView edad;
    @ViewById(R.id.telefono)
    TextView telefono;
    @ViewById(R.id.genero)
    Spinner genero;
    @ViewById(R.id.qrImagen)
    ImageView qrImagen;
    @RestService
    RestClient restClient;
    @Bean
    MyRestErrorHandler myErrorhandler;

    public PerfilFragment() {
        // Required empty public constructor
    }


    Usuario usuario;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_perfil, container, false);
        restClient.setRestErrorHandler(myErrorhandler);
        Usuario usuario=Utilidades.db.getUsuario();

        return view;
    }
    @AfterViews
    void cargarPerfil(){
        usuario= Utilidades.db.getUsuario();
        nombre.setText(usuario.getFullname());
        Utilidades.cargarImageView(avatar,
                usuario.foto,
                R.mipmap.ic_launcher);
        try {
            Utilidades.cargarImageView(qrImagen,
                    Utilidades.encodeAsBitmap(usuario.codigo));
        } catch (WriterException e) {
            e.printStackTrace();
        }
        genero.setSelection(usuario.genero!=null&&usuario.genero.equals("Femenino")?1:0);
        genero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizarCliente();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        edad.setText(usuario.edad);
        telefono.setText(usuario.telefono);

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

    @FocusChange({R.id.telefono,R.id.edad})
    void focusChange(){
        actualizarCliente();
    }
    @Background
    void actualizarCliente(){
        try{
            Usuario usuario=Utilidades.db.getUsuario();
            usuario.edad=edad.getText().toString();
            usuario.telefono=telefono.getText().toString();
            usuario.genero=genero.getSelectedItem().toString();
            usuario.save();
            String respuesta=restClient.createUserAuth(
                    String.valueOf(usuario.username),
                    String.valueOf(usuario.nombre),
                    String.valueOf(usuario.apellido),
                    String.valueOf(usuario.edad),
                    String.valueOf(usuario.genero),
                    String.valueOf(usuario.email),
                    String.valueOf(usuario.telefono));
            Log.d(TAG, "actualizarCliente: "+ respuesta);
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
