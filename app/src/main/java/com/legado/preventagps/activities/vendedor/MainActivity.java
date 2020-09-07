package com.legado.preventagps.activities.vendedor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.legado.preventagps.R;

public  class MainActivity extends BaseActivity {
//    BottomNavigationViewEx bnve = (BottomNavigationViewEx) findViewById(R.id.bnve);
//    private ActivityBadgeViewBinding bind;
    private BroadcastReceiver br;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        final LottieAnimationView animationView= (LottieAnimationView) findViewById(R.id.logonivel);
//        animationView.loop(true);
//        animationView.playAnimation();
       // String  codCliente = getIntent().getStringExtra("key");
       // Log.wtf("EN MAIN ACTIVITY",codCliente);
       setupToolbar();
//        addBadgeAt(2, 1);
      //  Toast.makeText(this,codCliente,Toast.LENGTH_LONG).show();

        br=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Toast.makeText(MainActivity.this, intent.getStringExtra("body"), Toast.LENGTH_SHORT).show();

            }
        };

   }

//    private Badge addBadgeAt(int position, int number) {
//        // add badge
//        return new QBadgeView(this)
//                .setBadgeNumber(number)
//                .setGravityOffset(12, 2, true)
//                .bindTarget(bind.bnve.getBottomNavigationItemView(position))
//                .setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
//                    @Override
//                    public void onDragStateChanged(int dragState, Badge badge, View targetView) {
//                        if (Badge.OnDragStateChangedListener.STATE_SUCCEED == dragState)
//                            Toast.makeText(BadgeViewActivity.this, R.string.tips_badge_removed, Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
    @Override
    public boolean providesActivityToolbar() {
        return true;
    }


    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);

        ab.setTitle(
                Html.fromHtml("<font color='#FFFFFF'>"
                        + "DATOS DEL CLIENTEE"
                        + "</font>"));
        ab.setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                openDrawer();

                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_inicio, menu);
        return true;
    }
    @Override
    protected void goToNavDrawerItem(int item) {
//        switch (item) {
//            case R.id.menuPreferencias:
//                startActivity(new Intent(this, MapaClientesActivity.class));
//                finish();
//
//                break;
//            case R.id.logout:
//                startActivity(new Intent(this, MapaClientesActivity.class));
//                finish();
////                logout();
//                break;
//
//
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(br);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(br,new IntentFilter("MENSAJE"));

    }
}
