package com.twine.arca_app;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.twine.arca_app.general.Utilidades;
import com.twine.arca_app.models.Usuario;

import org.androidannotations.annotations.EActivity;

@EActivity
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DescuentosFragment.OnFragmentInteractionListener,
        ComerciosFragment.OnFragmentInteractionListener,
        GoogleApiClient.OnConnectionFailedListener{
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        if(Utilidades.atuenticado_o_redirect(this)){
            Utilidades.checkPermision(MainActivity.this);


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            Usuario usuario =Utilidades.db.getUsuario();
            View header=navigationView.getHeaderView(0);
            ((TextView)header.findViewById(R.id.Nombre)).setText(usuario.getFullname());
            ((TextView)header.findViewById(R.id.Email)).setText(usuario.email);
            ImageView avatar=(ImageView) header.findViewById(R.id.imageViewAvatar);
            Utilidades.cargarImageView(avatar,usuario.foto);
            if (savedInstanceState == null) {
                FragmentManager fm = MainActivity.this.getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.container, new ComerciosFragment_(), "DescuentosFragmentTag");
                ft.addToBackStack("DescuentosFragmentTag");
                ft.commit();
                fm.executePendingTransactions();
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_comecios) {
            FragmentManager fm = MainActivity.this.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container, new ComerciosFragment_(), "DescuentosFragmentTag");
            ft.addToBackStack("DescuentosFragmentTag");
            ft.commit();
            fm.executePendingTransactions();
        } else if (id == R.id.nav_descuentos) {
            FragmentManager fm = MainActivity.this.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container, new DescuentosFragment_(), "DescuentosFragmentTag");
            ft.addToBackStack("DescuentosFragmentTag");
            ft.commit();
            fm.executePendingTransactions();
        } else if (id == R.id.nav_perfil) {

        } else if (id == R.id.nav_salir) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Utilidades.db.localLogOut();
                            Utilidades.atuenticado_o_redirect(MainActivity.this);
                        }
                    });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                android.app.AlertDialog.Builder saveDialog = new android.app.AlertDialog.Builder(MainActivity.this);
                saveDialog.setTitle("Permisos de la aplicacion");
                saveDialog.setMessage("La aplicacion aun necesita que usted otorgue algunos permisos\n\n" +
                        "Si desea otorgarlos ahora, haga clic [Si] y en el siguiente menu contextual clic en [Permitir]. \n\n" +
                        "Si por el contrario hace clic [No] este modulo procederá a cerrarse");
                saveDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Utilidades.checkPermision(MainActivity.this);
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}
