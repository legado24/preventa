package com.legado.preventagps.activities.vendedor;

import android.content.Intent;

import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import com.legado.preventagps.R;
import com.legado.preventagps.fragments.PlanificadorFragment;
import com.legado.preventagps.util.SessionUsuario;

public  class PlanificadorActivity extends BaseActivity{
    SessionUsuario sessionUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planificador);
        sessionUsuario=new SessionUsuario(this);
        setupToolbar();

        PlanificadorFragment fragment = (PlanificadorFragment) getSupportFragmentManager().findFragmentById(R.id.content_planificador);
        if (fragment == null) {
            fragment = PlanificadorFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_planificador, fragment).addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);

        ab.setTitle(
                Html.fromHtml("<font color='#FFFFFF'>"
                        + "PLANIFICADOR"
                        + "</font>"));
        ab.setDisplayHomeAsUpEnabled(true);

    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_logout, menu);
//        return true;
//    }
    @Override
    protected void goToNavDrawerItem(int item) {
        switch (item) {

            case R.id.menu0:
                Intent intent0 = new Intent(this, InicioActivity.class);
                startActivity(intent0);
                //finish();
                break;
            case R.id.menu1:
                Intent intent1 = new Intent(this, MenuPreventaActivity.class);
                startActivity(intent1);
                // finish();
                break;
            case R.id.menu2:
                Intent intent2 = new Intent(this, ContenedorAltaActivity.class);
                startActivity(intent2);
                //finish();
                break;
            case R.id.menu3:
                Intent intent3 = new Intent(this, MenuCobranzaActivity.class);
                startActivity(intent3);
                // finish();
                break;
            case R.id.menu4:
                Intent intent4 = new Intent(this, MenuBandejasActivity.class);
                startActivity(intent4);
                // finish();
                break;

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                openDrawer();
                return true;
            case R.id.action_logout:
                logout(sessionUsuario);
                return true;



        }
        return super.onOptionsItemSelected(item);
    }

    boolean exit=false;
    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 1) {
            if (exit) {
                finish(); // finish activity
            } else {

                exit = true;
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                Intent intent4 = new Intent(PlanificadorActivity.this, MenuPreventaActivity.class);
                intent4.putExtra("origen","planificador");
                startActivity(intent4);
                finish();
                exit = false;
//                    }
//                }, 2 * 1000);

            }
        } else {
            getSupportFragmentManager().popBackStackImmediate();
            int c = getSupportFragmentManager().getBackStackEntryCount();
        }
        //        Intent intent4 = new Intent(this, MenuPreventaActivity.class);
//        intent4.putExtra("origen","consultaPedidos");
//        startActivity(intent4);
//        finish();
    }

}
