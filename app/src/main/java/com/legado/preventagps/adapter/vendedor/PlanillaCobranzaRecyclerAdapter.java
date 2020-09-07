package com.legado.preventagps.adapter.vendedor;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.legado.preventagps.R;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.dialogs.MyDialogProgress;
import com.legado.preventagps.enums.TIPODOCUMENTO;
import com.legado.preventagps.fragments.CobranzaFragment;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Cobranza;
import com.legado.preventagps.modelo.PlanillaCobranza;
import com.legado.preventagps.util.AdapterCommunication;
import com.legado.preventagps.util.SessionUsuario;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by __Adrian__ on 19/4/2017.
 */

public class PlanillaCobranzaRecyclerAdapter extends RecyclerView.Adapter<PlanillaCobranzaRecyclerAdapter.PlanillaCobranzaRecyclerViewHolder> implements ItemPlanillaListener {
    private static final String TAG = "PlanillaCobranzaRecyclerAdapter";

    private List<PlanillaCobranza> documentoDeudas = new ArrayList<>();
    private FragmentActivity activity;
    private AdapterCommunication mListener;
    private Context context;
    private String codCliente;
    private SessionUsuario sessionUsuario;
    Fragment fragment;
    MyDialogProgress dialogPago;

    SwipeRefreshLayout swiperefresh;
    Bundle args;

    public void setOnClickListener(AdapterCommunication listener) {
        mListener = listener;
    }


    public PlanillaCobranzaRecyclerAdapter(List<PlanillaCobranza> documentoDeudas, FragmentActivity activity, SwipeRefreshLayout swiperefresh, Fragment fragment) {
        this.activity = activity;
        this.documentoDeudas = documentoDeudas;
        this.swiperefresh = swiperefresh;
        this.fragment = fragment;
    }

    @Override
    public PlanillaCobranzaRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_planilla_cobranza, parent, false);
        sessionUsuario = new SessionUsuario(v.getContext());
        return new PlanillaCobranzaRecyclerViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(final PlanillaCobranzaRecyclerViewHolder holder, final int position) {
        if(documentoDeudas.get(position).getTipoDoc().equals(TIPODOCUMENTO.FACTURA.getCod())){
            holder.numeroDoc.setText(TIPODOCUMENTO.FACTURA.getLetra()+documentoDeudas.get(position).getNumeroDoc());
        }else if(documentoDeudas.get(position).getTipoDoc().equals(TIPODOCUMENTO.BOLETA.getCod())){
            holder.numeroDoc.setText(TIPODOCUMENTO.BOLETA.getLetra()+documentoDeudas.get(position).getNumeroDoc());
        }

      //  holder.tipoDoc.setText(documentoDeudas.get(position).getTipoDoc());
         holder.codCliente.setText(documentoDeudas.get(position).getCodCliente());
        holder.descCliente.setText(documentoDeudas.get(position).getDescCliente());
        holder.monto.setText(documentoDeudas.get(position).getMonto().toString());
        holder.saldo.setText(documentoDeudas.get(position).getSaldo().toString());
        holder.txtEstado.setText(documentoDeudas.get(position).getEstadoDeuda().toString());
        holder.txtDireccion.setText(documentoDeudas.get(position).getDireccionCliente());
        holder.txtCodVendedor.setText(documentoDeudas.get(position).getCodVendedor());
        holder.txtDescVendedor.setText(documentoDeudas.get(position).getDescVendedor());
        holder.itemView.setSelected(true);
        holder.itemView.setBackgroundResource(R.drawable.selector_row_cobranza);


    }

    public void showDialog(final Cobranza cobranza, final int pos) {
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("CONFIRMACION")
                .setMessage("Desea registrar el pago?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                        dialogPago = new MyDialogProgress();
                        dialogPago.show(ft, "dialog");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                registrarPago(cobranza, pos);
                            }
                        }, 1000);
                    }
                }).setNegativeButton(android.R.string.no, null).show();
    }

    private void registrarPago(Cobranza cobranza, final int pos) {
        Call<JsonRespuesta<Integer>> call = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getCobranzaService().registrarCobranza(cobranza);
        call.enqueue(new Callback<JsonRespuesta<Integer>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<Integer>> call, Response<JsonRespuesta<Integer>> response) {
                if (response.body().getEstado() == -1) {

                } else {
                    notifyItemRemoved(pos);
                    notifyDataSetChanged();
                    documentoDeudas.remove(pos);
                    notifyItemRemoved(pos);
                    notifyDataSetChanged();
                    notifyItemRangeChanged(pos, documentoDeudas.size());
                    swiperefresh.post(new Runnable() {
                        @Override
                        public void run() {
                            swiperefresh.setRefreshing(false);
                            ((CobranzaFragment) fragment).cargarDocumentosDeuda(codCliente);
                        }
                    });

                }

                dialogPago.dismiss();
            }

            @Override
            public void onFailure(Call<JsonRespuesta<Integer>> call, Throwable t) {
                showLoginError(t.getMessage());
            }
        });

    }

    private void showLoginError(String error) {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public int getItemCount() {
        return documentoDeudas.size();
    }

    public List<PlanillaCobranza> listaPlanilla() {
        return documentoDeudas;
    }

    public void updateList(List<PlanillaCobranza> planilla) {
        documentoDeudas = new ArrayList<>();
        documentoDeudas.addAll(planilla);
        notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {

        CobranzaFragment cobranzaFragment = new CobranzaFragment();
        cobranzaFragment.setArguments(args);
        Bundle args = new Bundle();
        args.putString("codCliente", documentoDeudas.get(position).getCodCliente());
        args.putBoolean("fromPlanilla", true);
        cobranzaFragment.setArguments(args);
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_planilla_cobranza, cobranzaFragment);
        ft.addToBackStack("PlanillaCobranzaFragment");

        ft.commit();
    }


    public static class PlanillaCobranzaRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView numeroDoc;
         public TextView codCliente;
        public TextView descCliente;
        //public TextView tipoDoc;
        public TextView monto;
        public TextView saldo;
        public TextView txtEstado;
        public TextView txtCodVendedor;
        public TextView txtDescVendedor;
        public TextView txtDireccion;
        public ItemPlanillaListener listener;

        public PlanillaCobranzaRecyclerViewHolder(View itemView, ItemPlanillaListener listener) {
            super(itemView);
            numeroDoc = (TextView) itemView.findViewById(R.id.txtNumeroDoc);
           codCliente = (TextView) itemView.findViewById(R.id.txtCodCliente);
            descCliente = (TextView) itemView.findViewById(R.id.txtDescCliente);
           // tipoDoc = (TextView) itemView.findViewById(R.id.txtTipoDoc);
            monto = (TextView) itemView.findViewById(R.id.txtMonto);
            saldo = (TextView) itemView.findViewById(R.id.txtSaldo);
            txtEstado = (TextView) itemView.findViewById(R.id.txtEstado);
            txtCodVendedor = (TextView) itemView.findViewById(R.id.txtCodVendedor);
            txtDescVendedor=(TextView) itemView.findViewById(R.id.txtDescVendedor);
            txtDireccion = (TextView) itemView.findViewById(R.id.txtDireccion);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(view, getAdapterPosition());
        }
    }
}

interface ItemPlanillaListener {

    void onItemClick(View view, int position);
}



