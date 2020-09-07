package com.legado.preventagps.adapter.vendedor;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.legado.preventagps.R;
import com.legado.preventagps.modelo.IngresoCompra;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by __Adrian__ on 19/4/2017.
 */

public class IngresoCompraRecyclerAdapter extends RecyclerView.Adapter<IngresoCompraRecyclerAdapter.IngresoCompraRecyclerViewHolder>  {
    private static final String TAG = "IngresoCompraRecyclerAdapter";

    private List<IngresoCompra> ingresoCompras =new ArrayList<>();
    private FragmentActivity activity;
    Fragment fragment;
    Bundle args;

    public IngresoCompraRecyclerAdapter(List<IngresoCompra> ingresoCompras, FragmentActivity activity, Fragment fragment) {
         this.activity=activity;
         this.ingresoCompras=ingresoCompras;
        this.fragment=fragment;
    }
    @Override
    public IngresoCompraRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bandeja_compra, parent, false);
         return new IngresoCompraRecyclerViewHolder(v);
    }
    @Override
    public void onBindViewHolder(final IngresoCompraRecyclerViewHolder holder, final int position) {

        holder.codItem.setText(ingresoCompras.get(position).getCodItem());
        holder.descItem.setText(ingresoCompras.get(position).getDescItem());
        holder.unidadMedida.setText(ingresoCompras.get(position).getUmItem());
        holder.cantidad.setText(ingresoCompras.get(position).getCantidad().toString());
        holder.itemView.setSelected(true);
        holder.itemView.setBackgroundResource(R.drawable.selector_row_cobranza);


    }


    @Override
    public int getItemCount() {
        return ingresoCompras.size();
    }



    public static class IngresoCompraRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView codItem;
        public TextView descItem;
        public TextView unidadMedida;
         public TextView cantidad;
        public TextView hora;



        public IngresoCompraRecyclerViewHolder(View itemView) {
            super(itemView);
             codItem = (TextView) itemView.findViewById(R.id.txtCodItem);
            descItem = (TextView) itemView.findViewById(R.id.txtDescItem);
            unidadMedida = (TextView) itemView.findViewById(R.id.txtUM);
            cantidad = (TextView) itemView.findViewById(R.id.txtCantidad);


         }

        @Override
        public void onClick(View view) {
         }
    }
}





