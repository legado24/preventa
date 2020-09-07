package com.legado.preventagps.adapter.vendedor;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.legado.preventagps.R;

import java.util.ArrayList;

public class SpinnerVendedorCustomAdapter extends ArrayAdapter<Object[]> {
    private static final String TAG = "SpinnerSimpleAdapter";
    private ArrayList datos;
    private Activity activity;

    public SpinnerVendedorCustomAdapter(Activity a, int textViewResourceId, ArrayList datos) {
        super(a, textViewResourceId, datos);
        this.datos = datos;
        activity = a;
    }

    public static class ViewHolderSpinnerVend {
        public TextView txtTipoPedido;
    }

    SpinnerVendedorCustomAdapter.ViewHolderSpinnerVend holder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_datos_vend_item, null);
            holder = new SpinnerVendedorCustomAdapter.ViewHolderSpinnerVend();
            holder.txtTipoPedido = (TextView) v.findViewById(R.id.txtTipoPedido);
            v.setTag(holder);
        } else holder = (SpinnerVendedorCustomAdapter.ViewHolderSpinnerVend)v.getTag();
        final ArrayList dpto =  ((ArrayList) datos.get(position));
        if (datos != null) {
            holder.txtTipoPedido.setText(dpto.get(4).toString());
        }
        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent){
        return getView(position,convertView,parent);
    }

}
