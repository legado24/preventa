package com.legado.preventagps.activities.vendedor;

import android.content.Intent;
import android.graphics.Color;

import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBar;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.legado.preventagps.R;
import com.legado.preventagps.adapter.vendedor.DocumentoDeudaCobranzaRecyclerAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.dialogs.MyDialogErrorCobranza;
import com.legado.preventagps.dialogs.MyDialogProgress;
import com.legado.preventagps.dialogs.MyDialogRegistrarOperacion;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Cobranza;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.SimpleDividerItemDecorator;
import com.legado.preventagps.util.UtilAndroid;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public  class VoucherCobranzaActivity extends BaseActivity {
    SessionUsuario sessionUsuario;

    @BindView(R.id.reciclador)
    RecyclerView reciclador;
    @BindView(R.id.list_empty)
    TextView txtEmpty;

    @BindView(R.id.txtCobranzaDiaria)
    TextView txtCobranzaDiaria;

    @BindView(R.id.layoutTotal)
    LinearLayout layoutTotal;

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;
    RecyclerView.LayoutManager lManager;
    private int year, month, day;
    MyDialogProgress progressConsulta;

    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposito_cobranza);
        ButterKnife.bind(this);
        setupToolbar();
        sessionUsuario = new SessionUsuario(this);
        CardView menu2= (CardView) findViewById(R.id.menu3);
        menu2.setCardBackgroundColor(Color.parseColor("#FF9800"));

        inicializarFechaHoy();
        reciclador.setVisibility(View.VISIBLE);
         reciclador.setHasFixedSize(true);
        lManager = new LinearLayoutManager(this);
        cargarCobranzaByUsuario(sessionUsuario.getUsuario(), UtilAndroid.fechaDevice("dd-MM-YYYY"));
        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        cargarCobranzaByUsuario(sessionUsuario.getUsuario(),UtilAndroid.fechaDevice("dd-MM-YYYY"));
                        swiperefresh.setRefreshing(false);
                    }
                });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                 MyDialogRegistrarOperacion newFragment =MyDialogRegistrarOperacion.newInstance(detallePedidoRecyclerAdapter);
                Bundle args=new Bundle();
                args.putString("montoCobrado",txtCobranzaDiaria.getText().toString());
                newFragment.setArguments(args);
                newFragment.show(ft, "dialog");
            }
        });

    }
    DocumentoDeudaCobranzaRecyclerAdapter detallePedidoRecyclerAdapter;
    public void cargarCobranzaByUsuario(final String usuario, final String fecha) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        progressConsulta= new MyDialogProgress();
        progressConsulta.show(ft, "dialog");
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario",usuario);
        dataConsulta.put("fecha", fecha);
        Call<JsonRespuesta<Cobranza>> call =   ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getCobranzaService().consultaCobranza(dataConsulta);
        call.enqueue(new Callback<JsonRespuesta<Cobranza>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<Cobranza>> call, Response<JsonRespuesta<Cobranza>> response) {

                if(response.body()!=null){
                    Cobranza cobranza = response.body().getItem();
                    reciclador.setHasFixedSize(true);
                    reciclador.setLayoutManager(lManager);
                    reciclador.setNestedScrollingEnabled(false);
                    detallePedidoRecyclerAdapter= new DocumentoDeudaCobranzaRecyclerAdapter(cobranza.getListaDoc(), getApplicationContext(),usuario,fecha,swiperefresh,txtCobranzaDiaria, VoucherCobranzaActivity.this);
                    reciclador.setAdapter(detallePedidoRecyclerAdapter);
                    reciclador.addItemDecoration(new SimpleDividerItemDecorator(getApplicationContext()));
                    txtCobranzaDiaria.setText(detallePedidoRecyclerAdapter.montoTotalCobrado().toString());
                    if(cobranza.getListaDoc().size()==0){
                        fab.setEnabled(false);
                    }else{
                        fab.setEnabled(true);
                    }
                    progressConsulta.dismiss();
                }else{
                    new SweetAlertDialog(VoucherCobranzaActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.txtMensajeServidorCaido))
                            .show();
                    progressConsulta.dismiss();
                }



            }

            @Override
            public void onFailure(Call<JsonRespuesta<Cobranza>> call, Throwable t) {
              /*  FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                MyDialogErrorCobranza newFragment = MyDialogErrorCobranza.newInstance();
                newFragment.show(ft, "dialog");*/
                new SweetAlertDialog(VoucherCobranzaActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.txtMensajeConexion))
                        .show();
                fab.setEnabled(false);
                progressConsulta.dismiss();

            }
        });



    }

//    public Dialog onCreateDialog(int id) {
//        // TODO Auto-generated method stub
//        if (id == 999) {
//            return new DatePickerDialog(this, R.style.DialogTheme
//                    , new DatePickerDialog.OnDateSetListener() {
//                @Override
//                public void onDateSet(DatePicker datePicker, int yyyy, int mm, int dd) {
//                    //txtFecha.setText(getFormatoFecha(yyyy, mm, dd));
//                    cargarCobranzaByUsuario(sessionUsuario.getUsuario(),UtilAndroid.fechaDevice("YYYY-MM-DD"));
//                }
//            }, year, month, day);
//        }
//        return null;
//    }

    public String getFormatoFecha(int yyyy, int mm, int dd) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = dateFormat.parse(dd + "-" + (mm + 1) + "-" + yyyy);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outDate = dateFormat.format(date);

        return outDate;

    }

    public void inicializarFechaHoy() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        //txtFecha.setText(getFormatoFecha(year, month, day));
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
                        + "VOUCHER COBRANZA"
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
        return true;
    }


//    public void onBackPressed() {
//        Intent intent0 = new Intent(this, ClienteActivity.class);
//        startActivity(intent0);
//        finish();
//        super.onBackPressed();
//    }
@Override
public void onBackPressed() {
    Intent intent4 = new Intent(this, MenuCobranzaActivity.class);
    intent4.putExtra("origen","voucherDeCobranza");
    startActivity(intent4);
    finish();

}


}
