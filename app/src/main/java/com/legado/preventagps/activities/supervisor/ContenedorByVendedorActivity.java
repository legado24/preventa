package com.legado.preventagps.activities.supervisor;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.IdRes;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;

import com.legado.preventagps.R;
import com.legado.preventagps.dialogs.MyDialogAlerta;
import com.legado.preventagps.dialogs.MyDialogAlertaClienteNuevo;
import com.legado.preventagps.dialogs.MyDialogAlertaMayorista;
import com.legado.preventagps.dialogs.MyDialogAlertaNoVencido;
import com.legado.preventagps.dialogs.MyDialogInfo;
import com.legado.preventagps.enums.CANAL;
import com.legado.preventagps.enums.STATUSCLIENTE;
import com.legado.preventagps.fragments.CobranzaFragment;
import com.legado.preventagps.fragments.PreventaFragment;
import com.legado.preventagps.fragments.supervisor.DatosClienteByVendedorFragment;
import com.legado.preventagps.fragments.supervisor.PreventaByVendedorFragment;
import com.legado.preventagps.modelo.PaqueteUsuario;
import com.legado.preventagps.util.SessionUsuario;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class ContenedorByVendedorActivity extends BaseActivityByVendedor {

    private static final String TAG = "ContenedorActivity";
    private SessionUsuario sessionUsuario;
    private  static  ContenedorActivityListener contenedorActivityListenerThis;

    public static  void setContenedorActivityListener(ContenedorActivityListener contenedorActivityListener) {
        contenedorActivityListenerThis = contenedorActivityListener;
    }
    public static MyDialogInfo.DialogInfoListener dialogInfoListener;
    public static void setDialogInfoListener(MyDialogInfo.DialogInfoListener dialogInfoL) {
         dialogInfoListener = dialogInfoL;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenedor);
        final Bundle args = getIntent().getExtras();
        final BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_datos);
        sessionUsuario = new SessionUsuario(this);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                final ActionBar ab = getActionBarToolbar();
                switch (tabId) {
                    case R.id.tab_datos:
                        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
                        ab.setTitle(  Html.fromHtml("<font color='#FFFFFF'>"
                                        + "DATOS DE CLIENTE"
                                        + "</font>"));
                        ab.setDisplayHomeAsUpEnabled(true);
                        sessionUsuario.guardarArrayListSugeridos(null); //limpiar sugeridos
                        if(args.getBoolean("onlycobranza")){

                            bottomBar.getTabWithId(R.id.tab_preventa).setEnabled(false);
                            bottomBar.getTabWithId(R.id.tab_cobranza).setEnabled(true);
                        }else{
                            CardView menu1 = (CardView) findViewById(R.id.menu1);
                            menu1.setCardBackgroundColor(Color.parseColor("#FF9800"));
                            bottomBar.getTabWithId(R.id.tab_preventa).setEnabled(true);
                            bottomBar.getTabWithId(R.id.tab_cobranza).setEnabled(false);
                        }
                        DatosClienteByVendedorFragment datosClienteFragment = DatosClienteByVendedorFragment.newInstance(bottomBar);
                        datosClienteFragment.setArguments(args);
                        getSupportFragmentManager().beginTransaction().replace(R.id.contenedorFragment, datosClienteFragment).
                                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                        break;
                    case R.id.tab_preventa:
                        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
                        ab.setTitle(Html.fromHtml("<font color='#FFFFFF'>"
                                        + "REGISTRAR PREVENTA"
                                        + "</font>"));
                        ab.setDisplayHomeAsUpEnabled(true);
                         bottomBar.getTabWithId(R.id.tab_preventa).setEnabled(false);
                        PaqueteUsuario paqueteUsuario=sessionUsuario.getPaqueteUsuario();
                        if(sessionUsuario.getArrayListSugeridos()!=null){
                            args.putParcelableArrayList("listaSugeridos",sessionUsuario.getArrayListSugeridos());//pasar los sugeridos
                        }

                        if(getIntent().getExtras().getString("statusCliente").equals(STATUSCLIENTE.PENDIENTE.getCod())){
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            MyDialogAlertaClienteNuevo newFragment = MyDialogAlertaClienteNuevo.newInstance(bottomBar, ab);
                            newFragment.setDialogInfoListener(dialogInfoListener);
                            newFragment.setArguments(args);
                            newFragment.show(ft, "dialog");
                        }else{
                            if(!paqueteUsuario.getCodCanal().equals(CANAL.MAYORISTA.getCodCanal())){
                                if (sessionUsuario.getDeudaVencida() != null) {
                                    Double deudaVencida = new Double(sessionUsuario.getDeudaVencida().equals("") ? "0" : sessionUsuario.getDeudaVencida());
                                    if (deudaVencida > 0) {
                                        args.putBoolean("deudavencida", true);
                                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                        MyDialogAlerta newFragment = MyDialogAlerta.newInstance(bottomBar, ab);
                                        newFragment.setDialogInfoListener(dialogInfoListener);
                                        newFragment.setArguments(args);
                                        newFragment.show(ft, "dialog");
                                    } else {
                                        args.putBoolean("deudavencida", false);
                                        PreventaByVendedorFragment preventaFragment = new PreventaByVendedorFragment();
                                        preventaFragment.setDialogInfoListener(dialogInfoListener);
                                        preventaFragment.setArguments(args);
                                        getSupportFragmentManager().beginTransaction().replace(R.id.contenedorFragment, preventaFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                                        if(sessionUsuario.getDeudaNoVencida() != null){
                                            Double deudaNoVencida = new Double(sessionUsuario.getDeudaNoVencida().equals("") ? "0" : sessionUsuario.getDeudaNoVencida());
                                            if(deudaNoVencida>0){
                                                args.putBoolean("deudanovencida", true);
                                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                                MyDialogAlertaNoVencido newFragment = MyDialogAlertaNoVencido.newInstance(bottomBar, ab);
                                                newFragment.setDialogInfoListener(dialogInfoListener);
                                                newFragment.setArguments(args);
                                                newFragment.show(ft, "dialog");
                                            }
                                        }
                                    }
                                } else {
                                    PreventaFragment preventaFragment = new PreventaFragment();
                                    preventaFragment.setArguments(args);
                                    getSupportFragmentManager().beginTransaction().replace(R.id.contenedorFragment, preventaFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                                }

                            }else{

                                if (sessionUsuario.getDeudaVencida() != null) {
                                    Double deudaVencida = new Double(sessionUsuario.getDeudaVencida().equals("") ? "0" : sessionUsuario.getDeudaVencida());
                                    if (deudaVencida > 0) {
                                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                        MyDialogAlertaMayorista newFragment = MyDialogAlertaMayorista.newInstance(bottomBar, ab);
                                        newFragment.setDialogInfoListener(dialogInfoListener);
                                        newFragment.setArguments(args);
                                        newFragment.show(ft, "dialog");
                                    } else {
                                        PreventaFragment preventaFragment = new PreventaFragment();
                                        preventaFragment.setDialogInfoListener(dialogInfoListener);
                                        preventaFragment.setArguments(args);
                                        getSupportFragmentManager().beginTransaction().replace(R.id.contenedorFragment, preventaFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                                        if(sessionUsuario.getDeudaNoVencida() != null){
                                            Double deudaNoVencida = new Double(sessionUsuario.getDeudaNoVencida().equals("") ? "0" : sessionUsuario.getDeudaNoVencida());
                                            if(deudaNoVencida>0){
                                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                                MyDialogAlertaMayorista newFragment = MyDialogAlertaMayorista.newInstance(bottomBar, ab);
                                                newFragment.setDialogInfoListener(dialogInfoListener);
                                                newFragment.setArguments(args);
                                                newFragment.show(ft, "dialog");
                                            }
                                        }
                                    }
                                }else{
                                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                    MyDialogAlertaMayorista newFragment = MyDialogAlertaMayorista.newInstance(bottomBar, ab);
                                    newFragment.setDialogInfoListener(dialogInfoListener);
                                    newFragment.setArguments(args);
                                    newFragment.show(ft, "dialog");
                                }
                            }

                        }


                        break;
                    case R.id.tab_cobranza:
                        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
                        ab.setTitle(
                                Html.fromHtml("<font color='#FFFFFF'>"
                                        + "REALIZAR COBRANZA"
                                        + "</font>"));
                        ab.setDisplayHomeAsUpEnabled(true);
                        CobranzaFragment cobranzaFragment=new CobranzaFragment();
                        cobranzaFragment.setArguments(args);
                        getSupportFragmentManager().beginTransaction().replace(R.id.contenedorFragment,cobranzaFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

                     break;
                }

            }
        });


    }

    @Override
    public boolean providesActivityToolbar() {
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

    @Override
    protected void goToNavDrawerItem(int item) {
        switch (item) {
            case R.id.menuByVendedor0:
                Intent intent0 = new Intent(this, InicioByVendedorActivity.class);
                startActivity(intent0);
                //finish();
                break;
            case R.id.menuByVendedor1:
                Intent intent1 = new Intent(this, ClienteByVendedorActivity.class);
                startActivity(intent1);
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
    public void onBackPressed() {
       // moveTaskToBack(true);


         super.onBackPressed();
    }



    @Override
    protected void onResume() {
        Log.d(TAG, "volvi");
        if(contenedorActivityListenerThis!=null){
            contenedorActivityListenerThis.dismissDialog();
        }

        super.onRestart();
    }



    @Override
    protected void onRestart(){

        super.onRestart();
    }

    public interface ContenedorActivityListener {
        void dismissDialog();
    }
}
