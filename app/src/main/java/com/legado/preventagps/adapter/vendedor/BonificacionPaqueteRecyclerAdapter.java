package com.legado.preventagps.adapter.vendedor;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.legado.preventagps.R;
import com.legado.preventagps.modelo.BonificacionPaquete;
import com.legado.preventagps.util.Tools;
import com.legado.preventagps.util.ViewAnimation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by __Adrian__ on 19/4/2017.
 */

public class BonificacionPaqueteRecyclerAdapter extends RecyclerView.Adapter<BonificacionPaqueteRecyclerAdapter.BonificacionPaqueteRecyclerViewHolder>  {
    private static final String TAG = "BonificacionPaqueteRecyclerAdapter";

    private List<BonificacionPaquete> bonificaciones =new ArrayList<>();
    private FragmentActivity activity;
   // Fragment fragment;
    Bundle args;
    BonificacionPaqueteRecyclerAdapter participacionPaqueteRecyclerAdapter;
    BonifDetPaqueteRecyclerAdapter bonificacionPaqueteRecyclerAdapter;
    String tipo;

    public BonificacionPaqueteRecyclerAdapter(List<BonificacionPaquete> bonificaciones, FragmentActivity activity, String tipo) {
         this.activity=activity;
         this.bonificaciones=bonificaciones;
        this.tipo=tipo;
    }
    @Override
    public BonificacionPaqueteRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      // View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bandeja_bonifpaquete ,parent, false);

        View v=null;
        if(tipo.equals("C")){
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bandeja_bonifpaquete ,parent, false);

        }else if(tipo.equals("D")){
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bandeja_bonifpaquetedet ,parent, false);

        }
         return new BonificacionPaqueteRecyclerViewHolder(v,tipo);
    }
    @Override
    public void onBindViewHolder(final BonificacionPaqueteRecyclerViewHolder holder, final int position) {


        if(tipo.equals("C")){
            holder.codPaquete.setText(bonificaciones.get(position).getCodPaquete());
            holder.descPaquete.setText(bonificaciones.get(position).getDescPaquete());
            holder.codListaPrecios.setText(bonificaciones.get(position).getCodLista());
            holder.bt_toggle_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleSectionInfo( view, holder);
                }
            });
            holder.recicladorParticipacion.setVisibility(View.VISIBLE);
            holder.recicladorParticipacion.setHasFixedSize(true);
            LinearLayoutManager lManager = new LinearLayoutManager(activity);
            holder.recicladorParticipacion.setLayoutManager(lManager);
            participacionPaqueteRecyclerAdapter = new BonificacionPaqueteRecyclerAdapter(bonificaciones.get(position).getParticipaciones(), activity, "D");
            holder.recicladorParticipacion.setAdapter(participacionPaqueteRecyclerAdapter);


            holder.recicladorBonificacion.setVisibility(View.VISIBLE);
            holder.recicladorBonificacion.setHasFixedSize(true);
            LinearLayoutManager lManagerB = new LinearLayoutManager(activity);
            holder.recicladorBonificacion.setLayoutManager(lManagerB);
            bonificacionPaqueteRecyclerAdapter = new BonifDetPaqueteRecyclerAdapter(bonificaciones.get(position).getBonificaciones(), activity);
            holder.recicladorBonificacion.setAdapter(bonificacionPaqueteRecyclerAdapter);

        }else if(tipo.equals("D")){
            holder.txtCodItem.setText(bonificaciones.get(position).getCodItem());
            holder.txtDescItem.setText(bonificaciones.get(position).getDescItem());


        }






    }


    @Override
    public int getItemCount() {
        return bonificaciones.size();
    }

    private void toggleSectionInfo(View view, BonificacionPaqueteRecyclerViewHolder holder) {
        boolean show = toggleArrow(view);
        if (show) {
            ViewAnimation.expand(holder.lyt_expand_info, new ViewAnimation.AnimListener() {
                @Override
                public void onFinish() {
                    Tools.nestedScrollTo(holder.nested_scroll_view, holder.lyt_expand_info);
                }
            });
        } else {
            ViewAnimation.collapse(holder.lyt_expand_info);
        }
    }
    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }


    public static class BonificacionPaqueteRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView codListaPrecios;
        public TextView codPaquete;
        public TextView descPaquete;
        public TextView umItemVenta;
        public TextView cantMin;
        public TextView cantMax;
        public TextView codItemBonif;
        public TextView descItemBonif;
        public TextView umItemBonif;

        public TextView mensajeBonif;

        private  NestedScrollView nested_scroll_view;
        private ImageButton bt_toggle_info;
        private Button bt_hide_info;
        private View lyt_expand_info;
        private  TextView txtVerArticulos;
        private RecyclerView recicladorParticipacion;
        private RecyclerView recicladorBonificacion;


        private TextView txtCodItem;
        private TextView txtDescItem;


        public BonificacionPaqueteRecyclerViewHolder(View itemView,String tipo) {
            super(itemView);
            if(tipo.equals("C")){

                nested_scroll_view=(NestedScrollView) itemView.findViewById(R.id.nested_scroll_view);
                bt_toggle_info=(ImageButton) itemView.findViewById(R.id.bt_toggle_info);
                codPaquete = (TextView) itemView.findViewById(R.id.txtCodPaquete);
                descPaquete = (TextView) itemView.findViewById(R.id.txtDescPaquete);
                bt_hide_info = (Button) itemView.findViewById(R.id.bt_hide_info);
                lyt_expand_info=(View) itemView.findViewById(R.id.lyt_expand_info);
                txtVerArticulos= (TextView) itemView.findViewById(R.id.txtVerArticulos);
                codListaPrecios=(TextView)itemView.findViewById(R.id.txtCodLista);
                recicladorParticipacion=(RecyclerView)itemView.findViewById(R.id.recicladorParticipacion);
                recicladorBonificacion=(RecyclerView)itemView.findViewById(R.id.recicladorBonificacion);

            }else if(tipo.equals("D")){
                txtCodItem=(TextView)itemView.findViewById(R.id.txtCodItem) ;
                txtDescItem=(TextView)itemView.findViewById(R.id.txtDescItem) ;

            }



         }

        @Override
        public void onClick(View view) {
         }
    }
}





