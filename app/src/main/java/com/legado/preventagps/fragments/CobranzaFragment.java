package com.legado.preventagps.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.legado.preventagps.R;
import com.legado.preventagps.activities.vendedor.ContenedorActivity;
import com.legado.preventagps.activities.vendedor.PlanillaCobranzaActivity;
import com.legado.preventagps.adapter.vendedor.DocumentoDeudaRecyclerAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.enums.CLIENTEENUM;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.DocumentoDeuda;
import com.legado.preventagps.util.SessionUsuario;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CobranzaFragment extends Fragment {

    @BindView(R.id.recicladorDocDeuda)
    RecyclerView recicladorDocDeuda;
    RecyclerView.LayoutManager lManagerPedido;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;
    SessionUsuario sessionUsuario;
    Fragment fragmentCobranza;
    ProgressDialog pd;
    public CobranzaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_cobranza, container, false);
        ButterKnife.bind(this,rootView);
        sessionUsuario=new SessionUsuario(rootView.getContext());
        pd=new ProgressDialog(getActivity());
        final String codCliente=getArguments().getString(CLIENTEENUM.CODCLIENTE.getClave());
        fragmentCobranza=this;
        cargarDocumentosDeuda(codCliente);
        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        cargarDocumentosDeuda(codCliente);
                        swiperefresh.setRefreshing(false);
                    }
                });

//        rootView.requestFocus();
//        rootView.setOnKeyListener( new View.OnKeyListener()
//        {
//            @Override
//            public boolean onKey( View v, int keyCode, KeyEvent event )
//            {
//                if( keyCode == KeyEvent.KEYCODE_BACK )
//                {
//                    getActivity().finish();
//
//                    return true;
//                }
//                return false;
//            }
//        } );



        return rootView;
    }

    public void cargarDocumentosDeuda(final String codCliente) {
        pd.show();
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("codCliente",codCliente);
        dataConsulta.put("usuario", sessionUsuario.getUsuario());
        Call<JsonRespuesta<DocumentoDeuda>> call =   ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().deudaByCliente(dataConsulta);
        call.enqueue(new Callback<JsonRespuesta<DocumentoDeuda>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<DocumentoDeuda>> call, Response<JsonRespuesta<DocumentoDeuda>> response) {
                List<DocumentoDeuda> documentos=response.body().getData();
                if(documentos.size()>0){
                    recicladorDocDeuda.setHasFixedSize(true);
                    lManagerPedido = new LinearLayoutManager(getContext());
                    recicladorDocDeuda.setLayoutManager(lManagerPedido);
                    recicladorDocDeuda.setNestedScrollingEnabled(false);
                    DocumentoDeudaRecyclerAdapter detallePedidoRecyclerAdapter = new DocumentoDeudaRecyclerAdapter(documentos, getContext(), getActivity(),codCliente,swiperefresh,fragmentCobranza,getArguments());
                    recicladorDocDeuda.setAdapter(detallePedidoRecyclerAdapter);
                    pd.dismiss();
                }else{
                    if(getArguments().getBoolean("fromPlanilla")){
                        Intent intent = new Intent(getContext(), PlanillaCobranzaActivity.class);
                        intent.putExtras(getArguments());
                        getContext().startActivity(intent);

                    }else {
                        Intent intent = new Intent(getContext(), ContenedorActivity.class);
                         intent.putExtras(getArguments());
                        getActivity().finish();
                        Toast.makeText(getContext(),"CLIENTE NO PRESENTA DEUDA",Toast.LENGTH_LONG);

                        // getContext().startActivity(intent);
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonRespuesta<DocumentoDeuda>> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(getContext(), "Problemas de conexión ! " , Toast.LENGTH_LONG).show();
            }
        });
    }




}
