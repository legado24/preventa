package com.legado.preventagps.dialogs;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
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
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.legado.preventagps.R;
import com.legado.preventagps.activities.vendedor.ClienteActivity;
import com.legado.preventagps.adapter.vendedor.AutoCompleteSimpleAdapter;
import com.legado.preventagps.adapter.vendedor.SpinnerSimpleAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.enums.CLIENTEENUM;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.LocalCliente;
import com.legado.preventagps.modelo.PaqueteAlta;
import com.legado.preventagps.util.OnMapAndViewReadyListener;
import com.legado.preventagps.util.PermissionUtils;
import com.legado.preventagps.util.SessionUsuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class MyDialogLocalMapa extends DialogFragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener,
        SeekBar.OnSeekBarChangeListener,
        GoogleMap.OnInfoWindowLongClickListener,
        GoogleMap.OnInfoWindowCloseListener,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {
    SessionUsuario sessionUsuario;
    private GoogleMap mMap;
    @BindView(R.id.spinnerRuta)
    Spinner spinnerRuta;
    @BindView(R.id.txtCoordenadas)
    EditText txtCoordenadas;
    @BindView(R.id.txtDireccFact)
    EditText txtDireccFact;
    @BindView(R.id.txtReferencia)
    EditText txtReferencia;
    @BindView(R.id.btnRegistrarLocal)
    Button btnRegistrarLocal;
    @BindView(R.id.btnCerrar)
    Button btnCerrar;
    @BindView(R.id.spinnerDpto)
    AutoCompleteTextView spinnerDpto;
    @BindView(R.id.spinnerProv)
    AutoCompleteTextView spinnerProv;
    @BindView(R.id.spinnerDist)
    AutoCompleteTextView spinnerDist;
    View rootView;
    LatLng newLongitud;

    static ArrayList listaRutasDialog;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private boolean isNetworkLocation, isGPSLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    MyDialogProgress dialogRegistrarLocal;

    public static MyDialogLocalMapa newInstance(ArrayList listaRutas) {
        listaRutasDialog = listaRutas;
        MyDialogLocalMapa fragment = new MyDialogLocalMapa();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_dialog_local_mapa, container, false);
        ButterKnife.bind(this, rootView);
        toUpper();
        SpinnerSimpleAdapter rutaAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, listaRutasDialog);
        rutaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRuta.setAdapter(rutaAdapter);
        loadSpinnerDpto();
        sessionUsuario = new SessionUsuario(getActivity());
        checkMyPermissionLocation(this);
        final String codCliente = getArguments().getString(CLIENTEENUM.CODCLIENTE.getClave());
        spinnerDpto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Object[] item = (Object[]) adapterView.getItemAtPosition(position);
                PaqueteAlta paq = sessionUsuario.getPaqueteAlta();
                if (paq == null) {
                    paq = new PaqueteAlta();
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
                Object[] item = (Object[]) adapterView.getItemAtPosition(position);
                PaqueteAlta paq = sessionUsuario.getPaqueteAlta();
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
                Object[] item = (Object[]) adapterView.getItemAtPosition(position);
                PaqueteAlta paq = sessionUsuario.getPaqueteAlta();
                paq.setCodDist(item[0].toString());
                paq.setDescDist(item[1].toString());
                sessionUsuario.guardarPaqueteAlta(paq);
                loadSpinnerDistrito(item[0].toString());

            }
        });


        btnRegistrarLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar()){
                    LocalCliente lc = new LocalCliente();
                    lc.setDireccion(txtDireccFact.getText().toString());
                    //  lc.setDescLocal("fkfkfkfkf");
                    lc.setCoordenadas(txtCoordenadas.getText().toString());
                    ArrayList ruta = (ArrayList) spinnerRuta.getSelectedItem();
                    lc.setCodRuta(ruta.get(0).toString());
                    lc.setUbigeo(sessionUsuario.getPaqueteAlta().getCodDist());
                    lc.setObservaciones(txtReferencia.getText().toString());
                    lc.setUsuario(sessionUsuario.getUsuario());
                    lc.setCodCliente(codCliente);
                    lc.setOperacion(1);
                    showDialog(lc);
                    Gson gson = new Gson();
                    String lcJson = gson.toJson(lc);
                    Log.d("assas", lcJson);

                }

            }
        });
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        return rootView;
    }

    public void showDialog(final LocalCliente localCliente) {
        new AlertDialog.Builder(getContext())
                .setTitle("CONFIRMACION")
                .setMessage("Desea registrar el nuevo local?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
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
                }).setNegativeButton(android.R.string.no, null).show();
    }

    private void registrarLocal(LocalCliente localCliente) {
        Call<JsonRespuesta<Integer>> call = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().mantenimientoLocal(localCliente);
        call.enqueue(new Callback<JsonRespuesta<Integer>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<Integer>> call, Response<JsonRespuesta<Integer>> response) {
                if (response.body().getEstado() == -1) {
                    Snackbar.make(getView(), response.body().getMensaje(), Snackbar.LENGTH_INDEFINITE).setAction("Cerrar", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    }).show();
                    dialogRegistrarLocal.dismiss();
                } else {
                    try {
                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();

                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().deleteTablaClientes();

                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                    } finally {
                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                    }


                    Toast.makeText(getContext(), "Se registró el local correctamente " , Toast.LENGTH_LONG).show();
                    dialogRegistrarLocal.dismiss();
                    Intent intent = new Intent(getActivity(), ClienteActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<JsonRespuesta<Integer>> call, Throwable t) {
                dialogRegistrarLocal.dismiss();
                Snackbar.make(getView(), "Problemas de conexion  ", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Cerrar", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).show();

            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    private boolean validar() {
        boolean band = true;

        if (spinnerDist.getText().toString().equals("")) {
            spinnerDist.setError(Html.fromHtml("<font color='red'>Ingrese Distrito.</font>"));
            spinnerDist.requestFocus();
            band = false;
        } else {
            spinnerDist.setError(null);
        }

        if (spinnerProv.getText().toString().equals("")) {
            spinnerProv.setError(Html.fromHtml("<font color='red'>Ingrese Provincia.</font>"));
            spinnerProv.requestFocus();
            band = false;
        } else {
            spinnerProv.setError(null);
        }

        if (spinnerDpto.getText().toString().equals("")) {
            spinnerDpto.setError(Html.fromHtml("<font color='red'>Ingrese Departamento.</font>"));
            spinnerDpto.requestFocus();
            band = false;
        } else {
            spinnerDpto.setError(null);
        }

        if (txtCoordenadas.getText().toString().equals("")) {
            txtCoordenadas.setError(Html.fromHtml("<font color='red'>Capture coordenadas.</font>"));
            txtCoordenadas.requestFocus();
            band = false;
        } else {
            txtCoordenadas.setError(null);
        }

        if (txtDireccFact.getText().toString().equals("")) {
            txtDireccFact.setError(Html.fromHtml("<font color='red'>Ingrese dirección.</font>"));
            txtDireccFact.requestFocus();
            band = false;
        } else {
            txtDireccFact.setError(null);
        }

//        if (txtReferencia.getText().toString().equals("")) {
//            txtReferencia.setError(Html.fromHtml("<font color='red'>Ingrese referencia.</font>"));
//            txtReferencia.requestFocus();
//            band = false;
//        } else {
//            txtReferencia.setError(null);
//        }






        return band;

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onInfoWindowClose(Marker marker) {

    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        mMap.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkMyPermissionLocation(this);
        } else {
            initGoogleMapLocation(this);
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Hide the zoom controls as the button panel will cover it.
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setMyLocationEnabled(true);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(getActivity());
        }

        //enableMyLocation();
        // Add lots of markers to the map.
        // addMarkersToMap();

        // Setting an info window adapter allows us to change the both the contents and look of the
        // info window.
        // mMap.setInfoWindowAdapter(new MapaClientesActivity.CustInfoWindowAdapter());

        // Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnInfoWindowCloseListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        mMap.setContentDescription("Map with lots of markers.");
        Double x = mCurrentLocation.getLatitude();
        Double y = mCurrentLocation.getLongitude();

        LatLng miUbic = new LatLng(x, y);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miUbic, 9.2f));

        mMap.addMarker(new MarkerOptions().position(miUbic).title("Direccion"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(miUbic));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miUbic, 15));
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                newLongitud = new LatLng(latLng.latitude, latLng.longitude);
//                mMap.clear();
//                mMap.addMarker(new MarkerOptions().position(newLongitud));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(newLongitud));
//                txtCoordenadas.setText(String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude));
//            }
//        });

    }

    private void checkMyPermissionLocation(OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener listener) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //Permission Check
            PermissionUtils.requestPermission(getActivity());
        } else {

            initGoogleMapLocation(listener);

        }
    }

    private void initGoogleMapLocation(final OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener listener) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
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
                SupportMapFragment mapFragment =
                        (SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapaCliente);
                new OnMapAndViewReadyListener(mapFragment, listener);

                txtCoordenadas.setText(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());

                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }

            //Locatio nMeaning that all relevant information is available
            @Override
            public void onLocationAvailability(LocationAvailability availability) {
                //boolean isLocation = availability.isLocationAvailable();
            }
        };
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        //To get location information only once here
        mLocationRequest.setNumUpdates(5);

        LocationManager mListener = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (mListener != null) {
            isGPSLocation = mListener.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkLocation = mListener.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }


        if (isGPSLocation) {
            //Accuracy is a top priority regardless of battery consumption
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        } else if (isNetworkLocation) {
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        } else {
            //Device location is not set
            PermissionUtils.LocationSettingDialog a = PermissionUtils.LocationSettingDialog.newInstance();
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

    private void loadSpinnerDpto() {
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("tipoUbic", "DEPAR");
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().datosUbicacionGeo(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if (response.body().getEstado() == 1) {
                    AutoCompleteSimpleAdapter autoCompleteSimpleAdapter = new AutoCompleteSimpleAdapter(getContext(), R.layout.list_simplespinner_item, (ArrayList) response.body().getData());
                    spinnerDpto.setAdapter(autoCompleteSimpleAdapter);

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

    private void loadSpinnerProvincia(String codDep) {
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("tipoUbic", "PROVI");
        dataConsulta.put("filtro", codDep);
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().datosUbicacionGeo(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if (response.body().getEstado() == 1) {
                    AutoCompleteSimpleAdapter autoCompleteSimpleAdapter = new AutoCompleteSimpleAdapter(getContext(), R.layout.list_simplespinner_item, (ArrayList) response.body().getData());
                    spinnerProv.setAdapter(autoCompleteSimpleAdapter);

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

    private void loadSpinnerDistrito(String codProv) {
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("filtro", codProv);
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().datosUbicacionGeo(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if (response.body().getEstado() == 1) {
                    AutoCompleteSimpleAdapter autoCompleteSimpleAdapter = new AutoCompleteSimpleAdapter(getContext(), R.layout.list_simplespinner_item, (ArrayList) response.body().getData());
                    spinnerDist.setAdapter(autoCompleteSimpleAdapter);
                } else {

                    Snackbar.make(getView(), response.body().getMensaje(), Snackbar.LENGTH_INDEFINITE).setAction("Cerrar", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    }).show();
                }
            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
            }
        });
    }



    @Override
    public void onDestroyView() {

        SupportMapFragment f = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.mapaCliente);
        if (f.isResumed()){
            getFragmentManager().beginTransaction().remove(f).commit();
        }
         super.onDestroyView();
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
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    txtDireccFact.setText(s);
                }
                txtDireccFact.setSelection(txtDireccFact.getText().length());
            }
        });
    }

}
