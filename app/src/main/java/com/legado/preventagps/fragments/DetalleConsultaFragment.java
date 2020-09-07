package com.legado.preventagps.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import com.legado.preventagps.R;
import com.legado.preventagps.adapter.vendedor.ConsultaDetalleAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.ConsultaDetalle;
import com.legado.preventagps.util.SessionUsuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetalleConsultaFragment extends Fragment {

    private static final String TAG = DetalleConsultaFragment.class.getSimpleName();
    @BindView(R.id.list_detalle)
    ListView listaDetalle;
    public static ProgressDialog progressDialog;
    List<ConsultaDetalle> detalles=new ArrayList<>();
    SessionUsuario sessionUsuario;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_detalle_consulta, container, false);
        ButterKnife.bind(this, view);
        sessionUsuario=new SessionUsuario(getActivity());

        progressDialog = new ProgressDialog(getActivity(),R.style.AppTheme_MyDialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        sessionUsuario.guardarEstadoFragment(false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String codEmpresa = (String) bundle.getString("codEmpresa");
            String nroPedido = (String) bundle.getString("nroPedido");
            String codAlmacen=(String)bundle.getString("codAlmacen");
            String descCliente=(String)bundle.getString("descCliente");
            getActivity().setTitle(
                    Html.fromHtml("<font color='#FFFFFF'>"
                            +nroPedido
                            + "</font>"));
            progressDialog.show();
             cargarDetallesPedidos(codEmpresa,nroPedido,codAlmacen);
       }

       // view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    sessionUsuario.guardarEstadoFragment(true);

                    return true;
                }
                return false;
            }
        } );



        return view;
    }


    public void cargarDetallesPedidos(String codEmpresa, String nroPedido,String codAlmacen){
        progressDialog.show();
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("codEmpresa",codEmpresa);
        dataConsulta.put("nroPedido", nroPedido);
        dataConsulta.put("codAlmacen",codAlmacen);

        Call<JsonRespuesta<ConsultaDetalle>> call =   ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getVentaService().consultaDetalllesPedidos(dataConsulta);
        call.enqueue(new Callback<JsonRespuesta<ConsultaDetalle>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<ConsultaDetalle>> call, Response<JsonRespuesta<ConsultaDetalle>> response) {
                if(response.body()!=null){
                    JsonRespuesta<ConsultaDetalle> rpta=response.body();
                    ConsultaDetalleAdapter detalleAdapter = new ConsultaDetalleAdapter(getActivity(), R.id.list_detalle,rpta.getData() );
                    listaDetalle.setAdapter(detalleAdapter);
                    progressDialog.dismiss();
                }else{
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.txtMensajeServidorCaido))
                            .show();
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<JsonRespuesta<ConsultaDetalle>> call, Throwable t) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.txtMensajeConexion))
                        .show();
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public void onResume() {
//        getActivity().setTitle( Html.fromHtml("<font color='#FFFFFF'>"
//                + "REGISTRAR CLIENTE"
//                + "</font>"));
        super.onResume();
    }
}
