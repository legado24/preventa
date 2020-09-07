package com.legado.preventagps.activities.vendedor;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.legado.preventagps.R;
import com.legado.preventagps.api.ApiRetrofitLong;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.dialogs.MyDialogErrorInicio;
import com.legado.preventagps.dialogs.MyDialogProgress;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.LinearModel;
import com.legado.preventagps.util.MyReceiver;
import com.legado.preventagps.util.PermissionUtils;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.UpdateHelper;
import com.legado.preventagps.util.UtilAndroid;
import com.pd.chocobar.ChocoBar;
//import com.tapadoo.alerter.Alerter;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class InicioActivity extends BaseActivity implements UpdateHelper.OnUpdateCheckListener {
    @BindView(R.id.txtBienvenida)
    TextView txtBienvenida;
    @BindView(R.id.txtMensajeFecha)
    TextView txtMensajeFecha;
    @BindView(R.id.txtProgressCuota)
    TextView txtProgressCuota;
    @BindView(R.id.txtTicketDiario)
    TextView txtTicketDiario;
    @BindView(R.id.txtTicketMensual)
    TextView txtTicketMensual;
    @BindView(R.id.pb)
    ProgressBar pb;
    @BindView(R.id.cardInicio)
    CardView cardInicio;
    SweetAlertDialog dialogDatosUsuario;
    SessionUsuario sessionUsuario;
    Context c;
    DecimalFormat formateador;
    String fechaDevice;
    private Double progressStatus = 0.0;
    private Handler handler = new Handler();

    MyReceiver oMyReceiver;
    String urlApp;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        c = getApplicationContext();
        ButterKnife.bind(this);
        sessionUsuario = new SessionUsuario(this);
        fechaDevice = UtilAndroid.fechaDevice("dd-MM-yyyy");
        dialogDatosUsuario = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialogDatosUsuario.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialogDatosUsuario.setTitleText("Cargando..");
        dialogDatosUsuario.setCancelable(false);
//        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        wifi.setWifiEnabled(false);
        formateador = new DecimalFormat("###,###.##");
        setupToolbar();
         if (ApiSqlite.getInstance(InicioActivity.this.getApplicationContext()).getOperacionesBaseDatos().listarMetricasByFecha(fechaDevice).isEmpty()) {
            if (ApiSqlite.getInstance(InicioActivity.this.getApplicationContext()).getOperacionesBaseDatos().contarTablas() == 15) {
                limpiarMetricas();
                limpiarPedidosNoPedidosAnteriores(fechaDevice);
                cargarDatosVendedorSueldo(sessionUsuario.getPaqueteUsuario().getUsuario());
            } else {
                limpiarMetricas();
                limpiarPedidosNoPedidosAnteriores(fechaDevice);
                cargarDatosVendedorSueldo(sessionUsuario.getPaqueteUsuario().getUsuario());
            }

        } else {
            cargarDatosVendedorSueldoOffline();
        }



        Init();
       UpdateHelper.with(this).onUpdateCheck(this).check();


        CardView menu0 = (CardView) findViewById(R.id.menu0);
        menu0.setCardBackgroundColor(Color.parseColor("#FF9800"));



    }




    private  void Init(){
        oMyReceiver=new MyReceiver(InicioActivity.this);
        oMyReceiver.registrar(oMyReceiver);

    }
    private void cargarDatosVendedorSueldo(String usuario) {
        dialogDatosUsuario = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialogDatosUsuario.getProgressHelper().setBarColor(Color.parseColor("#ef4437"));
        dialogDatosUsuario.setTitleText("Cargando..");
        dialogDatosUsuario.setCancelable(false);
        dialogDatosUsuario.show();

        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario", usuario);
        Call<JsonRespuesta> loginCall = ApiRetrofitLong.getInstance(sessionUsuario.getUrlPreventa()).getOfflineService().datosVendedorSueldo(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if (response.body() != null) {
                    if (response.body().getEstado() == 1) {
                        ArrayList objetoDatosUs = (ArrayList) response.body().getData();
                        ArrayList listaDatosUsuario = (ArrayList) objetoDatosUs.get(2);
                        txtBienvenida.setText(listaDatosUsuario.get(1).toString());
                        txtMensajeFecha.setText("AL " + objetoDatosUs.get(3).toString() + "\n\n TUS AVANCES PARA EL " + objetoDatosUs.get(4).toString() + " SON:");
                        String tickDiarioFormateado = formateador.format(Double.parseDouble(objetoDatosUs.get(5).toString()));
                        txtTicketDiario.setText(tickDiarioFormateado);
                        String tickMensualFormateado = formateador.format(Double.parseDouble(objetoDatosUs.get(6).toString()));
                        txtTicketMensual.setText(tickMensualFormateado);
                        ArrayList objeto = (ArrayList) response.body().getData();
                        ArrayList objeto1 = (ArrayList) objeto.get(0);
                        ArrayList nombres = (ArrayList) objeto.get(1);
                        ArrayList cabecerasAvances = (ArrayList) nombres.get(1);
                        if (objeto1.size() > 0) {
                            ArrayList valores = (ArrayList) objeto1.get(0);
                            ArrayList cabecerasComisiones = (ArrayList) nombres.get(0);
                            BigDecimal sueldoTotal = new BigDecimal(0);
                            for (int i = 0; i < cabecerasComisiones.size(); i++) {
                                sueldoTotal = sueldoTotal.add(new BigDecimal(valores.get(2 + cabecerasAvances.size() + i).toString()));
                            }

                            BigDecimal sueldoTotalFinal = sueldoTotal.setScale(2, RoundingMode.HALF_UP);
                            cabecerasAvances.addAll(cabecerasComisiones);

                            try {
                                ApiSqlite.getInstance(InicioActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
                                valores.add(sueldoTotalFinal.doubleValue());
                                for (int i = 2; i < valores.size(); i++) {
                                    Double monto = (Double) valores.get(i);
                                    ApiSqlite.getInstance(InicioActivity.this.getApplicationContext()).getOperacionesBaseDatos().insertarMontos(monto);//inserta montos sqlite
                                }
                                cabecerasAvances.add("SUELDO TOTAL");
                                for (int i = 0; i < cabecerasAvances.size(); i++) {
                                    String cabeceraAv = (String) cabecerasAvances.get(i);
                                    ApiSqlite.getInstance(InicioActivity.this.getApplicationContext()).getOperacionesBaseDatos().insertarCabeceraMontos(cabeceraAv);//inserta cabecera montos
                                }

                                ApiSqlite.getInstance(InicioActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                            } finally {
                                ApiSqlite.getInstance(InicioActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                            }


                            List<LinearModel> campos = new ArrayList<LinearModel>();
                            for (int i = 0; i < cabecerasAvances.size(); i++) {
                                LinearModel lm = new LinearModel();
                                lm.setTitulo(cabecerasAvances.get(i).toString());
                                lm.setValor(valores.get(i + 2).toString());
                                campos.add(lm);
                            }

                            LinearLayout myRoot = (LinearLayout) findViewById(R.id.idLinear);
                            Integer cociente = (campos.size()) / 2;
                            Integer resto = (campos.size()) % 2;
                            for (int i = 0; i < cociente; i++) {
                                LinearLayout L1 = new LinearLayout(InicioActivity.this);
                                L1.setBackgroundColor(Color.WHITE);
                                L1.setOrientation(LinearLayout.HORIZONTAL);
                                LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                L1.setWeightSum(1f);
                                L1.setGravity(Gravity.CENTER);
                                L1.setLayoutParams(LLParams);

                                for (int j = i * 2; j < i * 2 + 2; j++) {
                                    LinearLayout L11 = new LinearLayout(InicioActivity.this);
                                    L11.setBackgroundColor(Color.WHITE);
                                    L11.setOrientation(LinearLayout.VERTICAL);
                                    LinearLayout.LayoutParams LL1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
                                    L11.setLayoutParams(LL1Params);

                                    TextView textView1 = new TextView(InicioActivity.this);
                                    textView1.setTypeface(textView1.getTypeface(), Typeface.BOLD);
                                    textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                                    LinearLayout.LayoutParams LL1tParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
                                    textView1.setLayoutParams(LL1tParams);
                                    textView1.setText(campos.get(j).getTitulo());
                                    TextView textView2 = new TextView(InicioActivity.this);
                                    if (campos.get(j).getTitulo().equals("AVANCE CUOTA")) {
                                        showProgressCuota(campos.get(j).getValor());
                                    }

                                    textView2.setText(formateador.format(Double.parseDouble(campos.get(j).getValor())));
                                    textView2.setLayoutParams(LL1tParams);
                                    L11.addView(textView1);
                                    L11.addView(textView2);

                                    L1.addView(L11);
                                }
                                myRoot.addView(L1);
                                View linea = new View(InicioActivity.this);
                                final float scale = getResources().getDisplayMetrics().density;
                                int dpHeightInPx = (int) (1 * scale);
                                int dpMarginInPx = (int) (10 * scale);
                                LinearLayout.LayoutParams lview = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpHeightInPx);
                                lview.setMargins(0, dpMarginInPx, 0, 0);
                                linea.setLayoutParams(lview);
                                linea.setBackground(c.getResources().getDrawable(R.color.main_color_grey_400));
                                myRoot.addView(linea);

                            }

                            if (resto > 0) {
                                for (int i = 0; i < resto; i++) {
                                    LinearLayout L1 = new LinearLayout(InicioActivity.this);
                                    L1.setBackgroundColor(Color.WHITE);
                                    L1.setOrientation(LinearLayout.HORIZONTAL);
                                    LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    L1.setWeightSum(1f);
                                    L1.setGravity(Gravity.CENTER);
                                    L1.setLayoutParams(LLParams);

                                    LinearLayout L21 = new LinearLayout(InicioActivity.this);
                                    L21.setBackgroundColor(Color.WHITE);
                                    L21.setOrientation(LinearLayout.VERTICAL);
                                    LinearLayout.LayoutParams LL2Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                                    L21.setLayoutParams(LL2Params);

                                    TextView textViewSueldo1 = new TextView(InicioActivity.this);
                                    textViewSueldo1.setTypeface(textViewSueldo1.getTypeface(), Typeface.BOLD);
                                    textViewSueldo1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                                    LinearLayout.LayoutParams LL2tParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    textViewSueldo1.setLayoutParams(LL2tParams);
                                    textViewSueldo1.setText("SUELDO TOTAL");
                                    TextView textViewSueldo2 = new TextView(InicioActivity.this);
                                    textViewSueldo2.setText(sueldoTotalFinal.toString());
                                    textViewSueldo2.setLayoutParams(LL2tParams);
                                    L21.addView(textViewSueldo1);
                                    L21.addView(textViewSueldo2);
                                    L1.addView(L21);

                                    myRoot.addView(L1);


                                    View linea = new View(InicioActivity.this);
                                    final float scale = getResources().getDisplayMetrics().density;
                                    int dpHeightInPx = (int) (1 * scale);
                                    int dpMarginInPx = (int) (10 * scale);

                                    LinearLayout.LayoutParams lview = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpHeightInPx);
                                    lview.setMargins(0, dpMarginInPx, 0, 0);
                                    linea.setLayoutParams(lview);
                                    linea.setBackground(c.getResources().getDrawable(R.color.main_color_grey_400));

                                    myRoot.addView(linea);
                                }
                            } else {

                                LinearLayout L21 = new LinearLayout(InicioActivity.this);
                                L21.setBackgroundColor(Color.WHITE);
                                L21.setOrientation(LinearLayout.VERTICAL);
                                LinearLayout.LayoutParams LL2Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                                L21.setLayoutParams(LL2Params);

                                myRoot.addView(L21);
                            }
                            String rutas = nombres.get(4).toString();


                            try {
                                ApiSqlite.getInstance(InicioActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
                                ApiSqlite.getInstance(InicioActivity.this.getApplicationContext()).getOperacionesBaseDatos().insertarDatosMetricas(listaDatosUsuario.get(1).toString(), objetoDatosUs.get(3).toString(), objetoDatosUs.get(4).toString(), Double.parseDouble(objetoDatosUs.get(5).toString()), Double.parseDouble(objetoDatosUs.get(6).toString()), rutas, new Double(objetoDatosUs.get(7).toString()).intValue());//inserta datos de usuario codigo fecha periodo ,rutas ..
                                ApiSqlite.getInstance(InicioActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                            } finally {
                                ApiSqlite.getInstance(InicioActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                            }

                            ChocoBar.builder().setBackgroundColor(c.getResources().getColor(R.color.colorInfo)).setTextSize(12)
                                    .setTextColor(Color.parseColor("#FFFFFF"))
                                    .setTextTypefaceStyle(Typeface.ITALIC)
                                    .setText(rutas)
                                    .setMaxLines(90)
                                    .centerText()
                                    .setActionText("Cerrar")
                                    .setActionClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {


                                        }
                                    })
                                    .setActionTextColor(c.getResources().getColor(R.color.amber900))
                                    .setActionTextSize(12)
                                    .setActionTextTypefaceStyle(Typeface.BOLD)
                                    .setIcon(R.drawable.ic_info_outline_white_48dp)
                                    .setActivity(InicioActivity.this)
                                    .setDuration(ChocoBar.LENGTH_INDEFINITE).build().show();

                        } else {
                            String rutas = nombres.get(4).toString();
                            try {
                                ApiSqlite.getInstance(InicioActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
                                ApiSqlite.getInstance(InicioActivity.this.getApplicationContext()).getOperacionesBaseDatos().insertarDatosMetricas(listaDatosUsuario.get(1).toString(), objetoDatosUs.get(3).toString(), objetoDatosUs.get(4).toString(), Double.parseDouble(objetoDatosUs.get(5).toString()), Double.parseDouble(objetoDatosUs.get(6).toString()), rutas, new Double(objetoDatosUs.get(7).toString()).intValue());
                                ApiSqlite.getInstance(InicioActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                            } finally {
                                ApiSqlite.getInstance(InicioActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                            }

                            ChocoBar.builder().setBackgroundColor(c.getResources().getColor(R.color.colorInfo)).setTextSize(12)
                                    .setTextColor(Color.parseColor("#FFFFFF"))
                                    .setTextTypefaceStyle(Typeface.ITALIC)
                                    .setText(rutas)
                                    .setMaxLines(90)
                                    .centerText()
                                    .setActionText("Cerrar")
                                    .setActionClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    })
                                    .setActionTextColor(c.getResources().getColor(R.color.amber900))
                                    .setActionTextSize(12)
                                    .setActionTextTypefaceStyle(Typeface.BOLD)
                                    .setIcon(R.drawable.ic_info_outline_white_48dp)
                                    .setActivity(InicioActivity.this)
                                    .setDuration(ChocoBar.LENGTH_INDEFINITE).build().show();

                            ChocoBar.builder().setBackgroundColor(c.getResources().getColor(R.color.colorWarning)).setTextSize(12)
                                    .setTextColor(Color.parseColor("#FFFFFF"))
                                    .setTextTypefaceStyle(Typeface.ITALIC)
                                    .setText("No se han cargado las cuotas")
                                    .setMaxLines(90)
                                    .centerText()
                                    .setActionText("Cerrar")
                                    .setActionClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    })
                                    .setActionTextColor(c.getResources().getColor(R.color.amber900))
                                    .setActionTextSize(12)
                                    .setActionTextTypefaceStyle(Typeface.BOLD)
                                    .setIcon(R.drawable.ic_warning_white_48dp)
                                    .setActivity(InicioActivity.this)
                                    .setDuration(ChocoBar.LENGTH_INDEFINITE).build().show();

                        }


                    }
                    dialogDatosUsuario.dismissWithAnimation();
                    cardInicio.setVisibility(View.VISIBLE);
                    checkMyPermissionLocation();
                } else {
                    new SweetAlertDialog(InicioActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.txtMensajeServidorCaido))
                            .show();
                    dialogDatosUsuario.dismissWithAnimation();
                }
            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                dialogDatosUsuario.dismissWithAnimation();
                SweetAlertDialog pDialog = new SweetAlertDialog(InicioActivity.this, SweetAlertDialog.ERROR_TYPE);
                // pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Oops...");
                pDialog.setContentText(getString(R.string.txtMensajeConexion));
                pDialog.setConfirmText("Reintentar");
                pDialog.setContentTextSize(12);
                 pDialog.setCancelable(false);
                // pDialog. getWindow().getDecorView().setMinimumWidth(800);
                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        cargarDatosVendedorSueldo(usuario);
                    }
                });

                pDialog.show();

            }
        });
    }

    private void cargarDatosVendedorSueldoOffline() {
        dialogDatosUsuario.show();
        List<String> datosMetricas = ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().listarDatosMetricas();
        txtBienvenida.setText(datosMetricas.get(0));
        txtMensajeFecha.setText("AL " + datosMetricas.get(1) + "\n\n TUS AVANCES PARA EL " + datosMetricas.get(2) + " SON:");
        String tickDiarioFormateado = formateador.format(Double.parseDouble(datosMetricas.get(3)));
        txtTicketDiario.setText(tickDiarioFormateado);
        String tickMensualFormateado = formateador.format(Double.parseDouble(datosMetricas.get(4)));
        txtTicketMensual.setText(tickMensualFormateado);
        List<String> cabecera = ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().listarCabeceraMontos();
        List<Double> valores = ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().listarValoresMontos();

        List<LinearModel> campos = new ArrayList<LinearModel>();
        for (int i = 0; i < cabecera.size(); i++) {
            LinearModel lm = new LinearModel();
            lm.setTitulo(cabecera.get(i));
            lm.setValor(valores.get(i).toString());
            campos.add(lm);
        }

        LinearLayout myRoot = (LinearLayout) findViewById(R.id.idLinear);
        Integer cociente = campos.size() / 2;
        Integer resto = campos.size() % 2;
        for (int i = 0; i < cociente; i++) {
            LinearLayout L1 = new LinearLayout(InicioActivity.this);
            L1.setBackgroundColor(Color.WHITE);
            L1.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            L1.setWeightSum(1f);
            L1.setGravity(Gravity.CENTER);
            L1.setLayoutParams(LLParams);

            for (int j = i * 2; j < i * 2 + 2; j++) {
                LinearLayout L11 = new LinearLayout(InicioActivity.this);
                L11.setBackgroundColor(Color.WHITE);
                L11.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams LL1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
                L11.setLayoutParams(LL1Params);

                TextView textView1 = new TextView(InicioActivity.this);
                textView1.setTypeface(textView1.getTypeface(), Typeface.BOLD);
                textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                LinearLayout.LayoutParams LL1tParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
                textView1.setLayoutParams(LL1tParams);
                textView1.setText(campos.get(j).getTitulo());
                TextView textView2 = new TextView(InicioActivity.this);
                if (campos.get(j).getTitulo().equals("AVANCE CUOTA")) {
                    showProgressCuota(campos.get(j).getValor());
                }

                textView2.setText(formateador.format(Double.parseDouble(campos.get(j).getValor())));
                textView2.setLayoutParams(LL1tParams);
                L11.addView(textView1);
                L11.addView(textView2);

                L1.addView(L11);
            }
            myRoot.addView(L1);

            View linea = new View(InicioActivity.this);
            final float scale = getResources().getDisplayMetrics().density;
            int dpHeightInPx = (int) (1 * scale);
            int dpMarginInPx = (int) (10 * scale);
            LinearLayout.LayoutParams lview = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpHeightInPx);
            lview.setMargins(0, dpMarginInPx, 0, 0);
            linea.setLayoutParams(lview);
            linea.setBackground(c.getResources().getDrawable(R.color.main_color_grey_400));
            myRoot.addView(linea);

        }

        if (resto > 0) {
            for (int i = 0; i < resto; i++) {
                LinearLayout L1 = new LinearLayout(InicioActivity.this);
                L1.setBackgroundColor(Color.WHITE);
                L1.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                L1.setWeightSum(1f);
                L1.setGravity(Gravity.CENTER);
                L1.setLayoutParams(LLParams);

                LinearLayout L21 = new LinearLayout(InicioActivity.this);
                L21.setBackgroundColor(Color.WHITE);
                L21.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams LL2Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                L21.setLayoutParams(LL2Params);

                TextView textViewSueldo1 = new TextView(InicioActivity.this);
                textViewSueldo1.setTypeface(textViewSueldo1.getTypeface(), Typeface.BOLD);
                textViewSueldo1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                LinearLayout.LayoutParams LL2tParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textViewSueldo1.setLayoutParams(LL2tParams);
                textViewSueldo1.setText(cabecera.get(cabecera.size() - 1));
                TextView textViewSueldo2 = new TextView(InicioActivity.this);
                textViewSueldo2.setText(valores.get(valores.size() - 1).toString());
                textViewSueldo2.setLayoutParams(LL2tParams);
                L21.addView(textViewSueldo1);
                L21.addView(textViewSueldo2);

                L1.addView(L21);
                myRoot.addView(L1);

                View linea = new View(InicioActivity.this);
                final float scale = getResources().getDisplayMetrics().density;
                int dpHeightInPx = (int) (1 * scale);
                int dpMarginInPx = (int) (10 * scale);

                LinearLayout.LayoutParams lview = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpHeightInPx);
                lview.setMargins(0, dpMarginInPx, 0, 0);
                linea.setLayoutParams(lview);
                linea.setBackground(c.getResources().getDrawable(R.color.main_color_grey_400));
                myRoot.addView(linea);
            }
        } else {

            LinearLayout L21 = new LinearLayout(InicioActivity.this);
            L21.setBackgroundColor(Color.WHITE);
            L21.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams LL2Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            L21.setLayoutParams(LL2Params);

            TextView textViewSueldo1 = new TextView(InicioActivity.this);
            textViewSueldo1.setTypeface(textViewSueldo1.getTypeface(), Typeface.BOLD);
            textViewSueldo1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            LinearLayout.LayoutParams LL2tParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textViewSueldo1.setLayoutParams(LL2tParams);
            if (!cabecera.isEmpty()) {
                textViewSueldo1.setText(cabecera.get(cabecera.size() - 1));
            }

            TextView textViewSueldo2 = new TextView(InicioActivity.this);
            if (!valores.isEmpty()) {
                textViewSueldo2.setText(valores.get(valores.size() - 1).toString());
            }
            textViewSueldo2.setLayoutParams(LL2tParams);
            L21.addView(textViewSueldo1);
            L21.addView(textViewSueldo2);
            myRoot.addView(L21);
        }


        ChocoBar.builder().setBackgroundColor(c.getResources().getColor(R.color.colorInfo)).setTextSize(12)
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setTextTypefaceStyle(Typeface.ITALIC)
                .setText(datosMetricas.get(5))
                .setMaxLines(90)
                .centerText()
                .setActionText("Cerrar")
                .setActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setActionTextColor(c.getResources().getColor(R.color.amber900))
                .setActionTextSize(12)
                .setActionTextTypefaceStyle(Typeface.BOLD)
                .setIcon(R.drawable.ic_info_outline_white_48dp)
                .setActivity(InicioActivity.this)
                .setDuration(ChocoBar.LENGTH_INDEFINITE).build().show();

                dialogDatosUsuario.dismiss();
                cardInicio.setVisibility(View.VISIBLE);
                 checkMyPermissionLocation();

    }

    private void showProgressCuota(final String s) {
        progressStatus = 0.0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < new Double(s)) {
                    progressStatus += 0.1;
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            pb.setProgress(progressStatus.intValue());
                            txtProgressCuota.setText(UtilAndroid.round(progressStatus, 3) + "%");
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setTitle(Html.fromHtml("<font color='#FFFFFF'>"
                + "MI INFORMACIÓN"
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
            case R.id.refrescarInfo:
                limpiarMetricas();
                Intent intent = new Intent(this, InicioActivity.class);
                startActivity(intent);
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
                break;
            case R.id.menu1:
                Intent intent1 = new Intent(this, MenuPreventaActivity.class);
                startActivity(intent1);
                break;
            case R.id.menu2:
                Intent intent2 = new Intent(this, ContenedorAltaActivity.class);
                startActivity(intent2);
                break;
            case R.id.menu3:
                Intent intent3 = new Intent(this, MenuCobranzaActivity.class);
                startActivity(intent3);
                break;
            case R.id.menu4:
                Intent intent4 = new Intent(this, MenuBandejasActivity.class);
                startActivity(intent4);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inicio, menu);
        return true;
    }

    private void checkMyPermissionLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //Permission Check
            PermissionUtils.requestPermission(this);
        }
    }

    private void checkMyPermissionStorageWrite() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //Permission Check
            PermissionUtils.requestPermissionStorageWrite(this);
        }
    }

    @Override
    public void onBackPressed() {

        moveTaskToBack(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
//
//            if (requestCode == 101)
//                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
//        }
//
//
//    }
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
{
    switch (requestCode) {
        case 1: {

            // Validamos si el usuario acepta el permiso para que la aplicación acceda a los datos internos del equipo, si no denegamos el acceso
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                oMyReceiver.descargar(urlApp);

            } else {

                Toast.makeText(InicioActivity.this, "Has negado el permiso a la aplicación", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }
}

    private void consultarPermiso(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(InicioActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(InicioActivity.this, permission)) {

                ActivityCompat.requestPermissions(InicioActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(InicioActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            oMyReceiver.descargar(urlApp);
           // Toast.makeText(this,permission + " El permiso a la aplicación esta concedido.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onUpdateCheckListener(String urlAppFirebase) {
        urlApp=urlAppFirebase;
        AlertDialog alertDialog=new AlertDialog.Builder(this)
                .setTitle("Actualización")
                .setMessage("Hay una actualización disponible")
                .setPositiveButton("ACTUALIZAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        consultarPermiso(Manifest.permission.WRITE_EXTERNAL_STORAGE, 400);


                    }
                })
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
        alertDialog.show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        oMyReceiver.borrarRegistro(oMyReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        oMyReceiver.registrar(oMyReceiver);
    }
}
