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

public class SpinnerSimpleAdapter extends ArrayAdapter<Object[]> {

    private static final String TAG = "SpinnerSimpleAdapter";
    private ArrayList departamentos;
    private Activity activity;

    public SpinnerSimpleAdapter(Activity a, int textViewResourceId, ArrayList departamentos) {
        super(a, textViewResourceId, departamentos);
        this.departamentos = departamentos;
        activity = a;
    }

    public static class ViewHolderDpto {
        public TextView descDpto;
    }

    SpinnerSimpleAdapter.ViewHolderDpto holder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_simplespinner_item, null);
            holder = new SpinnerSimpleAdapter.ViewHolderDpto();
            holder.descDpto = (TextView) v.findViewById(R.id.desc);
            v.setTag(holder);
        } else holder = (SpinnerSimpleAdapter.ViewHolderDpto)v.getTag();
        final ArrayList dpto =  ((ArrayList) departamentos.get(position));
        if (departamentos != null) {
            holder.descDpto.setText(dpto.get(1).toString());
        }
        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent){
        return getView(position,convertView,parent);
    }

}
