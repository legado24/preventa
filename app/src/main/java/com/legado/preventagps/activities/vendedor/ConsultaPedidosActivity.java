package com.legado.preventagps.activities.vendedor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;

import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import com.legado.preventagps.R;
import com.legado.preventagps.fragments.ConsultaPedidoFragment;
import com.legado.preventagps.util.SessionUsuario;

public  class ConsultaPedidosActivity extends BaseActivity {
SessionUsuario sessionUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_pedidos);
        sessionUsuario=new SessionUsuario(this);
        setupToolbar();
        CardView menu2= (CardView) findViewById(R.id.menu1);
        menu2.setCardBackgroundColor(Color.parseColor("#FF9800"));
        ConsultaPedidoFragment fragment = (ConsultaPedidoFragment) getSupportFragmentManager().findFragmentById(R.id.content_consulta_pedidos);
        if (fragment == null) {
            fragment = ConsultaPedidoFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_consulta_pedidos, fragment).addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);

//        ab.setTitle(
//                Html.fromHtml("<font color='#FFFFFF'>"
//                        + "CONSULTA PEDIDOS"
//                        + "</font>"));
        ab.setDisplayHomeAsUpEnabled(true);

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
                                Intent intent4 = new Intent(ConsultaPedidosActivity.this, MenuPreventaActivity.class);
                                intent4.putExtra("origen","consultaPedidos");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
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
}
