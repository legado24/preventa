package com.legado.preventagps.adapter.vendedor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.legado.preventagps.R;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.enums.STATUSPEDIDO;
import com.legado.preventagps.fragments.ConsultaPedidoFragment;
import com.legado.preventagps.fragments.DetalleConsultaFragment;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.ConsultaCabecera;
import com.legado.preventagps.util.SessionUsuario;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by __Adrian__ on 19/4/2017.
 */

public class CabeceraRecyclerAdapter  extends RecyclerView.Adapter<CabeceraRecyclerAdapter.CabeceraRecyclerViewHolder> implements  ItemClickListener{
    private static final String TAG = "CabeceraRecyclerAdapter";

    private List<ConsultaCabecera> pedidos;
    private FragmentActivity activity;
    SwipeRefreshLayout swiperefresh;
  SessionUsuario sessionUsuario;
    Fragment fragment;
    String fecha;
    String codLocalidad;
    String codAlmacen;

    public CabeceraRecyclerAdapter(List<ConsultaCabecera> pedidos, FragmentActivity activity,Fragment fragment,SwipeRefreshLayout swiperefresh,String fecha,String codLocalidad,String codAlmacen) {
        this.pedidos = pedidos;
        this.activity = activity;
        this.fragment=fragment;
        sessionUsuario=new SessionUsuario(activity);
        this.swiperefresh=swiperefresh;
        this.fecha=fecha;
        this.codLocalidad=codLocalidad;
        this.codAlmacen=codAlmacen;
     }

    @Override
    public CabeceraRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.listcabecera_pedido,parent,false);
        return new CabeceraRecyclerViewHolder(v,this);
    }

    @Override
    public void onBindViewHolder(CabeceraRecyclerViewHolder holder, int position) {
        holder.nroPedido.setText(pedidos.get(position).getNroPedido() );
        holder.descCliente.setText(pedidos.get(position).getDescCliente());
        holder.status.setText(pedidos.get(position).getStatusPedido());
        holder.montoFinal.setText(pedidos.get(position).getMontoFinal().toString());
        holder.montoFacturado.setText(pedidos.get(position).getMontoFacturado().toString());
        holder.condicionPago.setText(pedidos.get(position).getCodCondicion());
        holder.itemView.setSelected(true);
        if (pedidos.get(position).getStatusPedido().equals(STATUSPEDIDO.ANULADO.getDesc())) {
        holder.itemView.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.material_deep_orange_A400));
            holder.nroPedido.setTextColor(activity.getResources().getColor(R.color.white));
            holder.descCliente.setTextColor(activity.getResources().getColor(R.color.white));
            holder.status.setTextColor(activity.getResources().getColor(R.color.white));
            holder.montoFinal.setTextColor(activity.getResources().getColor(R.color.white));
            holder.montoFacturado.setTextColor(activity.getResources().getColor(R.color.white));
            holder.condicionPago.setTextColor(activity.getResources().getColor(R.color.white));

        }else if(pedidos.get(position).getStatusPedido().equals(STATUSPEDIDO.APROBADO.getDesc())) {

            holder.itemView.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.material_green_A700));
            holder.nroPedido.setTextColor(activity.getResources().getColor(R.color.white));
            holder.descCliente.setTextColor(activity.getResources().getColor(R.color.white));
            holder.status.setTextColor(activity.getResources().getColor(R.color.white));
            holder.montoFinal.setTextColor(activity.getResources().getColor(R.color.white));
            holder.montoFacturado.setTextColor(activity.getResources().getColor(R.color.white));
            holder.condicionPago.setTextColor(activity.getResources().getColor(R.color.white));
        }else if(pedidos.get(position).getStatusPedido().equals(STATUSPEDIDO.INGRESADO.getDesc())) {

            holder.itemView.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.material_orange_600));
            holder.nroPedido.setTextColor(activity.getResources().getColor(R.color.white));
            holder.descCliente.setTextColor(activity.getResources().getColor(R.color.white));
            holder.status.setTextColor(activity.getResources().getColor(R.color.white));
            holder.montoFinal.setTextColor(activity.getResources().getColor(R.color.white));
            holder.montoFacturado.setTextColor(activity.getResources().getColor(R.color.white));
            holder.condicionPago.setTextColor(activity.getResources().getColor(R.color.white));
        }else{
            holder.itemView.setBackgroundResource(R.drawable.selector_row);
        }

    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    @Override
    public void onItemClick(View view, int position) {
        Fragment fragment= new DetalleConsultaFragment();
        Bundle args = new Bundle();
        args.putString("codEmpresa", pedidos.get(position).getCodEmpresa());
        args.putString("nroPedido",pedidos.get(position).getNroPedido());
        args.putString("descCliente",pedidos.get(position).getDescCliente());
        args.putString("codAlmacen",codAlmacen);
        fragment.setArguments(args);
        FragmentTransaction ft =  activity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_consulta_pedidos, fragment,TAG);
        ft.addToBackStack("ConsultaPedidoFragment");
        ft.commit();
    }

    @Override
    public void onItemLongClick(View view,final int position) {
        if (pedidos.get(position).getStatusPedido().equals(STATUSPEDIDO.INGRESADO.getDesc())) {
            new AlertDialog.Builder(activity).setTitle("CONFIRMACIÓN")
                    .setMessage("Desea anular el pedido " + pedidos.get(position).getNroPedido() + "?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            final ConsultaCabecera pedidoAnular = pedidos.get(position);
                            pedidos.get(position).setStatusPedido("I");
                            pedidoAnular.setUsuarioAnulacion(sessionUsuario.getUsuario());
                            anularPedido(pedidoAnular);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }

    public BigDecimal montoTotalDiario(){
        BigDecimal montoVenta=new BigDecimal(0);
        for (int i = 0; i <pedidos.size() ; i++) {
            if(pedidos.get(i).getStatusPedido().equals(STATUSPEDIDO.INGRESADO.getDesc()) ||pedidos.get(i).getStatusPedido().equals(STATUSPEDIDO.APROBADO.getDesc())) {
                montoVenta = montoVenta.add(pedidos.get(i).getMontoFinal());
           }
        }
        return montoVenta;
    }
    public List<Integer> cantidadPedidos(){
        List<Integer> cantidadPedidos=new ArrayList<>();
       Integer cantidadAnulados=0;
        Integer cantidadAprobados=0;
        Integer cantidadIngresados=0;
        for (int i = 0; i <pedidos.size() ; i++) {
            if(pedidos.get(i).getStatusPedido().equals(STATUSPEDIDO.ANULADO.getDesc())) {
                cantidadAnulados = cantidadAnulados+1;
            }else if(pedidos.get(i).getStatusPedido().equals(STATUSPEDIDO.APROBADO.getDesc())){
                cantidadAprobados=cantidadAprobados+1;
            }else if(pedidos.get(i).getStatusPedido().equals(STATUSPEDIDO.INGRESADO.getDesc())){
                cantidadIngresados=cantidadIngresados+1;
            }
        }
        cantidadPedidos.add(cantidadIngresados);
        cantidadPedidos.add(cantidadAprobados);
        cantidadPedidos.add(cantidadAnulados);
        return cantidadPedidos;
    }
    public void anularPedido(ConsultaCabecera cabecera) {
        Call<JsonRespuesta<Integer>> call = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getVentaService().anularPedido(cabecera);
        call.enqueue(new Callback<JsonRespuesta<Integer>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<Integer>> call, retrofit2.Response<JsonRespuesta<Integer>> response) {
                if(response.body()!=null){
                    if (response.body().getEstado() == 1) {
                        Toast.makeText(activity, "Se anuló correctamente el pedido", Toast.LENGTH_SHORT).show();
                        swiperefresh.post(new Runnable() {
                            @Override public void run() {
                                swiperefresh.setRefreshing(false);
                                ( (ConsultaPedidoFragment) fragment).cargarPedidos(fecha,codLocalidad,codAlmacen);
                            }
                        });


                    } else if (response.body().getEstado() == -1) {
                        Toast.makeText(activity, response.body().getMensaje(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(activity.getString(R.string.txtMensajeServidorCaido))
                            .show();
                }


            }

            @Override
            public void onFailure(Call<JsonRespuesta<Integer>> call, Throwable t) {
                new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(activity.getString(R.string.txtMensajeConexion))
                        .show();

            }
        });
    }
    public static class CabeceraRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView nroPedido;
        public TextView codCliente;
        public TextView descCliente;
        public TextView status;
        public TextView montoFinal;
        public TextView condicionPago;
        public TextView montoFacturado;


        public ItemClickListener listener;

        public CabeceraRecyclerViewHolder(View itemView, ItemClickListener listener) {
            super(itemView);
             nroPedido=(TextView) itemView.findViewById(R.id.nroPedido);
             descCliente=(TextView) itemView.findViewById(R.id.descCliente);
            status=(TextView) itemView.findViewById(R.id.status);
            montoFinal=(TextView) itemView.findViewById(R.id.monto);
            condicionPago= (TextView) itemView.findViewById(R.id.condicionPago);
            montoFacturado= (TextView) itemView.findViewById(R.id.txtMontoFacturado);
            this.listener=listener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {

            listener.onItemClick(view,getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onItemLongClick(view, getAdapterPosition());
            return true;
        }
    }


}

interface ItemClickListener{

    void onItemClick(View view, int position);
    void onItemLongClick(View view, int position);
}


