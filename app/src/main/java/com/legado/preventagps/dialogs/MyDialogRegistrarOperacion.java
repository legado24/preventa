package com.legado.preventagps.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.legado.preventagps.R;
import com.legado.preventagps.activities.vendedor.ClienteActivity;
import com.legado.preventagps.activities.vendedor.VoucherCobranzaActivity;
import com.legado.preventagps.adapter.vendedor.DocumentoDeudaCobranzaRecyclerAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Cobranza;
import com.legado.preventagps.util.SessionUsuario;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class MyDialogRegistrarOperacion extends DialogFragment{
    private static final String TAG = "MyDialogRegistrarOperacion";

    @BindView(R.id.txtMontoCobrado)
    EditText txtMontoCobrado;

    @BindView(R.id.txtMontoDeposito)
    EditText txtMontoDeposito;

    @BindView(R.id.txtBanco)
    EditText txtBanco;

    @BindView(R.id.txtNroOperaacion)
    EditText txtNroOperaacion;


    @BindView(R.id.btnRegistrar)
    Button btnRegistrar;
    @BindView(R.id.btnCerrar)
    Button btnCerrar;

    SessionUsuario sessionUsuario;

   static DocumentoDeudaCobranzaRecyclerAdapter detallePedidoRecyclerAdapterDialog;

    public static MyDialogRegistrarOperacion newInstance( DocumentoDeudaCobranzaRecyclerAdapter detallePedidoRecyclerAdapter) {
        MyDialogRegistrarOperacion fragment= new MyDialogRegistrarOperacion();
        detallePedidoRecyclerAdapterDialog=detallePedidoRecyclerAdapter;
        return  fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_dialog_registraroperacion, container, false);
        ButterKnife.bind(this, rootView);
        sessionUsuario=new SessionUsuario(getActivity());
        final LottieAnimationView animationView= (LottieAnimationView) rootView.findViewById(R.id.alert);
        final Bundle args=getArguments();
        animationView.loop(false);
        animationView.playAnimation();
        txtMontoCobrado.setText(args.getString("montoCobrado"));
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar()){
                    Cobranza cobranza=new Cobranza();
                    cobranza.setUsuario(sessionUsuario.getUsuario());
                    cobranza.setBanco(txtBanco.getText().toString());
                    cobranza.setOperacion(2);
                    cobranza.setNumeroOperacion(txtNroOperaacion.getText().toString());
                    cobranza.setListaDoc(detallePedidoRecyclerAdapterDialog.listaDocumentosDeudas());
                    Gson gson = new Gson();
                    String clienteJson = gson.toJson(cobranza);
                    Log.d("assas",clienteJson);
                   showDialog(cobranza);
                }


            }
        });

        return rootView;
    }
    private boolean validar() {
        boolean band = true;

        if (txtBanco.getText().toString().equals("")) {
            txtBanco.setError(Html.fromHtml("<font color='red'>Ingrese Banco.</font>"));
            band = false;
        } else {
            txtBanco.setError(null);
        }

        if (txtNroOperaacion.getText().toString().equals("")) {
            txtNroOperaacion.setError(Html.fromHtml("<font color='red'>Ingrese Nro Operacion.</font>"));
            band = false;
        } else {
            txtNroOperaacion.setError(null);
        }

        if (txtMontoDeposito.getText().toString().equals("")) {
            txtMontoDeposito.setError(Html.fromHtml("<font color='red'>Ingrese Monto.</font>"));
            band = false;
        } else {
            txtMontoDeposito.setError(null);
        }

        if (txtMontoCobrado.getText().toString().equals("")) {
            txtMontoCobrado.setError(Html.fromHtml("<font color='red'>Debe Tener Monto</font>"));
            band = false;
        } else {

            txtMontoCobrado.setError(null);
        }


        return band;

    }
    private void registrarDeposito(Cobranza cobranza) {
        cobranza.setOperacion(2);
        Call<JsonRespuesta<Integer>> call = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getCobranzaService().registrarCobranza(cobranza);
        call.enqueue(new Callback<JsonRespuesta<Integer>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<Integer>> call, Response<JsonRespuesta<Integer>> response) {
               if(response.body()!=null){
                   if (response.body().getEstado() == -1) {
                   } else {
                       dismiss();
                       Intent intent = new Intent(getActivity(), ClienteActivity.class);
                       startActivity(intent);
                       getActivity().finish();
                   }
               }else{
                   new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                           .setTitleText("Oops...")
                           .setContentText(getString(R.string.txtMensajeServidorCaido))
                           .show();
                   dismiss();
               }

            }

            @Override
            public void onFailure(Call<JsonRespuesta<Integer>> call, Throwable t) {

                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.txtMensajeConexion))
                        .show();
                dismiss();

            }
        });

    }
    public void showDialog(final Cobranza cobranza) {
        new AlertDialog.Builder(getContext())
                .setTitle("CONFIRMACIÓN")
                .setMessage("Desea realizar el Depósito de "+txtMontoDeposito.getText()+ " \n\n" +
                        "con número de operación "+txtNroOperaacion.getText()+"\n" +
                        "\n en el banco "+txtBanco.getText()+"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        registrarDeposito(cobranza);

                    }
                }).setNegativeButton(android.R.string.no, null).show();
    }


}
