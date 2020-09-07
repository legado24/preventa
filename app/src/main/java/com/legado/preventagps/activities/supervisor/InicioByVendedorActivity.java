package com.legado.preventagps.activities.supervisor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.legado.preventagps.R;
import com.legado.preventagps.api.ApiRetrofitLong;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.dialogs.MyDialogProgress;
import com.legado.preventagps.dialogs.supervisor.MyDialogErrorInicioByVendedor;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.LinearModel;
import com.legado.preventagps.util.PermissionUtils;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.UtilAndroid;
import com.pd.chocobar.ChocoBar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.tapadoo.alerter.Alerter;

public class InicioByVendedorActivity extends BaseActivityByVendedor {
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
    MyDialogProgress dialogDatosUsuario;
    SessionUsuario sessionUsuario;
    private Double progressStatus = 0.0;
    private Handler handler = new Handler();
    Context c;
    DecimalFormat formateador;
    FragmentTransaction ft;
     MyDialogErrorInicioByVendedor newFragment;
    private final static int REQUEST_CODE = 42;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_by_vendedor);
        c = getApplicationContext();
        ButterKnife.bind(this);
        sessionUsuario = new SessionUsuario(this);
        formateador = new DecimalFormat("###,###.##");
        setupToolbar();
        cargarDatosVendedorSueldo(getIntent().getStringExtra("usuario"));
        CardView menu0 = (CardView) findViewById(R.id.menuByVendedor0);
        menu0.setCardBackgroundColor(Color.parseColor("#FF9800"));

        TextView txtDashboard=(TextView) findViewById(R.id.txtDashboard);
        txtDashboard.setText(getIntent().getStringExtra("codVendedor")+"-"+getIntent().getStringExtra("descVendedor"));

    }


    private void cargarDatosVendedorSueldo(String usuario) {
        ft = getSupportFragmentManager().beginTransaction();
        dialogDatosUsuario = new MyDialogProgress();
        dialogDatosUsuario.show(ft, "dialog");
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario", usuario);
         Call<JsonRespuesta> loginCall = ApiRetrofitLong.getInstance(sessionUsuario.getUrlPreventa()).getOfflineService().datosVendedorSueldo(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
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

                         valores.add(sueldoTotalFinal.doubleValue());

                         cabecerasAvances.add("SUELDO TOTAL");

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
                            LinearLayout L1 = new LinearLayout(InicioByVendedorActivity.this);
                            L1.setBackgroundColor(Color.WHITE);
                            L1.setOrientation(LinearLayout.HORIZONTAL);
                            LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            L1.setWeightSum(1f);
                            L1.setGravity(Gravity.CENTER);
                            L1.setLayoutParams(LLParams);

                            for (int j = i * 2; j < i * 2 + 2; j++) {
                                LinearLayout L11 = new LinearLayout(InicioByVendedorActivity.this);
                                L11.setBackgroundColor(Color.WHITE);
                                L11.setOrientation(LinearLayout.VERTICAL);
                                LinearLayout.LayoutParams LL1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
                                L11.setLayoutParams(LL1Params);

                                TextView textView1 = new TextView(InicioByVendedorActivity.this);
                                textView1.setTypeface(textView1.getTypeface(), Typeface.BOLD);
                                textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                                LinearLayout.LayoutParams LL1tParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
                                textView1.setLayoutParams(LL1tParams);
                                textView1.setText(campos.get(j).getTitulo());
                                TextView textView2 = new TextView(InicioByVendedorActivity.this);
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
                            View linea = new View(InicioByVendedorActivity.this);
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
                                LinearLayout L1 = new LinearLayout(InicioByVendedorActivity.this);
                                L1.setBackgroundColor(Color.WHITE);
                                L1.setOrientation(LinearLayout.HORIZONTAL);
                                LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                L1.setWeightSum(1f);
                                L1.setGravity(Gravity.CENTER);
                                L1.setLayoutParams(LLParams);

                                LinearLayout L21 = new LinearLayout(InicioByVendedorActivity.this);
                                L21.setBackgroundColor(Color.WHITE);
                                L21.setOrientation(LinearLayout.VERTICAL);
                                LinearLayout.LayoutParams LL2Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                                L21.setLayoutParams(LL2Params);

                                TextView textViewSueldo1 = new TextView(InicioByVendedorActivity.this);
                                textViewSueldo1.setTypeface(textViewSueldo1.getTypeface(), Typeface.BOLD);
                                textViewSueldo1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                                LinearLayout.LayoutParams LL2tParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                textViewSueldo1.setLayoutParams(LL2tParams);
                                textViewSueldo1.setText("SUELDO TOTAL");
                                TextView textViewSueldo2 = new TextView(InicioByVendedorActivity.this);
                                textViewSueldo2.setText(sueldoTotalFinal.toString());
                                textViewSueldo2.setLayoutParams(LL2tParams);
                                L21.addView(textViewSueldo1);
                                L21.addView(textViewSueldo2);
                                L1.addView(L21);

                                myRoot.addView(L1);


                                View linea = new View(InicioByVendedorActivity.this);
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

                            LinearLayout L21 = new LinearLayout(InicioByVendedorActivity.this);
                            L21.setBackgroundColor(Color.WHITE);
                            L21.setOrientation(LinearLayout.VERTICAL);
                            LinearLayout.LayoutParams LL2Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                            L21.setLayoutParams(LL2Params);

                            myRoot.addView(L21);
                        }
                        String rutas = nombres.get(4).toString();



                                ChocoBar.builder().setBackgroundColor(c.getResources().getColor(R.color.colorInfo)).setTextSize(12)
                                        .setTextColor(Color.parseColor("#FFFFFF"))
                                        .setTextTypefaceStyle(Typeface.ITALIC)
                                        .setText(rutas)
                                        .setMaxLines(4)
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
                                        .setActivity(InicioByVendedorActivity.this)
                                        .setDuration(ChocoBar.LENGTH_INDEFINITE).build().show();



                    } else {
                        String rutas = nombres.get(4).toString();

                        ChocoBar.builder().setBackgroundColor(c.getResources().getColor(R.color.colorInfo)).setTextSize(12)
                                .setTextColor(Color.parseColor("#FFFFFF"))
                                .setTextTypefaceStyle(Typeface.ITALIC)
                                .setText(rutas)
                                .setMaxLines(4)
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
                                .setActivity(InicioByVendedorActivity.this)
                                .setDuration(ChocoBar.LENGTH_INDEFINITE).build().show();



                        ChocoBar.builder().setBackgroundColor(c.getResources().getColor(R.color.colorWarning)).setTextSize(12)
                                .setTextColor(Color.parseColor("#FFFFFF"))
                                .setTextTypefaceStyle(Typeface.ITALIC)
                                .setText("No se han cargado las cuotas")
                                .setMaxLines(4)
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
                                .setActivity(InicioByVendedorActivity.this)
                                .setDuration(ChocoBar.LENGTH_INDEFINITE).build().show();


                    }



                } else {
//PENDIENTE
                }
                dialogDatosUsuario.dismiss();
                cardInicio.setVisibility(View.VISIBLE);
                checkMyPermissionLocation();


            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                newFragment = MyDialogErrorInicioByVendedor.newInstance();
                Bundle args=getIntent().getExtras();
                newFragment.setArguments(args);
                newFragment.show(ft, "dialog");

            }
        });
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
                + "DATOS DEL VENDEDOR"
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

                Intent intent = new Intent(this, InicioByVendedorActivity.class);
                startActivity(intent);

                 return true;


        }
        return super.onOptionsItemSelected(item);
    }


@Override
protected void goToNavDrawerItem(int item) {
    switch (item) {
        case R.id.menuByVendedor0:
            Intent intent0 = new Intent(this, InicioByVendedorActivity.class);
            intent0.putExtra("usuario",getIntent().getStringExtra("usuario"));
            intent0.putExtra("codVendedor", getIntent().getStringExtra("codVendedor"));
            intent0.putExtra("descVendedor", getIntent().getStringExtra("descVendedor"));

            startActivity(intent0);
             finish();
            break;
        case R.id.menuByVendedor1:
            Intent intent1 = new Intent(this, ClienteByVendedorActivity.class);
            intent1.putExtra("usuario",getIntent().getStringExtra("usuario"));
            intent1.putExtra("codVendedor", getIntent().getStringExtra("codVendedor"));
            intent1.putExtra("descVendedor", getIntent().getStringExtra("descVendedor"));

            startActivity(intent1);
             finish();
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

    @Override
    public void onBackPressed() {

        Intent intent4 = new Intent(this, MenuByVendedorActivity.class);
        intent4.putExtra("origen","InicioByVendedorActiviy");
        intent4.putExtra("usuario",getIntent().getStringExtra("usuario"));
        intent4.putExtra("codVendedor",getIntent().getStringExtra("codVendedor"));
        intent4.putExtra("descVendedor",getIntent().getStringExtra("descVendedor"));
        startActivity(intent4);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(newFragment!=null){
            newFragment.dismiss();
        }
        if(dialogDatosUsuario!=null){
            dialogDatosUsuario.dismiss();
        }


        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {

            if (requestCode == 101)
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
