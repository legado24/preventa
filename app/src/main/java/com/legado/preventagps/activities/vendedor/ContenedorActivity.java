package com.legado.preventagps.activities.vendedor;


import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.IdRes;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.legado.preventagps.R;
import com.legado.preventagps.dialogs.MyDialogAlertaClienteNuevo;
import com.legado.preventagps.dialogs.MyDialogAlertaMayorista;
import com.legado.preventagps.dialogs.MyDialogAlertaNoVencido;
import com.legado.preventagps.dialogs.MyDialogInfo;
import com.legado.preventagps.enums.CANAL;
import com.legado.preventagps.enums.STATUSCLIENTE;
import com.legado.preventagps.fragments.CobranzaFragment;
import com.legado.preventagps.fragments.DatosClienteFragment;
import com.legado.preventagps.dialogs.MyDialogAlerta;
import com.legado.preventagps.fragments.PreventaFragment;
import com.legado.preventagps.modelo.PaqueteUsuario;
import com.legado.preventagps.util.SessionUsuario;
import com.pd.chocobar.ChocoBar;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public  class ContenedorActivity extends BaseActivity {

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
    BottomBar bottomBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenedor);
        final Bundle args = getIntent().getExtras();
        bottomBar= (BottomBar) findViewById(R.id.bottomBar);
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
                        DatosClienteFragment datosClienteFragment = DatosClienteFragment.newInstance(bottomBar);
                        datosClienteFragment.setArguments(args);
                        getSupportFragmentManager().beginTransaction().replace(R.id.contenedorFragment, datosClienteFragment,"DatosClienteFragment").
                                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                        break;
                    case R.id.tab_preventa:


                        if(!sessionUsuario.getIsTodoSincronizado()&&!sessionUsuario.getIsOnlyOnline()){


                             ChocoBar.builder().setBackgroundColor( getResources().getColor(R.color.colorError)).setTextSize(12)
                                    .setTextColor(Color.parseColor("#FFFFFF"))
                                    .setTextTypefaceStyle(Typeface.ITALIC)
                                    .setText("LA DATA NO ESTA COMPLETA , PORFAVOR REGRESAR Y VOLVER A INICIAR LA JORNADA!!")
                                    .setActionText("Cerrar")
                                    .setActionClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            bottomBar.selectTabWithId(R.id.tab_datos);
                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(R.color.amber900))
                                    .setActionTextSize(12)
                                    .setMaxLines(4)
                                    .centerText()
                                    .setActionTextSize(12)
                                    .setActionTextTypefaceStyle(Typeface.BOLD)
                                    .setIcon(R.drawable.ic_error_outline_white_48dp)
                                    .setActivity(ContenedorActivity.this)
                                    .setDuration(ChocoBar.LENGTH_INDEFINITE).build().show();
                        }else {


                                        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
                                        ab.setTitle(Html.fromHtml("<font color='#FFFFFF'>"
                                                + "REGISTRAR PREVENTA"
                                                + "</font>"));
                                        ab.setDisplayHomeAsUpEnabled(true);
                                        bottomBar.getTabWithId(R.id.tab_preventa).setEnabled(false);
                                        PaqueteUsuario paqueteUsuario = sessionUsuario.getPaqueteUsuario();
                                        if (sessionUsuario.getArrayListSugeridos() != null) {
                                            args.putParcelableArrayList("listaSugeridos", sessionUsuario.getArrayListSugeridos());//pasar los sugeridos
                                        }

                                        if (getIntent().getExtras().getString("statusCliente").equals(STATUSCLIENTE.PENDIENTE.getCod())) {
                                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                            MyDialogAlertaClienteNuevo newFragment = MyDialogAlertaClienteNuevo.newInstance(bottomBar, ab);
                                            newFragment.setDialogInfoListener(dialogInfoListener);
                                            newFragment.setArguments(args);
                                            newFragment.show(ft, "dialog");
                                        } else {
                                            if (!paqueteUsuario.getCodCanal().equals(CANAL.MAYORISTA.getCodCanal())) {
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
                                                        PreventaFragment preventaFragment = new PreventaFragment();
                                                        preventaFragment.setDialogInfoListener(dialogInfoListener);
                                                        preventaFragment.setArguments(args);
                                                        getSupportFragmentManager().beginTransaction().replace(R.id.contenedorFragment, preventaFragment, "PreventaFragment").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                                                        if (sessionUsuario.getDeudaNoVencida() != null) {
                                                            Double deudaNoVencida = new Double(sessionUsuario.getDeudaNoVencida().equals("") ? "0" : sessionUsuario.getDeudaNoVencida());
                                                            if (deudaNoVencida > 0) {
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
                                                    getSupportFragmentManager().beginTransaction().replace(R.id.contenedorFragment, preventaFragment, "PreventaFragment").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                                                }

                                            } else {

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
                                                        getSupportFragmentManager().beginTransaction().replace(R.id.contenedorFragment, preventaFragment, "PreventaFragment").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                                                        if (sessionUsuario.getDeudaNoVencida() != null) {
                                                            Double deudaNoVencida = new Double(sessionUsuario.getDeudaNoVencida().equals("") ? "0" : sessionUsuario.getDeudaNoVencida());
                                                            if (deudaNoVencida > 0) {
                                                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                                                MyDialogAlertaMayorista newFragment = MyDialogAlertaMayorista.newInstance(bottomBar, ab);
                                                                newFragment.setDialogInfoListener(dialogInfoListener);
                                                                newFragment.setArguments(args);
                                                                newFragment.show(ft, "dialog");
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                                    MyDialogAlertaMayorista newFragment = MyDialogAlertaMayorista.newInstance(bottomBar, ab);
                                                    newFragment.setDialogInfoListener(dialogInfoListener);
                                                    newFragment.setArguments(args);
                                                    newFragment.show(ft, "dialog");
                                                }
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
                        //getSupportFragmentManager().beginTransaction().replace(R.id.contenedorFragment,cobranzaFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.contenedorFragment, cobranzaFragment,"CobranzaFragmentOut");
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.addToBackStack(null);
                        ft.commit();

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
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }
    boolean exit=false;
    @Override
    public void onBackPressed() {
        CobranzaFragment fragmentCobranza = (CobranzaFragment) getSupportFragmentManager().findFragmentByTag("CobranzaFragment");
        DatosClienteFragment fragmentDatosCliente= (DatosClienteFragment) getSupportFragmentManager().findFragmentByTag("DatosClienteFragment");

        PreventaFragment fragmentPreventa= (PreventaFragment) getSupportFragmentManager().findFragmentByTag("PreventaFragment");
        CobranzaFragment fragmentCobranzaOut = (CobranzaFragment) getSupportFragmentManager().findFragmentByTag("CobranzaFragmentOut");

        if (fragmentCobranza != null && fragmentCobranza.isVisible()) {
            bottomBar.setDefaultTab(R.id.tab_datos);
            bottomBar.getTabWithId(R.id.tab_cobranza).setEnabled(false);
        }else if(fragmentCobranzaOut != null && fragmentCobranzaOut.isVisible()) {
            bottomBar.setDefaultTab(R.id.tab_datos);
            bottomBar.getTabWithId(R.id.tab_cobranza).setEnabled(true);
        }else if(fragmentPreventa != null && fragmentPreventa.isVisible()) {
            finish();
        }else if(fragmentDatosCliente != null && fragmentDatosCliente.isVisible()) {
                finish();
        }else{
            super.onBackPressed();
        }
//        PreventaFragment fragmentPreventa = (PreventaFragment) getSupportFragmentManager().findFragmentByTag("PreventaFragment");
//        if (fragmentPreventa != null && fragmentPreventa.isVisible()) {
//            bottomBar.setDefaultTab(R.id.tab_datos);
//            bottomBar.getTabWithId(R.id.tab_cobranza).setEnabled(false);
//        }



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
