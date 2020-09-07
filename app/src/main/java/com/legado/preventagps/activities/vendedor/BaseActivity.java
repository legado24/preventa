package com.legado.preventagps.activities.vendedor;

import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;

import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.legado.preventagps.R;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.bd.ContratoPreventa;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Notification;
import com.legado.preventagps.modelo.PaqueteMensaje;
import com.legado.preventagps.modelo.PreVenta;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.UtilAndroid;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by __Adrian__ on 20/03/2019.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar actionBarToolbar;
    protected static final int NAV_DRAWER_ITEM_INVALID = -1;
    SessionUsuario sessionUsuario;
    private BroadcastReceiver br;
    public static final String CHANNEL_ID = "exampleServiceChannel";


    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";
    private Location location;
    private LatLng latLng;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private String provider;
    private boolean isNetworkLocation, isGPSLocation;
    private FusedLocationProviderClient mFusedLocationClient;


    String fechaDevice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionUsuario = new SessionUsuario(this);
        fechaDevice = UtilAndroid.fechaDevice("dd-MM-yyyy");
        createNotificationChannel();
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // enviarUbicacion();
                PaqueteMensaje paqueteMensaje = new PaqueteMensaje();
                Notification not = new Notification(intent.getStringExtra("title"), intent.getStringExtra("body"), intent.getStringExtra("mensaje"));
                paqueteMensaje.setNotification(not);
                Alerter.create(BaseActivity.this)
                        .setTitle(not.getTitle()).setBackgroundColor(R.color.amber900)
                        .setText(not.getBody())
                        .show();
            }
        };


    }

    public void enviarUbicacion() {
        PaqueteMensaje mensaje = new PaqueteMensaje();
        List<String> usuarios = new ArrayList<>();
        usuarios.add("CRISDIAZ");
        mensaje.setUsuarios(usuarios);
        Notification notification = new Notification("aa", latLng.longitude + ";" + latLng.latitude, "ggg");
        notification.setClickAction("INGRESOSCOMPRA");
        mensaje.setNotification(notification);
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getNotificacionService().enviarUbicacionVendedor(mensaje);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if (response.body().getEstado() == 1) {


                } else {
                    Toast.makeText(getApplicationContext(), response.body().getMensaje(), Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                Log.wtf("as", t.toString());
                Toast.makeText(getApplicationContext(), "NO HAY CONEXION CON EL SERVIDOR", Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
    }

    private void setupNavDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout == null) {
            return;
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupCardSelectListener();
            setSelectedItem(navigationView);
        }
    }

    private void setSelectedItem(NavigationView navigationView) {
        int selectedItem = getSelfNavDrawerItem();
        navigationView.setCheckedItem(selectedItem);
    }

    private void onNavigationItemClicked(final int itemId) {
        if (itemId == getSelfNavDrawerItem()) {
            closeDrawer();
            return;
        }
        goToNavDrawerItem(itemId);
    }

    protected void openDrawer() {
        if (drawerLayout == null)
            return;
        ObjectAnimator.ofFloat(actionBarToolbar, View.ROTATION.getName(), 360).start();
        drawerLayout.openDrawer(GravityCompat.START);

        long cantPendientes = ApiSqlite.getInstance(getApplicationContext()).getOperacionesBaseDatos().getCountNoPedidosPendientes(fechaDevice, sessionUsuario.getPaqueteUsuario().getUsuario()) + ApiSqlite.getInstance(getApplicationContext()).getOperacionesBaseDatos().getCountPedidosPendientes(fechaDevice, sessionUsuario.getPaqueteUsuario().getUsuario());
        TextView txtAlertaNav = (TextView) findViewById(R.id.txtAlertaNav);
        ImageView imgAlertaNav = (ImageView) findViewById(R.id.imagenAlertaNav);
        if(cantPendientes>0){
            txtAlertaNav.setVisibility(View.VISIBLE);
            imgAlertaNav.setVisibility(View.VISIBLE);
            txtAlertaNav.setText(cantPendientes + "");
        }else{
            txtAlertaNav.setVisibility(View.INVISIBLE);
            imgAlertaNav.setVisibility(View.INVISIBLE);
        }




    }

    protected void closeDrawer() {
        if (drawerLayout == null)
            return;
        drawerLayout.closeDrawer(GravityCompat.START);
    }


    private void setupCardSelectListener() {
        final CardView menu0 = (CardView) findViewById(R.id.menu0);
        final CardView menu1 = (CardView) findViewById(R.id.menu1);
        final CardView menu2 = (CardView) findViewById(R.id.menu2);
        final CardView menu3 = (CardView) findViewById(R.id.menu3);
        final CardView menu4 = (CardView) findViewById(R.id.menu4);
        final TextView linkManualDash = findViewById(R.id.linkManualDash);
        linkManualDash.setMovementMethod(LinkMovementMethod.getInstance());
        menu0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNavigationItemClicked(menu0.getId());
            }
        });
        menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNavigationItemClicked(menu1.getId());
            }
        });
        menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNavigationItemClicked(menu2.getId());
            }
        });
        menu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNavigationItemClicked(menu3.getId());
            }
        });
        menu4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNavigationItemClicked(menu4.getId());
            }
        });
    }

    protected void goToNavDrawerItem(int item) {
    }

    protected ActionBar getActionBarToolbar() {
        if (actionBarToolbar == null) {
            actionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (actionBarToolbar != null) {
                setSupportActionBar(actionBarToolbar);
            }
        }
        return getSupportActionBar();
    }

    protected int getSelfNavDrawerItem() {
        return NAV_DRAWER_ITEM_INVALID;
    }

    public abstract boolean providesActivityToolbar();

    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void logout(SessionUsuario sessionUsuario) {

        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Confirmación")
                .setContentText("Desea cerrar la sesión?")
                .setConfirmText("SI")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                        sessionUsuario.borrarDatosSesion();
                        limpiarTablaClientes();
                        limpiarMetricas();
                        limpiarDatosUsuario();
                        limpiarCondiciones();
                        limpiarArticulos();
                        limpiarFocusSugeridos();
                        limpiarPedidosNoPedidosAnteriores(fechaDevice);
                        sessionUsuario.guardarIsTodoSincronizado(false);
                        sessionUsuario.guardarUrlPreventa(null);
                        finish();
                        startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                    }
                })
                .setCancelButton("NO", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();



    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(br);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(br, new IntentFilter("MENSAJE"));
    }

    public void limpiarTablaClientes() {
        try {
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deleteTablaClientes();
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
        } finally {
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
        }
    }

    public void limpiarMetricas() {
        try {
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deleteTablaCabeceraMontosVendedor();
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deleteTablaMontosVendedor();
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deleteTablaDatosMetricas();
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
        } finally {
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
        }
    }

    public void limpiarPedidosNoPedidosAnteriores(String fechaDevice) {
        try {
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deleteNoPedidosLocalesAnteriores(fechaDevice);
           // ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deletePedidosLocalesAnteriores(fechaDevice);
           List<PreVenta> listCabecera= ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().listarPedidosLocalesAnteriores(fechaDevice,null,sessionUsuario.getUsuario());
            for (int i = 0; i <listCabecera.size() ; i++) {
                ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deletePedidoLocalById(listCabecera.get(i).getUid());
                ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deleteDetallePedidoLocalById(listCabecera.get(i).getUid());
            }


            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
        } finally {
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
        }
    }

//    public void limpiarPedidosNoPedidos() {
//        try {
//            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
//            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deleteNoPedidosLocales();
//            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deletePedidosLocales();
//            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
//        } finally {
//            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
//        }
//    }

    public void limpiarDatosUsuario() {
        try {
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deleteDatosUsuario();
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
        } finally {
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
        }
    }

    public void limpiarCondiciones() {
        try {
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deleteDatosCondiciones();
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
        } finally {
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
        }
    }

    public void limpiarArticulos() {
        try {
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deleteArticulos();
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deleteSugerido();
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
        } finally {
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
        }
    }

    public void limpiarFocusSugeridos() {
        try {
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deleteSugerido();
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deleteFocus();
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
        } finally {
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
