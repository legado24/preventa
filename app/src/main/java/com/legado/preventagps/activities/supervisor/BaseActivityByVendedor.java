package com.legado.preventagps.activities.supervisor;

import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.navigation.NavigationView;
import com.legado.preventagps.R;
import com.legado.preventagps.activities.vendedor.LoginActivity;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.util.SessionUsuario;

/**
 * Created by __Adrian__ on 20/03/2019.
 */

public abstract class BaseActivityByVendedor extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar actionBarToolbar;
    protected static final int NAV_DRAWER_ITEM_INVALID = -1;
    SessionUsuario sessionUsuario;
    private BroadcastReceiver br;
    public static final String CHANNEL_ID = "exampleServiceChannel";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionUsuario = new SessionUsuario(this);
       // createNotificationChannel();
//        br = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                PaqueteMensaje paqueteMensaje=new PaqueteMensaje();
//                Notification not=new Notification(intent.getStringExtra("title"),intent.getStringExtra("body"),intent.getStringExtra("mensaje"));
//                paqueteMensaje.setNotification(not);
//
//                Alerter.create(BaseActivityByVendedor.this)
//                        .setTitle(not.getTitle()).setBackgroundColor(R.color.amber900)
//                        .setText(not.getBody())
//                        .show();
//
//
//            }
//        };

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
    }

    private void setupNavDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_by_vendedor);
        if (drawerLayout == null) {
            return;
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_by_vendedor);
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
    }

    protected void closeDrawer() {
        if (drawerLayout == null)
            return;
        drawerLayout.closeDrawer(GravityCompat.START);
    }

private void setupCardSelectListener() {
    TextView txtDashboard= (TextView)findViewById(R.id.txtDashboard);
  //  txtDashboard.setText(getIntent().getStringExtra("codVendedor"));

    final CardView menuByVendedor0 = (CardView) findViewById(R.id.menuByVendedor0);
    final CardView menuByVendedor1 = (CardView) findViewById(R.id.menuByVendedor1);


//    final TextView linkManualDash=findViewById(R.id.linkManualDash);
//    linkManualDash.setMovementMethod(LinkMovementMethod.getInstance());


    menuByVendedor0.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onNavigationItemClicked(menuByVendedor0.getId());
        }
    });
    menuByVendedor1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onNavigationItemClicked(menuByVendedor1.getId());
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
        sessionUsuario.borrarDatosSesion();
        limpiarTablaClientes();
        limpiarMetricas();
        limpiarDatosUsuario();
        limpiarCondiciones();
        limpiarArticulos();
        sessionUsuario.guardarIsTodoSincronizado(false);
        finish();
        startActivity(new Intent(this, LoginActivity.class));
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

    public  void limpiarTablaClientes(){
        try {
             ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();

            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deleteTablaClientes();

            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
        } finally {
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
        }
    }

    public  void limpiarMetricas(){
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

    public  void limpiarDatosUsuario(){
        try {
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();

            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deleteDatosUsuario();
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
        } finally {
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
        }
    }

    public  void limpiarCondiciones(){
        try {
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();

            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deleteDatosCondiciones();
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
        } finally {
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
        }
    }

    public  void limpiarArticulos(){
        try {
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();

            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deleteArticulos();
            ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().deleteSugerido();
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
