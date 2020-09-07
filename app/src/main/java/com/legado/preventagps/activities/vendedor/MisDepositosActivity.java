package com.legado.preventagps.activities.vendedor;

import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.ActionBar;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import com.legado.preventagps.R;
import com.legado.preventagps.fragments.ConsultaPagosFragment;
import com.legado.preventagps.util.SessionUsuario;

public  class MisDepositosActivity extends BaseActivity {
SessionUsuario sessionUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_pagos);
        sessionUsuario=new SessionUsuario(this);
         setupToolbar();
        CardView menu5= (CardView) findViewById(R.id.menu3);
        menu5.setCardBackgroundColor(Color.parseColor("#FF9800"));
        ConsultaPagosFragment fragment = (ConsultaPagosFragment) getSupportFragmentManager().findFragmentById(R.id.content_consulta_pagos);
        if (fragment == null) {
            fragment = ConsultaPagosFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_consulta_pagos, fragment)
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
        ab.setTitle(
                Html.fromHtml("<font color='#FFFFFF'>"
                        + "CONSULTA DEPOSITOS"
                        + "</font>"));
        ab.setDisplayHomeAsUpEnabled(true);

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
//            case R.id.mapa:
//                Intent intent3 = new Intent(this, MapaClientesActivity.class);
//                startActivity(intent3);
//                return true;

            case R.id.jornada:
                 limpiarTablaClientes();
                Intent intent4 = new Intent(this, ClienteActivity.class);
                startActivity(intent4);
                finish();


                return true;


        }
        return super.onOptionsItemSelected(item);
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
    public void onBackPressed() {
        Intent intent4 = new Intent(this, MenuCobranzaActivity.class);
        intent4.putExtra("origen","misDepositos");
        startActivity(intent4);
        finish();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cliente, menu);
        return true;
    }

}
