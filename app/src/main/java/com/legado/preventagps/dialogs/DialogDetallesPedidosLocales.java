package com.legado.preventagps.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.legado.preventagps.R;
import com.legado.preventagps.adapter.vendedor.DetallePedidoLocalRecyclerAdapter;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.modelo.CarritoCompras;
import com.legado.preventagps.util.SessionUsuario;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class DialogDetallesPedidosLocales extends DialogFragment {
    SessionUsuario sessionUsuario;


    @BindView(R.id.btnCerrar)
    Button btnCerrar;

    @BindView(R.id.recicladorDetalles)
    RecyclerView recicladorDetalles;
    DetallePedidoLocalRecyclerAdapter detallePedidoLocalRecyclerAdapter;

    RecyclerView.LayoutManager lManagerPedido;


    Bundle args;

    public static DialogDetallesPedidosLocales newInstance() {
        DialogDetallesPedidosLocales fragment = new DialogDetallesPedidosLocales();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_dialog_detalles_pedidos_locales, container, false);
            ButterKnife.bind(this, rootView);
            getDialog().setCanceledOnTouchOutside(false);
            sessionUsuario = new SessionUsuario(getActivity());
            args = getArguments();

            cargarPedidos(args.getString("nroPedidoLocal"));

            btnCerrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    dismiss();
                }
            });

            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener()
            {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, android.view.KeyEvent event) {
                    if ((keyCode ==  android.view.KeyEvent.KEYCODE_BACK))
                    {

                        return true; // pretend we've processed it
                    }
                    else
                        return false; // pass on to be processed as normal
                }
            });



        return rootView;
    }


    public void cargarPedidos(String nroPedido) {

       List<CarritoCompras> listaDetalles= ApiSqlite.getInstance(getContext()).getOperacionesBaseDatos().listaDetallesPedidosLocales(nroPedido);
        recicladorDetalles.setVisibility(View.VISIBLE);
        recicladorDetalles.setHasFixedSize(true);
        lManagerPedido = new LinearLayoutManager(getContext());
        recicladorDetalles.setLayoutManager(lManagerPedido);
        recicladorDetalles.setNestedScrollingEnabled(false);
        detallePedidoLocalRecyclerAdapter = new DetallePedidoLocalRecyclerAdapter(listaDetalles,getContext(),getActivity(),this);
        recicladorDetalles.setAdapter(detallePedidoLocalRecyclerAdapter);

    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();


    }


}
