package com.legado.preventagps.activities.vendedor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.legado.preventagps.R;
import com.legado.preventagps.adapter.vendedor.ClienteCobranzaAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Cliente;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.SimpleDividerItemDecorator;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public  class CobranzaOutActivity extends BaseActivity {

    @BindView(R.id.reciclador)
    RecyclerView recycler;
    private ClienteCobranzaAdapter adapter;
    private RecyclerView.LayoutManager lManager;
    private ClienteActivity.ContenedorListener contenedorListener;

    public void setContenedorListener(ClienteActivity.ContenedorListener contenedorListener) {
        this.contenedorListener = contenedorListener;
    }
    Integer permiteOffline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionUsuario = new SessionUsuario(this);
        setContentView(R.layout.activity_pedido_out);
        ButterKnife.bind(this);
        setupToolbar();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CardView menu2= (CardView) findViewById(R.id.menu3);
        menu2.setCardBackgroundColor(Color.parseColor("#FF9800"));
        permiteOffline = ApiSqlite.getInstance(getApplicationContext()).getOperacionesBaseDatos().getPermiteOffline();
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
                        + "BUSCAR CLIENTES"
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem mSearch = menu.findItem(R.id.action_search);
        final SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("BUSCAR CLIENTE");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mSearchView.getQuery().length() >= 4) {
                    consultarClientes(mSearchView.getQuery().toString());
                }

                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void consultarClientes(final String filtro) {

        final String upperFiltro = filtro.toUpperCase();
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario", sessionUsuario.getUsuario());
        dataConsulta.put("filtro", upperFiltro);
        Call<JsonRespuesta<Cliente>> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().clientesOutRuta(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta<Cliente>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<Cliente>> call, Response<JsonRespuesta<Cliente>> response) {
                if(response.body()!=null){
                    if (response.body().getEstado() == 1) {
                        adapter = new ClienteCobranzaAdapter(response.body().getData(), getApplicationContext());
                        recycler.setHasFixedSize(true);
                        lManager = new LinearLayoutManager(CobranzaOutActivity.this);
                        recycler.setLayoutManager(lManager);
                        recycler.setAdapter(adapter);
                        recycler.addItemDecoration(new SimpleDividerItemDecorator(CobranzaOutActivity.this));
                    }
                }else{
                    new SweetAlertDialog(CobranzaOutActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.txtMensajeServidorCaido))
                            .show();

                }

            }

            @Override
            public void onFailure(Call<JsonRespuesta<Cliente>> call, Throwable t) {
                new SweetAlertDialog(CobranzaOutActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.txtMensajeConexion))
                        .show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent4 = new Intent(this, MenuCobranzaActivity.class);
        intent4.putExtra("origen","cobranzaFueraDeRuta");
        startActivity(intent4);
        finish();


    }


}
