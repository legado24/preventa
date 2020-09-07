package com.legado.preventagps.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.legado.preventagps.R;
import com.legado.preventagps.activities.vendedor.VoucherCobranzaActivity;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.fragments.CarritoFragment;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Cobranza;
import com.legado.preventagps.modelo.DocumentoDeuda;
import com.legado.preventagps.util.SessionUsuario;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class MyDialogEditCobro extends DialogFragment {
    private static final String TAG = "MyDialogEditCobro";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    static CarritoFragment carritoFragmentEdit;
    static DocumentoDeuda documentoDeudaDialog;
    static TextView titleListDialog;
    static Spinner spinnerVendFindDialog;
    static Spinner spinnerCondFindDialog;
    private SessionUsuario sessionUsuario;
    static int positionDialog;
    static TextView txtMontoDialog;
    MyDialogInfo.DialogInfoListener dialogInfoListener;
    static AppCompatActivity activityDialog;
    static String fechaDialog;
    public void setDialogInfoListener(MyDialogInfo.DialogInfoListener dialogInfoListener) {
        this.dialogInfoListener = dialogInfoListener;
    }

    public static MyDialogEditCobro newInstance(DocumentoDeuda documentoDeuda, int position , TextView txtMonto, AppCompatActivity activity,String fecha) {
        documentoDeudaDialog=documentoDeuda;
         positionDialog=position;
        txtMontoDialog=txtMonto;
        activityDialog=activity;
        fechaDialog=fecha;
        MyDialogEditCobro fragment = new MyDialogEditCobro();
        return fragment;
    }

  EditText txtCantidad;
    MyDialogProgress  dialogPago;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_dialog_edit, container, false);
        getDialog().setTitle("Edit Dialog");
        sessionUsuario=new SessionUsuario(getContext());

         txtCantidad = (EditText) rootView.findViewById(R.id.txtCantidadEdit);
        String json = getArguments().getString("objCobranza", "");
        Gson gson = new Gson();
        final Cobranza objCobranza = gson.fromJson(json, Cobranza.class);

       txtCantidad.setText(objCobranza.getListaDoc().get(0).getMontoPagado().toString());
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        txtCantidad.requestFocus();
        Button dismiss = (Button) rootView.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        Button btnActualizar = (Button) rootView.findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar()){
                objCobranza.getListaDoc().get(0).setMontoPagado(new BigDecimal(txtCantidad.getText().toString()));
                actualizarMonto(objCobranza);
                }

            }
        });

        return rootView;
    }

    private void actualizarMonto(Cobranza objCobranza) {
        showDialog(objCobranza);
    }

    public void showDialog(final Cobranza cobranza) {
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("CONFIRMACION")
                .setMessage("Desea actualizar el pago?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        FragmentTransaction ft = ((AppCompatActivity)getContext()).getSupportFragmentManager().beginTransaction();

                        dialogPago= new MyDialogProgress();
                        dialogPago.show(ft, "dialog");
                        new Handler().postDelayed(new Runnable() {
                            @Override public void run() {
                                updatePago(cobranza);
                            }
                        }, 1000);
                    }
                }).setNegativeButton(android.R.string.no, null).show();
    }
    private void updatePago(Cobranza cobranza) {
        Call<JsonRespuesta<Integer>> call = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getCobranzaService().registrarCobranza(cobranza);
        call.enqueue(new Callback<JsonRespuesta<Integer>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<Integer>> call, Response<JsonRespuesta<Integer>> response) {
                if (response.body().getEstado() == -1) {

                } else {

                    ( (VoucherCobranzaActivity)activityDialog).cargarCobranzaByUsuario(sessionUsuario.getUsuario(),fechaDialog);

                }
                dismiss();

                dialogPago.dismiss();
                Toast.makeText(getContext(),response.body().getMensaje(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<JsonRespuesta<Integer>> call, Throwable t) {
                //showLoginError(t.getMessage());

                dialogPago.dismiss();
                Snackbar.make(getView(), "Error de conexión!!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("cerrar", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                           // ( (VoucherCobranzaActivity)activityDialog).cargarCobranzaByUsuario(sessionUsuario.getUsuario(),fechaDialog);

                            }
                        }).show();
            }
        });

    }
//    private void actualizar(Bundle args) {
//        List<CarritoCompras>lista= sessionUsuario.getPaqueteCarrito().getListaCarrito();
//        for (int i = 0; i <lista.size() ; i++) {
//            if(carritoComprasDialog.getArticulo().getCodItem().equals(lista.get(i).getArticulo().getCodItem())){
//                lista.get(i).setCantidad(new Integer(txtCantidad.getText().toString()));
//                BigDecimal cant=new BigDecimal(txtCantidad.getText().toString());
//                lista.get(i).setImporte(cant.multiply(lista.get(i).getArticulo().getPrecioSugerido()));
//                break;
//            }
//        }
//        PaqueteCarrito paq=new PaqueteCarrito();
//        paq.setListaCarrito(lista);
//        sessionUsuario.guardarPaqueteCarrito(paq);
//        BigDecimal montoVenta=new BigDecimal(0);
//        for (int i = 0; i <lista.size() ; i++) {
//            montoVenta=montoVenta.add(lista.get(i).getImporte());
//        }
//        txtMontoDialog.setText(montoVenta.toString());
//        CarritoFragment   carritoFragmentEdit=CarritoFragment.newInstance(titleListDialog,spinnerVendFindDialog,spinnerCondFindDialog);
//        carritoFragmentEdit.setArguments(args);
//        carritoFragmentEdit.setDialogInfoListener(dialogInfoListener);
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.replace(R.id.content_carritocompras, carritoFragmentEdit,TAG);
//        ft.commit();
//        dismiss();
//    }

    private boolean validar() {
        boolean band = true;


        if (txtCantidad.getText().toString().equals("")) {
            txtCantidad.setError("INGRESE MONTO");
            band = false;
        } else {
            if (new BigDecimal(txtCantidad.getText().toString()).equals(BigDecimal.ZERO)) {
                txtCantidad.setError("DEBE SER MAYOR A 0");
                band = false;
            } else {
                txtCantidad.setError(null);
            }


            BigDecimal firstBigDecimal=new BigDecimal(txtCantidad.getText().toString());
            BigDecimal secondBigDecimal=documentoDeudaDialog.getSaldo();
            if (firstBigDecimal.compareTo(secondBigDecimal) > 0 ) {
                 txtCantidad.setError(Html.fromHtml("<font color='red'>El monto debe ser menor o igual a la deuda!!</font>"));
                band = false;
            } else if((firstBigDecimal.compareTo(BigDecimal.ZERO)==0)) {
                txtCantidad.setError(Html.fromHtml("<font color='red'>El monto debe ser mayor a 0.</font>"));
                band = false;
            }else{
                txtCantidad.setError(null);
            }

        }




        return band;

    }

}
