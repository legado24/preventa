package com.legado.preventagps.activities.vendedor;

import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.legado.preventagps.R;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.util.SessionUsuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public  class DatosClienteActivity extends BaseActivity {
    @BindView(R.id.txtDescripcion)
    TextView txtDescripcion;

    @BindView(R.id.txtDireccion)
    TextView txtDireccion;

    @BindView(R.id.txtListaPrecios)
    TextView txtListaPrecios;

    @BindView(R.id.txtLimiteCredito)
    TextView txtLimiteCredito;

    @BindView(R.id.txtDeuda)
    TextView txtDeuda;

    @BindView(R.id.txtTipoCliente)
    TextView txtTipoCliente;
    SessionUsuario sessionUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_cliente);
        sessionUsuario=new SessionUsuario(this);
        final LottieAnimationView animationView= (LottieAnimationView) findViewById(R.id.logonivel);
        animationView.loop(true);
        animationView.playAnimation();
        ButterKnife.bind(this);
        setupToolbar();
        String  codCliente = getIntent().getStringExtra("codCliente");
       String  descCliente = getIntent().getStringExtra("descCliente");
        String dirCliente = getIntent().getStringExtra("dirCliente");
        String codLista = getIntent().getStringExtra("codListaPrecio");
        String descLista = getIntent().getStringExtra("descListaPrecio");
        String codEmpresa= getIntent().getStringExtra("codEmpresa");
         txtDescripcion.setText(descCliente);
        txtDireccion.setText(dirCliente);
        txtListaPrecios.setText(codLista+"-"+descLista);
        cargarDatosCliente(codCliente,codEmpresa);
        CardView menu2= (CardView) findViewById(R.id.menu2);
        menu2.setCardBackgroundColor(Color.parseColor("#FF9800"));
    }

    public  void cargarDatosCliente(String codCliente,String codEmpresa){
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("codCliente", codCliente);
        dataConsulta.put("codEmpresa", codEmpresa);
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().datosCliente(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if(response.body().getEstado()==1){
                 ArrayList objeto= (ArrayList) response.body().getItem();
                    System.out.println();
                    Log.d("DatosClienteActivity"," CREDITO=>>>"+objeto.get(0));
                    txtLimiteCredito.setText(objeto.get(0).toString());
                    Log.d("DatosClienteActivity"," DEUDA=>>>"+objeto.get(1));
                    txtDeuda.setText(objeto.get(1).toString());
                    txtTipoCliente.setText(objeto.get(3).toString());

                }else{

                }
            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                Log.wtf("as",t.toString());
                Toast.makeText(getApplicationContext(), "NO HAY CONEXION CON EL SERVIDOR", Toast.LENGTH_LONG).show();
            }
        });

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
            case R.id.action_logout:
                logout(sessionUsuario);
                return true;
            case R.id.jornada:
             limpiarTablaClientes();
                Intent intent4=new Intent(this, ClienteActivity.class);
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
//                finish();
                break;
            case R.id.menu1:
                Intent intent1 = new Intent(this, MenuPreventaActivity.class);
                startActivity(intent1);
//                finish();
                break;
            case R.id.menu2:
                Intent intent2 = new Intent(this, MenuCobranzaActivity.class);
                startActivity(intent2);
//                finish();
                break;
            case R.id.menu3:
                Intent intent3 = new Intent(this, ContenedorAltaActivity.class);
                startActivity(intent3);
//                finish();
                break;
            case R.id.menu4:
                Intent intent4 = new Intent(this, MenuBandejasActivity.class);
                startActivity(intent4);
//                  finish();
                break;



        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cliente, menu);
        return true;
    }
}
