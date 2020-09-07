package com.legado.preventagps.activities.supervisor;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.legado.preventagps.R;
import com.legado.preventagps.activities.vendedor.ClienteActivity;
import com.legado.preventagps.activities.vendedor.InicioActivity;
import com.legado.preventagps.adapter.supervisor.VendedorSupAdapter;
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

public class InicioSupervisorActivity extends BaseActivitySupervisor {

    @BindView(R.id.reciclador)
    RecyclerView recycler;
    private VendedorSupAdapter adapter;
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
        setContentView(R.layout.activity_inicio_supervisor);
        ButterKnife.bind(this);
        setupToolbar();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        consultarVendedores("");

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
                        + "BUSCAR VENDEDORES"
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



        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem mSearch = menu.findItem(R.id.action_search);
        final SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("BUSCAR VENDEDOR");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //if (mSearchView.getQuery().length() >= 4) {
                    consultarVendedores(mSearchView.getQuery().toString());
               // }

                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void consultarVendedores(final String filtro) {
        final String upperFiltro = filtro.toUpperCase();
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario", sessionUsuario.getUsuario());
        dataConsulta.put("filtro", upperFiltro);
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getVendedorService().vendedoresBySupervisor(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if(response.isSuccessful()){
                    if (response.body().getEstado() == 1) {
                        ArrayList data= (ArrayList) response.body().getData();
                        adapter = new VendedorSupAdapter(data, getApplicationContext());
                        recycler.setHasFixedSize(true);
                        lManager = new LinearLayoutManager(InicioSupervisorActivity.this);
                        recycler.setLayoutManager(lManager);
                        recycler.setAdapter(adapter);
                     }
                }else{
                    Toast.makeText(InicioSupervisorActivity.this,R.string.txtMensajeServidorCaido, Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                Toast.makeText(InicioSupervisorActivity.this,R.string.txtMensajeConexion, Toast.LENGTH_LONG).show();

            }
        });
    }
    @Override
    public void onBackPressed() {
//        Intent intent4 = new Intent(this, MenuCobranzaActivity.class);
//        intent4.putExtra("origen","cobranzaFueraDeRuta");
//        startActivity(intent4);
//        finish();


    }


}
