package com.legado.preventagps.adapter.vendedor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.legado.preventagps.R;
import com.legado.preventagps.dialogs.MyDialogLocalMapaEdit;
import com.legado.preventagps.dialogs.MyDialogLocalMapaEditv2;
import com.legado.preventagps.enums.STATUS;
import com.legado.preventagps.modelo.LocalCliente;

import java.util.List;

/**
 * Created by __Adrian__ on 19/4/2017.
 */
/*
public class DireccionRecyclerAdapter extends RecyclerView.Adapter<DireccionRecyclerAdapter.DireccionRecyclerViewHolder> implements DireccionItemClickListener {
    private static final String TAG = DireccionRecyclerAdapter.class.getName();

    private List<LocalCliente> direcciones;
    private AppCompatActivity activity;
    private String codigoCliente;
    MyDialogLocalMapaEdit.OnDialogFinishCallback listenerUpdate;
    public DireccionRecyclerAdapter(List<LocalCliente> direcciones, AppCompatActivity activity) {
        this.direcciones = direcciones;
        this.activity = activity;
     }

    public DireccionRecyclerAdapter(List<LocalCliente> direcciones, String codigoCliente, AppCompatActivity activity, MyDialogLocalMapaEdit.OnDialogFinishCallback listenerUpdate) {
        this.direcciones = direcciones;
        this.listenerUpdate=listenerUpdate;
        this.activity = activity;
        this.codigoCliente=codigoCliente;
    }

    public List<LocalCliente> getDirecciones() {
        return direcciones;
    }

    @Override
    public DireccionRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_direccion, parent, false);

        return new DireccionRecyclerViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(DireccionRecyclerViewHolder holder, int position) {

     holder.codLocal.setText(direcciones.get(position).getCodLocal());
         holder.descLocal.setText(direcciones.get(position).getDireccion());
        holder.codRuta.setText(direcciones.get(position).getCodRuta());
        holder.descRuta.setText(direcciones.get(position).getDescRuta());
        holder.status.setText(direcciones.get(position).getStatus());

      holder.itemView.setSelected(true);
        if (direcciones.get(position).getStatus().equals(STATUS.INACTIVO.getCod())) {
            holder.itemView.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.material_red_300));
        }else {
            holder.itemView.setBackgroundResource(R.drawable.selector_row);
        }

//      holder.btnEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
//
//                MyDialogLocalMapaEdit newFragment = MyDialogLocalMapaEdit.newInstance(direcciones.get(position));
//
//                newFragment.show(ft, "edit");
//            }
//        });
    }
    public  void addItem(LocalCliente lc){
        direcciones.add(lc);
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return direcciones.size();
    }

    @Override
    public void onItemClick(View view, int position) {

    }


    public static class DireccionRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView codLocal;
        public TextView descLocal;
        public TextView codRuta;
        public TextView descRuta;
        public TextView status;

//        public Button btnEdit;


        public DireccionItemClickListener listener;

        public DireccionRecyclerViewHolder(View itemView, DireccionItemClickListener listener) {
            super(itemView);

            codLocal = (TextView) itemView.findViewById(R.id.codLocal);
            descLocal = (TextView) itemView.findViewById(R.id.descLocal);
            codRuta = (TextView) itemView.findViewById(R.id.codRuta);
            descRuta = (TextView) itemView.findViewById(R.id.descRuta);
//            btnEdit=(Button)itemView.findViewById(R.id.btnEditar);
            status=(TextView)itemView.findViewById(R.id.status) ;


            this.listener = listener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            listener.onItemClick(view, getAdapterPosition());
        }
    }


}

interface DireccionItemClickListener {

    void onItemClick(View view, int position);
}
*/


import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.legado.preventagps.R;
import com.legado.preventagps.dialogs.MyDialogLocalMapa;
import com.legado.preventagps.dialogs.MyDialogLocalMapaEdit;
import com.legado.preventagps.enums.STATUS;
import com.legado.preventagps.modelo.LocalCliente;

import java.util.List;

/**
 * Created by __Adrian__ on 19/4/2017.
 */

public class DireccionRecyclerAdapter extends RecyclerView.Adapter<DireccionRecyclerAdapter.DireccionRecyclerViewHolder> implements DireccionItemClickListener {
    private static final String TAG = DireccionRecyclerAdapter.class.getName();

    private List<LocalCliente> direcciones;
    private AppCompatActivity activity;
    private String codigoCliente;
    MyDialogLocalMapaEdit.OnDialogFinishCallback listenerUpdate;

    String codLocal;


    public DireccionRecyclerAdapter(List<LocalCliente> direcciones, String codigoCliente, AppCompatActivity activity, MyDialogLocalMapaEdit.OnDialogFinishCallback listenerUpdate) {
        this.direcciones = direcciones;
        this.listenerUpdate=listenerUpdate;
        this.activity = activity;
        this.codigoCliente=codigoCliente;
    }
    public DireccionRecyclerAdapter(List<LocalCliente> direcciones, String codigoCliente, AppCompatActivity activity, MyDialogLocalMapaEdit.OnDialogFinishCallback listenerUpdate,String codLocal) {
        this.direcciones = direcciones;
        this.listenerUpdate=listenerUpdate;
        this.activity = activity;
        this.codigoCliente=codigoCliente;
        this.codLocal=codLocal;
    }


    public List<LocalCliente> getDirecciones() {
        return direcciones;
    }

    @Override
    public DireccionRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_direccion, parent, false);

        return new DireccionRecyclerViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(DireccionRecyclerViewHolder holder, int position) {

//        holder.codLocal.setText(direcciones.get(position).getCodLocal());
        holder.descLocal.setText(direcciones.get(position).getDireccion());
       // holder.codRuta.setText(direcciones.get(position).getCodRuta());
        holder.descRuta.setText(direcciones.get(position).getCodRuta()+"-"+direcciones.get(position).getDescRuta());
       // holder.status.setText(direcciones.get(position).getStatus());




        // holder.txtObservacion.setText(direcciones.get(position).getObservacionesAudi());
        holder.itemView.setSelected(true);
        //  holder.itemView.setBackgroundResource(R.drawable.selector_row);
        if (direcciones.get(position).getStatus().equals(STATUS.INACTIVO.getCod())) {
            holder.itemView.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.material_red_300));
        }else {
            holder.itemView.setBackgroundResource(R.drawable.selector_row);
        }
        if (codLocal!=null) {
            if (codLocal.equals(direcciones.get(position).getCodLocal())||codLocal.equals("0")) {
                holder.btnEdit.setVisibility(View.VISIBLE);
            } else {
                holder.btnEdit.setVisibility(View.GONE);
            }
        }

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                MyDialogLocalMapaEditv2 newFragment = MyDialogLocalMapaEditv2.newInstance(direcciones.get(position),codigoCliente,listenerUpdate);
                newFragment.show(ft, "edit");
            }
        });
    }

    public  void addItem(LocalCliente lc){
        direcciones.add(lc);
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return direcciones.size();
    }

    @Override
    public void onItemClick(View view, int position) {

    }


    public static class DireccionRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
      //  public TextView codLocal;
        public TextView descLocal;
        //public TextView txtReferencia;
        // public TextView txtObservacion;
       // public TextView codRuta;
        public TextView descRuta;
      //  public LinearLayout layoutReferencia;
        public ImageButton btnEdit;
        //public TextView status;

        public DireccionItemClickListener listener;

        public DireccionRecyclerViewHolder(View itemView, DireccionItemClickListener listener) {
            super(itemView);

        //    codLocal = (TextView) itemView.findViewById(R.id.codLocal);
            descLocal = (TextView) itemView.findViewById(R.id.descLocal);
        //    txtReferencia=(TextView) itemView.findViewById(R.id.txtReferencia);
            //txtObservacion=(TextView)itemView.findViewById(R.id.txtObservaciones) ;
            //codRuta = (TextView) itemView.findViewById(R.id.codRuta);
            descRuta = (TextView) itemView.findViewById(R.id.descRuta);
            //status=(TextView) itemView.findViewById(R.id.status);
            btnEdit=(ImageButton)itemView.findViewById(R.id.btnEditar);
          //  layoutReferencia=(LinearLayout)itemView.findViewById(R.id.layoutReferencia);

            this.listener = listener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            listener.onItemClick(view, getAdapterPosition());
        }
    }


}

interface DireccionItemClickListener {

    void onItemClick(View view, int position);
}

