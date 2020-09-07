package com.legado.preventagps.adapter.vendedor;




        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.graphics.Color;

        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

        import androidx.fragment.app.FragmentActivity;
        import androidx.recyclerview.widget.RecyclerView;

        import com.legado.preventagps.R;
        import com.legado.preventagps.api.ApiSqlite;
        import com.legado.preventagps.enums.STATUSSINCRONIZACION;
        import com.legado.preventagps.modelo.NoPedido;
        import com.legado.preventagps.util.AdapterCommunication;
        import com.legado.preventagps.util.NoPedidoLocalItemClickListener;

        import java.util.List;

/**
 * Created by __Adrian__ on 19/4/2017.
 */

public class NoPedidoLocalRecyclerAdapter extends RecyclerView.Adapter<NoPedidoLocalRecyclerAdapter.NoPedidoLocalRecyclerViewHolder> implements NoPedidoLocalItemClickListener {
    private static final String TAG = "PedidoLocalRecyclerAdapter";

    private List<NoPedido> noPedidosLocales;
    private FragmentActivity activity;
    private AdapterCommunication mListener;
    private Context context;


    public void setOnClickListener(AdapterCommunication listener){
        mListener = listener;
    }

    public NoPedidoLocalRecyclerAdapter(List<NoPedido> noPedidosLocales, Context context, FragmentActivity activity) {
        this.noPedidosLocales = noPedidosLocales;
        this.context=context;
        this.activity=activity;

    }


    @Override
    public  NoPedidoLocalRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_consultnopedidolocal, parent, false);
        return new NoPedidoLocalRecyclerViewHolder(v, this);
    }
    @Override
    public void onBindViewHolder(NoPedidoLocalRecyclerViewHolder holder,final int position) {
        holder.codCliente.setText(noPedidosLocales.get(position).getCodCliente());
         holder.cliente.setText(noPedidosLocales.get(position).getDescCliente());
         holder.motivo.setText(noPedidosLocales.get(position).getDescMotivo());
        holder.itemView.setBackgroundResource(R.drawable.selector_row);
        holder.itemView.setSelected(true);
        if (noPedidosLocales.get(position).getStatusNoPedido().equals(STATUSSINCRONIZACION.PENDIENTE.getCod())) {// INGRESADO
            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.btnEliminar.setVisibility(View.VISIBLE);
        }else   if (noPedidosLocales.get(position).getStatusNoPedido().equals(STATUSSINCRONIZACION.SINCRONIZADO.getCod())) {// INGRESADO
            holder.itemView.setBackgroundResource(R.color.material_green_400);
            holder.btnEliminar.setVisibility(View.INVISIBLE);
        }else {

            holder.itemView.setBackgroundResource(R.drawable.selector_row);
        }
        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Desea elminar registro de visita  ?");
                builder.setMessage("Se eliminará el registro  almacenado en su celular de forma permanente!!");
                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
                            ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().deleteNoPedidoLocalById(noPedidosLocales.get(position).getIdNoPedidoLocal());
                             ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                        } finally {
                            ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                        }
                        noPedidosLocales.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, noPedidosLocales.size());

                    }
                });
                builder.setNegativeButton("NO", null);
                builder.show();
            }
        });


    }


    @Override
    public int getItemCount() {
        return noPedidosLocales.size();
    }

    @Override
    public void onItemClick(View view, final int position) {
        if (noPedidosLocales.get(position).getStatusNoPedido().equals("INGRESADO")) {// INGRESADO
            new AlertDialog.Builder(context)
                    .setTitle("CONFIRMACION")
                    .setMessage("Desea eliminar el item  de la lista ?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {


                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();


        }

    }

    @Override
    public void updateRecycler() {
        notifyDataSetChanged();
    }




    public static class NoPedidoLocalRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView codCliente;
        public TextView cliente;
        public TextView motivo;
        public ImageView btnEliminar;


        public NoPedidoLocalItemClickListener listener;

        public NoPedidoLocalRecyclerViewHolder(View itemView,  NoPedidoLocalItemClickListener listener) {
            super(itemView);
            codCliente = (TextView) itemView.findViewById(R.id.txtCodCliente);
            cliente = (TextView) itemView.findViewById(R.id.txtCliente);
            motivo= (TextView) itemView.findViewById(R.id.txtMotivo);
            btnEliminar= (ImageView) itemView.findViewById(R.id.btnEliminar);



            this.listener = listener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            listener.onItemClick(view, getAdapterPosition());

        }
    }


}




