package com.legado.preventagps.adapter.vendedor;


import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.legado.preventagps.R;
import com.legado.preventagps.modelo.BonificacionItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by __Adrian__ on 19/4/2017.
 */

public class BonificacionItemRecyclerAdapter extends RecyclerView.Adapter<BonificacionItemRecyclerAdapter.BonificacionItemRecyclerViewHolder>  {
    private static final String TAG = "IngresoCompraRecyclerAdapter";

    private List<BonificacionItem> bonificaciones =new ArrayList<>();
    private FragmentActivity activity;
    Fragment fragment;
    Bundle args;

    public BonificacionItemRecyclerAdapter(List<BonificacionItem> bonificaciones, FragmentActivity activity, Fragment fragment) {
         this.activity=activity;
         this.bonificaciones=bonificaciones;
        this.fragment=fragment;
    }
    @Override
    public BonificacionItemRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bandeja_bonifitem ,parent, false);
         return new BonificacionItemRecyclerViewHolder(v);
    }
    @Override
    public void onBindViewHolder(final BonificacionItemRecyclerViewHolder holder, final int position) {
        holder.codItemVenta.setText(bonificaciones.get(position).getCodItemVenta());
        holder.descItemVenta.setText(bonificaciones.get(position).getDescItem());
        holder.codItemBonif.setText(bonificaciones.get(position).getCodItemBonif());
        holder.descItemBonif.setText(bonificaciones.get(position).getDescItemBonif());

        String cadenaAux1="POR CADA ";
        int long1=cadenaAux1.length();
        String cadenaCantidadItem=bonificaciones.get(position).getCantMinima().toString();
        int long2=cadenaCantidadItem.length();
        String cadenaUmVenta=" "+bonificaciones.get(position).getUmItemVenta();
        int long3=cadenaUmVenta.length();
        String cadenaAux2=" DE ";
        int long4=cadenaAux2.length();
        String cadenaCodItemVenta=bonificaciones.get(position).getCodItemVenta();
        int long5=cadenaCodItemVenta.length();
        String cadenaAux3=" SE BONIFICA ";
        int long6=cadenaAux3.length();
        String cadenaCantBonif=bonificaciones.get(position).getCantBonif().toString();
        int long7=cadenaCantBonif.length();
        String cadenaUmBonif=" "+bonificaciones.get(position).getUmItemBonif();
        int long8=cadenaUmBonif.length();
        String cadenaCodItemBonif=bonificaciones.get(position).getCodItemBonif();
        int long9=cadenaCodItemBonif.length();



        String mensajeBonif= cadenaAux1+ cadenaCantidadItem+cadenaUmVenta+
                cadenaAux2+cadenaCodItemVenta+cadenaAux3+ cadenaCantBonif+ cadenaUmBonif +cadenaAux2+cadenaCodItemBonif;

        SpannableStringBuilder texto= new SpannableStringBuilder(mensajeBonif);

        StyleSpan letraEnNegrita1= new StyleSpan(android.graphics.Typeface.BOLD); // Para hacer negrita
        StyleSpan letraEnNegrita2 = new StyleSpan(Typeface.BOLD);
        StyleSpan letraEnNegrita3 = new StyleSpan(Typeface.BOLD);
        StyleSpan letraEnNegrita4 = new StyleSpan(Typeface.BOLD);

        texto.setSpan(letraEnNegrita1, long1,long1+ long2+long3, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // Convierte los primeros 4 caracteres en negrita, tu puedes decirle cuantos caracteres :)
        texto.setSpan(letraEnNegrita2, long1+ long2+long3+long4,long1+ long2+long3+long4+long5, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // Convierte los primeros 4 caracteres en negrita, tu puedes decirle cuantos caracteres :)
        texto.setSpan(letraEnNegrita3, long1+ long2+long3+long4+long5+long6,long1+ long2+long3+long4+long5+long6+long7+long8, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // Convierte los primeros 4 caracteres en negrita, tu puedes decirle cuantos caracteres :)
        texto.setSpan(letraEnNegrita4, long1+ long2+long3+long4+long5+long6+long7+long8+long4,mensajeBonif.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // Convierte los primeros 4 caracteres en negrita, tu puedes decirle cuantos caracteres :)

        holder.mensajeBonif.setText(texto);




    }


    @Override
    public int getItemCount() {
        return bonificaciones.size();
    }



    public static class BonificacionItemRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView codListaPrecios;
        public TextView codItemVenta;
        public TextView descItemVenta;
        public TextView umItemVenta;
        public TextView cantMin;
        public TextView cantMax;
        public TextView codItemBonif;
        public TextView descItemBonif;
        public TextView umItemBonif;

        public TextView mensajeBonif;






        public BonificacionItemRecyclerViewHolder(View itemView) {
            super(itemView);
         //   codListaPrecios=(TextView)itemView.findViewById(R.id.)
             codItemVenta = (TextView) itemView.findViewById(R.id.txtCodItemVenta);
            descItemVenta = (TextView) itemView.findViewById(R.id.txtDescItemVenta);
            codItemBonif= (TextView) itemView.findViewById(R.id.txtCodBonif);
            descItemBonif= (TextView) itemView.findViewById(R.id.txtDescBonif);
            mensajeBonif= (TextView) itemView.findViewById(R.id.txtMensajeBonif);

         }

        @Override
        public void onClick(View view) {
         }
    }
}





