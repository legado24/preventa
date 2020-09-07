package com.legado.preventagps.dialogs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.legado.preventagps.R;
import com.legado.preventagps.activities.vendedor.ContenedorActivity;
import com.legado.preventagps.activities.vendedor.PlanillaCobranzaActivity;
import com.legado.preventagps.adapter.vendedor.DocumentoDeudaRecyclerAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.enums.CLIENTEENUM;
import com.legado.preventagps.fragments.ActualizarClienteFragment;
import com.legado.preventagps.fragments.AgregarDireccionFragment;
import com.legado.preventagps.fragments.AuditarClienteFragment;
import com.legado.preventagps.fragments.AuditarClienteFragmentv2;
import com.legado.preventagps.fragments.DialogLocalCliente;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.DocumentoDeuda;
import com.legado.preventagps.util.SessionUsuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class MyDialogAlertaCliente extends DialogFragment{
    private static final String TAG = "MyDialogAlertaCliente";
    static ArrayList listaRutasDialog;
    static String mensajeDialog;
    private TextView txtDialogTitleTelefono;
    private SessionUsuario sessionUsuario;
        public static MyDialogAlertaCliente newInstance(  ArrayList listaRutas,String mensaje) {
            listaRutasDialog=listaRutas;
            mensajeDialog=mensaje;
            MyDialogAlertaCliente fragment= new MyDialogAlertaCliente();

             return  fragment;
        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_dialog_alert_cliente, container, false);
        sessionUsuario=new SessionUsuario(getActivity());
        txtDialogTitleTelefono=rootView.findViewById(R.id.txtDialogTitleTelefono);
        getDialog().setCanceledOnTouchOutside(false);
        //getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final LottieAnimationView animationView=  rootView.findViewById(R.id.alert);
        txtDialogTitleTelefono.setText(mensajeDialog);
              //  txtDialogTitleTelefono.setTextSize(TypedValue.COMPLEX_UNIT_SP,14));
        final Bundle args=getArguments();
        animationView.loop(false);
        animationView.playAnimation();
       final Button btnActualizar = (Button) rootView.findViewById(R.id.btnActualizarCliente);
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                validarEdicion(args);



            }
        });
        final ImageButton btnCerrar = (ImageButton) rootView.findViewById(R.id.bt_cerrar);
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();

            }
        });

        return rootView;
    }

    private void validarEdicion(Bundle args) {
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario", sessionUsuario.getUsuario());
        Call<JsonRespuesta> call =   ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getVendedorService().validarEdicion(dataConsulta);
        call.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                        if(response.body().getEstado()==1){
                            AuditarClienteFragmentv2 auditarClienteFragment=new  AuditarClienteFragmentv2(args.getString("codCliente"));
                            auditarClienteFragment.setArguments(args);
                            final FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                            ft.replace(R.id.contenedorFragmentAlta, auditarClienteFragment, TAG);
                            ft.addToBackStack(null);
                            ft.commit();
                            dismiss();
//                            ft.add(R.id.contenedorFragmentAlta, auditarClienteFragment)
//                                    .commit();
//                            dismiss();




                        }else if(response.body().getEstado()==0){
                            ActualizarClienteFragment actualizarClienteFragment=new  ActualizarClienteFragment();
                            actualizarClienteFragment.setArguments(args);
                            final FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                            ft.replace(R.id.contenedorFragmentAlta, actualizarClienteFragment, TAG);
                            ft.addToBackStack(null);
                            ft.commit();
                            dismiss();
                        }

            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {

                Toast.makeText(getContext(), "Problemas de conexión ! " , Toast.LENGTH_LONG).show();
            }
        });
    }


}
