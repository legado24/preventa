package com.legado.preventagps.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.legado.preventagps.R;
import com.legado.preventagps.adapter.vendedor.PagosRecyclerAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.ConsultaPagos;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.SimpleDividerItemDecorator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
public class ConsultaPagosFragment extends Fragment {


    @BindView(R.id.txtFecha)
    TextView txtFecha;

    @BindView(R.id.reciclador)
    RecyclerView reciclador;
    @BindView(R.id.list_empty)
    TextView txtEmpty;
    @BindView(R.id.txtCobranzaDiaria)
    TextView txtCobranzaDiaria;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;
    RecyclerView.LayoutManager lManager;

    ProgressDialog progressDialog;
    private int year, month, day;
    private SessionUsuario sessionUsuario;
    public static ConsultaPagosFragment newInstance() {
        ConsultaPagosFragment fragment = new ConsultaPagosFragment();
        return fragment;
    }

    public Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(getActivity(), R.style.DialogTheme
                    , new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int yyyy, int mm, int dd) {
                    txtFecha.setText(getFormatoFecha(yyyy, mm, dd));
               consultaPagos(txtFecha.getText().toString());
                }
            }, year, month, day);
        }
        return null;
    }

    public String getFormatoFecha(int yyyy, int mm, int dd) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = dateFormat.parse(dd + "-" + (mm + 1) + "-" + yyyy);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outDate = dateFormat.format(date);

        return outDate;

    }

    public void inicializarFechaHoy() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        txtFecha.setText(getFormatoFecha(year, month, day));
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_consultapagos, container, false);
        ButterKnife.bind(this, rootView);
        sessionUsuario=new SessionUsuario(getContext());
        progressDialog = new ProgressDialog(getActivity(),R.style.AppTheme_MyDialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        txtFecha.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog dialog = onCreateDialog(999);
                        if (dialog != null) {
                            dialog.show();
                        }
                    }
                }
        );
            progressDialog.show();
            inicializarFechaHoy();
        consultaPagos(txtFecha.getText().toString());
        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        consultaPagos(txtFecha.getText().toString());
                        swiperefresh.setRefreshing(false);
                    }
                });




        return rootView;
    }


    public void consultaPagos(String fecha){
        progressDialog.show();
         Map<String, String> dataConsulta = new HashMap<>();
          dataConsulta.put("usuario",sessionUsuario.getUsuario());
        dataConsulta.put("fecha", fecha);
        Call<JsonRespuesta<ConsultaPagos>> call =   ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getVendedorService().pagosByUsuario(dataConsulta);
        call.enqueue(new Callback<JsonRespuesta<ConsultaPagos>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<ConsultaPagos>> call, Response<JsonRespuesta<ConsultaPagos>> response) {
                   if(response.body()!=null){
                       reciclador.setVisibility(View.VISIBLE);
                       txtEmpty.setVisibility(View.GONE);
                       reciclador.setHasFixedSize(true);
                       lManager = new LinearLayoutManager(getActivity());
                       reciclador.setLayoutManager(lManager);
                       JsonRespuesta<ConsultaPagos> rpta = response.body();
                       PagosRecyclerAdapter pagosRecyclerAdapter = new PagosRecyclerAdapter(rpta.getData(), getActivity(),ConsultaPagosFragment.this,swiperefresh,txtFecha.getText().toString());
                       reciclador.setAdapter(pagosRecyclerAdapter);
                       txtCobranzaDiaria.setText(pagosRecyclerAdapter.montoTotalDiario().toString());
                       reciclador.addItemDecoration(new SimpleDividerItemDecorator(getActivity()));
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
            public void onFailure(Call<JsonRespuesta<ConsultaPagos>> call, Throwable t) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.txtMensajeConexion))
                        .show();
                progressDialog.dismiss();
            }
        });




    }




}
