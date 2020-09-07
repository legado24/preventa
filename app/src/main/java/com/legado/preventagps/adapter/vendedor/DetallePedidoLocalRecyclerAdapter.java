package com.legado.preventagps.adapter.vendedor;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.legado.preventagps.R;
import com.legado.preventagps.dialogs.DialogDetallesPedidosLocales;
import com.legado.preventagps.modelo.CarritoCompras;
import com.legado.preventagps.util.CarritoItemClickListener;
import com.legado.preventagps.util.Metodos;
import com.legado.preventagps.util.SessionUsuario;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by __Adrian__ on 19/4/2017.
 */

public class DetallePedidoLocalRecyclerAdapter extends RecyclerView.Adapter<DetallePedidoLocalRecyclerAdapter.CarritoRecyclerViewHolder> implements CarritoItemClickListener {
    private static final String TAG = "DetallePedidoLocalRecyclerAdapter";

    private List<CarritoCompras> detallePedidos =new ArrayList<>();
    private FragmentActivity activity;
     private Context context;
    private SessionUsuario sessionUsuario;

    Bundle args;
     DialogDetallesPedidosLocales dialogDetallesPedidosLocales;


    public DetallePedidoLocalRecyclerAdapter(List<CarritoCompras> detallePedidos, Context context, FragmentActivity activity, DialogDetallesPedidosLocales dialogDetallesPedidosLocales) {
        this.detallePedidos = detallePedidos;
        this.context=context;
        this.activity=activity;
        this.dialogDetallesPedidosLocales=dialogDetallesPedidosLocales;
    }
    @Override
    public CarritoRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_detallepedidolocal, parent, false);
        sessionUsuario=new SessionUsuario(v.getContext());
        return new CarritoRecyclerViewHolder(v, this);
    }
    @Override
    public void onBindViewHolder(CarritoRecyclerViewHolder holder,final int position) {
        holder.descArticulo.setText(detallePedidos.get(position).getArticulo().getDescItem());
        holder.cantidad.setText(detallePedidos.get(position).getCantidad().toString());
        BigDecimal montoItemTotal=Metodos.redondearDosDecimales(detallePedidos.get(position).getArticulo().getPrecioSugerido().multiply(new BigDecimal(detallePedidos.get(position).getCantidad())));
        holder.montoItem.setText(montoItemTotal.toString());

    }

    @Override
    public int getItemCount() {
        return detallePedidos.size();
    }

    public BigDecimal montoTotalDiario(){
        BigDecimal montoVenta=new BigDecimal(0);
        for (int i = 0; i <detallePedidos.size() ; i++) {
            montoVenta=montoVenta.add(detallePedidos.get(i).getImporte());
        }

        return Metodos.redondearDosDecimales(montoVenta);

    }

    @Override
    public void onItemClick(View view, int position) {

    }


    public static class CarritoRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
         public TextView descArticulo;
         public TextView cantidad;
        public TextView montoItem;

        public CarritoItemClickListener listener;

        public CarritoRecyclerViewHolder(View itemView, CarritoItemClickListener listener) {
            super(itemView);
             descArticulo = (TextView) itemView.findViewById(R.id.descArticulo);
            cantidad = (TextView) itemView.findViewById(R.id.txtCantidad);
            montoItem=(TextView)itemView.findViewById(R.id.txtMontoItem) ;
            this.listener = listener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            listener.onItemClick(view, getAdapterPosition());

        }
    }


}




