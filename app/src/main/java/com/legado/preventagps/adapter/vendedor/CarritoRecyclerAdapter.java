package com.legado.preventagps.adapter.vendedor;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


import com.legado.preventagps.R;
import com.legado.preventagps.dialogs.MyDialogEdit;
import com.legado.preventagps.dialogs.MyDialogInfo;
import com.legado.preventagps.fragments.CarritoFragment;
import com.legado.preventagps.modelo.CarritoCompras;
import com.legado.preventagps.modelo.PaqueteCarrito;
import com.legado.preventagps.util.AdapterCommunication;
import com.legado.preventagps.util.CarritoItemClickListener;
import com.legado.preventagps.util.Metodos;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.UtilAndroid;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by __Adrian__ on 19/4/2017.
 */

public class CarritoRecyclerAdapter extends RecyclerView.Adapter<CarritoRecyclerAdapter.CarritoRecyclerViewHolder> implements CarritoItemClickListener {
    private static final String TAG = "CarritoRecyclerAdapter";

    private List<CarritoCompras> detallePedidos =new ArrayList<>();
    private FragmentActivity activity;
    private AdapterCommunication mListener;
    private Context context;
    private CarritoFragment carritoFragment;
    private SessionUsuario sessionUsuario;
    private TextView txtMonto;
    TextView titleList;
    Spinner spinnerVendFind;
    Spinner spinnerCondFind;
    Spinner spinnerAlmacenesFind;
    Bundle args;
    MyDialogInfo.DialogInfoListener dialogInfoListener;
    public void setOnClickListener(AdapterCommunication listener){
        mListener = listener;
    }

    public CarritoRecyclerAdapter(List<CarritoCompras> detallePedidos, Context context, FragmentActivity activity, CarritoFragment carritoFragment, TextView txtMonto, TextView titleList, Spinner spinnerVendFind, Spinner spinnerCondFind, Bundle args, MyDialogInfo.DialogInfoListener dialogInfoListener,Spinner spinnerAlmacenesFind) {
        this.detallePedidos = detallePedidos;
        this.args=args;
        this.titleList=titleList;
        this.spinnerVendFind=spinnerVendFind;
        this.spinnerCondFind=spinnerCondFind;
        this.context=context;
        this.activity=activity;
        this.carritoFragment=carritoFragment;
        this.txtMonto=txtMonto;
        this.dialogInfoListener=dialogInfoListener;
        this.spinnerAlmacenesFind=spinnerAlmacenesFind;

    }
    @Override
    public CarritoRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_carrito, parent, false);
        sessionUsuario=new SessionUsuario(v.getContext());
        return new CarritoRecyclerViewHolder(v, this);
    }
    @Override
    public void onBindViewHolder(CarritoRecyclerViewHolder holder,final int position) {
        holder.descArticulo.setText(detallePedidos.get(position).getArticulo().getDescItem());
        holder.cantidad.setText(detallePedidos.get(position).getCantidad().toString());
        BigDecimal montoItemTotal=Metodos.redondearDosDecimales(detallePedidos.get(position).getArticulo().getPrecioSugerido().multiply(new BigDecimal(detallePedidos.get(position).getCantidad())));
        holder.montoItem.setText(montoItemTotal.toString());
        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("CONFIRMACION")
                        .setMessage("Desea eliminar el articulo de la lista ?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                detallePedidos.remove(position);
                                notifyItemRemoved(position);
                                PaqueteCarrito paqueteCarrito=new PaqueteCarrito();
                                paqueteCarrito.setListaCarrito(detallePedidos);
                                sessionUsuario.guardarPaqueteCarrito(paqueteCarrito);
                                notifyItemRangeChanged(position, detallePedidos.size());
                                txtMonto.setText("TOTAL="+montoTotalDiario().toString());
                                titleList.setText("LISTA DE ARTICULOS (" + detallePedidos.size() + ")");
                                UtilAndroid.toNegrita(titleList);

                            }})
                        .setNegativeButton(android.R.string.no, null).show();



            }
        });
        holder.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                 MyDialogEdit newFragment = MyDialogEdit.newInstance(detallePedidos.get(position),position,carritoFragment,titleList,spinnerVendFind,spinnerCondFind,txtMonto,spinnerAlmacenesFind);
               newFragment.setDialogInfoListener(dialogInfoListener);
                newFragment.setArguments(args);
                newFragment.show(ft, "edit");
            }
        });
        holder.tipoCliente.setVisibility(View.GONE);
        if(detallePedidos.get(position).getArticulo().getTipo()!=null){
            if(detallePedidos.get(position).getArticulo().getTipo().equals("F")){
                holder.tipoCliente.setVisibility(View.VISIBLE);
                holder.tipoCliente.setBackgroundResource(R.drawable.ic_focus_item_sugerido);
            }else  if(detallePedidos.get(position).getArticulo().getTipo().equals("S")){
                holder.tipoCliente.setVisibility(View.VISIBLE);
                holder.tipoCliente.setBackgroundResource(R.drawable.ic_item_sugerido);
            } else {
                holder.tipoCliente.setVisibility(View.GONE);
            }
        }



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
//        public TextView codArticulo;
        public TextView descArticulo;
         public TextView cantidad;
        public TextView montoItem;
        public ImageView btnEliminar;
        public ImageView btnEditar;
        private ImageView tipoCliente;



        public CarritoItemClickListener listener;

        public CarritoRecyclerViewHolder(View itemView, CarritoItemClickListener listener) {
            super(itemView);
//            codArticulo = (TextView) itemView.findViewById(R.id.codArticulo);
            descArticulo = (TextView) itemView.findViewById(R.id.descArticulo);
            cantidad = (TextView) itemView.findViewById(R.id.txtCantidad);
            btnEliminar= (ImageView) itemView.findViewById(R.id.btnEliminar);
             btnEditar=(ImageView) itemView.findViewById(R.id.btnEditar);
            montoItem=(TextView)itemView.findViewById(R.id.txtMontoItem) ;
            tipoCliente=(ImageView)itemView.findViewById(R.id.tipoArticulo) ;
            this.listener = listener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            listener.onItemClick(view, getAdapterPosition());

        }
    }


}




