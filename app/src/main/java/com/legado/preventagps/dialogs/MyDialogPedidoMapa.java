package com.legado.preventagps.dialogs;

import android.Manifest;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import android.widget.Toast;

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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.legado.preventagps.R;
import com.legado.preventagps.activities.vendedor.ClienteActivity;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Cliente;
import com.legado.preventagps.modelo.PreVenta;
import com.legado.preventagps.util.PermissionUtils;
import com.legado.preventagps.util.SessionUsuario;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class MyDialogPedidoMapa extends DialogFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener ,GoogleMap.OnMyLocationButtonClickListener{
    SessionUsuario sessionUsuario;
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
    @BindView(R.id.btnCerrar)
    Button btnCerrar;
    @BindView(R.id.btnConfirmar)
    Button btnConfirmar;
    LatLng newLongitud;
    LatLng latLngYo;
    private static View rootView;
    Bundle args;
    static  PreVenta preVentaDialog;

    public static MyDialogPedidoMapa newInstance(PreVenta preventa) {
        MyDialogPedidoMapa fragment = new MyDialogPedidoMapa();
        preVentaDialog=preventa;
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));//Registrar el receptor de difusión para comprobar el estado del GPS.

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.layout_dialog_pedido, container, false);
            ButterKnife.bind(this, rootView);
            getDialog().setCanceledOnTouchOutside(false);
            sessionUsuario = new SessionUsuario(getActivity());
            args = getArguments();
            initGoogleAPIClient();  //Init Google API Client
            checkPermissions();     //Check Permission
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

                showSettingDialog();
            }


            mapFragment.getMapAsync(this);

            stateMap = false;

            btnCerrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                         editDialogClientHolderListener.showButtonGeolocalizar();
//                         editDialogClientHolderListener.showButtonVerGeo();
                    mGoogleApiClient.disconnect();
                        getActivity().registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));//Registrar el receptor de difusión para comprobar el estado del GPS.

                    dismiss();
                }
            });
            btnConfirmar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(latLng==null){
                        Snackbar.make(getView(), "DEBE ACEPTAR PERMITIR SU UBICACION  ", Snackbar.LENGTH_INDEFINITE)
                                .setAction("REINTENTAR", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showSettingDialog();
                                    }
                                }).show();
                    }else {
                        preVentaDialog.setCoordenadas(latLng.latitude+","+latLng.longitude);
                        registrarPreventa(preVentaDialog);
                    }
                }
            });
            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener()
            {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode,android.view.KeyEvent event) {
                    if ((keyCode ==  android.view.KeyEvent.KEYCODE_BACK))
                    {
//
//                            editDialogClientHolderListener.showButtonGeolocalizar();
//
//                            editDialogClientHolderListener.showButtonVerGeo();


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

    public void registrarPreventa(final PreVenta preVenta) {
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
                    //dialogRegistrarVenta.dismiss();
                }else if (response.body().getEstado() == -2){

                    Snackbar.make(getView(), response.body().getMensaje(), Snackbar.LENGTH_INDEFINITE).setAction("Cerrar", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                         }
                    }).show();

                }else {
                    try {
                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();

                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().updateClienteEstadoJornada("P",preVenta.getCodCliente(),preVenta.getCodLocal());

                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                    } finally {
                        ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                    }

                    Toast.makeText(getContext(), "Se registró el pedido correctamente " , Toast.LENGTH_LONG).show();
                  dismiss();
                    startActivity(new Intent(getActivity(), ClienteActivity.class));

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
        mCurrLocationMarker = mMap.addMarker(markerOptions);
         mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));//AQUI MODIFICA EL ZOOM AL MAPA SEGUN TUS NECESIDADES

        ///////////////////////////////////////ESTE ES EL MARCADOR DEL DESTINO (ES UN MARCADOR CON UNA POCISION ESTATICA) ///////////////////////////////////////
//        latLngOtro = new LatLng(19.432602, -99.133248);
//        MarkerOptions markerOptions2 = new MarkerOptions();
//        markerOptions2.position(latLngOtro);
//        markerOptions2.title("ESTE ES TU DESTINO");
//        markerOptions2.icon(iconClient);
//        markerClient = mMap.addMarker(markerOptions2);
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
//        if (provider.equalsIgnoreCase(LocationManager.GPS_PROVIDER)) {
//            //Accuracy is a top priority regardless of battery consumption
//            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        } else {
            //Acquired location information based on balance of battery and accuracy (somewhat higher accuracy)
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
       // }

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

//    private void enableMyLocation() {
//        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Permission to access the location is missing.
//            PermissionUtils.requestPermission(getActivity());
//        } else if (mMap != null) {
//            // Access to the location has been granted to the app.
//            mMap.setMyLocationEnabled(true);
//            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//            Criteria criteria = new Criteria();
//            Location yo = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
//            if (yo == null) {
//                enableMyLocation();
//                latLngYo = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//            } else {
//                latLngYo = new LatLng(yo.getLatitude(), yo.getLongitude());
//                mMap.addMarker(new MarkerOptions().position(latLngYo));
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngYo, 18));
//                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                    @Override
//                    public void onMapClick(LatLng latLng) {
//                        newLongitud = new LatLng(latLng.latitude, latLng.longitude);
//                        mMap.clear();
//                        mMap.addMarker(new MarkerOptions().position(newLongitud));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLongitud));
//                    }
//                });
//            }
//        }
//    }

//    private void checkMyPermissionLocation() {
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            //Permission Check
//            PermissionUtils.requestPermission(getActivity());
//        } else {
//            //If you're authorized, start setting your location
//            initGoogleMapLocation();
//        }
//    }

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
//        if (gpsLocationReceiver != null)
//            getActivity().unregisterReceiver(gpsLocationReceiver);

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
                     dismiss();
                }

            }
        }
    };

}
