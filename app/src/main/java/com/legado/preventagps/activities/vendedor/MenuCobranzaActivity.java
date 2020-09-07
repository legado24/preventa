package com.legado.preventagps.activities.vendedor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;

import android.view.MenuItem;
import android.view.View;

import com.legado.preventagps.R;
import com.legado.preventagps.util.SessionUsuario;

public  class MenuCobranzaActivity extends BaseActivity {
    SessionUsuario sessionUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cobranza2);

        if(getIntent().getExtras()!=null){

            if(getIntent().getExtras().getString("origen").equals("cobranzaEnRuta")){
                CardView menuCobranza0 = (CardView) findViewById(R.id.menuCobranza0);
                menuCobranza0.setCardBackgroundColor(Color.parseColor("#FF9800"));
            }else if(getIntent().getExtras().getString("origen").equals("cobranzaFueraDeRuta")){
                CardView menuCobranza1 = (CardView) findViewById(R.id.menuCobranza1);
                menuCobranza1.setCardBackgroundColor(Color.parseColor("#FF9800"));
            }else if(getIntent().getExtras().getString("origen").equals("voucherDeCobranza")){
                CardView menuCobranza2 = (CardView) findViewById(R.id.menuCobranza2);
                menuCobranza2.setCardBackgroundColor(Color.parseColor("#FF9800"));
            }else if(getIntent().getExtras().getString("origen").equals("misDepositos")){
                CardView menuCobranza3 = (CardView) findViewById(R.id.menuCobranza3);
                menuCobranza3.setCardBackgroundColor(Color.parseColor("#FF9800"));
            }

        }

        CardView menuCobranza0 = (CardView) findViewById(R.id.menuCobranza0);
        menuCobranza0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent0 = new Intent(MenuCobranzaActivity.this, PlanillaCobranzaActivity.class);
                startActivity(intent0);
                finish();
            }
        });

        CardView menuCobranza1 = (CardView) findViewById(R.id.menuCobranza1);
        menuCobranza1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent0 = new Intent(MenuCobranzaActivity.this, CobranzaOutActivity.class);
                startActivity(intent0);
                finish();
            }
        });

        CardView menuCobranza2 = (CardView) findViewById(R.id.menuCobranza2);
        menuCobranza2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent0 = new Intent(MenuCobranzaActivity.this, VoucherCobranzaActivity.class);
                startActivity(intent0);
                finish();
            }
        });

        CardView menuCobranza3 = (CardView) findViewById(R.id.menuCobranza3);
        menuCobranza3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent0 = new Intent(MenuCobranzaActivity.this, MisDepositosActivity.class);
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