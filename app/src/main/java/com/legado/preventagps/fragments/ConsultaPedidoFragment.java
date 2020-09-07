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

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.legado.preventagps.R;
import com.legado.preventagps.adapter.vendedor.CabeceraRecyclerAdapter;
import com.legado.preventagps.adapter.vendedor.SpinnerSimpleAdapter;
import com.legado.preventagps.adapter.vendedor.SpinnerVendedorCustomAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.ConsultaCabecera;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.SimpleDividerItemDecorator;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
public class ConsultaPedidoFragment extends Fragment {

    @BindView(R.id.txtFecha)
    TextView txtFecha;

    @BindView(R.id.reciclador)
    RecyclerView reciclador;
    @BindView(R.id.list_empty)
    TextView txtEmpty;

    @BindView(R.id.txtVentaDiaria)
    TextView txtVentaDiaria;



    @BindView(R.id.txtCantidadIngresados)
    TextView txtCantidadIngresados;
    @BindView(R.id.txtCantidadAprobados)
    TextView txtCantidadAprobados;
    @BindView(R.id.txtCantidadAnulados)
    TextView txtCantidadAnulados;

    @BindView(R.id.layoutTotal)
    LinearLayout layoutTotal;

    @BindView(R.id.layoutCantidadDet)
    LinearLayout layoutCantidadDet;

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;
    RecyclerView.LayoutManager lManager;

    @BindView(R.id.spinnerVend)
    Spinner spinnerVend;

    @BindView(R.id.spinnerAlmacenes)
    Spinner spinnerAlmacenes;
    ProgressDialog progressDialog;
    private int year, month, day;
    private SessionUsuario sessionUsuario;
    private String codLocalidad;
    private String codAlmacen;


    public static ConsultaPedidoFragment newInstance() {
        ConsultaPedidoFragment fragment = new ConsultaPedidoFragment();
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
                 cargarPedidos(txtFecha.getText().toString(),codLocalidad,codAlmacen);
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
        View rootView = inflater.inflate(R.layout.fragment_consultapedidos, container, false);
        ButterKnife.bind(this, rootView);

       getActivity().setTitle(
                Html.fromHtml("<font color='#FFFFFF'>"
                        + "CONSULTA PEDIDOS"
                        + "</font>"));
        sessionUsuario=new SessionUsuario(getContext());
        //mRootLayout = findViewById(R.id.root_layout);
        progressDialog = new ProgressDialog(getActivity(),R.style.AppTheme_MyDialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);

        obtenerDatosUsuario(sessionUsuario.getUsuario());
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



           inicializarFechaHoy();

        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                         cargarPedidos(txtFecha.getText().toString(),codLocalidad,codAlmacen);
                        swiperefresh.setRefreshing(false);
                    }
                });

        spinnerVend.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ArrayList objVend = (ArrayList) spinnerVend.getSelectedItem();
                codLocalidad=objVend.get(0).toString();
//                getArguments().putString(PREVENTAENUM.CODALMACEN.getClave(),objVend.get(10).toString());
//
                obtenerAlmacenes(objVend.get(0).toString(),objVend.get(8).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        spinnerAlmacenes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ArrayList objAlmacen = (ArrayList) spinnerAlmacenes.getSelectedItem();
                codAlmacen=objAlmacen.get(0).toString();
//                getArguments().putString(PREVENTAENUM.CODALMACEN.getClave(),objVend.get(10).toString());
//
                cargarPedidos(txtFecha.getText().toString(),codLocalidad,codAlmacen);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });



        return rootView;
    }
    private void obtenerAlmacenes(String codEmpresa, final String codLocalidad) {
        progressDialog.show();
        final Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("codEmpresa", codEmpresa);
        dataConsulta.put("codLocalidad", codLocalidad);
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getAlmacenService().almacenesByLocalidad(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if(response.body().getEstado()==1) {
                    SpinnerSimpleAdapter spinnerSimpleAdapter=new SpinnerSimpleAdapter(getActivity(),R.layout.list_simplespinner_item,(ArrayList) response.body().getData());
                    spinnerAlmacenes.setAdapter(spinnerSimpleAdapter);
                    //dialogPreventa.dismiss();
                    ArrayList objLocalidad= (ArrayList) spinnerVend.getSelectedItem();
                    ArrayList objAlmacen= (ArrayList) spinnerAlmacenes.getSelectedItem();

                    codAlmacen=objAlmacen.get(0).toString();
                    progressDialog.dismiss();
                    cargarPedidos(txtFecha.getText().toString(),objLocalidad.get(0).toString(),objAlmacen.get(0).toString());
                }else{

                }

            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {

//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                MyDialogErrorPreventa newFragment = MyDialogErrorPreventa.newInstance(bottomBarDialog,dialogPreventa);
//                newFragment.setArguments(getArguments());
//                newFragment.show(ft, "dialog");
            }
        });

    }
    private void obtenerDatosUsuario(String usuario) {
        progressDialog.show();
        final Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario", usuario);
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getVendedorService().datosUsuario(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
               if(response.body()!=null){
                   if(response.body().getEstado()==1){
                       SpinnerVendedorCustomAdapter spinnerVendedorCustomAdapter=new SpinnerVendedorCustomAdapter(getActivity(),R.layout.list_datos_vend_item,(ArrayList) response.body().getData());
                       spinnerVend.setAdapter(spinnerVendedorCustomAdapter);
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
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.txtMensajeConexion))
                        .show();
            }
        });

    }




    public void cargarPedidos(String fecha, final String codLocalidad, final String codAlmacen){
        progressDialog.show();
         Map<String, String> dataConsulta = new HashMap<>();
          dataConsulta.put("usuario",sessionUsuario.getUsuario());
        dataConsulta.put("fechaPedido", fecha);
        dataConsulta.put("codLocalidad", codLocalidad);
        dataConsulta.put("codAlmacen", codAlmacen);
        Call<JsonRespuesta<ConsultaCabecera>> call =   ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getVentaService().consultaPedidos(dataConsulta);
        call.enqueue(new Callback<JsonRespuesta<ConsultaCabecera>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<ConsultaCabecera>> call, Response<JsonRespuesta<ConsultaCabecera>> response) {

                if(response.body()!=null){
                    if(response.code()==401){
                        Toast.makeText(
                                getActivity(), "SE EXPIRÓ EL TIEMPO DE LA TOMA DE PEDIDOS PARA SU USUARIO ,POR FAVOR COMUNIQUESE CON SU COORDINADOR.",
                                Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }else if(response.code()==403){

                        Toast.makeText(getActivity(), "USUARIO INACTIVO, COMUNIQUESE CON CON SU COORDINADOR.",
                                Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();

                    }else {
                        reciclador.setVisibility(View.VISIBLE);
                        txtEmpty.setVisibility(View.GONE);
                        reciclador.setHasFixedSize(true);
                        lManager = new LinearLayoutManager(getActivity());
                        reciclador.setLayoutManager(lManager);
                        JsonRespuesta<ConsultaCabecera> rpta = response.body();
                        CabeceraRecyclerAdapter cabeceraRecyclerAdapter = new CabeceraRecyclerAdapter(rpta.getData(), getActivity(),ConsultaPedidoFragment.this,swiperefresh,txtFecha.getText().toString(),codLocalidad,codAlmacen);
                        reciclador.setAdapter(cabeceraRecyclerAdapter);
                        reciclador.addItemDecoration(new SimpleDividerItemDecorator(getActivity()));
                        txtVentaDiaria.setText(cabeceraRecyclerAdapter.montoTotalDiario().toString());
                        List<Integer> cantidades=cabeceraRecyclerAdapter.cantidadPedidos();
                        txtCantidadIngresados.setText(cantidades.get(0).toString()+"");
                        txtCantidadAprobados.setText(cantidades.get(1).toString()+"");
                        txtCantidadAnulados.setText(cantidades.get(2).toString()+"");
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
            public void onFailure(Call<JsonRespuesta<ConsultaCabecera>> call, Throwable t) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.txtMensajeConexion))
                        .show();
                progressDialog.dismiss();
            }
        });




    }




}
