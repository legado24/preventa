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
import com.legado.preventagps.adapter.vendedor.BonificacionItemRecyclerAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.BonificacionItem;
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
public class BonificacionItemFragment extends Fragment {


    @BindView(R.id.reciclador)
    RecyclerView reciclador;
    @BindView(R.id.list_empty)
    TextView txtEmpty;
    private RecyclerView.LayoutManager lManager;


    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;
    ProgressDialog progressDialog;
    private SessionUsuario sessionUsuario;
     BonificacionItemRecyclerAdapter bonificacionItemRecyclerAdapter;

    public static BonificacionItemFragment newInstance() {
        BonificacionItemFragment fragment = new BonificacionItemFragment();
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

         cargarBonificacionesItem();
        setHasOptionsMenu(true);
        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        //cargarNewIngresosAlmacen();
                        swiperefresh.setRefreshing(false);
                    }
                });




        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_logout, menu);

    }

    public void cargarBonificacionesItem(){
        progressDialog.show();
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario",sessionUsuario.getUsuario());
        Call<JsonRespuesta<BonificacionItem>> call =   ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getNotificacionService().bandejaBonifItem(dataConsulta);
        call.enqueue(new Callback<JsonRespuesta<BonificacionItem>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<BonificacionItem>> call, Response<JsonRespuesta<BonificacionItem>> response) {

                    reciclador.setVisibility(View.VISIBLE);
                    txtEmpty.setVisibility(View.GONE);
                    reciclador.setHasFixedSize(true);
                    lManager = new LinearLayoutManager(getActivity());
                    reciclador.setLayoutManager(lManager);
                bonificacionItemRecyclerAdapter = new BonificacionItemRecyclerAdapter(response.body().getData(), getActivity(), BonificacionItemFragment.this);
                    reciclador.setAdapter(bonificacionItemRecyclerAdapter);
                    progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<JsonRespuesta<BonificacionItem>> call, Throwable t) {
                progressDialog.dismiss();

            }
        });
    }




}
