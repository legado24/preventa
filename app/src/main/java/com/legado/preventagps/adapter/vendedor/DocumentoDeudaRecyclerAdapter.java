package com.legado.preventagps.adapter.vendedor;


import android.app.Activity;
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
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.legado.preventagps.R;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.dialogs.MyDialogProgress;
import com.legado.preventagps.enums.TIPODOCUMENTO;
import com.legado.preventagps.fragments.CobranzaFragment;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Cobranza;
import com.legado.preventagps.modelo.DocumentoDeuda;
import com.legado.preventagps.util.AdapterCommunication;
import com.legado.preventagps.util.DocDeudaItemClickListener;
import com.legado.preventagps.util.SessionUsuario;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by __Adrian__ on 19/4/2017.
 */

public class DocumentoDeudaRecyclerAdapter extends RecyclerView.Adapter<DocumentoDeudaRecyclerAdapter.DocDeudaRecyclerViewHolder> implements DocDeudaItemClickListener {
    private static final String TAG = "DocumentoDeudaRecyclerAdapter";

    private List<DocumentoDeuda> documentoDeudas =new ArrayList<>();
    private FragmentActivity activity;
    private AdapterCommunication mListener;
    private Context context;
     private String codCliente;
    private SessionUsuario sessionUsuario;
    Fragment fragment;
    MyDialogProgress dialogPago;

    SwipeRefreshLayout swiperefresh;
    Bundle args;
    public void setOnClickListener(AdapterCommunication listener){
        mListener = listener;
    }



    public DocumentoDeudaRecyclerAdapter(List<DocumentoDeuda> documentoDeudas, Context context, FragmentActivity activity, String codCliente, SwipeRefreshLayout swiperefresh,Fragment fragment,Bundle args) {
        this.context=context;
        this.activity=activity;
        this.documentoDeudas=documentoDeudas;
        this.codCliente=codCliente;
        this.swiperefresh=swiperefresh;
        this.fragment=fragment;
        this.args=args;


    }
    @Override
    public DocDeudaRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_doc_deuda, parent, false);
        sessionUsuario=new SessionUsuario(v.getContext());
        return new DocDeudaRecyclerViewHolder(v, this);
    }
    @Override
    public void onBindViewHolder(final DocDeudaRecyclerViewHolder holder, final int position) {
        if(documentoDeudas.get(position).getTipoDoc().equals(TIPODOCUMENTO.FACTURA.getCod())){
            holder.serie.setText(TIPODOCUMENTO.FACTURA.getLetra()+documentoDeudas.get(position).getSerie());
        }else if(documentoDeudas.get(position).getTipoDoc().equals(TIPODOCUMENTO.BOLETA.getCod())){
            holder.serie.setText(TIPODOCUMENTO.BOLETA.getLetra()+documentoDeudas.get(position).getSerie());
        }

        holder.preimpreso.setText(documentoDeudas.get(position).getPreimpreso());

        holder.monto.setText(documentoDeudas.get(position).getMonto().toString());
        holder.saldo.setText(documentoDeudas.get(position).getSaldo().toString());
        holder.txtFechaEmision.setText(documentoDeudas.get(position).getFechaEmision().toString());
        holder.txtEstado.setText(documentoDeudas.get(position).getEstadoDeuda().toString());
        holder.txtCodVendedor.setText(documentoDeudas.get(position).getCodVendedor());
        holder.txtDescVendedor.setText(documentoDeudas.get(position).getDescVendedor());


        ArrayList tiposPago=new ArrayList();
        ArrayList tipoPago1=new ArrayList();
        tipoPago1.add("1");
        tipoPago1.add("EFECTIVO");
        ArrayList tipoPago2=new ArrayList();
        tipoPago2.add("0");
        tipoPago2.add("DEPOSITO");
        tiposPago.add(tipoPago1);
        tiposPago.add(tipoPago2);
        Activity activity = (Activity) context;
        SpinnerSimpleAdapter tipoPagoAdapter = new SpinnerSimpleAdapter(activity, R.id.desc, tiposPago);
        tipoPagoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerTipoPago.setAdapter(tipoPagoAdapter);
        holder.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar(holder)){
                    Cobranza cobranza=new Cobranza();
                    cobranza.setUsuario(sessionUsuario.getUsuario());
                    List<DocumentoDeuda> documentos=new ArrayList<>();
                    DocumentoDeuda documento=documentoDeudas.get(position);
                    documento.setMontoPagado(new BigDecimal(holder.txtMontoPagado.getText().toString()));
                    documento.setStatusPago("I");
                    ArrayList tipoPago= (ArrayList)  holder.spinnerTipoPago.getSelectedItem();
                    documento.setEfectivo(tipoPago.get(0).toString());
                    documentos.add(documento);
                    documento.setCodCliente(codCliente);
                    cobranza.setUsuario(sessionUsuario.getUsuario());
                    cobranza.setListaDoc(documentos);
                    cobranza.setOperacion(1);

                    Gson gson = new Gson();
                    String lcJson = gson.toJson(cobranza);
                    Log.d("assas", lcJson);

                    showDialog(cobranza,position);
                }

            }
        });




    }

    public void showDialog(final Cobranza cobranza,final int pos) {
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("CONFIRMACION")
                .setMessage("Desea registrar el pago?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        FragmentTransaction ft = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                         dialogPago= new MyDialogProgress();
                        dialogPago.show(ft, "dialog");
                        new Handler().postDelayed(new Runnable() {
                            @Override public void run() {
                                registrarPago(cobranza,pos);
                            }
                        }, 1000);
                    }
                }).setNegativeButton(android.R.string.no, null).show();
    }
    private boolean validar( DocDeudaRecyclerViewHolder holder) {
        boolean band = true;
        if (holder.txtMontoPagado.getText().toString().equals("")) {
            holder.txtMontoPagado.setError(Html.fromHtml("<font color='red'>Ingrese monto</font>"));
            band = false;
        } else {
            BigDecimal firstBigDecimal=new BigDecimal(holder.txtMontoPagado.getText().toString());
            BigDecimal secondBigDecimal=new BigDecimal(holder.saldo.getText().toString());
            if (firstBigDecimal.compareTo(secondBigDecimal) > 0 ) {
                holder.txtMontoPagado.setError(Html.fromHtml("<font color='red'>El monto debe ser menor o igual a la deuda!!</font>"));
                band = false;
            } else if((firstBigDecimal.compareTo(BigDecimal.ZERO)==0)) {
                holder.txtMontoPagado.setError(Html.fromHtml("<font color='red'>El monto debe ser mayor a 0.</font>"));
                band = false;
            }else{
                holder.txtMontoPagado.setError(null);
            }
        }
        return band;

    }
    private void registrarPago(Cobranza cobranza,final int pos) {
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
                    notifyItemRangeChanged(pos,documentoDeudas.size());
                    swiperefresh.post(new Runnable() {
                        @Override public void run() {
                            swiperefresh.setRefreshing(false);
                            ( (CobranzaFragment) fragment).cargarDocumentosDeuda(codCliente);
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



    @Override
    public void onItemClick(View view, int position) {

    }


    public static class DocDeudaRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
         public TextView serie;
         public TextView preimpreso;

        public TextView monto;
        public TextView saldo;
        public TextView fechaEmision;
        public TextView condicionPago;
        public EditText txtMontoPagado;
        private Spinner spinnerTipoPago;
        public TextView txtFechaEmision;
        public TextView txtEstado;
        public TextView txtCodVendedor;
        public TextView txtDescVendedor;
        public Button btnOk;


        public DocDeudaItemClickListener listener;

        public DocDeudaRecyclerViewHolder(View itemView, DocDeudaItemClickListener listener) {
            super(itemView);
           serie = (TextView) itemView.findViewById(R.id.txtSerie);
            preimpreso = (TextView) itemView.findViewById(R.id.txtPreImpreso);

            monto = (TextView) itemView.findViewById(R.id.txtMonto);
            saldo = (TextView) itemView.findViewById(R.id.txtSaldo);
            btnOk= (Button) itemView.findViewById(R.id.btnOk);
            spinnerTipoPago=(Spinner) itemView.findViewById(R.id.spinnerTipoPago);
            txtMontoPagado=itemView.findViewById(R.id.txtMontoPagado);
            txtFechaEmision = (TextView) itemView.findViewById(R.id.txtFechaEmision);
            txtEstado=(TextView) itemView.findViewById(R.id.txtEstado);
            txtCodVendedor=(TextView) itemView.findViewById(R.id.txtCodVendedor);
            txtDescVendedor=(TextView)itemView.findViewById(R.id.txtDescVendedor) ;
            this.listener = listener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            listener.onItemClick(view, getAdapterPosition());

        }
    }


}




