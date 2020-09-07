package com.legado.preventagps.adapter.vendedor;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.legado.preventagps.R;
import com.legado.preventagps.enums.STATUSCOBRANZA;

import com.legado.preventagps.modelo.ConsultaPagos;
import com.legado.preventagps.util.SessionUsuario;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by __Adrian__ on 24/05/2019.
 */

public class PagosRecyclerAdapter  extends RecyclerView.Adapter<PagosRecyclerAdapter.PagosRecyclerViewHolder> {
    private static final String TAG = "CabeceraRecyclerAdapter";

    private List<ConsultaPagos> pedidos;
    private FragmentActivity activity;
    SwipeRefreshLayout swiperefresh;
    SessionUsuario sessionUsuario;
    Fragment fragment;
    String fecha;
    Context c;
    public PagosRecyclerAdapter(List<ConsultaPagos> pedidos, FragmentActivity activity,Fragment fragment,SwipeRefreshLayout swiperefresh,String fecha) {
        this.pedidos = pedidos;
        this.activity = activity;
        this.fragment=fragment;
        sessionUsuario=new SessionUsuario(activity);
        this.swiperefresh=swiperefresh;
        this.fecha=fecha;
    }

    @Override
    public PagosRecyclerAdapter.PagosRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.listconsulta_pagos,parent,false);
        return new PagosRecyclerAdapter.PagosRecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PagosRecyclerAdapter.PagosRecyclerViewHolder holder, int position) {
      holder.serie.setText(pedidos.get(position).getSerie() );
        holder.preimpreso.setText(pedidos.get(position).getPreimpreso());
        holder.fechaEmision.setText(pedidos.get(position).getFecha());
        holder.saldo.setText(pedidos.get(position).getSaldo().toString());
        holder.descCliente.setText(pedidos.get(position).getDescCliente().toString());
        holder.montoCobrado.setText(pedidos.get(position).getMontoCobrado().toString());
        holder.estadoCobranza.setText(pedidos.get(position).getStatusCobranza());
        holder.itemView.setSelected(true);
        if (pedidos.get(position).getStatusCobranza().equals(STATUSCOBRANZA.LIQUIDADO.getDesc())) {
            holder.itemView.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.material_green_200));
        }else {
            holder.itemView.setBackgroundResource(R.drawable.selector_row);
        }

    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

//    @Override
//    public void onItemClick(View view, int position) {
//        Fragment fragment= new DetalleConsultaFragment();
//        Bundle args = new Bundle();
//        args.putString("codEmpresa", pedidos.get(position).getCodEmpresa());
//        args.putString("nroPedido",pedidos.get(position).getNroPedido());
//        fragment.setArguments(args);
//        FragmentTransaction ft =  activity.getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.content_consulta_pedidos, fragment,TAG);
//        ft.addToBackStack( TAG);
//        ft.commit();
  //  }

//    @Override
//    public void onItemLongClick(View view,final int position) {
//        if (pedidos.get(position).getStatusPedido().equals("INGRESADO")) {
//            new AlertDialog.Builder(activity).setTitle("CONFIRMACIÓN")
//                    .setMessage("Desea anular el pedido " + pedidos.get(position).getNroPedido() + "?")
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int whichButton) {
//                            final ConsultaCabecera pedidoAnular = pedidos.get(position);
//                            pedidoAnular.setUsuarioAnulacion(sessionUsuario.getUsuario());
//                            anularPedido(pedidoAnular);
//                        }
//                    })
//                    .setNegativeButton(android.R.string.no, null).show();
//        }
  //  }

    public BigDecimal montoTotalDiario(){
        BigDecimal montoVenta=new BigDecimal(0);
        for (int i = 0; i <pedidos.size() ; i++) {
//            if(!pedidos.get(i).getStatusCobranza().equals("ANULADO")) {
                montoVenta = montoVenta.add(pedidos.get(i).getMontoCobrado());
//            }
        }
        return montoVenta;
    }
//    public List<Integer> cantidadPedidos(){
//        List<Integer> cantidadPedidos=new ArrayList<>();
//        Integer cantidadAnulados=0;
//        Integer cantidadAprobados=0;
//        Integer cantidadIngresados=0;
//        for (int i = 0; i <pedidos.size() ; i++) {
//            if(pedidos.get(i).getStatusPedido().equals("ANULADO")) {
//                cantidadAnulados = cantidadAnulados+1;
//            }else if(pedidos.get(i).getStatusPedido().equals("APROBADO")){
//                cantidadAprobados=cantidadAprobados+1;
//            }else{
//                cantidadIngresados=cantidadIngresados+1;
//            }
//        }
//        cantidadPedidos.add(cantidadIngresados);
//        cantidadPedidos.add(cantidadAprobados);
//        cantidadPedidos.add(cantidadAnulados);
//        return cantidadPedidos;
//    }
     public static class PagosRecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView serie;
        public TextView preimpreso;
         public TextView fechaEmision;
        public TextView saldo;
        public TextView descCliente;
         public TextView montoCobrado;
        public TextView estadoCobranza;


        public PagosRecyclerViewHolder(View itemView) {
            super(itemView);
            serie=(TextView) itemView.findViewById(R.id.txtSerie);
            preimpreso=(TextView) itemView.findViewById(R.id.txtPreImpreso);
            fechaEmision=(TextView) itemView.findViewById(R.id.txtFechaEmision);
            saldo=(TextView) itemView.findViewById(R.id.txtSaldo);
            montoCobrado=(TextView) itemView.findViewById(R.id.txtMonto);
            estadoCobranza=(TextView) itemView.findViewById(R.id.txtEstado);
            descCliente=(TextView) itemView.findViewById(R.id.txtCliente);
//            montoFinal=(TextView) itemView.findViewById(R.id.monto);
//            condicionPago= (TextView) itemView.findViewById(R.id.condicionPago);
//            montoFacturado= (TextView) itemView.findViewById(R.id.txtMontoFacturado);


        }


    }


}

//interface ItemClickListener{
//
//    void onItemClick(View view, int position);
//    void onItemLongClick(View view, int position);
//}
//

