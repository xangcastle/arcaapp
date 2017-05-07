package com.twine.arca_app.general;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import com.activeandroid.query.Select;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.squareup.picasso.Picasso;
import com.twine.arca_app.LoginActivity_;
import com.twine.arca_app.R;
import com.twine.arca_app.models.Comercio;
import com.twine.arca_app.models.Comercio_Categoria;
import com.twine.arca_app.models.Cupon;
import com.twine.arca_app.models.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TWINE-DELL on 22/3/2017.
 */

public  class Utilidades {
    private static final String TAG = "Utilidades";
    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 400;
    public final static int HEIGHT = 400;
    //public final static String BASE_URL="http://192.168.0.38:8000";
    //public final static String BASE_URL="http://192.168.232.1:8000";
    public final static String BASE_URL="http://demos.deltacopiers.com";
    public static boolean is_autenticado(Context context){
        Usuario usuario=new Select().from(Usuario.class).where("activo=?",true).executeSingle();
        if (usuario!=null)
            return true;
        else{
            return false;
            /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            return opr.isDone();*/
        }

    }
    public static Boolean atuenticado_o_redirect(Context context){
        if (!is_autenticado(context)){
            context.startActivity(new Intent(context, LoginActivity_.class));
            ((Activity) context).finish();
            return false;
        }
        return true;
    }
    public static Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }
    public static  void cargarImageView(ImageView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
    public static  void cargarImageView(ImageView imageView, String URL) {
        try {
            if(URL.contains("DCIM")){
                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(URL), 600, 600);
                imageView.setTag(URL);
                imageView.setImageBitmap(ThumbImage);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }else {
                Picasso picaso= Picasso.with(imageView.getContext());
                picaso.load(URL)
                        .resize(400, 400).into(imageView);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static  void cargarImageView(ImageView imageView, String URL, int drawableid ) {
        try {
            if(URL.contains("DCIM")){
                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(URL), 600, 600);
                imageView.setTag(URL);
                imageView.setImageBitmap(ThumbImage);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }else {
                Picasso picaso= Picasso.with(imageView.getContext());
                picaso.load(URL)
                        .error(drawableid)
                        .placeholder(drawableid).into(imageView);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static class db {
        public static Usuario getUsuario() {
            return new Select().from(Usuario.class).where("activo=?", true).executeSingle();
        }

        public static List<Cupon> getCupones() {
            return new Select().from(Cupon.class)
                    .orderBy("creado desc")
                    .execute();
        }

        public static List<Comercio_Categoria> getCategorias() {
            List<Comercio_Categoria> respuesta = new ArrayList<>();
            List<Comercio_Categoria> categorias = new Select().from(Comercio_Categoria.class)
                    .execute();
            for (Comercio_Categoria categoria : categorias) {
                if (new Select().from(Comercio.class).where("pk_categoria=?", categoria.getId()).count() > 0)
                    respuesta.add(categoria);
            }
            return respuesta;
        }

        public static List<Comercio> getComercios() {
            return new Select().from(Comercio.class).execute();
        }

        public static List<Comercio> getComerciobyCategoria(Comercio_Categoria categoria) {
            return new Select().from(Comercio.class)
                    .where("pk_categoria =?", categoria.getId())
                    .execute();
        }

        public static List<Comercio> getComerciobyNombre(String filtro) {
            return new Select().from(Comercio.class)
                    .where("nombre LIKE ?", new String[]{'%' + filtro + '%'})
                    .or("telefono LIKE ?", new String[]{'%' + filtro + '%'})
                    .or("direccion LIKE ?", new String[]{'%' + filtro + '%'})
                    .execute();
        }

        public static List<Cupon> getCuponesPendientesCarga() {
            return new Select().from(Cupon.class)
                    .where("id_cupon=?", 0)
                    .execute();
        }

        public static void localLogOut() {
            Usuario usuario = getUsuario();
            usuario.activo = false;
            usuario.save();
        }
    }
    //region PERMISOS
    public static final int PERMISIONS_REQUEST = 12;
    public static void checkPermision(Context context) {


        ArrayList<String> requestedPermissionsGranted=new ArrayList<String>();
        try {
            ApplicationInfo MyapplicationInfo = context.getApplicationInfo();
            PackageManager pm =context.getPackageManager();

            PackageInfo packageInfo = pm.getPackageInfo(MyapplicationInfo.packageName, PackageManager.GET_PERMISSIONS);

            //Get Permissions
            String[] requestedPermissions = packageInfo.requestedPermissions;

            if(requestedPermissions != null) {
                for (int i = 0; i < requestedPermissions.length; i++) {
                    Log.d(TAG, "MyPermisions: " + requestedPermissions[i]);
                    if(ContextCompat.checkSelfPermission(context, requestedPermissions[i]) != PackageManager.PERMISSION_GRANTED)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requestedPermissions[i].contains("SYSTEM_ALERT_WINDOW")){
                            Log.d( TAG, "MyPermisions: no se permite permiso SYSTEM_ALERT_WINDOW en Marshmallow");
                        }else if(requestedPermissions[i].contains("BIND_ACCESSIBILITY_SERVICE")){
                            Log.d( TAG, "MyPermisions: no se permite permiso BIND_ACCESSIBILITY_SERVICE en Marshmallow");
                        }
                        else
                            requestedPermissionsGranted.add(requestedPermissions[i]);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(requestedPermissionsGranted.size()>0){
            ActivityCompat.requestPermissions(((Activity) context),
                    requestedPermissionsGranted.toArray(new String[] {}),
                    PERMISIONS_REQUEST);
        }
    }
    //endregion
    //region STATUS DE CONECCION
    public static boolean isConnected(Context context) {
        try{
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }}
        catch (NullPointerException ex){return false;}
        catch (Exception ex){return false;}
    }
    public static boolean isWifiNetWork(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

    //endregion
}
