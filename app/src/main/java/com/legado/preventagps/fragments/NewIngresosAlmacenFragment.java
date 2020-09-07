package com.legado.preventagps.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.legado.preventagps.R;
import com.legado.preventagps.adapter.vendedor.IngresoCompraRecyclerAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.IngresoCompra;
import com.legado.preventagps.util.SessionUsuario;

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
public class NewIngresosAlmacenFragment extends Fragment {


    @BindView(R.id.reciclador)
    RecyclerView reciclador;
    @BindView(R.id.list_empty)
    TextView txtEmpty;
    private RecyclerView.LayoutManager lManager;


    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;
    ProgressDialog progressDialog;
    private SessionUsuario sessionUsuario;
     IngresoCompraRecyclerAdapter ingresoCompraRecyclerAdapter;

    public static NewIngresosAlmacenFragment newInstance() {
        NewIngresosAlmacenFragment fragment = new NewIngresosAlmacenFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_ingresos_almacen, container, false);
        ButterKnife.bind(this, rootView);
        sessionUsuario=new SessionUsuario(getContext());
         progressDialog = new ProgressDialog(getActivity(),R.style.AppTheme_MyDialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);

        cargarNewIngresosAlmacen();
        setHasOptionsMenu(true);
        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        cargarNewIngresosAlmacen();
                        swiperefresh.setRefreshing(false);
                    }
                });




        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_logout, menu);

    }

    public void cargarNewIngresosAlmacen(){
        progressDialog.show();
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario",sessionUsuario.getUsuario());
        Call<JsonRespuesta<IngresoCompra>> call =   ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getNotificacionService().bandejaIngresos(dataConsulta);
        call.enqueue(new Callback<JsonRespuesta<IngresoCompra>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<IngresoCompra>> call, Response<JsonRespuesta<IngresoCompra>> response) {

                    reciclador.setVisibility(View.VISIBLE);
                    txtEmpty.setVisibility(View.GONE);
                    reciclador.setHasFixedSize(true);
                    lManager = new LinearLayoutManager(getActivity());
                    reciclador.setLayoutManager(lManager);
                    ingresoCompraRecyclerAdapter = new IngresoCompraRecyclerAdapter(response.body().getData(), getActivity(),NewIngresosAlmacenFragment.this);
                    reciclador.setAdapter(ingresoCompraRecyclerAdapter);
                    progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<JsonRespuesta<IngresoCompra>> call, Throwable t) {
                progressDialog.dismiss();

            }
        });
    }




}
