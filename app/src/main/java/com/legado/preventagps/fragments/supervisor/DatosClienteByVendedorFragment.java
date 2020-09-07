package com.legado.preventagps.fragments.supervisor;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.legado.preventagps.R;
import com.legado.preventagps.adapter.vendedor.FocusAndSugeridoRecyclerAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.dialogs.MyDialogError;
import com.legado.preventagps.dialogs.MyDialogProgress;
import com.legado.preventagps.dialogs.MyDialogRegistrarCumpleaños;
import com.legado.preventagps.enums.CLIENTEENUM;
import com.legado.preventagps.enums.PREVENTAENUM;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.Tools;
import com.legado.preventagps.util.ViewAnimation;
import com.roughike.bottombar.BottomBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DatosClienteByVendedorFragment extends Fragment {
    private static final String TAG = "DatosClienteByVendedorFragment";
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

    public static DatosClienteByVendedorFragment newInstance(BottomBar bottomBar) {
        DatosClienteByVendedorFragment fragment = new DatosClienteByVendedorFragment();
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
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialogDatosCliente = new MyDialogProgress();
        dialogDatosCliente.show(ft, "dialog");
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


        return rootView;
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


    public void cargarDatosCliente(final String codCliente, String codEmpresa, String codLista) {
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario", sessionUsuario.getPaqueteUsuario().getUsuario());
        dataConsulta.put("codCliente", codCliente);
        dataConsulta.put("codEmpresa", codEmpresa);
        dataConsulta.put("codLista", codLista);
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().datosClienteV1(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {

                if (response.isSuccessful()) {
                    if (response.body().getEstado() == 1) {
                        ArrayList objeto = (ArrayList) response.body().getItem();
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

                        ArrayList listaSugeridos = (ArrayList) objeto.get(9);
                        for (int i = 0; i < listaSugeridos.size(); i++) {
                            ArrayList obj = (ArrayList) listaSugeridos.get(i);
                            System.out.println(obj.get(0));
                        }

                        recicladorSugerido.setVisibility(View.VISIBLE);
                        recicladorSugerido.setHasFixedSize(true);
                        lManagerPedido = new LinearLayoutManager(getContext());
                        recicladorSugerido.setLayoutManager(lManagerPedido);
                        recicladorSugerido.setNestedScrollingEnabled(false);

                        sugeridoRecyclerAdapter = new FocusAndSugeridoRecyclerAdapter(listaSugeridos, getActivity());
                        recicladorSugerido.setAdapter(sugeridoRecyclerAdapter);
                        dialogDatosCliente.dismiss();
                        sessionUsuario.guardarArrayListSugeridos(sugeridoRecyclerAdapter.getItemsRecycler());

                    } else {

                        Toast.makeText(getActivity(), R.string.txtMensajeServidorCaido, Toast.LENGTH_LONG).show();


                    }


                }


            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                MyDialogError newFragment = MyDialogError.newInstance(bottomBarDialog, dialogDatosCliente);
                newFragment.setArguments(getArguments());
                newFragment.show(ft, "dialog");
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();

    }
}
