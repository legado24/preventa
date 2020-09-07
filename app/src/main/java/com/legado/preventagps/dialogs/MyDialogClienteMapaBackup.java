package com.legado.preventagps.dialogs;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

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
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.enums.CLIENTEENUM;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Cliente;
import com.legado.preventagps.modelo.LocalCliente;
import com.legado.preventagps.util.OnMapAndViewReadyListener;
import com.legado.preventagps.util.PermissionUtils;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.UtilAndroid;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class MyDialogClienteMapaBackup extends DialogFragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener,
        SeekBar.OnSeekBarChangeListener,
        GoogleMap.OnInfoWindowLongClickListener,
        GoogleMap.OnInfoWindowCloseListener,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {
    SessionUsuario sessionUsuario;
    private GoogleMap mMap;
    @BindView(R.id.btnCerrar)
    Button btnCerrar;
    @BindView(R.id.btnConfirmar)
    Button btnConfirmar;
    LatLng newLongitud;
    LatLng latLngYo;
    private EditDialogClientListener editDialogClientListener;
    EditDialogClientHolderListener editDialogClientHolderListener;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    SupportMapFragment mapFragment;
    private boolean isNetworkLocation, isGPSLocation;
    private String provider;
    private static View rootView;
    Bundle args;

    public static MyDialogClienteMapaBackup newInstance() {
        MyDialogClienteMapaBackup fragment = new MyDialogClienteMapaBackup();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.layout_dialog_mapa, container, false);
            ButterKnife.bind(this, rootView);
            getDialog().setCanceledOnTouchOutside(false);
            sessionUsuario = new SessionUsuario(getActivity());
            args = getArguments();
            mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapaCliente);
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
                PermissionUtils.LocationSettingDialog a = PermissionUtils.LocationSettingDialog.newInstance();
                a.show(getFragmentManager(), "Setting");
                a.setCancelable(false);
            }
            new OnMapAndViewReadyListener(mapFragment, this);
            btnCerrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // if (getArguments().getString("coordenadas") == null) {
                        editDialogClientHolderListener.showButtonGeolocalizar();
                   // } else {
                        editDialogClientHolderListener.showButtonVerGeo();
                    //}
                    dismiss();
                }
            });
            btnConfirmar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LocalCliente lc = new LocalCliente();
                    lc.setUsuario(sessionUsuario.getUsuario());
                    lc.setCodCliente(args.getString(CLIENTEENUM.CODCLIENTE.getClave()));
                    lc.setCodLocal(args.getString("codLocal"));
                    if (newLongitud != null) {
                        lc.setCoordenadas(newLongitud.latitude + "," + newLongitud.longitude);
                    } else {
                        lc.setCoordenadas(latLngYo.latitude + "," + latLngYo.longitude);
                    }
                    lc.setOperacion(2);
                    Gson gson = new Gson();
                    String lcJson = gson.toJson(lc);
                    Log.d("assas", lcJson);
                    regitrarCoordenadas(lc);
                }
            });
            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener()
            {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode,android.view.KeyEvent event) {
                    if ((keyCode ==  android.view.KeyEvent.KEYCODE_BACK))
                    {

                            editDialogClientHolderListener.showButtonGeolocalizar();

                            editDialogClientHolderListener.showButtonVerGeo();


                        return true; // pretend we've processed it
                    }
                    else
                        return false; // pass on to be processed as normal
                }
            });



        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }


        return rootView;
    }

    public void setEditDialogClientHolderListener(EditDialogClientHolderListener editDialogClientHolderListener) {
        this.editDialogClientHolderListener = editDialogClientHolderListener;
    }

    public void setEditDialogClientListener(EditDialogClientListener editDialogClientListener) {
        this.editDialogClientListener = editDialogClientListener;
    }

    private void regitrarCoordenadas(final LocalCliente lc) {
        Call<JsonRespuesta<Integer>> call = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().mantenimientoLocal(lc);
        call.enqueue(new Callback<JsonRespuesta<Integer>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<Integer>> call, Response<JsonRespuesta<Integer>> response) {
                if (response.body().getEstado() == -1) {
                    Snackbar.make(getView(), response.body().getMensaje(), Snackbar.LENGTH_INDEFINITE).setAction("Cerrar", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    }).show();
                    //  dialogRegistrarCoordenada.dismiss();
                } else {

//                    List<Cliente> clientes = sessionUsuario.getPaqueteCliente().getClientesSecuenciados();
//                    for (int i = 0; i < clientes.size(); i++) {
//                        if (clientes.get(i).getCodCliente().equals(lc.getCodCliente())) {
//                            clientes.get(i).setCoordenadas(lc.getCoordenadas());
//                            clientes.get(i).setEstadoGeoSis("1");
//                             break;
//                        }
//                    }
//                    PaqueteCliente paq = new PaqueteCliente();
//                    paq.setClientesSecuenciados(clientes);
//                    sessionUsuario.guardarPaqueteCliente(paq);
                    try {
                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();

                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().updateAfterRegistrarCoordenadas(lc.getCoordenadas(),"1",lc.getCodCliente(),lc.getCodLocal());

                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                    } finally {
                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                    }


                     editDialogClientListener.updateRecycler(ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().listarClientes(UtilAndroid.fechaDevice("yyyy-MM-dd")));
                     editDialogClientHolderListener.showButtonGeolocalizar();
                     editDialogClientHolderListener.showButtonVerGeo();

                    dismiss();

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
                // dialogRegistrarLocal.dismiss();
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
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
            checkMyPermissionLocation();
        } else {
            initGoogleMapLocation();
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setOnMyLocationButtonClickListener(this);
        if (getArguments().getString("coordenadas") == null) {
            mMap.setMyLocationEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                checkMyPermissionLocation();
            } else {
                initGoogleMapLocation();
            }

        } else {

            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                PermissionUtils.requestPermission(getActivity());
            } else if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                String xString = getArguments().getString("coordenadas").split(",")[0];
                String yString = getArguments().getString("coordenadas").split(",")[1];
                Double x = Double.parseDouble(xString);
                Double y = Double.parseDouble(yString);
                LatLng latLngEl = new LatLng(x, y);
                Marker m = mMap.addMarker(new MarkerOptions().position(latLngEl).title(getArguments().getString("descCliente")));
                m.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngEl, 18));
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


        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnInfoWindowCloseListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//
//        // Override the default content description on the view, for accessibility mode.
//        // Ideally this string would be localised.
//        mMap.setContentDescription("Map with lots of markers.");
//        Double x = Double.parseDouble(xString);
//        Double y = Double.parseDouble(yString);
//
//        LatLng miUbic = new LatLng(x, y);
//
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miUbic, 9.2f));
//
//        mMap.addMarker(new MarkerOptions().position(miUbic).title("Direccion"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(miUbic));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miUbic, 18));
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//               newLongitud = new LatLng(latLng.latitude,latLng.longitude);
//                mMap.clear();
//                mMap.addMarker(new MarkerOptions().position(newLongitud));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(newLongitud));
//                //txtCoordenadas.setText(String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude));
//            }
//        });

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
//                MarkerOptions options = new MarkerOptions();
//                options.position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
//                BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
//                options.icon(icon);
//                Marker marker = mMap.addMarker(options);
                Marker m = mMap.addMarker(new MarkerOptions().position(latLngYo).title("Mi Ubicación"));
                m.showInfoWindow();

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(m.getPosition(), 17));
//                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                    @Override
//                    public void onMapClick(LatLng latLng) {
//                        newLongitud = new LatLng(latLng.latitude, latLng.longitude);
//                        mMap.clear();
//                        mMap.addMarker(new MarkerOptions().position(newLongitud));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLongitud));
//                    }
//                });
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
        if (provider.equalsIgnoreCase(LocationManager.GPS_PROVIDER)) {
            //Accuracy is a top priority regardless of battery consumption
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        } else {
            //Acquired location information based on balance of battery and accuracy (somewhat higher accuracy)
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }

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

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(getActivity());
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location yo = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (yo == null) {
                enableMyLocation();
                latLngYo = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            } else {
                latLngYo = new LatLng(yo.getLatitude(), yo.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLngYo));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngYo, 18));
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

    public interface EditDialogClientListener {
        void updateRecycler(List<Cliente> list);

    }

    public interface EditDialogClientHolderListener {
        void showButtonGeolocalizar();

        void showButtonVerGeo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SupportMapFragment f = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.mapaCliente);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
    }



//
//    @Override
//    public void onStart()
//    {
//        super.onStart();
//        Dialog dialog = getDialog();
//        if (dialog != null)
//        {
//            int width = ViewGroup.LayoutParams.MATCH_PARENT;
//            int height = ViewGroup.LayoutParams.MATCH_PARENT;
//            dialog.getWindow().setLayout(width, height);
//        }
//    }
}
