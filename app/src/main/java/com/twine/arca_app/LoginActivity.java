package com.twine.arca_app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.squareup.picasso.Picasso;
import com.twine.arca_app.general.Utilidades;
import com.twine.arca_app.models.Usuario;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.rest.spring.annotations.RestService;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
@EActivity
public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private ImageView google_icon;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;
    private GoogleApiClient mGoogleApiClient;
    @RestService
    RestClient restClient;
    @Bean
    MyRestErrorHandler myErrorhandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        restClient.setRestErrorHandler(myErrorhandler);
        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);
        google_icon = (ImageView) findViewById(R.id.google_icon);
        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestScopes(new Scope(Scopes.PLUS_ME))
                .requestScopes(new Scope(Scopes.PROFILE))
                .requestEmail()
                .requestProfile()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

        Utilidades.checkPermision(LoginActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }
    private void signIn() {
        if(!Utilidades.isConnected(this)){
            Toast.makeText(this,"Vefirique su conexión",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Background
    void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.

            GoogleSignInAccount acct = result.getSignInAccount();
            assert acct != null;

            /*if (mGoogleApiClient.hasConnectedApi(Plus.API)){
                Plus.PeopleApi.load(mGoogleApiClient, acct.getId()).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
                    @Override
                    public void onResult(@NonNull People.LoadPeopleResult loadPeopleResult) {
                        Person person = loadPeopleResult.getPersonBuffer().get(0);
                        Log.d(TAG,"Person loaded");
                        Log.d(TAG,"GivenName "+person.getName().getGivenName());
                        Log.d(TAG,"FamilyName "+person.getName().getFamilyName());
                        Log.d(TAG,("DisplayName "+person.getDisplayName()));
                        Log.d(TAG,"Gender "+person.getGender());
                        Log.d(TAG,"Url "+person.getUrl());
                        Log.d(TAG,"CurrentLocation "+person.getCurrentLocation());
                        Log.d(TAG,"AboutMe "+person.getAboutMe());
                        Log.d(TAG,"Birthday "+person.getBirthday());
                        Log.d(TAG,"Image "+person.getImage());
                    }
                });
            }*/

            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            Usuario usuario=new Select().from(Usuario.class).where("email=?", personEmail).executeSingle();
            if(usuario==null)
                usuario=new Usuario();
            usuario.nombre=personGivenName;
            usuario.apellido=personFamilyName;
            usuario.email=personEmail;
            usuario.username=personEmail.split("@")[0];
            usuario.foto=personPhoto.toString();
            usuario.activo=true;

            String resultado = restClient.createUserAuth(
                    String.valueOf(usuario.username),
                    String.valueOf(usuario.nombre),
                    String.valueOf(usuario.apellido),
                    String.valueOf(usuario.edad),
                    String.valueOf(usuario.genero),
                    String.valueOf(usuario.email),
                    String.valueOf(usuario.telefono));
            if(resultado!=null){
                try {
                    JSONObject jresultado=new JSONObject(resultado);
                    if (jresultado.getInt("code")==200) {
                        usuario.codigo= jresultado.getString("codigo");
                        usuario.id_usuario = jresultado.getInt("id_usuario");
                        usuario.edad=jresultado.getString("age");
                        usuario.telefono=jresultado.getString("telefono").equals("null")?"":
                                         jresultado.getString("telefono");
                        usuario.genero=jresultado.getString("gender").equals("null")?"Masculino":
                                        jresultado.getString("gender");
                        usuario.save();
                    }else
                        handleSignInResult(false);
                } catch (JSONException e) {
                    handleSignInResult(false);
                }
            }else
                handleSignInResult(false);
        }
        handleSignInResult(result.isSuccess());
    }
    @UiThread
    void handleSignInResult(Boolean success){
        if(success){
            Intent localIntent = new Intent(LoginActivity.this, MainTabActivity_.class);
            LoginActivity.this.startActivity(localIntent);
            LoginActivity.this.finish();
        }else {
            /*Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                        }
                    });*/
            Toast.makeText(this,"No fue posible autenticar\nIntentelo nuevamente",Toast.LENGTH_LONG).show();
        }
    }
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                android.app.AlertDialog.Builder saveDialog = new android.app.AlertDialog.Builder(LoginActivity.this);
                saveDialog.setTitle("Permisos de la aplicacion");
                saveDialog.setMessage("La aplicacion aun necesita que usted otorgue algunos permisos\n\n" +
                        "Si desea otorgarlos ahora, haga clic [Si] y en el siguiente menu contextual clic en [Permitir]. \n\n" +
                        "Si por el contrario hace clic [No] este modulo procederá a cerrarse");
                saveDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Utilidades.checkPermision(LoginActivity.this);
                    }
                });
                saveDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                saveDialog.show();
                return;
            }
        }
    }


}

