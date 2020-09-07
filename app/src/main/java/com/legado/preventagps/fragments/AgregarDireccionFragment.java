package com.legado.preventagps.fragments;

import android.app.Fragment;
import android.os.Bundle;


import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.SeekBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.legado.preventagps.R;

import com.legado.preventagps.util.OnMapAndViewReadyListener;
import com.legado.preventagps.util.PermissionUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgregarDireccionFragment extends DialogFragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener,
        SeekBar.OnSeekBarChangeListener,
        GoogleMap.OnInfoWindowLongClickListener,
        GoogleMap.OnInfoWindowCloseListener,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {
    private static final String TAG = AgregarDireccionFragment.class.getName();
//    private LocationRequest mLocationRequest;
//    private LocationCallback mLocationCallback;
//    private Location mCurrentLocation;
//    private boolean isNetworkLocation, isGPSLocation;
//    private FusedLocationProviderClient mFusedLocationClient;
//    @BindView(R.id.spinnerRuta)
//    Spinner spinnerRuta;
//    @BindView(R.id.txtCoordenadas)
//    EditText txtCoordenadas;
//    @BindView(R.id.txtDireccFact)
//    EditText txtDireccFact;
//   static ArrayList listaRutasDialog;
//    private GoogleMap mMap;
//    LatLng newLongitud;
GoogleMap myMap;
    MapView mMapView;
  View rootView;

    public static AgregarDireccionFragment newInstance(  ArrayList listaRutas) {
       // listaRutasDialog=listaRutas;
        AgregarDireccionFragment fragment= new AgregarDireccionFragment();
        return  fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_agregar_direccion, container, false);
        ButterKnife.bind(this, rootView);
       // ButterKnife.bind(this, rootView);
//        SpinnerSimpleAdapter rutaAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, listaRutasDialog);
//        rutaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerRuta.setAdapter(rutaAdapter);

       // checkMyPermissionLocation();
      SupportMapFragment   mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        //new OnMapAndViewReadyListener(mapFragment, this);

        return  rootView;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        try {

                double x =  -6.23;
                double y = -79.25;
                LatLng ub = new LatLng(x, y);
                myMap.addMarker(new MarkerOptions().position(ub).title(""));
                //myMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ub, 18));

            /*myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    System.out.println("OLA");
                    System.out.println(marker.getPosition().latitude);
                    System.out.println(marker.getPosition().longitude);
                    myMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                    return false;
                }
            });*/
            myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    LatLng cali = new LatLng(latLng.latitude,latLng.longitude);
                    myMap.clear();
                    myMap.addMarker(new MarkerOptions().position(cali));
                    myMap.moveCamera(CameraUpdateFactory.newLatLng(cali));
                 }
            });
        }catch (Exception e){
            System.out.println("ERROR: "+e.getMessage());
        }
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
        return false;
    }
}
