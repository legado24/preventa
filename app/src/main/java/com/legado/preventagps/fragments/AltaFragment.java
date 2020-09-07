package com.legado.preventagps.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.tonywills.loadingbutton.LoadingButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.legado.preventagps.R;
import com.legado.preventagps.activities.vendedor.ClienteActivity;
import com.legado.preventagps.adapter.vendedor.SpinnerSimpleAdapter;
import com.legado.preventagps.adapter.vendedor.AutoCompleteSimpleAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.dialogs.MyDialogAlertaCliente;
import com.legado.preventagps.dialogs.MyDialogClienteSuccess;
import com.legado.preventagps.dialogs.MyDialogMapa;
import com.legado.preventagps.dialogs.MyDialogProgress;
import com.legado.preventagps.enums.CLIENTEENUM;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Cliente;
import com.legado.preventagps.modelo.ClienteAlta;
import com.legado.preventagps.modelo.LocalCliente;
import com.legado.preventagps.modelo.PaqueteAlta;
import com.legado.preventagps.util.CustomEditText;
import com.legado.preventagps.util.PermissionUtils;
import com.legado.preventagps.util.SessionUsuario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AltaFragment extends Fragment implements MyDialogMapa.EditDialogListener {
    @BindView(R.id.txtDniRuc)
    CustomEditText txtDniRuc;
    @BindView(R.id.txtDescripcion)
    EditText txtDescripcion;
    @BindView(R.id.txtDireccFact)
    EditText txtDireccFact;
    @BindView(R.id.txtReferencia)
    EditText txtReferencia;
    @BindView(R.id.txtCodCliente)
    EditText txtCodCliente;
    @BindView(R.id.spinnerDpto)
    AutoCompleteTextView spinnerDpto;
    @BindView(R.id.spinnerProv)
    AutoCompleteTextView spinnerProv;
    @BindView(R.id.spinnerDist)
    AutoCompleteTextView spinnerDist;

    @BindView(R.id.scrollView1)
    ScrollView scrollView;


    @BindView(R.id.txtEmail)
    EditText txtEmail;

    @BindView(R.id.spinnerTipoCliente)
    Spinner spinnerTipoCliente;

    @BindView(R.id.spinnerGiroCliente)
    Spinner spinnerGiroCliente;
    @BindView(R.id.spinnerLp)
    Spinner spinnerLp;

    @BindView(R.id.spinnerRuta)
    Spinner spinnerRuta;


    @BindView(R.id.opciones_sexo)
    RadioGroup opciones_sexo;
    @BindView(R.id.txtFechaNacimiento)
    TextView txtFechaNacimiento;

    @BindView(R.id.txtCoordenadas)
    EditText txtCoordenadas;

    @BindView(R.id.btnRegistrar)
    Button btnRegistrar;
    @BindView(R.id.btnCapturar)
    LoadingButton btnCapturar;
    @BindView(R.id.imgMap)
    ImageView imgMap;
//    @BindView(R.id.btnMapa)
//    public LoadingButton btnMapa;

    MyDialogProgress dialogRegistrar;
    MyDialogProgress progressConsulta;
    MyDialogProgress progressConsultaDoc;
    SessionUsuario sessionUsuario;
    private int year, month, day;

    MyDialogClienteSuccess dialogSuccess;
    MyDialogAlertaCliente myDialogAlertaCliente;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private boolean isNetworkLocation, isGPSLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    MyDialogMapa.EditDialogListener listener;
    ArrayList listaRutas;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_alta, container, false);
        ButterKnife.bind(this, rootView);
        getActivity().setTitle( Html.fromHtml("<font color='#FFFFFF'>"
                + "REGISTRAR CLIENTE"
                + "</font>"));

        sessionUsuario=new SessionUsuario(getContext());
        if(sessionUsuario.getPaqueteUsuario().getIgnoreGps().equals(1)){
            btnCapturar.setVisibility(View.GONE);
            imgMap.setVisibility(View.GONE);
        }
        inicializarFechaHoy();
        listener=this;
        toUpper();
        txtFechaNacimiento.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      Dialog dialog=onCreateDialog(999);
                                                      if(dialog!=null){
                                                          dialog.show();
                                                      }
                                                  }
                                              }
        );
        cargarDatosAlta();
        txtDireccFact.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }
            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase()))
                {
                    s=s.toUpperCase();
                    txtDireccFact.setText(s);
                }
                txtDireccFact.setSelection(txtDireccFact.getText().length());
            }
        });
        txtDniRuc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View view, boolean hasFocus) {
                if (hasFocus) {
                    limpiar();
                }
            }
        });
        txtDireccFact.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    if(validarDoc()){
                        consultarDocumento();
                    }

                }
            }
        });

        spinnerDpto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Object[] item =(Object[]) adapterView.getItemAtPosition(position);
                PaqueteAlta paq=  sessionUsuario.getPaqueteAlta();
                if(paq==null){
                    paq=new PaqueteAlta();
                    paq.setCodDpto(item[0].toString());
                    paq.setDescDpto(item[1].toString());
                    sessionUsuario.guardarPaqueteAlta(paq);
                }

                loadSpinnerProvincia(item[0].toString());
                spinnerDist.setText("");
                spinnerProv.setText("");
                spinnerProv.requestFocus();
            }
        });

        spinnerProv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Object[] item =(Object[]) adapterView.getItemAtPosition(position);
                PaqueteAlta paq=  sessionUsuario.getPaqueteAlta();
                paq.setCodProv(item[0].toString());
                paq.setDescProv(item[1].toString());
                sessionUsuario.guardarPaqueteAlta(paq);
                loadSpinnerDistrito(item[0].toString());
                spinnerDist.setText("");
                spinnerDist.requestFocus();
            }
        });


        spinnerDist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Object[] item =(Object[]) adapterView.getItemAtPosition(position);
                PaqueteAlta paq=  sessionUsuario.getPaqueteAlta();
                paq.setCodDist(item[0].toString());
                paq.setDescDist(item[1].toString());
                sessionUsuario.guardarPaqueteAlta(paq);
                loadSpinnerDistrito(item[0].toString());

            }
        });

        spinnerDpto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    limpiarSpinner();
                }
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar()){
                    ClienteAlta clienteAlta=new ClienteAlta();
                    clienteAlta.setCodCliente(txtCodCliente.getText().toString());
                    if(txtDniRuc.getText().toString().length()==11){
                        clienteAlta.setRuc(txtDniRuc.getText().toString());
                    }else{
                        clienteAlta.setDni(txtDniRuc.getText().toString());
                    }
                    clienteAlta.setDescripcion(txtDescripcion.getText().toString());
                    clienteAlta.setCodCanal(sessionUsuario.getPaqueteUsuario().getCodCanal());
                    ArrayList tc= (ArrayList) spinnerTipoCliente.getSelectedItem();
                    clienteAlta.setCodTipoCliente(tc.get(0).toString());
                    ArrayList giro= (ArrayList) spinnerGiroCliente.getSelectedItem();
                    clienteAlta.setCodGiroNegocio(giro.get(0).toString());
                    clienteAlta.setUbigeo(sessionUsuario.getPaqueteAlta().getCodDist());
                    clienteAlta.setDireccion(txtDireccFact.getText().toString());
                    clienteAlta.setReferencia(txtReferencia.getText().toString());

                    int selectedId = opciones_sexo.getCheckedRadioButtonId();
                    RadioButton radioButton = (RadioButton) rootView.findViewById(selectedId);
                    clienteAlta.setSexo(radioButton.getText().toString());
                    clienteAlta.setFechaNacimiento(txtFechaNacimiento.getText().toString());
                    clienteAlta.setEmail(txtEmail.getText().toString());
                   // clienteAlta.setTelefonoFijo(txtTelefono.getText().toString());
                   // clienteAlta.setCelular(txtCelular.getText().toString());
                    clienteAlta.setUsuarioRegistra(sessionUsuario.getUsuario());
                    ArrayList lp= (ArrayList) spinnerLp.getSelectedItem();
                    clienteAlta.setCodLp(lp.get(0).toString());


                    LocalCliente localCliente=new LocalCliente();
                    localCliente.setCodLocal("1");
                    localCliente.setDescLocal(txtDescripcion.getText().toString());
                    localCliente.setDireccion(txtDireccFact.getText().toString());
                    ArrayList ruta= (ArrayList) spinnerRuta.getSelectedItem();
                    localCliente.setCodRuta(ruta.get(0).toString());
                    localCliente.setCoordenadas(txtCoordenadas.getText().toString());
                    localCliente.setUbigeo(sessionUsuario.getPaqueteAlta().getCodDist());
                    List<LocalCliente> locales=new ArrayList<>();
                    locales.add(localCliente);
                    clienteAlta.setLocales(locales);

                    Gson gson = new Gson();
                    String clienteJson = gson.toJson(clienteAlta);
                    Log.d("assas",clienteJson);


                    showDialog(clienteAlta,lp.get(1).toString(),giro.get(1).toString());
                }

            }
        });

        btnCapturar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCapturar.setEnabled(false);
                imgMap.setEnabled(false);
                imgMap.setVisibility(View.GONE);
                btnCapturar.setLoading(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkMyPermissionLocation();
                    }
                }, 500);
            }
        });

        return rootView;
    }

    private void cargarDatosAlta() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        progressConsulta= new MyDialogProgress();
        progressConsulta.show(ft, "dialog");
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario",sessionUsuario.getUsuario());
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().cargarDatosAlta(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {

                if(response.body()!=null){
                    if(response.body().getEstado()==1){
                        ArrayList lista= (ArrayList) response.body().getData();
                        ArrayList listaDptos= (ArrayList) lista.get(0);
                        ArrayList listaGiros= (ArrayList) lista.get(1);
                        ArrayList listaTipos= (ArrayList) lista.get(2);
                        ArrayList listaLp= (ArrayList) lista.get(3);
                        listaRutas= (ArrayList) lista.get(4);
                        AutoCompleteSimpleAdapter autoCompleteSimpleAdapter = new AutoCompleteSimpleAdapter(getContext(), R.layout.list_simplespinner_item, listaDptos);
                        spinnerDpto.setAdapter(autoCompleteSimpleAdapter);

                        SpinnerSimpleAdapter giroAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc,listaGiros);
                        giroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerGiroCliente.setAdapter(giroAdapter);

                        SpinnerSimpleAdapter tipoAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc,listaTipos);
                        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerTipoCliente.setAdapter(tipoAdapter);

                        SpinnerSimpleAdapter lpAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, listaLp);
                        lpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerLp.setAdapter(lpAdapter);

                        SpinnerSimpleAdapter rutaAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, listaRutas);
                        rutaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerRuta.setAdapter(rutaAdapter);
                        progressConsulta.dismiss();
                        scrollView.setVisibility(View.VISIBLE);




                    }else{

                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText(getString(R.string.txtMensajeServidorCaido))
                                .show();

                        progressConsulta.dismiss();
                    }

                }else{
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.txtMensajeConexion))
                            .show();

                    progressConsulta.dismiss();
                }

            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                progressConsulta.dismiss();
            }
        });

    }
    private void limpiarSpinner() {
        spinnerDpto.setText("");
        spinnerProv.setText("");
        spinnerDist.setText("");
    }
    private void loadSpinnerProvincia(String codDep) {
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("tipoUbic", "PROVI");
        dataConsulta.put("filtro", codDep);
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().datosUbicacionGeo(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {

               if(response.body()!=null){
                   if(response.body().getEstado()==1){
                       AutoCompleteSimpleAdapter autoCompleteSimpleAdapter = new AutoCompleteSimpleAdapter(getContext(), R.layout.list_simplespinner_item, (ArrayList) response.body().getData());
                       spinnerProv.setAdapter(autoCompleteSimpleAdapter);

                   }else{

                       CoordinatorLayout coordinatorLayout=(CoordinatorLayout)getView().findViewById(R.id.coordinatorLayout);
                       Snackbar snackbar = Snackbar.make(coordinatorLayout, "Text", Snackbar.LENGTH_LONG);
                       View view = snackbar.getView();
                       CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)view.getLayoutParams();
                       params.gravity = Gravity.TOP;
                       view.setLayoutParams(params);
                       snackbar.show();

                   }
               }else{
                   new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                           .setTitleText("Oops...")
                           .setContentText(getString(R.string.txtMensajeServidorCaido))
                           .show();
               }

            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {

                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.txtMensajeConexion))
                        .show();
            }
        });
    }
    private void loadSpinnerDistrito(String codProv) {
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("filtro", codProv);
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().datosUbicacionGeo(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
              if(response.body()!=null){
                  if(response.body().getEstado()==1){
                      AutoCompleteSimpleAdapter autoCompleteSimpleAdapter = new AutoCompleteSimpleAdapter(getContext(), R.layout.list_simplespinner_item, (ArrayList) response.body().getData());
                      spinnerDist.setAdapter(autoCompleteSimpleAdapter);
                  }else{

                      new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                              .setTitleText("Oops...")
                              .setContentText(response.body().getMensaje())
                              .show();

                  }
              }else{
                  new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                          .setTitleText("Oops...")
                          .setContentText(getString(R.string.txtMensajeServidorCaido))
                          .show();
              }


            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.txtMensajeConexion))
                        .show();
            }
        });
    }
    private void limpiar() {
        txtDescripcion.setText("");
        txtDireccFact.setText("");
        txtDireccFact.setEnabled(true);
        txtCodCliente.setText("");
        txtDniRuc.setText("");
    }



    public void consultarDocumento(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        progressConsultaDoc = new MyDialogProgress();
        progressConsultaDoc.show(ft, "dialog");
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("nroDoc", txtDniRuc.getText().toString());
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().datosAltaCliente(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {

                if(response.body()!=null){
                    if(response.body().getEstado()==1){
                        ArrayList objeto= (ArrayList) response.body().getItem();
                        if(txtDniRuc.getText().length()==11){
                            txtDireccFact.setEnabled(false);
                            txtDireccFact.setText(objeto.get(7).toString());
                            txtCodCliente.setText(txtDniRuc.getText().toString());
                            txtDescripcion.setText(objeto.get(0).toString().substring(13));
                            spinnerDpto.requestFocus();
                        }else{
                            txtDireccFact.setEnabled(true);
                            txtCodCliente.setText("DNI"+txtDniRuc.getText().toString());
                            txtDescripcion.setText(objeto.get(0).toString());
                            txtDireccFact.requestFocus();
                        }
                    }else if(response.body().getEstado()==-2){//activo
                        String codCliente="";
                        if(txtDniRuc.getText().length()==11){
                            codCliente=txtDniRuc.getText().toString();
                        }else{
                            codCliente="DNI"+txtDniRuc.getText().toString();
                        }
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        myDialogAlertaCliente= MyDialogAlertaCliente.newInstance(listaRutas,response.body().getMensaje());
                        Bundle args=getArguments();
                        args.putString(CLIENTEENUM.CODCLIENTE.getClave(),codCliente);
                        args.putBoolean("clienteActivo", true);
                        myDialogAlertaCliente.setArguments(args);
                        myDialogAlertaCliente.show(ft, "dialog");


                    }else if(response.body().getEstado()==-3){//inactivo
                        String codCliente="";
                        if(txtDniRuc.getText().length()==11){
                            codCliente=txtDniRuc.getText().toString();


                        }else{
                            codCliente="DNI"+txtDniRuc.getText().toString();
                        }
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        myDialogAlertaCliente= MyDialogAlertaCliente.newInstance(listaRutas,response.body().getMensaje());
                        Bundle args=getArguments();
                        args.putString(CLIENTEENUM.CODCLIENTE.getClave(),codCliente);
                        args.putBoolean("clienteActivo", false);
                        myDialogAlertaCliente.setArguments(args);
                        myDialogAlertaCliente.show(ft, "dialog");


                    }
                    //validar();
                    progressConsultaDoc.dismiss();
                }else{
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.txtMensajeServidorCaido))
                            .show();
                    progressConsultaDoc.dismiss();
                }


            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.txtMensajeConexion))
                        .show();
                progressConsultaDoc.dismiss();
            }
        });
    }
    public Dialog onCreateDialog(int id) {
        if (id == 999) {
            DatePickerDialog dpD= new DatePickerDialog(getActivity(),R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int yyyy, int mm, int dd) {
                    txtFechaNacimiento.setText( getFormatoFecha(yyyy,mm,dd));
                }
            }, year, month, day);

            dpD.getDatePicker().getTouchables().get(0).performClick();
            return  dpD;
        }
        return null;
    }
    public String getFormatoFecha(int yyyy,int mm,int dd) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            //date = dateFormat.parse(dd + "-" + (mm + 1) + "-" + yyyy);
            date = dateFormat.parse(yyyy+ "-" + (mm + 1) + "-" + dd );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outDate = dateFormat.format(date);

        return outDate;
    }
    public void inicializarFechaHoy(){
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -18);
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        txtFechaNacimiento.setText(getFormatoFecha(year,month,day));
    }

    public void registrarCliente(final ClienteAlta clientealta){

        clientealta.setOperacion(1);
        Call<JsonRespuesta<Integer>> call = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().registrarCliente(clientealta);
        call.enqueue(new Callback<JsonRespuesta<Integer>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<Integer>> call, Response<JsonRespuesta<Integer>> response) {

                if(response.body()!=null){
                    if(response.body().getEstado()==-1){
                        dialogRegistrar.dismiss();
                        Toast.makeText(getContext(),response.body().getMensaje(),Toast.LENGTH_LONG).show();
                        Snackbar.make(getView(), response.body().getMensaje(), Snackbar.LENGTH_INDEFINITE).setAction("Cerrar", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        }).show();

                    }else{
                        if(response.body().getEstado()==-2) {

                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            myDialogAlertaCliente = new MyDialogAlertaCliente();
                            myDialogAlertaCliente.getArguments().putString(CLIENTEENUM.CODCLIENTE.getClave(), clientealta.getCodCliente());
                            myDialogAlertaCliente.getArguments().putBoolean("clienteActivo", true);
                            myDialogAlertaCliente.show(ft, "dialog");
                        } else if(response.body().getEstado()==-3){
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            myDialogAlertaCliente= new MyDialogAlertaCliente();
                            myDialogAlertaCliente.getArguments().putString(CLIENTEENUM.CODCLIENTE.getClave(),clientealta.getCodCliente());
                            myDialogAlertaCliente.getArguments().putBoolean("clienteActivo", false);
                            myDialogAlertaCliente.show(ft, "dialog");

                        }else{

                            Toast.makeText(getContext(), "Se registró el cliente correctamente " , Toast.LENGTH_LONG).show();
                            dialogRegistrar.dismiss();

                            Intent intent = new Intent(getActivity(), ClienteActivity.class);
                            intent.putExtra("newRegistro",1);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                }else{
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.txtMensajeServidorCaido))
                            .show();
                    dialogRegistrar.dismiss();
                }


            }

            @Override
            public void onFailure(Call<JsonRespuesta<Integer>> call, Throwable t) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.txtMensajeConexion))
                        .show();
                dialogRegistrar.dismiss();


            }
        });
    }
    public void showDialog(final ClienteAlta clientealta, final String descLista,final String descNegocio) {
        new AlertDialog.Builder(getContext())
                .setTitle("CONFIRMACION")
                .setMessage("Desea registrar Cliente?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        dialogRegistrar = new MyDialogProgress();
                        dialogRegistrar.show(ft, "dialog");
                        new Handler().postDelayed(new Runnable() {
                            @Override public void run() {
                                registrarCliente(clientealta);
                            }
                        }, 1000);
                    }
                }).setNegativeButton(android.R.string.no, null).show();
    }
    public void mostrarIconoClear(){
        Drawable errorIcon = getResources().getDrawable(android.R.drawable.ic_search_category_default);
        errorIcon.setBounds(new Rect(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight()));
        txtDniRuc.setError(null,errorIcon);
    }
    private boolean validar() {
        boolean band = true;
        if (txtDniRuc.getText().toString().equals("")) {
            txtDniRuc.setError(Html.fromHtml("<font color='red'>Ingrese numero doc.</font>"));
           // txtDniRuc.requestFocus();

            band = false;
        } else {
            if(txtDniRuc.getText().toString().length()==8||txtDniRuc.getText().toString().length()==11){
                txtDniRuc.setError(null);
            }else{
                txtDniRuc.setError(Html.fromHtml("<font color='red'>NÚMERO DE DOC. INVALIDO</font>"));
                //txtDniRuc.requestFocus();
                band = false;
            }
        }


        if (spinnerDpto.getText().toString().equals("")) {
            spinnerDpto.setError(Html.fromHtml("<font color='red'>Ingrese Departamento.</font>"));
           // spinnerDpto.requestFocus();
            band = false;
        } else {
            spinnerDpto.setError(null);
        }
        if (spinnerProv.getText().toString().equals("")) {
            spinnerProv.setError(Html.fromHtml("<font color='red'>Ingrese Provincia.</font>"));
           // spinnerProv.requestFocus();
            band = false;
        } else {
            spinnerProv.setError(null);
        }
        if (spinnerDist.getText().toString().equals("")) {
            spinnerDist.setError(Html.fromHtml("<font color='red'>Ingrese Distrito.</font>"));
          //  spinnerDist.requestFocus();
            band = false;
        } else {
            spinnerDist.setError(null);
        }

        if (txtCoordenadas.getText().toString().equals("") && sessionUsuario.getPaqueteUsuario().getIgnoreGps().equals(0)) {
            txtCoordenadas.setError(Html.fromHtml("<font color='red'>Capture coordenadas.</font>"));
          //  txtCoordenadas.requestFocus();
            band = false;
        } else {
            txtCoordenadas.setError(null);
        }

        if (txtDireccFact.getText().toString().equals("")) {
            txtDireccFact.setError(Html.fromHtml("<font color='red'>Ingrese Dirección.</font>"));
          //  txtDireccFact.requestFocus();
            band = false;
        } else {
            txtDireccFact.setError(null);
        }

        if (txtCodCliente.getText().toString().equals("")) {
            txtCodCliente.setError(Html.fromHtml("<font color='red'>Codigo necesario</font>"));
            //  txtDireccFact.requestFocus();
            band = false;
        } else {
            txtCodCliente.setError(null);
        }
//        if (txtReferencia.getText().toString().equals("")) {
//            txtReferencia.setError(Html.fromHtml("<font color='red'>Ingrese Referencia.</font>"));
//           // txtReferencia.requestFocus();
//            band = false;
//        } else {
//            txtReferencia.setError(null);
//        }


        return band;

    }
    private boolean validarDoc() {
        boolean band = true;
        if (txtDniRuc.getText().toString().equals("")) {
            txtDniRuc.setError(Html.fromHtml("<font color='red'>Ingrese numero doc.</font>"));
            band = false;
        } else {
            if(txtDniRuc.getText().toString().length()==8||txtDniRuc.getText().toString().length()==11){
                txtDniRuc.setError(null);
            }else{
                txtDniRuc.setError(Html.fromHtml("<font color='red'>NÚMERO DE DOC. INVALIDO</font>"));
                band = false;
            }
        }


        return band;

    }

    private void checkMyPermissionLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //Permission Check
            PermissionUtils.requestPermission(getActivity());
        } else {

            initGoogleMapLocation();

        }
    }
    private void initGoogleMapLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(getActivity());
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                super.onLocationResult(result);
                //mCurrentLocation = locationResult.getLastLocation();
                mCurrentLocation = result.getLocations().get(0);
                if(mCurrentLocation!=null)
                {
                    Log.e("Location(Lat)==",""+mCurrentLocation.getLatitude());
                    Log.e("Location(Long)==",""+mCurrentLocation.getLongitude());
                }
                //  btnMapa.setLoading(false);

                txtCoordenadas.setText(mCurrentLocation.getLatitude()+","+mCurrentLocation.getLongitude());
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                MyDialogMapa newFragment = MyDialogMapa.newInstance();
                Bundle args=new Bundle();
                newFragment.setEditDialogListener(listener);
                args.putString("latitud",mCurrentLocation.getLatitude()+"");
                args.putString("longitud",mCurrentLocation.getLongitude()+"");
                newFragment.setArguments(args);
                newFragment.show(ft, "find");
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }

            //Locatio nMeaning that all relevant information is available
            @Override
            public void onLocationAvailability(LocationAvailability availability) {
                //boolean isLocation = availability.isLocationAvailable();
            }
        };
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        //To get location information only once here
        mLocationRequest.setNumUpdates(3);

        LocationManager mListener = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(mListener != null){
            isGPSLocation = mListener.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkLocation = mListener.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }


        if (isGPSLocation) {
            //Accuracy is a top priority regardless of battery consumption
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }else if(isNetworkLocation){
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }else{
            //Device location is not set
            PermissionUtils.LocationSettingDialog a=   PermissionUtils.LocationSettingDialog.newInstance();
            a.show(getFragmentManager(), "Setting");
            a.setCancelable(false);

        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        /**
         * Stores the type of location service the client wants to use. Also used for positioning.
         */
        LocationSettingsRequest mLocationSettingsRequest = builder.build();

        Task<LocationSettingsResponse> locationResponse = mSettingsClient.checkLocationSettings(mLocationSettingsRequest);
        locationResponse.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.e("Response", "Successful acquisition of location information!!");
                //
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            }
        });
        //When the location information is not set and acquired, callback
        locationResponse.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.e("onFailure", "Location environment check");
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Check location setting";
                        Log.e("onFailure", errorMessage);
                }
            }
        });
    }

    @Override
    public void updateResult(String inputText) {
        txtCoordenadas.setText(inputText);
    }
    private void toUpper() {
        txtDireccFact.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }
            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase()))
                {
                    s=s.toUpperCase();
                    txtDireccFact.setText(s);
                }
                txtDireccFact.setSelection(txtDireccFact.getText().length());
            }
        });
    }
    @Override
    public void showButton() {
        btnCapturar.setLoading(false);
        //validar();
        imgMap.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle( Html.fromHtml("<font color='#FFFFFF'>"
                + "REGISTRAR CLIENTE"
                + "</font>"));
        btnCapturar.setLoading(false);
        txtCoordenadas.setText("");
        imgMap.setVisibility(View.VISIBLE);
        // checkMyPermissionLocation();
    }

    public  void limpiarTablaClientes(){
        try {
            ApiSqlite.getInstance( getContext()).getOperacionesBaseDatos().getDb().beginTransaction();

            ApiSqlite.getInstance(getContext()).getOperacionesBaseDatos().deleteTablaClientes();

            ApiSqlite.getInstance(getContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
        } finally {
            ApiSqlite.getInstance(getContext()).getOperacionesBaseDatos().getDb().endTransaction();
        }
    }
}
