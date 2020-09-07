package com.legado.preventagps.adapter.vendedor;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.legado.preventagps.R;
import com.legado.preventagps.modelo.BonificacionItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by __Adrian__ on 19/4/2017.
 */

public class BonifDetPaqueteRecyclerAdapter extends RecyclerView.Adapter<BonifDetPaqueteRecyclerAdapter.BonifDetPaqueteRecyclerViewHolder>  {
    private static final String TAG = "IngresoCompraRecyclerAdapter";

    private List<BonificacionItem> bonificaciones =new ArrayList<>();
    private FragmentActivity activity;
     Bundle args;

    public BonifDetPaqueteRecyclerAdapter(List<BonificacionItem> bonificaciones, FragmentActivity activity) {
         this.activity=activity;
         this.bonificaciones=bonificaciones;

    }
    @Override
    public BonifDetPaqueteRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bandeja_bonifdetpaquete ,parent, false);
         return new BonifDetPaqueteRecyclerViewHolder(v);
    }
    @Override
    public void onBindViewHolder(final BonifDetPaqueteRecyclerViewHolder holder, final int position) {
        holder.codItemBonif.setText(bonificaciones.get(position).getCodItemBonif());
        holder.descItemBonif.setText(bonificaciones.get(position).getDescItemBonif());
        holder.cantBonif.setText(bonificaciones.get(position).getCantBonif().toString());
        holder.cantMin.setText(bonificaciones.get(position).getCantMinima().toString());
        holder.cantMax.setText(bonificaciones.get(position).getCantMaxima().toString());
        holder.multiplo.setText(bonificaciones.get(position).getMultiplo().toString());



    }


    @Override
    public int getItemCount() {
        return bonificaciones.size();
    }



    public static class BonifDetPaqueteRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView cantMin;
        public TextView cantMax;
        public TextView codItemBonif;
        public TextView descItemBonif;
        public TextView umItemBonif;
        public TextView cantBonif;
        public TextView multiplo;






        public BonifDetPaqueteRecyclerViewHolder(View itemView) {
            super(itemView);
              codItemBonif = (TextView) itemView.findViewById(R.id.txtCodItemBonif);
            descItemBonif = (TextView) itemView.findViewById(R.id.txtDescItemBonif);
            cantBonif= (TextView) itemView.findViewById(R.id.txtCantBonif);
            cantMin=(TextView) itemView.findViewById(R.id.txtCuotaMin);
            cantMax=(TextView) itemView.findViewById(R.id.txtCuotaMax);
            multiplo=(TextView) itemView.findViewById(R.id.txtMultiplo);


         }

        @Override
        public void onClick(View view) {
         }
    }
}





