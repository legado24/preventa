package com.legado.preventagps.adapter.vendedor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.legado.preventagps.R;
import com.legado.preventagps.modelo.ConsultaDetalle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by _ADRIAN_ on 02/01/2017.
 */


public class ConsultaDetalleAdapter extends ArrayAdapter<ConsultaDetalle> {

    private static final String TAG = "CabeceraAdapter";
    private List<ConsultaDetalle> detalles=new ArrayList<>();
    private FragmentActivity activity;



    public ConsultaDetalleAdapter(Activity a, int textViewResourceId,
                                  List<ConsultaDetalle> detalles) {
        super(a, textViewResourceId, detalles);
        this.detalles = detalles;
        activity = (FragmentActivity) a;


    }
    public static class ViewHolderDetallePedido{
     public TextView codItem;
     public TextView descItem;
        public TextView um;
        public TextView cantidad;
        public TextView ventaNeta;
        public TextView stock;



    }
    ViewHolderDetallePedido holder;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;



        if (v == null) {
            LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.listdetalle_pedido, null);
            holder = new ViewHolderDetallePedido();

            holder.codItem=(TextView) v.findViewById(R.id.codItem);
            holder.descItem=(TextView) v.findViewById(R.id.descItem);
            holder.um=(TextView) v.findViewById(R.id.um);
            holder.cantidad=(TextView) v.findViewById(R.id.cantidad);
            holder.ventaNeta=(TextView) v.findViewById(R.id.ventaNeta);
            holder.stock=(TextView) v.findViewById(R.id.stock);


            v.setTag(holder);

        } else

            holder=(ViewHolderDetallePedido) v.getTag();
             final ConsultaDetalle   detallePedido = detalles.get(position);
        if (detallePedido != null) {

         holder.codItem.setText(detallePedido.getCodItem());
       holder.descItem.setText(detallePedido.getDescItem());
        holder.um.setText(detallePedido.getUm());
            holder.cantidad.setText(detallePedido.getCantidad().toString());
            holder.ventaNeta.setText(detallePedido.getVentaNeta().toString());
            holder.stock.setText(detallePedido.getStock().toString());

            if (detallePedido.getStock()-detallePedido.getCantidad()<=0) {
                v.setBackgroundColor(Color.RED);
            } else {
                v.setBackgroundResource(R.drawable.selector_row_det);
            }




          }
        return v;
    }


//    @Nullable
//    @Override
//    public ConsultaCabecera getItem(int position) {
//        return pedidos.get(position);
//    }


}