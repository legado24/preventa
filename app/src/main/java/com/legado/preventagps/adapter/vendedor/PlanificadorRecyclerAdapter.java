package com.legado.preventagps.adapter.vendedor;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.legado.preventagps.R;
import com.legado.preventagps.modelo.Planificador;
import com.legado.preventagps.util.AdapterCommunication;
import com.legado.preventagps.util.SessionUsuario;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by __Adrian__ on 13/08/2019.
 */

public class PlanificadorRecyclerAdapter extends RecyclerView.Adapter<PlanificadorRecyclerAdapter.PlanificadorRecyclerViewHolder> implements ItemPlanificadorListener {
    private static final String TAG = "PlanillaCobranzaRecyclerAdapter";

    private List<Planificador> listaPlanificador =new ArrayList<>();
    private FragmentActivity activity;
    private AdapterCommunication mListener;
    private Context context;
    private String codCliente;
    private SessionUsuario sessionUsuario;
    Fragment fragment;
//    MyDialogProgress dialogPago;
//
   SwipeRefreshLayout swiperefresh;
//    Bundle args;
    public void setOnClickListener(AdapterCommunication listener){
        mListener = listener;
    }



    public PlanificadorRecyclerAdapter(List<Planificador> listaPlanificador, FragmentActivity activity, SwipeRefreshLayout swiperefresh, Fragment fragment) {
        this.activity=activity;
        this.listaPlanificador=listaPlanificador;
        this.swiperefresh=swiperefresh;
        this.fragment=fragment;
    }
    @Override
    public PlanificadorRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_planificador, parent, false);
        sessionUsuario=new SessionUsuario(v.getContext());
        return new PlanificadorRecyclerViewHolder(v, this);
    }
    @Override
    public void onBindViewHolder(final PlanificadorRecyclerViewHolder holder, final int position) {

//        holder.codCliente.setText(listaPlanificador.get(position).getCodCliente());
//        holder.descCliente.setText(listaPlanificador.get(position).getDescCliente());
        holder.itemView.setSelected(true);
        holder.itemView.setBackgroundResource(R.drawable.selector_row_cobranza);

//        if(holder.ly.getChildCount()>0){
//            holder.ly.removeAllViews();
//        }
//        LinearLayout.LayoutParams dim = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//        TablaAndroid tabla = new TablaAndroid(activity, holder.tabla);
//        tabla.agregarCabecera(R.array.cabecera_tabla);
//        for(int i = 0; i < 1; i++)
//        {
//            ArrayList<String> elementos = new ArrayList<String>();
//            elementos.add(Integer.toString(i));
//            elementos.add("Casilla [" + i + ", 0]");
//            elementos.add("Casilla [" + i + ", 1]");
//            elementos.add("Casilla [" + i + ", 2]");
//            elementos.add("Casilla [" + i + ", 3]");
//            tabla.agregarFilaTabla(elementos);
//        }

        for (int i = 0; i < listaPlanificador.get(position).getListaProveedores().size(); i++) {
//            Proveedor pv=listaPlanificador.get(position).getListaProveedores().get(i);
//            TextView textView=new TextView(fragment.getContext());
//            textView.setLayoutParams(dim);
//            textView.setText(pv.getCodProveedor());
//            holder.ly.addView(textView);




        }


    }

//    public void showDialog(final Cobranza cobranza,final int pos) {
//        new android.support.v7.app.AlertDialog.Builder(context)
//                .setTitle("CONFIRMACION")
//                .setMessage("Desea registrar el pago?")
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        FragmentTransaction ft = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
//                        dialogPago= new MyDialogProgress();
//                        dialogPago.show(ft, "dialog");
//                        new Handler().postDelayed(new Runnable() {
//                            @Override public void run() {
//                                registrarPago(cobranza,pos);
//                            }
//                        }, 1000);
//                    }
//                }).setNegativeButton(android.R.string.no, null).show();
//    }
//    private void registrarPago(Cobranza cobranza,final int pos) {
//        Call<JsonRespuesta<Integer>> call = ApiRetrofitShort.getInstance().getCobranzaService().registrarCobranza(cobranza);
//        call.enqueue(new Callback<JsonRespuesta<Integer>>() {
//            @Override
//            public void onResponse(Call<JsonRespuesta<Integer>> call, Response<JsonRespuesta<Integer>> response) {
//                if (response.body().getEstado() == -1) {
//
//                } else {
//                    notifyItemRemoved(pos);
//                    notifyDataSetChanged();
//                    documentoDeudas.remove(pos);
//                    notifyItemRemoved(pos);
//                    notifyDataSetChanged();
//                    notifyItemRangeChanged(pos,documentoDeudas.size());
//                    swiperefresh.post(new Runnable() {
//                        @Override public void run() {
//                            swiperefresh.setRefreshing(false);
//                            ( (CobranzaFragment) fragment).cargarDocumentosDeuda(codCliente);
//                        }
//                    });
//
//                }
//
//                dialogPago.dismiss();
//            }
//
//            @Override
//            public void onFailure(Call<JsonRespuesta<Integer>> call, Throwable t) {
//                showLoginError(t.getMessage());
//            }
//        });
//
//    }
//    private void showLoginError(String error) {
//        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
//    }
    @Override
    public int getItemCount() {
        return listaPlanificador.size();
    }

    public List<Planificador> listaPlanificador(){
        return listaPlanificador;
    }

//    public void updateList(List<PlanillaCobranza> planilla) {
//        documentoDeudas = new ArrayList<>();
//        documentoDeudas.addAll(planilla);
//        notifyDataSetChanged();
//    }
    @Override
    public void onItemClick(View view, int position) {

//        CobranzaFragment cobranzaFragment=new CobranzaFragment();
//        cobranzaFragment.setArguments(args);
//        Bundle args = new Bundle();
//        args.putString("codCliente", documentoDeudas.get(position).getCodCliente());
//        args.putBoolean("fromPlanilla", true);
//        cobranzaFragment.setArguments(args);
//        FragmentTransaction ft =  activity.getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.content_planilla_cobranza, cobranzaFragment,TAG);
//        ft.addToBackStack("PlanillaCobranzaFragment");
//        ft.commit();
    }


    public static class PlanificadorRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView codCliente;
        public TextView descCliente;
        TableLayout tabla;
        public LinearLayout ly;

        public ItemPlanificadorListener listener;

        public PlanificadorRecyclerViewHolder(View itemView, ItemPlanificadorListener listener) {
            super(itemView);
             codCliente=(TextView) itemView.findViewById(R.id.txtCodCliente);
            descCliente=(TextView) itemView.findViewById(R.id.txtDescCliente);

            tabla=(TableLayout)itemView.findViewById(R.id.tabla);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(view, getAdapterPosition());
        }
    }
}
interface ItemPlanificadorListener{

    void onItemClick(View view, int position);
}



