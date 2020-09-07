package com.legado.preventagps.activities.vendedor;

import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.legado.preventagps.R;
import com.legado.preventagps.util.SessionUsuario;

public  class MenuBandejasActivity extends BaseActivity {
    SessionUsuario sessionUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresosy_bonif2);

        if(getIntent().getExtras()!=null){

            if(getIntent().getExtras().getString("origen").equals("ingresoAlmacen")){
                CardView menuCobranza0 = (CardView) findViewById(R.id.menuIngresoyBonif0);
                menuCobranza0.setCardBackgroundColor(Color.parseColor("#FF9800"));
            }else if(getIntent().getExtras().getString("origen").equals("bonificacionItem")){
                CardView menuCobranza1 = (CardView) findViewById(R.id.menuIngresoyBonif1);
                menuCobranza1.setCardBackgroundColor(Color.parseColor("#FF9800"));
            }else if(getIntent().getExtras().getString("origen").equals("bonificacionPaquete")){
                CardView menuCobranza2 = (CardView) findViewById(R.id.menuIngresoyBonif2);
                menuCobranza2.setCardBackgroundColor(Color.parseColor("#FF9800"));
            }else if(getIntent().getExtras().getString("origen").equals("repartoventa")){
                CardView menuCobranza2 = (CardView) findViewById(R.id.menuIngresoyBonif3);
                menuCobranza2.setCardBackgroundColor(Color.parseColor("#FF9800"));
            }

        }


        CardView menuIngresoyBonif0 = (CardView) findViewById(R.id.menuIngresoyBonif0);
        menuIngresoyBonif0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent0 = new Intent(MenuBandejasActivity.this, NewIngresosAlmacenActivity.class);
                startActivity(intent0);
                finish();
            }
        });

        CardView menuIngresoyBonif1 = (CardView) findViewById(R.id.menuIngresoyBonif1);
        menuIngresoyBonif1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent0 = new Intent(MenuBandejasActivity.this, BonificacionItemActivity.class);
                startActivity(intent0);
                finish();
            }
        });

        CardView menuIngresoyBonif2 = (CardView) findViewById(R.id.menuIngresoyBonif2);
        menuIngresoyBonif2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent0 = new Intent(MenuBandejasActivity.this, BonificacionPaqueteActivity.class);
                startActivity(intent0);
                finish();
            }
        });

        CardView menuIngresoyBonif3 = (CardView) findViewById(R.id.menuIngresoyBonif3);
        menuIngresoyBonif3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent0 = new Intent(MenuBandejasActivity.this, RepartoVentaActivity.class);
                startActivity(intent0);
                finish();
            }
        });

    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

//    @Override
//    public void onBackPressed() {
//
//        moveTaskToBack(true);
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

            case R.id.jornada:
                limpiarTablaClientes();
                Intent intent4 = new Intent(this, ClienteActivity.class);
                startActivity(intent4);
                finish();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }
}
