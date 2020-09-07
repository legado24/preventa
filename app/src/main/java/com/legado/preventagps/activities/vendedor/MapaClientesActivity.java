package com.legado.preventagps.activities.vendedor;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.legado.preventagps.R;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.modelo.Cliente;
import com.legado.preventagps.util.MapUtils;
import com.legado.preventagps.util.OnMapAndViewReadyListener;
import com.legado.preventagps.util.PermissionUtils;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.UtilAndroid;

import java.util.ArrayList;
import java.util.List;

public class MapaClientesActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener,
        SeekBar.OnSeekBarChangeListener,
        GoogleMap.OnInfoWindowLongClickListener,
        GoogleMap.OnInfoWindowCloseListener,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private LatLng miUbic;
    private final List<Marker> mMarkerRainbow = new ArrayList<Marker>();
    SessionUsuario sessionUsuario;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    LatLng latLngYo;
    private boolean isNetworkLocation, isGPSLocation;
    private String provider;
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }


    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View mWindow;
        private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return mContents;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_clientes);
        sessionUsuario = new SessionUsuario(this);
//        SupportMapFragment mapFragment =
//                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        new OnMapAndViewReadyListener(mapFragment, this);
        SupportMapFragment mapFragment  = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        LocationManager mListener = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
            a.show(getSupportFragmentManager(), "Setting");
            a.setCancelable(false);
        }
        new OnMapAndViewReadyListener(mapFragment, this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setOnMyLocationButtonClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkMyPermissionLocation();
        } else {
            initGoogleMapLocation();
        }
        //mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnInfoWindowCloseListener(this);
        mMap.setOnInfoWindowLongClickListener(this);

//        Double x = Double.parseDouble(sessionUsuario.getUbicacion().split(",")[0]);
//        Double y = Double.parseDouble(sessionUsuario.getUbicacion().split(",")[1]);
//
//        Double xl = Double.parseDouble(sessionUsuario.getUbicacionLast().split(",")[0]);
//        Double yl = Double.parseDouble(sessionUsuario.getUbicacionLast().split(",")[1]);
//        miUbic = new LatLng(x, y);
//        LatLng miUbicLast = new LatLng(xl, yl);
//        LatLngBounds bounds = new LatLngBounds.Builder().include(miUbic).include(miUbicLast).build();
//        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 90));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miUbic, 12.0f));
    }
    private void checkMyPermissionLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //Permission Check
            PermissionUtils.requestPermission(this);
        } else {
            //If you're authorized, start setting your location
            initGoogleMapLocation();
        }
    }
    private void addMarkersToMap() {
        List<Cliente> clientes = ApiSqlite.getInstance(this.getApplicationContext()).getOperacionesBaseDatos().listarClientes(UtilAndroid.fechaDevice("yyyy-MM-dd"));
        ArrayList<LatLng> coordList = new ArrayList<LatLng>();
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getCoordenadas() != null) {
                Double x = Double.parseDouble(clientes.get(i).getCoordenadas().split(",")[0]);
                Double y = Double.parseDouble(clientes.get(i).getCoordenadas().split(",")[1]);
                LatLng lat = new LatLng(x, y);
                coordList.add(lat);
                if (x != null && y != null) {
                    MapUtils mapUtils = new MapUtils();
                    Bitmap bitmap = mapUtils.GetBitmapMarker(getApplicationContext(), R.drawable.circle, clientes.get(i).getSecuencia() == null ? "" : clientes.get(i).getSecuencia().toString());
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(x, y))
                            .title(clientes.get(i).getSecuencia() + "-" + clientes.get(i).getDescCliente())
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

                    mMarkerRainbow.add(marker);
                }
            }
        }

    }
    private void initGoogleMapLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(this);
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

                mFusedLocationClient.removeLocationUpdates(mLocationCallback);

                addMarkersToMap();
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
        locationResponse.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if (ActivityCompat.checkSelfPermission(MapaClientesActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            }
        });
        locationResponse.addOnFailureListener(this, new OnFailureListener() {
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
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Permission to access the location is missing.
//            PermissionUtils.requestPermission(this);
//        } else if (mMap != null) {
//            // Access to the location has been granted to the app.
//            mMap.setMyLocationEnabled(true);
//            LocationManager locationManager = (LocationManager)
//                    getSystemService(Context.LOCATION_SERVICE);
//            Criteria criteria = new Criteria();
//            Location yo= locationManager.getLastKnownLocation(locationManager
//                    .getBestProvider(criteria, false));
//            LatLng latLngYo = new LatLng(yo.getLatitude(), yo.getLongitude());
////            Marker marker = mMap.addMarker(new MarkerOptions()
////                    .position(latLngYo)
////                   // .title(clientes.get(i).getSecuencia() + "-" + clientes.get(i).getDescCliente())
////                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
////
////
////            mMap.addMarker(markerOptions);
//            mMap.addMarker(new MarkerOptions().position(latLngYo));
//
//        }
//    }


    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Called when the Clear button is clicked.
     */
    public void onClearMap(View view) {
        if (!checkReady()) {
            return;
        }
        mMap.clear();
    }

    /**
     * Called when the Reset button is clicked.
     */
    public void onResetMap(View view) {
        if (!checkReady()) {
            return;
        }
         mMap.clear();
        addMarkersToMap();
    }

    /**
     * Called when the Reset button is clicked.
     */

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!checkReady()) {
            return;
        }
        float rotation = seekBar.getProgress();
        for (Marker marker : mMarkerRainbow) {
            marker.setRotation(rotation);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Do nothing.
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Do nothing.
    }

    //
    // Marker related listeners.
    //

    @Override
    public boolean onMarkerClick(final Marker marker) {
//        if (marker.equals(mPerth)) {
//            // This causes the marker at Perth to bounce into position when it is clicked.
//            final Handler handler = new Handler();
//            final long start = SystemClock.uptimeMillis();
//            final long duration = 1500;
//
//            final Interpolator interpolator = new BounceInterpolator();
//
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    long elapsed = SystemClock.uptimeMillis() - start;
//                    float t = Math.max(
//                            1 - interpolator.getInterpolation((float) elapsed / duration), 0);
//                    marker.setAnchor(0.5f, 1.0f + 2 * t);
//
//                    if (t > 0.0) {
//                        // Post again 16ms later.
//                        handler.postDelayed(this, 16);
//                    }
//                }
//            });
//        } else if (marker.equals(mAdelaide)) {
//            // This causes the marker at Adelaide to change color and alpha.
//            marker.setIcon(BitmapDescriptorFactory.defaultMarker(mRandom.nextFloat() * 360));
//            marker.setAlpha(mRandom.nextFloat());
//        }
//
//        // Markers have a z-index that is settable and gettable.
//        float zIndex = marker.getZIndex() + 1.0f;
//        marker.setZIndex(zIndex);
//        Toast.makeText(this, marker.getTitle() + " z-index set to " + zIndex,
//                Toast.LENGTH_SHORT).show();
//
//        mLastSelectedMarker = marker;
//         We return false to indicate that we have not consumed the event and that we wish
//         for the default behavior to occur (which is for the camera to move such that the
//         marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Click Info Window", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        //Toast.makeText(this, "Close Info Window", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        Toast.makeText(this, "Info Window long click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
//        mTopText.setText("onMarkerDragStart");
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
//        mTopText.setText("onMarkerDragEnd");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
//        mTopText.setText("onMarkerDrag.  Current Position: " + marker.getPosition());
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


}




