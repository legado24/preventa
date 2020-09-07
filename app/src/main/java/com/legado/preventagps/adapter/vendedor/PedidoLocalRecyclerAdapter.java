package com.legado.preventagps.adapter.vendedor;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.legado.preventagps.R;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.dialogs.DialogDetallesPedidosLocales;
import com.legado.preventagps.enums.STATUSSINCRONIZACION;
import com.legado.preventagps.modelo.PreVenta;
import com.legado.preventagps.util.AdapterCommunication;
import com.legado.preventagps.util.PedidoLocalItemClickListener;

import java.util.List;

public class PedidoLocalRecyclerAdapter extends RecyclerView.Adapter<PedidoLocalRecyclerAdapter.PedidoLocalRecyclerViewHolder> implements PedidoLocalItemClickListener {
    private static final String TAG = "PedidoLocalRecyclerAdapter";

    private List<PreVenta> pedidosLocales;
    private FragmentActivity activity;
    private AdapterCommunication mListener;
    private Context context;


    public void setOnClickListener(AdapterCommunication listener) {
        mListener = listener;
    }

    public PedidoLocalRecyclerAdapter(List<PreVenta> pedidosLocales, Context context, FragmentActivity activity) {
        this.pedidosLocales = pedidosLocales;
        this.context = context;
        this.activity = activity;


    }


    @Override
    public PedidoLocalRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_consultpedidolocal, parent, false);
        return new PedidoLocalRecyclerViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(PedidoLocalRecyclerViewHolder holder, final int position) {
        holder.codCliente.setText(pedidosLocales.get(position).getCodCliente());
        holder.cliente.setText(pedidosLocales.get(position).getDescCliente());
        holder.monto.setText(pedidosLocales.get(position).getMontoVenta().toString());
        holder.condPago.setText(pedidosLocales.get(position).getCodCondicion());
        holder.itemView.setBackgroundResource(R.drawable.selector_row);
        holder.itemView.setSelected(true);


        if (pedidosLocales.get(position).getStatus().equals(STATUSSINCRONIZACION.PENDIENTE.getCod())) {// pendiente
            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.btnEliminar.setVisibility(View.VISIBLE);
        } else if (pedidosLocales.get(position).getStatus().equals(STATUSSINCRONIZACION.SINCRONIZADO.getCod())) {// SINCRONIZADO
            holder.itemView.setBackgroundResource(R.color.material_green_300);
            holder.btnEliminar.setVisibility(View.INVISIBLE);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.selector_row);
        }

        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Desea elminar el pedido  ?");
                builder.setMessage("Se eliminará el pedido almacenado en su celular de forma permanente!!");
                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
                            ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().deletePedidoLocalById(pedidosLocales.get(position).getUid());
                            ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().deleteDetallePedidoLocalById(pedidosLocales.get(position).getUid());
                            ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                        } finally {
                            ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                        }
                        pedidosLocales.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, pedidosLocales.size());

                    }
                });
                builder.setNegativeButton("NO", null);
                builder.show();
            }
        });


    }



    @Override
    public int getItemCount() {
        return pedidosLocales.size();
    }

    @Override
    public void onItemClick(View view, final int position) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        DialogDetallesPedidosLocales newFragment = DialogDetallesPedidosLocales.newInstance();
        Bundle args = new Bundle();
        args.putString("nroPedidoLocal", pedidosLocales.get(position).getUid());
        newFragment.setArguments(args);
        newFragment.show(ft, "find");
    }

    @Override
    public void updateRecycler() {
        notifyDataSetChanged();
    }


    public static class PedidoLocalRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView codCliente;
        public TextView cliente;
        public TextView monto;
        public TextView condPago;
        public ImageView btnEliminar;

        public PedidoLocalItemClickListener listener;

        public PedidoLocalRecyclerViewHolder(View itemView, PedidoLocalItemClickListener listener) {
            super(itemView);
            codCliente = (TextView) itemView.findViewById(R.id.txtCodCliente);
            cliente = (TextView) itemView.findViewById(R.id.txtCliente);
            monto = (TextView) itemView.findViewById(R.id.txtMonto);
            condPago = (TextView) itemView.findViewById(R.id.txtCondicionPago);
            btnEliminar = (ImageView) itemView.findViewById(R.id.btnEliminar);

            this.listener = listener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            listener.onItemClick(view, getAdapterPosition());

        }
    }


}





