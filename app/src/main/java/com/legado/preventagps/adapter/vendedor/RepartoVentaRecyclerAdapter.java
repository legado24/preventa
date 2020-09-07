package com.legado.preventagps.adapter.vendedor;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.legado.preventagps.R;
import com.legado.preventagps.enums.STATUSREPARTO;

import java.util.ArrayList;

/**
 * Created by __Adrian__ on 19/4/2017.
 */

public class RepartoVentaRecyclerAdapter extends RecyclerView.Adapter<RepartoVentaRecyclerAdapter.RepartoVentaRecyclerViewHolder>  {
    private static final String TAG = "RepartoVentaRecyclerAdapter";

    private ArrayList repartoVentaList =new ArrayList<>();
    private FragmentActivity activity;
    Fragment fragment;
    Bundle args;

    public RepartoVentaRecyclerAdapter(ArrayList repartoVentaList, FragmentActivity activity, Fragment fragment) {
         this.activity=activity;
         this.repartoVentaList=repartoVentaList;
        this.fragment=fragment;
    }
    @Override
    public RepartoVentaRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bandeja_repartoventa ,parent, false);
         return new RepartoVentaRecyclerViewHolder(v);
    }
    @Override
    public void onBindViewHolder(final RepartoVentaRecyclerViewHolder holder, final int position) {
        ArrayList objReparto= (ArrayList) repartoVentaList.get(position);
       holder.txtNroPedido.setText(objReparto.get(0).toString());
        holder.txtNroDocumento.setText(objReparto.get(1).toString());
        holder.txtMonto.setText(objReparto.get(3).toString());
        holder.txtDescCliente.setText(objReparto.get(2).toString());
        holder.flechaReparto.setColorFilter(ContextCompat.getColor(activity.getApplicationContext(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);//para vectordrawable
        if (objReparto.get(5).toString().equals(STATUSREPARTO.ENTREGADO.getCod())){
           holder.circuloReparto.setColorFilter(ContextCompat.getColor(activity.getApplicationContext(), R.color.colorSuccess), android.graphics.PorterDuff.Mode.MULTIPLY);
            holder.indicadorReparto.setColorFilter(ContextCompat.getColor(activity.getApplicationContext(), R.color.colorSuccess), android.graphics.PorterDuff.Mode.MULTIPLY);
            holder.flechaReparto.setImageResource(R.drawable.ic_arrow_entrega);
            holder.txtMotivo.setVisibility(View.GONE);

        }else{

            holder.circuloReparto.setColorFilter(ContextCompat.getColor(activity.getApplicationContext(), R.color.colorError), android.graphics.PorterDuff.Mode.MULTIPLY);
            holder.indicadorReparto.setColorFilter(ContextCompat.getColor(activity.getApplicationContext(), R.color.colorError), android.graphics.PorterDuff.Mode.MULTIPLY);
            holder.flechaReparto.setImageResource(R.drawable.ic_arrow_rebote);
            holder.txtMotivo.setVisibility(View.VISIBLE);
            holder.txtMotivo.setText(objReparto.get(6).toString());

        }

    }

    public int getPositionByPedido(String nroPedido){
        ArrayList objRepartoAux=null;
        for (int i = 0; i < repartoVentaList.size(); i++) {
            ArrayList objReparto= (ArrayList) repartoVentaList.get(i);
            if(objReparto.get(0).toString().equals(nroPedido)){
                objRepartoAux=objReparto;
                break;
            }
        }

       return repartoVentaList.indexOf(objRepartoAux);
    }

    @Override
    public int getItemCount() {
        return repartoVentaList.size();
    }



    public static class RepartoVentaRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtNroPedido;
        public TextView txtNroDocumento;
       public TextView txtMonto;
       public ImageView circuloReparto;
        public ImageView flechaReparto;
        public ImageView indicadorReparto;
       public TextView txtDescCliente;
       public TextView txtMotivo;







        public RepartoVentaRecyclerViewHolder(View itemView) {
            super(itemView);
            txtNroPedido=(TextView)itemView.findViewById(R.id.txtNroPedido);
            txtNroDocumento = (TextView) itemView.findViewById(R.id.txtNroDocumento);
            txtMonto=(TextView)itemView.findViewById(R.id.txtMonto);
            circuloReparto=(ImageView) itemView.findViewById(R.id.circuloReparto);
            flechaReparto=(ImageView) itemView.findViewById(R.id.flechaReparto);
            indicadorReparto=(ImageView) itemView.findViewById(R.id.indicadorReparto);
            txtDescCliente= (TextView) itemView.findViewById(R.id.txtDescCliente);
            txtMotivo=(TextView)itemView.findViewById(R.id.txtMotivo);


         }

        @Override
        public void onClick(View view) {
         }
    }
}





