package com.legado.preventagps.activities.supervisor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.legado.preventagps.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuByVendedorActivity extends AppCompatActivity {

    @BindView(R.id.txtDashboard)
    TextView txtDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_by_vendedor);
        ButterKnife.bind(this);
        txtDashboard.setText(getIntent().getStringExtra("codVendedor")+"-"+getIntent().getStringExtra("descVendedor"));

        if(getIntent().getExtras().getString("origen")!=null){
            if(getIntent().getExtras().getString("origen").equals("InicioByVendedorActiviy")){
                CardView menuByVendedor0 = (CardView) findViewById(R.id.menuVendedor0);
                menuByVendedor0.setCardBackgroundColor(Color.parseColor("#FF9800"));
            }else  if(getIntent().getExtras().getString("origen").equals("PedidoInRutaByVendedor")){
                CardView menuByVendedor1 = (CardView) findViewById(R.id.menuVendedor1);
                menuByVendedor1.setCardBackgroundColor(Color.parseColor("#FF9800"));
            }
        }

        CardView menuInformacionByVendedor = (CardView) findViewById(R.id.menuVendedor0);
        menuInformacionByVendedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent0 = new Intent(MenuByVendedorActivity.this, InicioByVendedorActivity.class);
                intent0.putExtra("usuario",getIntent().getStringExtra("usuario"));
                intent0.putExtra("codVendedor",getIntent().getStringExtra("codVendedor"));
                intent0.putExtra("descVendedor",getIntent().getStringExtra("descVendedor"));
                startActivity(intent0);
                finish();
            }
        });



        CardView menuClienteByVendedor = (CardView) findViewById(R.id.menuVendedor1);
        menuClienteByVendedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MenuByVendedorActivity.this, ClienteByVendedorActivity.class);
                intent1.putExtra("usuario",getIntent().getStringExtra("usuario"));
                intent1.putExtra("codVendedor",getIntent().getStringExtra("codVendedor"));
                intent1.putExtra("descVendedor",getIntent().getStringExtra("descVendedor"));
                startActivity(intent1);
                finish();
            }
        });
    }
}
