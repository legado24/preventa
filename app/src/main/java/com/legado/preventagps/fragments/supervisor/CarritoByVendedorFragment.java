package com.legado.preventagps.fragments.supervisor;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.legado.preventagps.R;
import com.legado.preventagps.activities.supervisor.ContenedorByVendedorActivity;
import com.legado.preventagps.adapter.supervisor.CarritoByVendedorRecyclerAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.dialogs.MyDialogInfo;
import com.legado.preventagps.enums.PREVENTAENUM;
import com.legado.preventagps.enums.STATUSSINCRONIZACION;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.CarritoCompras;
import com.legado.preventagps.modelo.PreVenta;
import com.legado.preventagps.util.PermissionUtils;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.UtilAndroid;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarritoByVendedorFragment extends Fragment implements ContenedorByVendedorActivity.ContenedorActivityListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,GoogleMap.OnMyLocationButtonClickListener {

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";
    private Location location;

    private LatLng latLng;


    @BindView(R.id.recicladorPedido)
    RecyclerView recicladorPedido;
    RecyclerView.LayoutManager lManagerPedido;
    @BindView(R.id.txtMonto)
    TextView txtMonto;
    @BindView(R.id.btnGuardar)
    FloatingActionButton btnGuardar;
    String codCliente;
    String codLocal;
    String codLista;
    String codRuta;
    String descCliente;

    static TextView titleListCountSet;
    SessionUsuario sessionUsuario;
    static Spinner spinnerVendCarrito;
    static Spinner spinnerCondCarrito;
    static Spinner spinnerAlmacenesCarrito;
     ProgressDialog dialogRegistrarVenta;
    MyDialogInfo.DialogInfoListener dialogInfoListener;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private String provider;
    private boolean isNetworkLocation, isGPSLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private String correlativo;


    ContenedorByVendedorActivity.ContenedorActivityListener contenedorActivityListener;

    public void setDialogInfoListener(MyDialogInfo.DialogInfoListener dialogInfoListener) {
        this.dialogInfoListener = dialogInfoListener;
    }

    public static CarritoByVendedorFragment newInstance(TextView titleList, Spinner spinnerVendFind, Spinner spinnerCondFind, Spinner spinnerAlmacenesFind) {

        CarritoByVendedorFragment fragment = new CarritoByVendedorFragment();
        titleListCountSet = titleList;
        spinnerVendCarrito = spinnerVendFind;
        spinnerCondCarrito = spinnerCondFind;
        spinnerAlmacenesCarrito=spinnerAlmacenesFind;
        return fragment;
    }

    Bundle args;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_carrito, container, false);



        contenedorActivityListener=this;
        ContenedorByVendedorActivity.setContenedorActivityListener(contenedorActivityListener);
         args=getArguments();
        codCliente = args.getString("codCliente");
        descCliente=args.getString(PREVENTAENUM.DESCCLIENTE.getClave());
        codLista =args.getString("codLista");
        codLocal = args.getString("codLocal");
        codRuta = args.getString("codRuta");
        correlativo=args.getString("correlativo");
       getActivity().registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));//Registrar el receptor de difusión para comprobar el estado del GPS.

        initGoogleAPIClient();  //Init Google API Client
        checkPermissions();     //Check Permission
        buildGoogleApiClient();


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
//                PermissionUtils.LocationSettingDialog a = PermissionUtils.LocationSettingDialog.newInstance();
//                a.show(getFragmentManager(), "Setting");
//                a.setCancelable(false);
            showSettingDialog();
        }


        ButterKnife.bind(this, rootView);
        sessionUsuario = new SessionUsuario(rootView.getContext());
        dialogRegistrarVenta = new ProgressDialog(getActivity(),R.style.AppTheme_MyDialog);
        dialogRegistrarVenta.setCancelable(false);
        dialogRegistrarVenta.setIndeterminate(true);
        cargarPedidos();
        guardarPedido();
        return rootView;
    }
    CarritoByVendedorRecyclerAdapter detallePedidoRecyclerAdapter;
    public void cargarPedidos() {
        recicladorPedido.setVisibility(View.VISIBLE);
        recicladorPedido.setHasFixedSize(true);
        lManagerPedido = new LinearLayoutManager(getContext());
        recicladorPedido.setLayoutManager(lManagerPedido);
        recicladorPedido.setNestedScrollingEnabled(false);

        detallePedidoRecyclerAdapter = new CarritoByVendedorRecyclerAdapter(sessionUsuario.getPaqueteCarrito().getListaCarrito(), getContext(), getActivity(), this, txtMonto,titleListCountSet,spinnerVendCarrito,spinnerCondCarrito,args,dialogInfoListener,spinnerAlmacenesCarrito);
        recicladorPedido.setAdapter(detallePedidoRecyclerAdapter);
        txtMonto.setText("TOTAL=" + detallePedidoRecyclerAdapter.montoTotalDiario().toString());
        titleListCountSet.setText("LISTA DE ARTICULOS (" + detallePedidoRecyclerAdapter.getItemCount() + ")");
       UtilAndroid.toNegrita(titleListCountSet);
        ;

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
        Log.e("TAG", "onLocationChanged");
        this.location = location;

         latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.e("TAG", latLng.latitude+"-"+latLng.longitude);

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

    public void guardarPedido() {
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));//Registrar el receptor de difusión para comprobar el estado del GPS.

                ArrayList condicion = (ArrayList) spinnerCondCarrito.getSelectedItem();
                ArrayList vend = (ArrayList) spinnerVendCarrito.getSelectedItem();
                ArrayList almacen = (ArrayList) spinnerAlmacenesCarrito.getSelectedItem();
                PreVenta preVenta = new PreVenta();
                preVenta.setCodEmpresa(vend.get(0).toString());
                preVenta.setCodLocalidad(vend.get(8).toString());
                preVenta.setCodAlmacen(almacen.get(0).toString());
                preVenta.setCodMesa(vend.get(1).toString());
                preVenta.setCodSede(vend.get(9).toString());
                preVenta.setCodCanal(vend.get(5).toString());
                preVenta.setCodCliente(codCliente);
                preVenta.setCodLocal(codLocal);
                preVenta.setCodLista(codLista);
                preVenta.setCodCondicion(condicion.get(0).toString());
                preVenta.setCodVendedor(vend.get(7).toString());
                preVenta.setCodRuta(codRuta);
                preVenta.setCodTipoDoc("01");
                preVenta.setUsuarioRegistro(sessionUsuario.getUsuario());
                preVenta.setUid(preVenta.getCodVendedor()+correlativo);
                preVenta.setDetalles(sessionUsuario.getPaqueteCarrito().getListaCarrito());
                if(sessionUsuario.getPaqueteCarrito().getListaCarrito().size()>0){
                    System.out.println("hhhh"+latLng);
                    if(latLng!=null){
                            showDialog(preVenta);
                    }else{

                        if(getArguments().getBoolean(PREVENTAENUM.ISONLINE.getClave())){
                            Snackbar.make(getView(), "DEBE ACEPTAR PERMITIR SU UBICACION  ", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("REINTENTAR", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            showSettingDialog();
                                        }
                                    }).show();
                        }else{
                            latLng=new LatLng(0, 0);// 0,0  cordenada offline
                            showDialog(preVenta);
                        }

                    }


                }else{
                    recicladorPedido.setAdapter(null);
                    detallePedidoRecyclerAdapter.notifyDataSetChanged();
                    sessionUsuario.guardarBandSugerido(true);
                    txtMonto.setText("TOTAL=" +0);
                    titleListCountSet.setText("LISTA DE ARTICULOS (" + 0 + ")");
                    Snackbar.make(getView(), "AGREGUE ARTICULOS PORFAVOR!!", Snackbar.LENGTH_INDEFINITE).setAction("Cerrar", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    }).show();
                }

            }
        });
    }
    private void checkMyPermissionLocation(PreVenta preVenta) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //Permission Check
            PermissionUtils.requestPermission(getActivity());
        } else {
            //initGoogleLocation(preVenta);

                preVenta.setCoordenadas(latLng.latitude+","+latLng.longitude);

                Gson gson=new Gson();

                String preventaJson = gson.toJson(preVenta);
                Log.d("assas", preventaJson);

                if(getArguments().getBoolean(PREVENTAENUM.ISONLINE.getClave())){
                    registrarPreventa(preVenta);
                }else{

                    ArrayList vend = (ArrayList) spinnerVendCarrito.getSelectedItem();
                    ArrayList almacenes = (ArrayList) spinnerAlmacenesCarrito.getSelectedItem();
                    preVenta.setDescLocalidad(vend.get(4).toString());
                    preVenta.setDescAlmacen(almacenes.get(1).toString());
                    preVenta.setDescCliente(descCliente);
                    preVenta.setMontoVenta(new BigDecimal(detallePedidoRecyclerAdapter.montoTotalDiario().toString()));
                    registrarPreventaOffLine(preVenta);

                }

                Log.wtf("COORDENADA REGISTRO",preVenta.getCoordenadas());
                 //dialogRegistrarVenta.dismiss();


        }
    }
    private void initGoogleLocation(final PreVenta preVenta) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(getActivity());
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                super.onLocationResult(result);
                mCurrentLocation = result.getLocations().get(0);
                if(mCurrentLocation!=null)
                {
                    Log.e("Location(Lat)==",""+mCurrentLocation.getLatitude());
                    Log.e("Location(Long)==",""+mCurrentLocation.getLongitude());
                }

                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                LatLng latLngYo  =new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
                preVenta.setCoordenadas(latLngYo.latitude+","+latLngYo.longitude);

                Gson gson=new Gson();

                String preventaJson = gson.toJson(preVenta);
                Log.d("assas", preventaJson);
                registrarPreventa(preVenta);
                Log.wtf("COORDENADA REGISTRO",preVenta.getCoordenadas());
                  //dialogRegistrarVenta.dismiss();
            }

            //Locatio nMeaning that all relevant information is available
            @Override
            public void onLocationAvailability(LocationAvailability availability) {
                //boolean isLocation = availability.isLocationAvailable();
            }
        };
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(4000);
        mLocationRequest.setFastestInterval(4000);
        mLocationRequest.setNumUpdates(4);
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
            a.show(getActivity().getSupportFragmentManager(), "Setting");
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
                        PermissionUtils.LocationSettingDialog a = PermissionUtils.LocationSettingDialog.newInstance();
                        a.show(getActivity().getSupportFragmentManager(), "Setting");
                        a.setCancelable(false);
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Check location setting";
                        Log.e("onFailure", errorMessage);
                }
            }
        });
    }

    public void registrarPreventaUbic(PreVenta preVenta) {
        checkMyPermissionLocation(preVenta);
    }

    public void registrarPreventa(final PreVenta preVenta) {
        Call<JsonRespuesta<Integer>> call = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getVentaService().registrarPedidoLocal(preVenta);
        call.enqueue(new Callback<JsonRespuesta<Integer>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<Integer>> call, Response<JsonRespuesta<Integer>> response) {
                if (response.body().getEstado() == -1) {
                    Toast.makeText(getContext(), response.body().getMensaje() , Toast.LENGTH_LONG).show();

                    Snackbar.make(getView(), response.body().getMensaje(), Snackbar.LENGTH_INDEFINITE).setAction("Cerrar", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    }).show();
                    dialogRegistrarVenta.dismiss();
                }else if (response.body().getEstado() == -2){
                    Toast.makeText(getContext(), response.body().getMensaje() , Toast.LENGTH_LONG).show();
                    Snackbar.make(getView(), response.body().getMensaje(), Snackbar.LENGTH_INDEFINITE).setAction("Cerrar", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //registrarPreventa(preVenta);
                        }
                    }).show();
                    dialogRegistrarVenta.dismiss();

                }else {
//                    List<Cliente> clientes=sessionUsuario.getPaqueteCliente().getClientesSecuenciados();
//                    //Cliente clienteAux=new Cliente();
//                    for (int i = 0; i <clientes.size() ; i++) {
//                        if(clientes.get(i).getCodCliente().equals(codCliente)&&clientes.get(i).getCodLocal().equals(codLocal)){
//                            clientes.get(i).setEstadoJornada("P");
//                             break;
//                        }
//                    }
//                    PaqueteCliente paq=new PaqueteCliente();
//                    paq.setClientesSecuenciados(clientes);
//                    sessionUsuario.guardarPaqueteCliente(paq);

                    try {
                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();

                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().updateClienteEstadoJornada(STATUSSINCRONIZACION.PENDIENTE.getCod(),codCliente,codLocal);

                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                    } finally {
                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                    }
                    Toast.makeText(getContext(), "Se registró el pedido correctamente " , Toast.LENGTH_LONG).show();
                        dialogRegistrarVenta.dismiss();
                    dialogInfoListener.updateRecyclerSuccess(ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().listarClientes(UtilAndroid.fechaDevice("yyyy-MM-dd")));
                    getActivity().unregisterReceiver(gpsLocationReceiver);
                    getActivity().finish();

                }
            }
            @Override
            public void onFailure(Call<JsonRespuesta<Integer>> call, Throwable t) {

                Snackbar.make(getView(), "Problemas de conexion  ", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Cerrar", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                            }
                        }).show();
                dialogRegistrarVenta.dismiss();
                //getActivity().finish();
                }

        });
    }

    public void registrarPreventaOffLine(final PreVenta preVenta){
        try{
            ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
            ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().insertarPedidoLocal(preVenta);
            for (int i = 0; i < preVenta.getDetalles().size(); i++) {
                CarritoCompras c=preVenta.getDetalles().get(i);
                ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().insertarDetallePedidoLocal(c, preVenta.getUid());
            }
            ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().updateClienteEstadoJornada("P",codCliente,codLocal);

            ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
        } finally {
            ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
        }
         Toast.makeText(getContext(), "Se registró el pedido correctamente " , Toast.LENGTH_LONG).show();
        dialogRegistrarVenta.dismiss();
        dialogInfoListener.updateRecyclerSuccess(ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().listarClientes(UtilAndroid.fechaDevice("yyyy-MM-dd")));
        getActivity().unregisterReceiver(gpsLocationReceiver);
        getActivity().finish();
    }
 public void showDialog(final PreVenta preVenta) {
        new AlertDialog.Builder(getContext())
                .setTitle("CONFIRMACION")
                .setMessage(getArguments().getBoolean(PREVENTAENUM.ISONLINE.getClave())?"Desea realizar el pedido?":"RECUERDA QUE ESTAS TRABAJANDO MODO OFFLINE , NO OLVIDES SINCRONIZAR TUS PEDIDOS!!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                      dialogRegistrarVenta.show();
                      registrarPreventaUbic(preVenta);

                    }
                }).setNegativeButton(android.R.string.no, null).show();
    }


    @Override
    public void dismissDialog() {
        if(dialogRegistrarVenta!=null){
            dialogRegistrarVenta.dismiss();
        }

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
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
                    latLng=null;
                    showSettingDialog();
//                    getActivity().registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));//Registrar el receptor de difusión para comprobar el estado del GPS.

                    // dismiss();
                }

            }
        }
    };
}
