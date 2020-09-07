package com.legado.preventagps.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.legado.preventagps.R;
import com.legado.preventagps.adapter.vendedor.RepartoVentaRecyclerAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.util.SessionUsuario;

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
public class RepartoVentaFragment extends Fragment {


    @BindView(R.id.reciclador)
    RecyclerView reciclador;
    @BindView(R.id.list_empty)
    TextView txtEmpty;
    private RecyclerView.LayoutManager lManager;

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;
    ProgressDialog progressDialog;
    private SessionUsuario sessionUsuario;
     RepartoVentaRecyclerAdapter repartoVentaRecyclerAdapter;

    public static RepartoVentaFragment newInstance() {
        RepartoVentaFragment fragment = new RepartoVentaFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bonificacionitem, container, false);
        ButterKnife.bind(this, rootView);
        sessionUsuario=new SessionUsuario(getContext());
         progressDialog = new ProgressDialog(getActivity(),R.style.AppTheme_MyDialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        if(getArguments()!=null){
            cargarRepartoVentaConPosicion(getArguments().getString("nroPedido"));
        }else{
            cargarRepartoVenta();
        }


        setHasOptionsMenu(true);
        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        cargarRepartoVenta();
                        swiperefresh.setRefreshing(false);
                    }
                });




        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_logout, menu);

    }

    public void cargarRepartoVentaConPosicion(String nroPedido){
        progressDialog.show();
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario",sessionUsuario.getUsuario());
        Call<JsonRespuesta> call =   ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getNotificacionService().bandejaRepartoVenta(dataConsulta);
        call.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {

                    reciclador.setVisibility(View.VISIBLE);
                    txtEmpty.setVisibility(View.GONE);
                    reciclador.setHasFixedSize(true);
                    lManager = new LinearLayoutManager(getActivity());
                    reciclador.setLayoutManager(lManager);
                   repartoVentaRecyclerAdapter = new RepartoVentaRecyclerAdapter((ArrayList) response.body().getData(), getActivity(), RepartoVentaFragment.this);
                    reciclador.setAdapter(repartoVentaRecyclerAdapter);
                reciclador.scrollToPosition(repartoVentaRecyclerAdapter.getPositionByPedido(nroPedido));
                    progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                progressDialog.dismiss();

            }
        });
    }

    public void cargarRepartoVenta(){
        progressDialog.show();
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario",sessionUsuario.getUsuario());
        Call<JsonRespuesta> call =   ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getNotificacionService().bandejaRepartoVenta(dataConsulta);
        call.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {

                reciclador.setVisibility(View.VISIBLE);
                txtEmpty.setVisibility(View.GONE);
                reciclador.setHasFixedSize(true);
                lManager = new LinearLayoutManager(getActivity());
                reciclador.setLayoutManager(lManager);
                repartoVentaRecyclerAdapter = new RepartoVentaRecyclerAdapter((ArrayList) response.body().getData(), getActivity(), RepartoVentaFragment.this);
                reciclador.setAdapter(repartoVentaRecyclerAdapter);
//                reciclador.scrollToPosition(10);
                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                progressDialog.dismiss();

            }
        });
    }


}
