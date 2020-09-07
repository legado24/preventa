package com.legado.preventagps.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.legado.preventagps.R;
import com.legado.preventagps.activities.vendedor.LoginActivity;
import com.legado.preventagps.adapter.vendedor.FocusAndSugeridoRecyclerAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.dialogs.MyDialogProgress;
import com.legado.preventagps.dialogs.MyDialogRegistrarCumpleaños;
import com.legado.preventagps.enums.CLIENTEENUM;
import com.legado.preventagps.enums.PREVENTAENUM;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.Tools;
import com.legado.preventagps.util.ViewAnimation;
import com.pd.chocobar.ChocoBar;
import com.roughike.bottombar.BottomBar;



import java.util.ArrayList;
import java.util.HashMap;
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
public class DatosClienteFragment extends Fragment {
    private static final String TAG = "DatosClienteFragment";
    @BindView(R.id.txtDescripcion)
    TextView txtDescripcion;
    @BindView(R.id.txtDireccion)
    TextView txtDireccion;
    @BindView(R.id.txtListaPrecios)
    TextView txtListaPrecios;
    @BindView(R.id.txtLimiteCredito)
    TextView txtLimiteCredito;
    @BindView(R.id.txtDeudaVencida)
    TextView txtDeudaVencida;
    @BindView(R.id.txtDeudaNoVencida)
    TextView txtDeudaNoVencida;
    @BindView(R.id.txtTipoCliente)
    TextView txtTipoCliente;
    @BindView(R.id.txtAntiguedad)
    TextView txtAntiguedad;
    @BindView(R.id.txtCumpleaños)
    TextView txtCumpleaños;
    @BindView(R.id.txtVelocidadPago)
    TextView txtVelocidadPago;
    @BindView(R.id.txtCountPedidos)
    TextView txtCountPedidos;
    @BindView(R.id.txtNroTelefono)
    TextView txtNroTelefono;
    @BindView(R.id.imgViewLLamar)
    ImageView imgViewLLamar;

    private SessionUsuario sessionUsuario;

    static BottomBar bottomBarDialog;

    @BindView(R.id.recicladorSugerido)
    RecyclerView recicladorSugerido;
    RecyclerView.LayoutManager lManagerPedido;

    MyDialogProgress dialogDatosCliente;

    @BindView(R.id.bt_toggle_info)
     ImageButton bt_toggle_info;

    @BindView(R.id.bt_hide_info)
    Button bt_hide_info;



    @BindView(R.id.nested_scroll_view)
    NestedScrollView nested_scroll_view;

    @BindView(R.id.lyt_expand_info)
    View lyt_expand_info;

    @BindView(R.id.txtVerArticulos)
    TextView txtVerArticulos;
    FocusAndSugeridoRecyclerAdapter sugeridoRecyclerAdapter;
    static final Integer PHONESTATS = 0x1;
    public static DatosClienteFragment newInstance(BottomBar bottomBar) {
        DatosClienteFragment fragment = new DatosClienteFragment();
        bottomBarDialog = bottomBar;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_datos_cliente, container, false);

        ButterKnife.bind(this, rootView);
        sessionUsuario = new SessionUsuario(rootView.getContext());
        sessionUsuario.guardarPaqueteCarrito(null);
        String codCliente = getArguments().getString(PREVENTAENUM.CODCLIENTE.getClave());
        String descCliente = getArguments().getString(PREVENTAENUM.DESCCLIENTE.getClave());
        String dirCliente = getArguments().getString(PREVENTAENUM.DIRCLIENTE.getClave());
        String codRuta = getArguments().getString(PREVENTAENUM.CODRUTA.getClave());
        String codLocal = getArguments().getString(PREVENTAENUM.CODLOCAL.getClave());
        String codLista = getArguments().getString(PREVENTAENUM.CODLISTA.getClave());
        String codEmpresa = getArguments().getString(PREVENTAENUM.CODEMPRESA.getClave());
        txtDescripcion.setText(descCliente);
        txtDireccion.setText(dirCliente);

        consultarPermiso(Manifest.permission.CALL_PHONE, PHONESTATS);


        cargarDatosCliente(codCliente, codEmpresa, codLista);


        bt_toggle_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionInfo(bt_toggle_info);
            }
        });
        txtVerArticulos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionInfo(bt_toggle_info);
            }
        });

        bt_hide_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionInfo(bt_toggle_info);
            }
        });
        imgViewLLamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txtNroTelefono.getText().toString().equals("Sin número de teléfono")){
                    llamar(txtNroTelefono.getText().toString());
                }

            }
        });


        if(!sessionUsuario.getIsTodoSincronizado()&&!sessionUsuario.getIsOnlyOnline()){
            bottomBarDialog.setDefaultTab(R.id.tab_datos);
            ChocoBar.builder().setBackgroundColor( getResources().getColor(R.color.colorError)).setTextSize(12)
                    .setTextColor(Color.parseColor("#FFFFFF"))
                    .setTextTypefaceStyle(Typeface.ITALIC)
                    .setText("LA DATA NO ESTA COMPLETA , PORFAVOR REGRESAR Y VOLVER A INICIAR LA JORNADA!!")
                    .setActionText("Cerrar")
                    .setActionClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.amber900))
                    .setActionTextSize(12)
                    .setMaxLines(4)
                    .centerText()
                    .setActionTextSize(12)
                    .setActionTextTypefaceStyle(Typeface.BOLD)
                    .setIcon(R.drawable.ic_error_outline_white_48dp)
                    .setActivity(getActivity())
                    .setDuration(ChocoBar.LENGTH_INDEFINITE).build().show();
        }




        return rootView;
    }

    private void cargarDatosClienteOffline() {
        txtLimiteCredito.setText(getArguments().getString("limiteCredito"));
        txtDeudaVencida.setText(getArguments().getString("deudaVencida"));
        txtDeudaNoVencida.setText(getArguments().getString("deudaNoVencida"));
        sessionUsuario.guardarDeudaNoVencida(txtDeudaNoVencida.getText().toString());
        txtTipoCliente.setText(getArguments().getString("tipoNegocio"));
        txtAntiguedad.setText(getArguments().getString("antiguedad"));
        txtVelocidadPago.setText(getArguments().getString("velocidadPago"));
        txtCountPedidos.setText(getArguments().getString("cantidadPedidos"));
        sessionUsuario.guardarDeudaVencida(txtDeudaVencida.getText().toString());
        txtListaPrecios.setText(getArguments().getString("descListaPrecios"));

        ArrayList listaSugeridosOffline= ApiSqlite.getInstance(getContext()).getOperacionesBaseDatos().listarSugeridosByCliente(getArguments().getString(PREVENTAENUM.CODCLIENTE.getClave()));
        ArrayList listaFocusOffline= ApiSqlite.getInstance(getContext()).getOperacionesBaseDatos().listarFocus();
        ArrayList listaSugeridoFocusOffline=new ArrayList();
        listaSugeridoFocusOffline.addAll(listaFocusOffline);
        listaSugeridoFocusOffline.addAll(listaSugeridosOffline);
        recicladorSugerido.setVisibility(View.VISIBLE);
        recicladorSugerido.setHasFixedSize(true);
        lManagerPedido = new LinearLayoutManager(getContext());
        recicladorSugerido.setLayoutManager(lManagerPedido);
        recicladorSugerido.setNestedScrollingEnabled(false);
        sugeridoRecyclerAdapter = new FocusAndSugeridoRecyclerAdapter(listaSugeridoFocusOffline, getActivity());
        recicladorSugerido.setAdapter(sugeridoRecyclerAdapter);
        sessionUsuario.guardarArrayListSugeridos(sugeridoRecyclerAdapter.getItemsRecycler());
        dialogDatosCliente.dismiss();

    }

    private void toggleSectionInfo(View view) {
        boolean show = toggleArrow(view);
        if (show) {
            ViewAnimation.expand(lyt_expand_info, new ViewAnimation.AnimListener() {
                @Override
                public void onFinish() {
                    Tools.nestedScrollTo(nested_scroll_view, lyt_expand_info);
                }
            });
        } else {
            ViewAnimation.collapse(lyt_expand_info);
        }
    }


    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }

    public void llamar(String nroTelefono){
        try{
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", nroTelefono, null)));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void consultarPermiso(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case 1: {

                // Validamos si el usuario acepta el permiso para que la aplicación acceda a los datos internos del equipo, si no denegamos el acceso
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                   llamar(txtNroTelefono.getText().toString());

                } else {

                    Toast.makeText(getActivity(), "Has negado el permiso a la aplicación", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    public void cargarDatosCliente(final String codCliente, String codEmpresa, String codLista) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialogDatosCliente = new MyDialogProgress();
        dialogDatosCliente.show(ft, "dialog");
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario", sessionUsuario.getPaqueteUsuario().getUsuario());
        dataConsulta.put("codCliente", codCliente);
        dataConsulta.put("codEmpresa", codEmpresa);
        dataConsulta.put("codLista", codLista);
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().datosClienteV1(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if(response.body()!=null){
                    if (response.body().getEstado() == 1) {
                        ArrayList objeto = (ArrayList) response.body().getItem();

                       if(new Double(objeto.get(10).toString()).intValue()==1) {
                           txtLimiteCredito.setText(objeto.get(0).toString());
                           txtDeudaVencida.setText(objeto.get(1).toString());
                           txtDeudaNoVencida.setText(objeto.get(8).toString());
                           sessionUsuario.guardarDeudaNoVencida(txtDeudaNoVencida.getText().toString());
                           txtTipoCliente.setText(objeto.get(2).toString());
                           txtAntiguedad.setText(objeto.get(3).toString());
                           txtVelocidadPago.setText(objeto.get(5).toString());
                           txtCountPedidos.setText(objeto.get(6).toString());
                           sessionUsuario.guardarDeudaVencida(txtDeudaVencida.getText().toString());
                           txtListaPrecios.setText(objeto.get(7).toString());
                            txtNroTelefono.setText(objeto.get(11).toString());
                           if (objeto.get(4) == null) {
                               FragmentTransaction ft = getFragmentManager().beginTransaction();
                               MyDialogRegistrarCumpleaños dialogDatosCumple = MyDialogRegistrarCumpleaños.newInstance(bottomBarDialog);
                               Bundle args = getArguments();
                               args.putString(CLIENTEENUM.CODCLIENTE.getClave(), codCliente);
                               dialogDatosCumple.setArguments(args);
                               dialogDatosCumple.show(ft, "dialog");
                           } else {
                               txtCumpleaños.setText(objeto.get(4).toString());
                           }
                           Log.d(TAG, " DATOSCLIENTE=>>>" + objeto);

                           ArrayList listaFocusAndSugeridos= (ArrayList) objeto.get(9);

                           recicladorSugerido.setVisibility(View.VISIBLE);
                           recicladorSugerido.setHasFixedSize(true);
                           lManagerPedido = new LinearLayoutManager(getContext());
                           recicladorSugerido.setLayoutManager(lManagerPedido);
                           recicladorSugerido.setNestedScrollingEnabled(false);

                           sugeridoRecyclerAdapter = new FocusAndSugeridoRecyclerAdapter(listaFocusAndSugeridos, getActivity());
                           recicladorSugerido.setAdapter(sugeridoRecyclerAdapter);
                           dialogDatosCliente.dismiss();
                           sessionUsuario.guardarArrayListSugeridos(sugeridoRecyclerAdapter.getItemsRecycler());

                       }else{
                           Toast.makeText(getContext(), "ES OBLIGATORIO REGISTRAR EL TELÉFONO!!", Toast.LENGTH_LONG).show();
                           getActivity().finish();

                       }


                    } else {
                        dialogDatosCliente.dismiss();

                            cargarDatosClienteOffline();

                    }


                }else{
                        dialogDatosCliente.dismiss();

                    //if(getArguments().getString("codigoLimite")==null){

                        cargarDatosClienteOffline();
//                    }else{
//                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
//                                .setTitleText("Oops...")
//                                .setContentText(getString(R.string.txtMensajeServidorCaido))
//                                .show();
//
//                    }
                }




            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                if(sessionUsuario.getIsTodoSincronizado()){
                    cargarDatosClienteOffline();
                }else{
                    dialogDatosCliente.dismiss();
                   if(!sessionUsuario.getIsOnlyOnline()){
                        ChocoBar.builder().setBackgroundColor( getResources().getColor(R.color.colorError)).setTextSize(12)
                                .setTextColor(Color.parseColor("#FFFFFF"))
                                .setTextTypefaceStyle(Typeface.ITALIC)
                                .setText("LA DATA NO ESTA COMPLETA , PORFAVOR REGRESAR Y VOLVER A INICIAR LA JORNADA!!")
                                .setActionText("Cerrar")
                                .setActionClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                })
                                .setActionTextColor(getResources().getColor(R.color.amber900))
                                .setActionTextSize(12)
                                .setMaxLines(4)
                                .centerText()
                                .setActionTextSize(12)
                                .setActionTextTypefaceStyle(Typeface.BOLD)
                                .setIcon(R.drawable.ic_error_outline_white_48dp)
                                .setActivity(getActivity())
                                .setDuration(ChocoBar.LENGTH_INDEFINITE).build().show();
                }  else{

                       SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                       pDialog.setTitleText("Oops...");
                     pDialog.setContentText(getString(R.string.txtMensajeConexion) );
                       pDialog.setConfirmText("Reintentar");
                       pDialog.setContentTextSize(12);
                       pDialog.setCancelable(false);
                       pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                           @Override
                           public void onClick(SweetAlertDialog sweetAlertDialog) {
                               sweetAlertDialog.dismissWithAnimation();
                               cargarDatosCliente(codCliente,codEmpresa,codLista);
                           }
                       });
                       pDialog.show();






                    }


                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

    }
}
