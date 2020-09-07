package com.legado.preventagps.adapter.vendedor;


import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.legado.preventagps.R;
import com.legado.preventagps.activities.vendedor.VoucherCobranzaActivity;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.dialogs.MyDialogEditCobro;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Cobranza;
import com.legado.preventagps.modelo.DocumentoDeuda;
import com.legado.preventagps.util.AdapterCommunication;
import com.legado.preventagps.util.DocDeudaItemClickListener;
import com.legado.preventagps.util.SessionUsuario;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by __Adrian__ on 19/4/2017.
 */

public class DocumentoDeudaCobranzaRecyclerAdapter extends RecyclerView.Adapter<DocumentoDeudaCobranzaRecyclerAdapter.DocDeudaRecyclerViewHolder> implements DocDeudaItemClickListener {
    private static final String TAG = "DocumentoDeudaCobranzaRecyclerAdapter";

    private List<DocumentoDeuda> documentoDeudas =new ArrayList<>();

    private AdapterCommunication mListener;
    private Context context;
     private String usuario;
    private String fecha;
    private SessionUsuario sessionUsuario;
    ProgressDialog pd;


    SwipeRefreshLayout swiperefresh;
    private TextView txtMonto;
    private AppCompatActivity activity;
    public void setOnClickListener(AdapterCommunication listener){
        mListener = listener;
    }
    public DocumentoDeudaCobranzaRecyclerAdapter(List<DocumentoDeuda> documentoDeudas, Context context,String usuario,String fecha, SwipeRefreshLayout swiperefresh,TextView txtMonto,AppCompatActivity activity) {
        this.context=context;
         this.documentoDeudas=documentoDeudas;
        this.usuario=usuario;
        this.swiperefresh=swiperefresh;
        this.fecha=fecha;
        this.txtMonto=txtMonto;
        this.activity=activity;


    }
    @Override
    public DocDeudaRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_doc_deuda_cobrada, parent, false);
        sessionUsuario=new SessionUsuario(v.getContext());
        return new DocDeudaRecyclerViewHolder(v, this);
    }
    @Override
    public void onBindViewHolder(final DocDeudaRecyclerViewHolder holder, final int position) {
        holder.serie.setText(documentoDeudas.get(position).getSerie());
        holder.preimpreso.setText(documentoDeudas.get(position).getPreimpreso());
         holder.txtDescVendedor.setText(documentoDeudas.get(position).getDescVendedor());
        holder.monto.setText(documentoDeudas.get(position).getMonto().toString());
        holder.saldo.setText(documentoDeudas.get(position).getSaldo().toString());
        holder.txtMontoPagado.setText(documentoDeudas.get(position).getMontoPagado().toString());
        holder.txtFechaEmision.setText(documentoDeudas.get(position).getFechaEmision().toString());
        holder.txtEstado.setText(documentoDeudas.get(position).getEstadoDeuda().toString());
        holder.txtCodVendedor.setText(documentoDeudas.get(position).getCodVendedor());

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                MyDialogEditCobro newFragment = MyDialogEditCobro.newInstance(documentoDeudas.get(position),position,txtMonto,activity,fecha);
               // newFragment.setDialogInfoListener(dialogInfoListener);
                Bundle args=new Bundle();

                Cobranza cobranza=new Cobranza();
                cobranza.setUsuario(sessionUsuario.getUsuario());
                List<DocumentoDeuda> documentos=new ArrayList<>();
                DocumentoDeuda documento=documentoDeudas.get(position);
                documento.setMontoPagado(new BigDecimal(holder.txtMontoPagado.getText().toString()));
                documentos.add(documento);
                documento.setCodCliente(documentoDeudas.get(position).getCodCliente());
                cobranza.setUsuario(sessionUsuario.getUsuario());
                cobranza.setListaDoc(documentos);
                cobranza.setOperacion(3);

                Gson gson = new Gson();
                String cobranzaJson = gson.toJson(cobranza);
                Log.d("assas", cobranzaJson);
                 args.putString("objCobranza",cobranzaJson);

                 newFragment.setArguments(args);
                newFragment.show(ft, "edit");
            }
        });

        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd=new ProgressDialog(activity);


                Cobranza cobranza=new Cobranza();
                cobranza.setUsuario(sessionUsuario.getUsuario());
                List<DocumentoDeuda> documentos=new ArrayList<>();
                DocumentoDeuda documento=documentoDeudas.get(position);
                documento.setMontoPagado(new BigDecimal(holder.txtMontoPagado.getText().toString()));

                documentos.add(documento);
                documento.setCodCliente(documentoDeudas.get(position).getCodCliente());
                cobranza.setUsuario(sessionUsuario.getUsuario());
                cobranza.setListaDoc(documentos);
                cobranza.setOperacion(4);

                Gson gson = new Gson();
                String cobranzaJson = gson.toJson(cobranza);
                Log.d("assas", cobranzaJson);
                 showDialog(cobranza);



            }
        });
    }
    public void showDialog(final Cobranza cobranza) {
        new AlertDialog.Builder(activity)
                .setTitle("CONFIRMACION")
                .setMessage("Desea eliminar el pago?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        eliminarMonto(cobranza);

                    }
                }).setNegativeButton(android.R.string.no, null).show();
    }

    private void eliminarMonto(Cobranza cobranza) {
        pd.show();
        Call<JsonRespuesta<Integer>> call = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getCobranzaService().registrarCobranza(cobranza);
        call.enqueue(new Callback<JsonRespuesta<Integer>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<Integer>> call, Response<JsonRespuesta<Integer>> response) {

               if(response.body()!=null){
                   if (response.body().getEstado() == -1) {
                   } else {
                       ( (VoucherCobranzaActivity)activity).cargarCobranzaByUsuario(sessionUsuario.getUsuario(),fecha);

                   }
                   pd.dismiss();
                   Toast.makeText(activity,response.body().getMensaje(),Toast.LENGTH_SHORT).show();
               }else{
                   new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                           .setTitleText("Oops...")
                           .setContentText(activity.getString(R.string.txtMensajeServidorCaido))
                           .show();
                   pd.dismiss();
               }

            }

            @Override
            public void onFailure(Call<JsonRespuesta<Integer>> call, Throwable t) {

                new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(activity.getString(R.string.txtMensajeConexion))
                        .show();
                pd.dismiss();
            }
        });
    }
    @Override
    public int getItemCount() {
        return documentoDeudas.size();
    }

    public BigDecimal montoTotalCobrado(){
        BigDecimal montoVenta=new BigDecimal(0);
        for (int i = 0; i <documentoDeudas.size() ; i++) {
                montoVenta = montoVenta.add(documentoDeudas.get(i).getMontoPagado());
        }

        return montoVenta;

    }

    public List<DocumentoDeuda> listaDocumentosDeudas(){
        return documentoDeudas;
    }

    @Override
    public void onItemClick(View view, int position) {

    }


    public static class DocDeudaRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
         public TextView serie;
         public TextView preimpreso;
//        public TextView tipoDoc;
        public TextView monto;
        public TextView saldo;
        public TextView fechaEmision;
        public TextView condicionPago;
        public EditText txtMontoPagado;
        public TextView txtFechaEmision;
        public TextView txtEstado;
        public TextView txtCodVendedor;
        public TextView txtDescVendedor;
        public Button btnEdit;
        public Button btnEliminar;
        //public Button btnOk;


        public DocDeudaItemClickListener listener;

        public DocDeudaRecyclerViewHolder(View itemView, DocDeudaItemClickListener listener) {
            super(itemView);
           serie = (TextView) itemView.findViewById(R.id.txtSerie);
            preimpreso = (TextView) itemView.findViewById(R.id.txtPreImpreso);
//            tipoDoc = (TextView) itemView.findViewById(R.id.txtTipoDoc);
            monto = (TextView) itemView.findViewById(R.id.txtMonto);
            saldo = (TextView) itemView.findViewById(R.id.txtSaldo);
             txtMontoPagado=itemView.findViewById(R.id.txtMontoPagado);

            btnEdit= (Button) itemView.findViewById(R.id.btnEditar);
            btnEliminar=(Button)itemView.findViewById(R.id.btnEliminar);
            txtFechaEmision = (TextView) itemView.findViewById(R.id.txtFechaEmision);
            txtEstado=(TextView) itemView.findViewById(R.id.txtEstado);
            txtCodVendedor=(TextView) itemView.findViewById(R.id.txtCodVendedor);
            txtDescVendedor=(TextView)itemView.findViewById(R.id.txtDescVendedor);
            this.listener = listener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(view, getAdapterPosition());
        }
    }


}




