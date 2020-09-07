package com.legado.preventagps.dialogs;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.legado.preventagps.R;

import com.legado.preventagps.activities.vendedor.InicioActivity;
import com.legado.preventagps.activities.vendedor.MapPlacesActivity;
import com.legado.preventagps.adapter.vendedor.AutoCompleteSimpleAdapter;
import com.legado.preventagps.adapter.vendedor.SpinnerSimpleAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.LocalCliente;
import com.legado.preventagps.util.PaqueteUtilLocales;
import com.legado.preventagps.util.PermissionUtils;
import com.legado.preventagps.util.SessionUsuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import it.beppi.tristatetogglebutton_library.TriStateToggleButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class MyDialogLocalMapaEditv2 extends DialogFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMyLocationButtonClickListener {
    SessionUsuario sessionUsuario;

    @BindView(R.id.txtCoordenadas)
    EditText txtCoordenadas;
    @BindView(R.id.txtReferencia)
    EditText txtReferencia;

    @BindView(R.id.txtDireccOld)
    EditText txtDireccOld;
    @BindView(R.id.btnActualizarLocal)
    MaterialButton btnActualizarLocal;

    @BindView(R.id.rutasAutoComplete)
    AutoCompleteTextView rutasAutoComplete;
    String codRuta;
    View rootView;
    LatLng newLongitud;

    @BindView(R.id.spinnerDirFormato1)
    Spinner spinnerDirFormato1;
    @BindView(R.id.txtDireccion1)
    EditText txtDireccion1;

    @BindView(R.id.txtDireccion2)
    EditText txtDireccion2;
    @BindView(R.id.txtDireccion3)
    EditText txtDireccion3;
    @BindView(R.id.txtDireccion4)
    EditText txtDireccion4;

    @BindView(R.id.spinnerDirFormato2)
    Spinner spinnerDirFormato2;

    @BindView(R.id.txtDireccion5)
    EditText txtDireccion5;
    @BindView(R.id.txtDireccion6)
    EditText txtDireccion6;

    @BindView(R.id.bt_cerrar)
    ImageButton bt_cerrar;
    @BindView(R.id.goMapaSearch)
    MaterialButton goMapaSearch;
    int LAUNCH_SECOND_ACTIVITY = 1;
boolean onChangeLocation=true;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private Location location;
    private Marker mCurrLocationMarker;//, markerClient;
    private BitmapDescriptor iconUser, iconClient;
    private LatLng latLng, latLngOtro;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private String provider;
    private boolean isNetworkLocation, isGPSLocation;
    private boolean stateMap;
    LatLng latLngYo;
     MyDialogProgress dialogRegistrarLocal;
    private LocalCliente localCliente;
    private String codigoCliente;
    private MyDialogLocalMapaEdit.OnDialogFinishCallback listenerUpdate;
    @BindView(R.id.contenedorDireccionDni)
    LinearLayout contenedorDireccionDni;
    @BindView(R.id.txtGeolocalizar)
    TextView txtGeolocalizar;

    public static MyDialogLocalMapaEditv2 newInstance(LocalCliente localCliente, String codigoCliente, MyDialogLocalMapaEdit.OnDialogFinishCallback listenerUpdate) {
         MyDialogLocalMapaEditv2 fragment = new MyDialogLocalMapaEditv2(localCliente,codigoCliente,listenerUpdate);

        return fragment;
    }


    public MyDialogLocalMapaEditv2(LocalCliente localCliente, String codigoCliente, MyDialogLocalMapaEdit.OnDialogFinishCallback listenerUpdate) {
        this.localCliente=localCliente;
        this.codigoCliente=codigoCliente;
        this.listenerUpdate=listenerUpdate;
    }
    private void loadRutas() {
        Map<String, String> dataConsulta = new HashMap<>();
       dataConsulta.put("usuario", sessionUsuario.getUsuario());
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().getRutasByUsuario(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if (response.body().getEstado() == 1) {
                    ArrayList lista= (ArrayList) response.body().getData();

                     AutoCompleteSimpleAdapter autoCompleteSimpleAdapter = new AutoCompleteSimpleAdapter(getContext(), R.layout.list_simplespinner_item, lista);
                    rutasAutoComplete.setAdapter(autoCompleteSimpleAdapter);
                    rutasAutoComplete.setText(localCliente.getCodRuta()+"-"+localCliente.getDescRuta());
                    codRuta=localCliente.getCodRuta();

                    rutasAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            Object[] item = (Object[]) adapterView.getItemAtPosition(position);

                               codRuta=item[0].toString();

                        }
                    });

                    rutasAutoComplete.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            final int DRAWABLE_LEFT = 0;
                            final int DRAWABLE_TOP = 1;
                            final int DRAWABLE_RIGHT = 2;
                            final int DRAWABLE_BOTTOM = 3;

                            if(event.getAction() == MotionEvent.ACTION_UP) {
                                if(event.getRawX() >= (rutasAutoComplete.getRight() - rutasAutoComplete.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                               rutasAutoComplete.setText("");

                                    return true;
                                }
                            }
                            return false;
                        }
                    });

                } else {

                    CoordinatorLayout coordinatorLayout = (CoordinatorLayout) getView().findViewById(R.id.coordinatorLayout);
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Text", Snackbar.LENGTH_LONG);
                    View view = snackbar.getView();
                    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    snackbar.show();

                }
            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_dialog_local_mapa_editv2, container, false);
        ButterKnife.bind(this, rootView);
        if(codigoCliente.contains("DNI")){
            contenedorDireccionDni.setVisibility(View.VISIBLE);
        }else{
            contenedorDireccionDni.setVisibility(View.GONE);
        }

        toUpper();
         sessionUsuario = new SessionUsuario(getActivity());
         initGoogleAPIClient();  //Init Google API Client
        checkPermissions();

        mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapaClientev2);
        LocationManager mListener = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (mListener != null) {
            isGPSLocation = mListener.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkLocation = mListener.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.e("gps, network", String.valueOf(isGPSLocation + "," + isNetworkLocation));
        }
        if (isGPSLocation) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (isNetworkLocation) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {

            showSettingDialog();
        }


        mapFragment.getMapAsync(this);

        stateMap = false;


        txtDireccOld.setText(localCliente.getDireccion());
        txtReferencia.setText(localCliente.getObservaciones());
        PaqueteUtilLocales pul=sessionUsuario.getPaqueteUtilLocales();


        if(localCliente.getFormatoDireccion()!=null){
            String[] arrForm=localCliente.getFormatoDireccion().split("_");
            String[] arrValForm=localCliente.getNewDirecion().split("_");

            txtDireccion1.setText(arrValForm[1].toString().equals("XXXX")?"":arrValForm[1].toString());

            cargarSpinnersFormato1(pul.getFormato1(), arrForm[1].toString());
            cargarSpinnersFormato2(pul.getFormato2(), arrForm[6].toString());

            txtDireccion2.setText(arrValForm[2].toString().equals("XXXX")?"":arrValForm[2].toString());
            txtDireccion3.setText(arrValForm[3].toString().equals("XXXX")?"":arrValForm[3].toString());
            txtDireccion4.setText(arrValForm[4].toString().equals("XXXX")?"":arrValForm[4].toString());
            txtDireccion5.setText(arrValForm[5].toString().equals("XXXX")?"":arrValForm[5].toString());
            txtDireccion6.setText(arrValForm[6].toString().equals("XXXX")?"":arrValForm[6].toString());

        }else{
            cargarSpinnersFormato1(pul.getFormato1(),"");
            cargarSpinnersFormato2(pul.getFormato2(),"");
        }



        btnActualizarLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar()){
                    LocalCliente lc = new LocalCliente();


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

                    lc.setFormatoDireccion(formFinal);
                    lc.setNewDirecion(dirFinal);
                    lc.setDireccion(txtDireccOld.getText().toString());


                      lc.setCoordenadas(txtCoordenadas.getText().toString());
                       lc.setObservaciones(txtReferencia.getText().toString());
                     lc.setUsuario(sessionUsuario.getUsuario());
                     lc.setCodLocal(localCliente.getCodLocal());
                   lc.setCodCliente(codigoCliente);

             //  lc.setStatus(l);
                   lc.setCodRuta(codRuta);
                    lc.setOperacion(2);
                    Gson gson = new Gson();
                    String lcJson = gson.toJson(lc);
                    Log.d("assas", lcJson);
                  showDialog(lc);


                }

            }
        });

        loadRutas();

        bt_cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

//        goMapaSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent i=new Intent(getActivity(), MapPlacesActivity.class);
//                i.putExtra("direccion",txtDireccOld.getText().toString());
//                startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
//
//            }
//        });
         txtGeolocalizar.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 Intent i=new Intent(getActivity(), MapPlacesActivity.class);
                 i.putExtra("direccion",txtDireccOld.getText().toString());
                 startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);

             }
         });

        return rootView;
    }


    public void showDialog(final LocalCliente localCliente) {

        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Confirmación")
                .setContentText("Desea actualizar el local?")
                .setConfirmText("SI")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        dialogRegistrarLocal = new MyDialogProgress();
                        dialogRegistrarLocal.show(ft, "dialog");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                registrarLocal(localCliente);
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

    private void registrarLocal(LocalCliente localCliente) {
        Call<JsonRespuesta<Integer>> call = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().mantenimientoLocal(localCliente);
        call.enqueue(new Callback<JsonRespuesta<Integer>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<Integer>> call, Response<JsonRespuesta<Integer>> response) {
                if (response.body().getEstado() == -1) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(response.body().getMensaje())
                            .show();
                    dialogRegistrarLocal.dismiss();
                } else {
                    try {
                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().updateAuditarCliente(localCliente.getCodCliente(), localCliente.getCodLocal());
                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                    } finally {
                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                    }

                    Toast.makeText(getContext(), "Se actualizó el local correctamente " , Toast.LENGTH_LONG).show();
                    dialogRegistrarLocal.dismiss();
                    listenerUpdate.refreshRecyclerView();
                    dismiss();


                }
            }

            @Override
            public void onFailure(Call<JsonRespuesta<Integer>> call, Throwable t) {
                dialogRegistrarLocal.dismiss();
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.txtMensajeConexion))
                        .show();

            }
        });
    }

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
            ArrayList form1 = (ArrayList) listaFormato2.get(j);
            if(form1.get(0).equals(codValor)){
                spinnerDirFormato2.setSelection(j);
                break;
            }
        }
    }


    private boolean validar() {
        boolean band = true;

        if (txtReferencia.getText().toString().equals("")) {
            txtReferencia.setError(Html.fromHtml("<font color='red'>Ingrese referencia.</font>"));
            txtReferencia.requestFocus();
            band = false;
        } else {
            txtReferencia.setError(null);
        }

        if (txtCoordenadas.getText().toString().equals("")) {
            txtCoordenadas.setError(Html.fromHtml("<font color='red'>Capture coordenadas.</font>"));
            txtCoordenadas.requestFocus();
            band = false;
        } else {
            txtCoordenadas.setError(null);
        }



        return band;

    }


    private boolean validarCoordenada() {
        boolean band = true;


        if (txtCoordenadas.getText().toString().equals("")) {
            txtCoordenadas.setError(Html.fromHtml("<font color='red'>Capture coordenadas.</font>"));
            txtCoordenadas.requestFocus();
            band = false;
        } else {
            txtCoordenadas.setError(null);
        }



        return band;

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
        txtReferencia.addTextChangedListener(new TextWatcher() {
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
                    txtReferencia.setText(s);
                }
                txtReferencia.setSelection(txtReferencia.getText().length());
            }
        });
    }



    private void initGoogleAPIClient() {
        System.out.println(getActivity());
        if(getActivity()!=null){
            System.out.println(LocationServices.API);
            System.out.println(mGoogleApiClient);

            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();
    }
    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.e("TAG", "SUCCESS");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.e("TAG", "RESOLUTION_REQUIRED");
                        try {
                            if(getActivity()!=null){
                                status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                            }

                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e("TAG", "GPS NO DISPONIBLE");
                        break;
                }
            }
        });
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("TAG", "onMapReady");
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        buildGoogleApiClient();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            } else {
                Toast.makeText(getActivity(), "No cuentas con los permisos necesarios, cierra y abre de nuevo la aplicación", Toast.LENGTH_SHORT).show();
            }
        } else {
            buildGoogleApiClient();
        }



    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected synchronized void buildGoogleApiClient() {
        Log.e("TAG", "buildGoogleApiClient");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.reconnect();
        } else {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(onChangeLocation){
            onChangeLocation=false;
            Log.e("TAG", "onLocationChanged");
            this.location = location;
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
                //   markerClient.remove();
            }
            stateMap=true;//en el caso de que se ejecute el Handler y entre a onLocationChanged va volver verdadero stateMap y no volvera a pedir permisos de GPS
            ///////////////////////////////////////ESTE ES EL MARCADOR DE TU UBICACIÓN ACTUAL///////////////////////////////////////
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Posición Actual");
            markerOptions.icon(iconUser);
            mMap.clear();
            mCurrLocationMarker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));//AQUI MODIFICA EL ZOOM AL MAPA SEGUN TUS NECESIDADES
           // txtCoordenadas.setText(latLng.latitude+","+latLng.longitude);

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    newLongitud = new LatLng(latLng.latitude, latLng.longitude);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(newLongitud));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(newLongitud));
                }
            });


        }


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("TAG", "onConnected");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(9000);
        mLocationRequest.setFastestInterval(9000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        try {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /* Método de permiso On Request para verificar si el permiso se ha otorgado o no a Marshmallow Devices */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("TAG", "onRequestPermissionsResult");
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_INTENT_ID: {
                // Si se cancela la solicitud, las matrices de resultados están vacías.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Si el permiso otorgado muestra el cuadro de diálogo de ubicación si APIClient no es nulo
                    if (mGoogleApiClient == null) {
                        initGoogleAPIClient();
                        showSettingDialog();
                    } else
                        showSettingDialog();


                } else {
                    Toast.makeText(getActivity(), "Location Permission denied.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("TAG", "onConnectionSuspended");
        mGoogleApiClient.connect(i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("TAG", "onConnectionFailed");
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


    @Override
    public boolean onMyLocationButtonClick() {
        mMap.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkMyPermissionLocation();
        } else {
            initGoogleMapLocation();
        }
        return true;

    }
    private void checkMyPermissionLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //Permission Check
            PermissionUtils.requestPermission(getActivity());
        } else {
            //If you're authorized, start setting your location
            initGoogleMapLocation();
        }
    }

    private void initGoogleMapLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(getActivity());
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                super.onLocationResult(result);
                //mCurrentLocation = locationResult.getLastLocation();
                mCurrentLocation = result.getLocations().get(0);
                if (mCurrentLocation != null) {
                    Log.e("Location(Lat)==", "" + mCurrentLocation.getLatitude());
                    Log.e("Location(Long)==", "" + mCurrentLocation.getLongitude());
                }
                latLngYo=new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
               // txtCoordenadas.setText(latLng.latitude+","+latLng.longitude);
//                MarkerOptions options = new MarkerOptions();
//                options.position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
//                BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
//                options.icon(icon);
//                Marker marker = mMap.addMarker(options);
                Marker m = mMap.addMarker(new MarkerOptions().position(latLngYo).title("Mi Ubicación"));
                m.showInfoWindow();

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(m.getPosition(), 10));

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
        mLocationRequest.setNumUpdates(3);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest mLocationSettingsRequest = builder.build();
        Task<LocationSettingsResponse> locationResponse = mSettingsClient.checkLocationSettings(mLocationSettingsRequest);
        locationResponse.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            }
        });
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
    public void onDestroyView() {
        SupportMapFragment f = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.mapaClientev2);
        if (f.isResumed()){
            getFragmentManager().beginTransaction().remove(f).commit();
        }
        super.onDestroyView();

    }
    //Ejecutar en la interfaz de usuario
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            showSettingDialog();
        }
    };
    private BroadcastReceiver gpsLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Si la acción es la ubicación
            if (intent.getAction().matches(BROADCAST_ACTION)) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                //Compruebe si el GPS está encendido o apagado
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.e("About GPS", "GPS is Enabled in your device");
                } else {
                    //Si el GPS está apagado, muestre el diálogo de ubicación
                    new Handler().postDelayed(sendUpdatesToUI, 10);
                    Log.e("About GPS", "GPS is Disabled in your device");
                    showSettingDialog();
                    //editDialogClientHolderListener.showButtonGeolocalizar();
                    dismiss();
                }

            }

        }
    };

    public interface CallbackResult {
        void sendResult(int requestCode, Object obj);
    }

    public interface OnDialogFinishCallback
    {
        void refreshRecyclerView();
    }

}
