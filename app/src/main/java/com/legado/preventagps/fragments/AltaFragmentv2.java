package com.legado.preventagps.fragments;

import android.Manifest;
import android.app.Activity;
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
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.legado.preventagps.R;
import com.legado.preventagps.activities.vendedor.BaseActivity;
import com.legado.preventagps.activities.vendedor.ClienteActivity;
import com.legado.preventagps.activities.vendedor.LoginActivity;
import com.legado.preventagps.activities.vendedor.MapPlacesActivity;
import com.legado.preventagps.adapter.vendedor.AutoCompleteSimpleAdapter;
import com.legado.preventagps.adapter.vendedor.SpinnerSimpleAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.dialogs.MyDialogAlertaCliente;
import com.legado.preventagps.dialogs.MyDialogClienteSuccess;
import com.legado.preventagps.dialogs.MyDialogMapa;
import com.legado.preventagps.dialogs.MyDialogProgress;
import com.legado.preventagps.enums.CLIENTEENUM;
import com.legado.preventagps.json.JsonRespuesta;
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
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AltaFragmentv2 extends Fragment implements MyDialogMapa.EditDialogListener {
    @BindView(R.id.txtDniRuc)
     EditText txtDniRuc;
    @BindView(R.id.txtDescCliente)
    EditText txtDescCliente;


    @BindView(R.id.txtDireccionFacturacionRuc)
    EditText txtDireccionFacturacionRuc;

    //estp es par dni
    @BindView(R.id.spinnerDirFormato1)
    Spinner spinnerDirFormato1;
    @BindView(R.id.txtDireccion1)
    EditText txtDireccion1;
    @BindView(R.id.txtDireccion5)
    EditText txtDireccion5;

    @BindView(R.id.txtDireccion2)
    EditText txtDireccion2;
    @BindView(R.id.txtDireccion3)
    EditText txtDireccion3;
    @BindView(R.id.txtDireccion4)
    EditText txtDireccion4;
    @BindView(R.id.spinnerDirFormato2)
    Spinner spinnerDirFormato2;
    SessionUsuario sessionUsuario;
    @BindView(R.id.txtDireccion6)
    EditText txtDireccion6;



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

    @BindView(R.id.txtEmail)
    EditText txtEmail;
    @BindView(R.id.spinnerTipoCliente)
    Spinner spinnerTipoCliente;
    @BindView(R.id.spinnerGiroNegocio)
    Spinner spinnerGiroNegocio;
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
    MaterialButton btnRegistrar;



    @BindView(R.id.txtGeolocalizar)
    TextView txtGeolocalizar;
    @BindView(R.id.txtTelefono)
    EditText txtTelefono;
    @BindView(R.id.txtCelular)
    EditText txtCelular;
    @BindView(R.id.contenedorDireccionRuc)
    RelativeLayout contenedorDireccionRuc;

    @BindView(R.id.btnConsultarDniRuc)
    ImageButton btnConsultarDniRuc;


    @BindView(R.id.contenedorDireccionDni)
    LinearLayout contenedorDireccionDni;

    MyDialogProgress dialogRegistrar;
    MyDialogProgress progressConsulta;
    MyDialogProgress progressConsultaDoc;
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

    int LAUNCH_SECOND_ACTIVITY = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_altav2, container, false);
        ButterKnife.bind(this, rootView);
        getActivity().setTitle( Html.fromHtml("<font color='#FFFFFF'>"
                + "REGISTRAR CLIENTE"
                + "</font>"));

        sessionUsuario=new SessionUsuario(getContext());
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

//        txtDniRuc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(final View view, boolean hasFocus) {
//                if (hasFocus) {
//                    limpiar();
//                }
//            }
//        });

        btnConsultarDniRuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarDoc()){
                    consultarDocumento();
                }
            }
        });
//
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
                        clienteAlta.setDireccion(txtDireccionFacturacionRuc.getText().toString());
                    }else{
                        clienteAlta.setDni(txtDniRuc.getText().toString());
                        String formFinal="";
                        String dirFinal="";
                        if(!txtDireccion1.getText().toString().isEmpty()){
                            ArrayList form1= (ArrayList) spinnerDirFormato1.getSelectedItem();
                            formFinal=formFinal+"_"+form1.get(0).toString();
                            dirFinal=dirFinal+"_"+txtDireccion1.getText();
                        }else{
                            formFinal=formFinal+"_"+"XXXX";
                            dirFinal=dirFinal+"_"+"XXXX";
                        }
                        if(!txtDireccion2.getText().toString().isEmpty()){
                            formFinal=formFinal+"_"+"NRO.";
                            dirFinal=dirFinal+"_"+txtDireccion2.getText();
                        }else{
                            formFinal=formFinal+"_"+"XXXX";
                            dirFinal=dirFinal+"_"+"XXXX";
                        }
                        if(!txtDireccion3.getText().toString().isEmpty()){
                            formFinal=formFinal+"_"+"MZ.";
                            dirFinal=dirFinal+"_"+txtDireccion3.getText();
                        }else{
                            formFinal=formFinal+"_"+"MZ.";
                            dirFinal=dirFinal+"_"+"XXXX";
                        }
                        if(!txtDireccion4.getText().toString().isEmpty()){
                            formFinal=formFinal+"_"+"LT.";
                            dirFinal=dirFinal+"_"+txtDireccion4.getText();
                        }else{
                            formFinal=formFinal+"_"+"LT.";
                            dirFinal=dirFinal+"_"+"XXXX";
                        }

                        if(!txtDireccion5.getText().toString().isEmpty()){
                            formFinal=formFinal+"_"+"HU.";
                            dirFinal=dirFinal+"_"+txtDireccion5.getText();
                        }else{
                            formFinal=formFinal+"_"+"HU.";
                            dirFinal=dirFinal+"_"+"XXXX";
                        }

                        if(!txtDireccion6.getText().toString().isEmpty()){
                            ArrayList form2= (ArrayList) spinnerDirFormato2.getSelectedItem();
                            formFinal=formFinal+"_"+form2.get(0).toString();
                            dirFinal=dirFinal+"_"+txtDireccion6.getText();
                        }else{
                            formFinal=formFinal+"_"+"XXXX";
                            dirFinal=dirFinal+"_"+"XXXX";
                        }


                        clienteAlta.setFormatoDireccion(formFinal);
                        clienteAlta.setNewDirecion(dirFinal);

                    }
                    clienteAlta.setDescripcion(txtDescCliente.getText().toString());
                    clienteAlta.setCodCanal(sessionUsuario.getPaqueteUsuario().getCodCanal());
                    ArrayList tc= (ArrayList) spinnerTipoCliente.getSelectedItem();
                    clienteAlta.setCodTipoCliente(tc.get(0).toString());
                    ArrayList giro= (ArrayList) spinnerGiroNegocio.getSelectedItem();
                    clienteAlta.setCodGiroNegocio(giro.get(0).toString());
                    clienteAlta.setUbigeo(sessionUsuario.getPaqueteAlta().getCodDist());

                    clienteAlta.setReferencia(txtReferencia.getText().toString());

                    int selectedId = opciones_sexo.getCheckedRadioButtonId();
                    RadioButton radioButton = (RadioButton) rootView.findViewById(selectedId);
                    clienteAlta.setSexo(radioButton.getText().toString());
                    clienteAlta.setFechaNacimiento(txtFechaNacimiento.getText().toString());
                    clienteAlta.setEmail(txtEmail.getText().toString());
                     clienteAlta.setTelefonoFijo(txtTelefono.getText().toString());
                     clienteAlta.setCelular(txtCelular.getText().toString());
                    clienteAlta.setUsuarioRegistra(sessionUsuario.getUsuario());
                    ArrayList lp= (ArrayList) spinnerLp.getSelectedItem();
                    clienteAlta.setCodLp(lp.get(0).toString());


                    LocalCliente localCliente=new LocalCliente();
                    localCliente.setCodLocal("1");
                    localCliente.setDescLocal(txtDescCliente.getText().toString());
                   // localCliente.setDireccion(txtDireccFact.getText().toString());
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

        txtGeolocalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(getActivity(), MapPlacesActivity.class);
               // i.putExtra("direccion",txtDireccOld.getText().toString());
                startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
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
                        ArrayList listaTipos= null;//(ArrayList) lista.get(2);
                        ArrayList listaLp= (ArrayList) lista.get(3);
                        listaRutas= (ArrayList) lista.get(4);
                        ArrayList listaRptaFormDir = (ArrayList) lista.get(5);


                        ArrayList forms1 = new ArrayList<>();
                        ArrayList forms2 = new ArrayList<>();
                        for (int i = 0; i < listaRptaFormDir.size(); i++) {
                            ArrayList form = (ArrayList) listaRptaFormDir.get(i);
                            if (form.get(2).toString().equals("1.0")) {
                                ArrayList obj = new ArrayList();
                                obj.add(form.get(0).toString());
                                obj.add(form.get(1).toString());

                                forms1.add(obj);
                            } else if (form.get(2).toString().equals("2.0")) {
                                ArrayList obj = new ArrayList();
                                obj.add(form.get(0).toString());
                                obj.add(form.get(1).toString());
                                forms2.add(obj);
                            }

                        }

                        cargarSpinnersFormato1(forms1,"");
                        cargarSpinnersFormato2(forms2,"");


                        AutoCompleteSimpleAdapter autoCompleteSimpleAdapter = new AutoCompleteSimpleAdapter(getContext(), R.layout.list_simplespinner_item, listaDptos);
                        spinnerDpto.setAdapter(autoCompleteSimpleAdapter);


                        SpinnerSimpleAdapter lpAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, listaLp);
                        lpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerLp.setAdapter(lpAdapter);

                        SpinnerSimpleAdapter rutaAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, listaRutas);
                        rutaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerRuta.setAdapter(rutaAdapter);


                        ArrayList listaRptaGirosMod=new ArrayList();
                        if(listaTipos==null){// SI TIPO ES NULO
                            ArrayList itemSeleccione=new ArrayList();
                            itemSeleccione.add("XXXX");
                            itemSeleccione.add("-SELECCIONE GIRO NEGOCIO-");
                            listaRptaGirosMod.add(itemSeleccione);
                            for (int i = 0; i <listaGiros.size() ; i++) {
                                listaRptaGirosMod.add(listaGiros.get(i));
                            }
                        }else{
                            listaRptaGirosMod.addAll(listaGiros);
                        }


                        SpinnerSimpleAdapter giroAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, listaRptaGirosMod);
                        giroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerGiroNegocio.setAdapter(giroAdapter);

                        spinnerGiroNegocio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                ArrayList a = (ArrayList) spinnerGiroNegocio.getSelectedItem();//(Object[]) adapterView.getItemAtPosition(i);
                                if(a.get(0).toString().equals("XXXX")){
                                    cargarSpinnerTipos("","");
                                }else{
                                    cargarSpinnerTipos(a.get(0).toString(),"");
                                }

                            }

                            public void onNothingSelected(AdapterView<?> adapterView) {
                                return;
                            }
                        });

                        progressConsulta.dismiss();







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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("coordenadas");
                txtCoordenadas.setText(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    private void cargarSpinnersFormato1(ArrayList listaFormato1, String codValor) {
        SpinnerSimpleAdapter formato1Adapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, listaFormato1);
        spinnerDirFormato1.setAdapter(formato1Adapter);

        for(int j=0;j<listaFormato1.size();j++){
            ArrayList form1 = (ArrayList) listaFormato1.get(j);
            if(form1.get(0).equals(codValor)){
                spinnerDirFormato1.setSelection(j);
                break;
            }
        }
    }
    private void cargarSpinnersFormato2(ArrayList listaFormato2, String codValor) {
        SpinnerSimpleAdapter formato1Adapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, listaFormato2);
        spinnerDirFormato2.setAdapter(formato1Adapter);
        for(int j=0;j<listaFormato2.size();j++){
            ArrayList form2 = (ArrayList) listaFormato2.get(j);
            if(form2.get(0).equals(codValor)){
                spinnerDirFormato2.setSelection(j);
                break;
            }
        }
    }

    private void cargarSpinnerTipos(String codGiro,String codTipo){
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("codGiro", codGiro);
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().getTiposByGiro(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if (response.body().getEstado() == 1) {
                    ArrayList lista = (ArrayList) response.body().getData();



                    ArrayList listaTiposMod=new ArrayList();
                    ArrayList itemSeleccione=new ArrayList();
                    itemSeleccione.add("XXXX");
                    itemSeleccione.add("-SELECCIONE TIPO" +
                            "CLIENTE-");
                    listaTiposMod.add(itemSeleccione);
                    listaTiposMod.addAll(lista);

                    SpinnerSimpleAdapter autoCompleteSimpleAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, listaTiposMod);
                    spinnerTipoCliente.setAdapter(autoCompleteSimpleAdapter);
                    for(int j=0;j<listaTiposMod.size();j++){
                        ArrayList tipo =  ((ArrayList) listaTiposMod.get(j));
                        if(tipo.get(0).equals(codTipo)){
                            spinnerTipoCliente.setSelection(j);
                            break;
                        }
                    }


                }
            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
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
        txtDescCliente.setText("");
//        txtDireccFact.setText("");
//        txtDireccFact.setEnabled(true);
//        txtCodCliente.setText("");
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
                            txtDireccionFacturacionRuc.setEnabled(false);
                            txtDireccionFacturacionRuc.setText(objeto.get(7).toString());
                           txtCodCliente.setText(txtDniRuc.getText().toString());
                            txtDescCliente.setText(objeto.get(0).toString().substring(13));
                            spinnerDpto.requestFocus();
                            contenedorDireccionRuc.setVisibility(View.VISIBLE);
                            contenedorDireccionDni.setVisibility(View.GONE);
                        }else{
                            contenedorDireccionRuc.setVisibility(View.GONE);
                            contenedorDireccionDni.setVisibility(View.VISIBLE);
                            txtDireccionFacturacionRuc.setText("");
                            txtCodCliente.setText("DNI"+txtDniRuc.getText().toString());
                            txtDescCliente.setText(objeto.get(0).toString());
                           //xtDireccFact.requestFocus();
                        }
                    }else if(response.body().getEstado()==-2||response.body().getEstado()==-4){//activo
                        String codCliente="";
                        if(txtDniRuc.getText().length()==11){
                            codCliente=txtDniRuc.getText().toString();
                        }else{
                            codCliente="DNI"+txtDniRuc.getText().toString();
                        }
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        myDialogAlertaCliente= MyDialogAlertaCliente.newInstance(listaRutas,response.body().getMensaje());
                        Bundle args=new Bundle();
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
                        Bundle args=new Bundle();
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
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Confirmación")
                .setContentText("Desea registrar Cliente?")
                .setConfirmText("SI")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        dialogRegistrar = new MyDialogProgress();
                        dialogRegistrar.show(ft, "dialog");
                        new Handler().postDelayed(new Runnable() {
                            @Override public void run() {
                                registrarCliente(clientealta);
                            }
                        }, 1000);

                    }
                })
                .setCancelButton("NO", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
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
            band = false;
        } else {
            if(txtDniRuc.getText().toString().length()==8||txtDniRuc.getText().toString().length()==11){
                txtDniRuc.setError(null);
            }else{
                txtDniRuc.setError(Html.fromHtml("<font color='red'>NÚMERO DE DOC. INVALIDO</font>"));
                band = false;
            }
        }

        if (txtDescCliente.getText().toString().equals("")) {
            txtDescCliente.setError(Html.fromHtml("<font color='red'>Descripción vacía</font>"));
            band = false;
        } else {

            txtDescCliente.setError(null);
        }


        if(txtCelular.getText().toString().length()==9){
            txtCelular.setError(null);
        }else{
            txtCelular.setError(Html.fromHtml("<font color='red'>NÚMERO DE CELULAR INVALIDO</font>"));
            band = false;
        }



    if(!txtEmail.getText().toString().trim().isEmpty()){

        if (!validarEmail(txtEmail.getText().toString())){
            txtEmail.setError("Email no válido");
            band = false;
        }else{
            txtEmail.setError(null);
        }
    }






        if (spinnerDpto.getText().toString().equals("")) {
            spinnerDpto.setError(Html.fromHtml("<font color='red'>Ingrese Departamento.</font>"));
            band = false;
        } else {
            spinnerDpto.setError(null);
        }
        if (spinnerProv.getText().toString().equals("")) {
            spinnerProv.setError(Html.fromHtml("<font color='red'>Ingrese Provincia.</font>"));
            band = false;
        } else {
            spinnerProv.setError(null);
        }
        if (spinnerDist.getText().toString().equals("")) {
            spinnerDist.setError(Html.fromHtml("<font color='red'>Ingrese Distrito.</font>"));
            band = false;
        } else {
            spinnerDist.setError(null);
        }

        if (txtCoordenadas.getText().toString().equals("") && sessionUsuario.getPaqueteUsuario().getIgnoreGps().equals(0)) {
            txtCoordenadas.setError(Html.fromHtml("<font color='red'>Capture coordenadas.</font>"));
             band = false;
        } else {
            txtCoordenadas.setError(null);
        }



        ArrayList giro= (ArrayList) spinnerGiroNegocio.getSelectedItem();
        LinearLayout lgiro = (LinearLayout) spinnerGiroNegocio.getSelectedView(); // get the parent layout view
        TextView tvgiro = (TextView) lgiro.getChildAt(0); // get the child text view
        if (giro.get(0).toString().equals("XXXX")) {
            tvgiro.setError(Html.fromHtml("<font color='red'>Seleccione Giro</font>"));
            band = false;
        } else {
            tvgiro.setError(null);
        }


        ArrayList tipo= (ArrayList) spinnerTipoCliente.getSelectedItem();
        LinearLayout ltipo = (LinearLayout) spinnerTipoCliente.getSelectedView(); // get the parent layout view
        TextView tvtipo= (TextView) ltipo.getChildAt(0); // get the child text view
        if (tipo.get(0).toString().equals("XXXX")) {
            tvtipo.setError(Html.fromHtml("<font color='red'>Seleccione Tipo cliente</font>"));
            band = false;
        } else {
            tvtipo.setError(null);
        }

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
    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
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

             @Override
            public void onLocationAvailability(LocationAvailability availability) {
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

        txtDireccion1.addTextChangedListener(new TextWatcher() {
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
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    txtDireccion1.setText(s);
                }
                txtDireccion1.setSelection(txtDireccion1.getText().length());
            }
        });
        txtDireccion2.addTextChangedListener(new TextWatcher() {
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
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    txtDireccion2.setText(s);
                }
                txtDireccion2.setSelection(txtDireccion2.getText().length());
            }
        });
        txtDireccion3.addTextChangedListener(new TextWatcher() {
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
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    txtDireccion3.setText(s);
                }
                txtDireccion3.setSelection(txtDireccion3.getText().length());
            }
        });
        txtDireccion4.addTextChangedListener(new TextWatcher() {
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
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    txtDireccion4.setText(s);
                }
                txtDireccion4.setSelection(txtDireccion4.getText().length());
            }
        });
        txtDireccion5.addTextChangedListener(new TextWatcher() {
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
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    txtDireccion5.setText(s);
                }
                txtDireccion5.setSelection(txtDireccion5.getText().length());
            }
        });
        txtDireccion6.addTextChangedListener(new TextWatcher() {
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
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    txtDireccion6.setText(s);
                }
                txtDireccion6.setSelection(txtDireccion6.getText().length());
            }
        });
    }
    @Override
    public void showButton() {
//        btnCapturar.setLoading(false);
//        imgMap.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle( Html.fromHtml("<font color='#FFFFFF'>"
                + "REGISTRAR CLIENTE"
                + "</font>"));
//        btnCapturar.setLoading(false);
//        txtCoordenadas.setText("");
//        imgMap.setVisibility(View.VISIBLE);
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
