package com.legado.preventagps.activities.vendedor;

import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.legado.preventagps.R;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.UtilAndroid;

import butterknife.BindView;
import butterknife.ButterKnife;

public  class MenuPreventaActivity extends BaseActivity {
    SessionUsuario sessionUsuario;
    @BindView(R.id.imagenAlerta)
    ImageView imagenAlerta;
    @BindView(R.id.txtAlerta)
    TextView txtAlerta;

    String fechaDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos2);
        ButterKnife.bind(this);

        if(getIntent().getExtras()!=null){

            if(getIntent().getExtras().getString("origen").equals("pedidoEnRuta")){
                CardView menuCobranza0 = (CardView) findViewById(R.id.menuPedidos0);
                menuCobranza0.setCardBackgroundColor(Color.parseColor("#FF9800"));
            }else if(getIntent().getExtras().getString("origen").equals("consultaPedidos")){
                CardView menuCobranza1 = (CardView) findViewById(R.id.menuPedidos1);
                menuCobranza1.setCardBackgroundColor(Color.parseColor("#FF9800"));
            }else if(getIntent().getExtras().getString("origen").equals("sincronizarPedidos")){
                CardView menuCobranza2 = (CardView) findViewById(R.id.menuPedidos2);
                menuCobranza2.setCardBackgroundColor(Color.parseColor("#FF9800"));
            }else if(getIntent().getExtras().getString("origen").equals("planificador")){
                CardView menuCobranza3 = (CardView) findViewById(R.id.menuPedidos3);
                menuCobranza3.setCardBackgroundColor(Color.parseColor("#FF9800"));
            }

        }


        CardView menuPedidos0 = (CardView) findViewById(R.id.menuPedidos0);
        menuPedidos0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent0 = new Intent(MenuPreventaActivity.this, ClienteActivity.class);
                startActivity(intent0);
                finish();
            }
        });

        CardView menuPedidos1 = (CardView) findViewById(R.id.menuPedidos1);
        menuPedidos1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent0 = new Intent(MenuPreventaActivity.this, ConsultaPedidosActivity.class);
                startActivity(intent0);
                finish();
            }
        });

        CardView menuPedidos2 = (CardView) findViewById(R.id.menuPedidos2);
        menuPedidos2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent0 = new Intent(MenuPreventaActivity.this, SincronizarActivity.class);
                startActivity(intent0);
                finish();
            }
        });

        CardView menuPedidos3 = (CardView) findViewById(R.id.menuPedidos3);
        menuPedidos3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent0 = new Intent(MenuPreventaActivity.this, PlanificadorActivity.class);
                startActivity(intent0);
                finish();
            }
        });

        sessionUsuario = new SessionUsuario(this);
        fechaDevice= UtilAndroid.fechaDevice("dd-MM-yyyy");
        long cantPendientes= ApiSqlite.getInstance(getApplicationContext()).getOperacionesBaseDatos().getCountNoPedidosPendientes(fechaDevice,sessionUsuario.getPaqueteUsuario().getUsuario())+ApiSqlite.getInstance(getApplicationContext()).getOperacionesBaseDatos().getCountPedidosPendientes(fechaDevice,sessionUsuario.getPaqueteUsuario().getUsuario());

        if(cantPendientes>0){
            txtAlerta.setText(cantPendientes+"");
            imagenAlerta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent0 = new Intent(MenuPreventaActivity.this, SincronizarActivity.class);
                    startActivity(intent0);
                }
            });
            txtAlerta.setVisibility(View.VISIBLE);
            imagenAlerta.setVisibility(View.VISIBLE);
         }else{
            txtAlerta.setVisibility(View.INVISIBLE);
            imagenAlerta.setVisibility(View.INVISIBLE);
        }

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