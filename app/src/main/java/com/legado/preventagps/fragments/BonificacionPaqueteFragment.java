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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.legado.preventagps.R;
import com.legado.preventagps.adapter.vendedor.BonificacionPaqueteRecyclerAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.BonificacionItem;
import com.legado.preventagps.modelo.BonificacionPaquete;
import com.legado.preventagps.util.SessionUsuario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
public class BonificacionPaqueteFragment extends Fragment {


    @BindView(R.id.reciclador)
    RecyclerView reciclador;
    @BindView(R.id.list_empty)
    TextView txtEmpty;
    private RecyclerView.LayoutManager lManager;


    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;
    ProgressDialog progressDialog;
    private SessionUsuario sessionUsuario;
    BonificacionPaqueteRecyclerAdapter bonificacionPaqueteRecyclerAdapter;

    public static BonificacionPaqueteFragment newInstance() {
        BonificacionPaqueteFragment fragment = new BonificacionPaqueteFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bonificacionpaquete, container, false);
        ButterKnife.bind(this, rootView);
        sessionUsuario = new SessionUsuario(getContext());
        progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_MyDialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);

        cargarBonificacionesPaquete();
        setHasOptionsMenu(true);
        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        //cargarNewIngresosAlmacen();
                        cargarBonificacionesPaquete();
                        swiperefresh.setRefreshing(false);
                    }
                });


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_logout, menu);

    }

    public void cargarBonificacionesPaquete() {
        progressDialog.show();
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario", sessionUsuario.getUsuario());
        Call<JsonRespuesta> call = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getNotificacionService().bandejaBonifPaquete(dataConsulta);
        call.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {

                ArrayList lista = (ArrayList) response.body().getData();
                ArrayList participaciones = (ArrayList) lista.get(0);
                ArrayList bonificaciones = (ArrayList) lista.get(1);

                List<BonificacionPaquete> listaParticipacion = new ArrayList<>();
                for (int i = 0; i < participaciones.size(); i++) {
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.toJsonTree(participaciones.get(i)).getAsJsonObject();
                    BonificacionPaquete participacion = gson.fromJson(jsonObject, BonificacionPaquete.class);
                    listaParticipacion.add(participacion);
                }


                List<BonificacionItem> listaBonificaciones = new ArrayList<>();
                for (int i = 0; i < bonificaciones.size(); i++) {
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.toJsonTree(bonificaciones.get(i)).getAsJsonObject();
                    BonificacionItem bonificacion = gson.fromJson(jsonObject, BonificacionItem.class);
                    listaBonificaciones.add(bonificacion);
                }


                //saca la cabecera de la participacion sin repetir
                List<BonificacionPaquete> detalles = listaParticipacion;
                HashSet<String> hashSetCabecera = new HashSet<>();
                for (int i = 0; i < listaParticipacion.size(); i++) {
                    BonificacionPaquete bp = listaParticipacion.get(i);
                    hashSetCabecera.add(bp.getHoraCreacion() + "|" + bp.getCodPaquete() + "|" + bp.getCodLista() + "|" + bp.getModoParticipacion() + "|" + bp.getDescPaquete() + "|" + bp.getUmPaquete());

                }

                List<String> listStringCabecera = new ArrayList<>(hashSetCabecera);
                BonificacionPaquete[] detallesAux = new BonificacionPaquete[listStringCabecera.size()];
                for (int i = 0; i < listStringCabecera.size(); i++) {
                    String[] cabeceraSplit = listStringCabecera.get(i).split("\\|");
                    BonificacionPaquete bpCab = new BonificacionPaquete();
                    bpCab.setHoraCreacion(cabeceraSplit[0]);
                    bpCab.setCodPaquete(cabeceraSplit[1]);
                    bpCab.setCodLista(cabeceraSplit[2]);
                    bpCab.setModoParticipacion(new Integer(cabeceraSplit[3]));
                    bpCab.setDescPaquete(cabeceraSplit[4]);
                    bpCab.setUmPaquete(cabeceraSplit[5]);
                    detallesAux[i] = bpCab;
                }
                //ordena la cabecera por hora
                Arrays.sort(detallesAux);
                List<BonificacionPaquete> listCabeceraAux = Arrays.asList(detallesAux);

                List<BonificacionPaquete> listCabeceraFinal = new ArrayList<>();


                for (int i = 0; i < listCabeceraAux.size(); i++) {

                    BonificacionPaquete bpCab = listCabeceraAux.get(i);

                    List<BonificacionPaquete> det = new ArrayList<>();
                    for (int j = 0; j < detalles.size(); j++) {
                        if (detalles.get(j).getCodLista().equals(bpCab.getCodLista()) &&
                                detalles.get(j).getCodPaquete().equals(bpCab.getCodPaquete()) &&
                                detalles.get(j).getModoParticipacion().equals(bpCab.getModoParticipacion()) &&
                                detalles.get(j).getUmPaquete().equals(bpCab.getUmPaquete())) {
                            BonificacionPaquete bpDet = new BonificacionPaquete();
                            bpDet.setCodItem(detalles.get(j).getCodItem());
                            bpDet.setDescItem(detalles.get(j).getDescItem());

                            det.add(bpDet);
                        }

                    }


                    List<BonificacionItem> bonDetalles = new ArrayList<>();
                    for (int j = 0; j < listaBonificaciones.size(); j++) {

                        if (listaBonificaciones.get(j).getCodLista().equals(bpCab.getCodLista()) &&
                                listaBonificaciones.get(j).getCodPaquete().equals(bpCab.getCodPaquete()) &&
                                listaBonificaciones.get(j).getModoParticipacion().equals(bpCab.getModoParticipacion()) &&
                                listaBonificaciones.get(j).getUmPaquete().equals(bpCab.getUmPaquete())) {
                            BonificacionItem bi = new BonificacionItem();
                            bi.setCodItemBonif(listaBonificaciones.get(j).getCodItemBonif());
                            bi.setDescItemBonif(listaBonificaciones.get(j).getDescItemBonif());
                            bi.setCantBonif(listaBonificaciones.get(j).getCantBonif());
                            bi.setMultiplo(listaBonificaciones.get(j).getMultiplo());
                            bi.setCantMinima(listaBonificaciones.get(j).getCantMinima());
                            bi.setCantMaxima(listaBonificaciones.get(j).getCantMaxima());
                            bonDetalles.add(bi);

                        }
                    }

                    bpCab.setParticipaciones(det);
                    bpCab.setBonificaciones(bonDetalles);

                    listCabeceraFinal.add(bpCab);

                }

                reciclador.setVisibility(View.VISIBLE);
                txtEmpty.setVisibility(View.GONE);
                reciclador.setHasFixedSize(true);
                lManager = new LinearLayoutManager(getActivity());
                reciclador.setLayoutManager(lManager);
                bonificacionPaqueteRecyclerAdapter = new BonificacionPaqueteRecyclerAdapter(listCabeceraFinal, getActivity(), "C");
               // bonificacionPaqueteRecyclerAdapter.setHasStableIds(true);
                reciclador.setAdapter(bonificacionPaqueteRecyclerAdapter);

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                progressDialog.dismiss();

            }
        });
    }


}
