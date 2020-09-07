package com.legado.preventagps.util;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.legado.preventagps.R;

/**
 * Created by __Adrian__ on 10/05/2019.
 */

public class CustInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static final String TAG = "CustInfoWindowAdapter";
    private LayoutInflater inflater;

    public CustInfoWindowAdapter(LayoutInflater inflater){
        this.inflater = inflater;
    }

    @Override
    public View getInfoContents(final Marker m) {
        //Carga layout personalizado.
        View v = inflater.inflate(R.layout.infowindow_layout, null);
        String[] info = m.getTitle().split("&");
        String url = m.getSnippet();
        ((TextView)v.findViewById(R.id.info_window_nombre)).setText("Lina Cortés");
        ((TextView)v.findViewById(R.id.info_window_placas)).setText("Placas: SRX32");
        ((TextView)v.findViewById(R.id.info_window_estado)).setText("Estado: Activo");
        return v;
    }

    @Override
    public View getInfoWindow(Marker m) {
        return null;
    }

}