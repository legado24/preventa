package com.legado.preventagps.activities.supervisor;

import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
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
import com.legado.preventagps.modelo.Notification;
import com.legado.preventagps.modelo.PaqueteMensaje;
import com.legado.preventagps.util.SessionUsuario;
import com.tapadoo.alerter.Alerter;

/**
 * Created by __Adrian__ on 20/03/2019.
 */

public abstract class BaseActivitySupervisor extends AppCompatActivity {
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
        createNotificationChannel();
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                PaqueteMensaje paqueteMensaje=new PaqueteMensaje();
                Notification not=new Notification(intent.getStringExtra("title"),intent.getStringExtra("body"),intent.getStringExtra("mensaje"));
                paqueteMensaje.setNotification(not);

                Alerter.create(BaseActivitySupervisor.this)
                        .setTitle(not.getTitle()).setBackgroundColor(R.color.amber900)
                        .setText(not.getBody())
                        .show();


            }
        };

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_supervisor);
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
    final CardView menu0 = (CardView) findViewById(R.id.menu0);
    final CardView menu1 = (CardView) findViewById(R.id.menu1);
    final CardView menu2 = (CardView) findViewById(R.id.menu2);
    final CardView menu3 = (CardView) findViewById(R.id.menu3);
    final CardView menu4 = (CardView) findViewById(R.id.menu4);


    final TextView linkManualDash=findViewById(R.id.linkManualDash);
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
        sessionUsuario.borrarDatosSesion();
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
