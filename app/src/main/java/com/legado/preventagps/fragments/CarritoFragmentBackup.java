package com.legado.preventagps.fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.legado.preventagps.R;
import com.legado.preventagps.activities.vendedor.ContenedorActivity;
import com.legado.preventagps.adapter.vendedor.CarritoRecyclerAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.dialogs.MyDialogInfo;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.PreVenta;
import com.legado.preventagps.util.PermissionUtils;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.UtilAndroid;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarritoFragmentBackup extends Fragment implements ContenedorActivity.ContenedorActivityListener {
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

    static TextView titleListCountSet;
    SessionUsuario sessionUsuario;
    static Spinner spinnerVendCarrito;
    static Spinner spinnerCondCarrito;
    // MyDialogProgress dialogRegistrarVenta;
    ProgressDialog dialogRegistrarVenta;
    MyDialogInfo.DialogInfoListener dialogInfoListener;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private boolean isNetworkLocation, isGPSLocation;
    private FusedLocationProviderClient mFusedLocationClient;

ContenedorActivity.ContenedorActivityListener contenedorActivityListener;

    public void setDialogInfoListener(MyDialogInfo.DialogInfoListener dialogInfoListener) {
        this.dialogInfoListener = dialogInfoListener;
    }

    public static CarritoFragmentBackup newInstance(TextView titleList, Spinner spinnerVendFind, Spinner spinnerCondFind) {

        CarritoFragmentBackup fragment = new CarritoFragmentBackup();
        titleListCountSet = titleList;
        spinnerVendCarrito = spinnerVendFind;
        spinnerCondCarrito = spinnerCondFind;
        return fragment;
    }

    Bundle args;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_carrito, container, false);

        contenedorActivityListener=this;
        ContenedorActivity.setContenedorActivityListener(contenedorActivityListener);
         args=getArguments();
        codCliente = args.getString("codCliente");
        codLista =args.getString("codLista");
        codLocal = args.getString("codLocal");
        codRuta = args.getString("codRuta");

        ButterKnife.bind(this, rootView);
        sessionUsuario = new SessionUsuario(rootView.getContext());
        dialogRegistrarVenta = new ProgressDialog(getActivity(),R.style.AppTheme_MyDialog);
        dialogRegistrarVenta.setCancelable(false);
        dialogRegistrarVenta.setIndeterminate(true);
        cargarPedidos();
        guardarPedido();
        return rootView;
    }
    CarritoRecyclerAdapter detallePedidoRecyclerAdapter;
    public void cargarPedidos() {
        recicladorPedido.setVisibility(View.VISIBLE);
        recicladorPedido.setHasFixedSize(true);
        lManagerPedido = new LinearLayoutManager(getContext());
        recicladorPedido.setLayoutManager(lManagerPedido);
        recicladorPedido.setNestedScrollingEnabled(false);
//
//        detallePedidoRecyclerAdapter = new CarritoRecyclerAdapter(sessionUsuario.getPaqueteCarrito().getListaCarrito(), getContext(), getActivity(), this, txtMonto,titleListCountSet,spinnerVendCarrito,spinnerCondCarrito,args,dialogInfoListener);
//        recicladorPedido.setAdapter(detallePedidoRecyclerAdapter);
//         txtMonto.setText("TOTAL=" + detallePedidoRecyclerAdapter.montoTotalDiario().toString());
//        titleListCountSet.setText("LISTA DE ARTICULOS (" + detallePedidoRecyclerAdapter.getItemCount() + ")");

    }
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//
//        if(dialogRegistrarVenta!=null){
//            dialogRegistrarVenta.dismiss();
//        }
//        super.onSaveInstanceState(outState);
//
//    }

//    @Override
//    public void onResume() {
//        if( dialogRegistrarVenta!=null){
//            dialogRegistrarVenta.dismiss();
//
//        }
//        super.onResume();
//    }

    public void guardarPedido() {
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList condicion = (ArrayList) spinnerCondCarrito.getSelectedItem();
                ArrayList vend = (ArrayList) spinnerVendCarrito.getSelectedItem();
                PreVenta preVenta = new PreVenta();
                preVenta.setCodEmpresa(vend.get(0).toString());
                preVenta.setCodLocalidad(vend.get(8).toString());
                preVenta.setCodAlmacen(vend.get(10).toString());
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
                preVenta.setDetalles(sessionUsuario.getPaqueteCarrito().getListaCarrito());
                if(sessionUsuario.getPaqueteCarrito().getListaCarrito().size()>0){
                    showDialog(preVenta);
                }else{
                    recicladorPedido.setAdapter(null);
                    detallePedidoRecyclerAdapter.notifyDataSetChanged();
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
            initGoogleLocation(preVenta);
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

    public void
    registrarPreventa(final PreVenta preVenta) {
        Call<JsonRespuesta<Integer>> call = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getVentaService().registrarPedidoLocal(preVenta);
        call.enqueue(new Callback<JsonRespuesta<Integer>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<Integer>> call, Response<JsonRespuesta<Integer>> response) {
                if (response.body().getEstado() == -1) {
                    Snackbar.make(getView(), response.body().getMensaje(), Snackbar.LENGTH_INDEFINITE).setAction("Cerrar", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    }).show();
                    dialogRegistrarVenta.dismiss();
                }else if (response.body().getEstado() == -2){

                    Snackbar.make(getView(), response.body().getMensaje(), Snackbar.LENGTH_INDEFINITE).setAction("Cerrar", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                         }
                    }).show();
                    dialogRegistrarVenta.dismiss();

                }else {
//                    List<Cliente> clientes=sessionUsuario.getPaqueteCliente().getClientesSecuenciados();
//                     for (int i = 0; i <clientes.size() ; i++) {
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

                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().updateClienteEstadoJornada("P",codCliente,codLocal);

                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                    } finally {
                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                    }

                    Toast.makeText(getContext(), "Se registró el pedido correctamente " , Toast.LENGTH_LONG).show();
                        dialogRegistrarVenta.dismiss();
                    dialogInfoListener.updateRecyclerSuccess(ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().listarClientes(UtilAndroid.fechaDevice("yyyy-MM-dd")));
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
                getActivity().finish();
                }


        });
    }

 public void showDialog(final PreVenta preVenta) {
        new AlertDialog.Builder(getContext())
                .setTitle("CONFIRMACION")
                .setMessage("Desea realizar el pedido?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialogRegistrarVenta.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override public void run() {
                                registrarPreventaUbic(preVenta);
                            }
                        }, 1000);
                    }
                }).setNegativeButton(android.R.string.no, null).show();
    }


    @Override
    public void dismissDialog() {
        if(dialogRegistrarVenta!=null){
            dialogRegistrarVenta.dismiss();
        }

    }




}
