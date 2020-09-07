package com.legado.preventagps.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.legado.preventagps.R;
import com.legado.preventagps.adapter.vendedor.PlanillaCobranzaRecyclerAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.PlanillaCobranza;
import com.legado.preventagps.util.PaquetePlanillaCobranza;
import com.legado.preventagps.util.SessionUsuario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class PlanillaCobranzaFragment extends Fragment {


@BindView(R.id.reciclador)
RecyclerView reciclador;
    @BindView(R.id.list_empty)
    TextView txtEmpty;
private RecyclerView.LayoutManager lManager;


    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;
    ProgressDialog progressDialog;
     private SessionUsuario sessionUsuario;
    PlanillaCobranzaRecyclerAdapter planillaCobranzaRecyclerAdapter;

    public static PlanillaCobranzaFragment newInstance() {
        PlanillaCobranzaFragment fragment = new PlanillaCobranzaFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_planilla_cobranza, container, false);
        ButterKnife.bind(this, rootView);
        sessionUsuario=new SessionUsuario(getContext());
        sessionUsuario.guardarPaquetePlanillaCobranza(null);
         progressDialog = new ProgressDialog(getActivity(),R.style.AppTheme_MyDialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        cargarPlanillaCobranza();
        setHasOptionsMenu(true);
        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        cargarPlanillaCobranza();
                        swiperefresh.setRefreshing(false);
                    }
                });




        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem mSearch = menu.findItem(R.id.action_search);
        final SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("BUSCAR CLIENTE");
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println();
            }
        });

         mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
              //  cargarPlanillaCobranza();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String clienteText=newText.toLowerCase();
                List<PlanillaCobranza> planillaList=new ArrayList<>();
               for(PlanillaCobranza pc : sessionUsuario.getPaquetePlanillaCobranza().getListaPlanilla()){
                    if(pc.getDescCliente().toLowerCase().contains(clienteText)){
                        planillaList.add(pc);
                    }
                }
                planillaCobranzaRecyclerAdapter.updateList(planillaList);
                planillaCobranzaRecyclerAdapter=new PlanillaCobranzaRecyclerAdapter(planillaList,getActivity(),swiperefresh,PlanillaCobranzaFragment.this);
                reciclador.setAdapter(planillaCobranzaRecyclerAdapter);
                return true;
            }
        });


    }

    public void cargarPlanillaCobranza(){
       progressDialog.show();
         Map<String, String> dataConsulta = new HashMap<>();
          dataConsulta.put("usuario",sessionUsuario.getUsuario());
         Call<JsonRespuesta<PlanillaCobranza>> call =   ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getCobranzaService().getPlanillaCobranza(dataConsulta);
        call.enqueue(new Callback<JsonRespuesta<PlanillaCobranza>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<PlanillaCobranza>> call, Response<JsonRespuesta<PlanillaCobranza>> response) {

                if(response.body()!=null){
                    if(response.code()==401){
                        Toast.makeText(
                                getActivity(), "SE EXPIRÓ EL TIEMPO DE LA TOMA DE PEDIDOS PARA SU USUARIO ,POR FAVOR COMUNIQUESE CON SU COORDINADOR.",
                                Toast.LENGTH_LONG).show();
                        //  progressDialog.dismiss();
                    }else if(response.code()==403){
                        Toast.makeText(getActivity(), "USUARIO INACTIVO, COMUNIQUESE CON CON SU COORDINADOR.",
                                Toast.LENGTH_LONG).show();
                        //   progressDialog.dismiss();

                    }else {
                        reciclador.setVisibility(View.VISIBLE);
                        txtEmpty.setVisibility(View.GONE);
                        reciclador.setHasFixedSize(true);
                        lManager = new LinearLayoutManager(getActivity());
                        reciclador.setLayoutManager(lManager);
                        JsonRespuesta<PlanillaCobranza> rpta = response.body();
                        planillaCobranzaRecyclerAdapter = new PlanillaCobranzaRecyclerAdapter(rpta.getData(), getActivity(),swiperefresh,PlanillaCobranzaFragment.this);
                        reciclador.setAdapter(planillaCobranzaRecyclerAdapter);
                        PaquetePlanillaCobranza paquetePlanillaCobranza=new PaquetePlanillaCobranza();
                        paquetePlanillaCobranza.setListaPlanilla(rpta.getData());
                        sessionUsuario.guardarPaquetePlanillaCobranza(paquetePlanillaCobranza);
                        progressDialog.dismiss();

                    }
                }else{
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.txtMensajeServidorCaido))
                            .show();
                    progressDialog.dismiss();

                }

            }

            @Override
            public void onFailure(Call<JsonRespuesta<PlanillaCobranza>> call, Throwable t) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.txtMensajeConexion))
                        .show();
                progressDialog.dismiss();

              //
            }
        });
    }




}
