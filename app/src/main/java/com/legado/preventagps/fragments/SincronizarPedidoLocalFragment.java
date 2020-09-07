package com.legado.preventagps.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.legado.preventagps.R;
import com.legado.preventagps.adapter.vendedor.PedidoLocalRecyclerAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.enums.STATUSSINCRONIZACION;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.PreVenta;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.SimpleDividerItemDecorator;
import com.tuanfadbg.snackalert.SnackAlert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SincronizarPedidoLocalFragment extends Fragment {

    private static final String TAG = "SincronizarPedidoLocalFragment";

    @BindView(R.id.recicladorPedidoLocal)
    RecyclerView recicladorPedidoLocal;
    RecyclerView.LayoutManager lManagerPedido;
    private int year, month, day;
    @BindView(R.id.btnSincronizar)
    FloatingActionButton fabSincronizar;
    public static ProgressDialog progressDialog;
    private SessionUsuario session;
    List<PreVenta> listaPedidoLocales;

    public static SincronizarPedidoLocalFragment newInstance() {
        SincronizarPedidoLocalFragment fragment = new SincronizarPedidoLocalFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sincronizar_pedido_local, container, false);
        ButterKnife.bind(this, rootView);
        session = new SessionUsuario(getContext());
         progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_MyDialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        listaPedidoLocales = ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().listarPedidosLocales(getFechaHoy(), null, session.getUsuario());
        cargarPedidosLocal(listaPedidoLocales);
        fabSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new androidx.appcompat.app.AlertDialog.Builder(getContext())
                        .setTitle("CONFIRMACION")
                        .setMessage("Desea sincronizar los pedidos?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (!listaPedidoLocales.isEmpty()) {
                                    sincronizarPreventa(0);
                                } else {
                                    SnackAlert sa = new SnackAlert(getActivity());
                                    sa.setTitle("ALERTA");
                                    sa.setMessage("NO TIENE PEDIDOS POR SINCRONIZAR");
                                    sa.setType(SnackAlert.WARNING);
                                    sa.show();
                                }
                            }
                        }).setNegativeButton(android.R.string.no, null).show();
            }
        });
        return rootView;
    }


    public void sincronizarPreventa(int posicion) {
        progressDialog.show();
        String status = listaPedidoLocales.get(posicion).getStatus();
        if (status.equals(STATUSSINCRONIZACION.PENDIENTE.getCod())) {
            Call<JsonRespuesta<Integer>> call = ApiRetrofitShort.getInstance(session.getUrlPreventa()).getVentaService().registrarPedidoLocalOffline(listaPedidoLocales.get(posicion));
            call.enqueue(new Callback<JsonRespuesta<Integer>>() {
                @Override
                public void onResponse(Call<JsonRespuesta<Integer>> call, Response<JsonRespuesta<Integer>> response) {
                    if(response.body()!=null){
                        if (response.body().getEstado() == 1) {
                            try {
                                ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
                                ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().updateStatusPedidoLocal(STATUSSINCRONIZACION.SINCRONIZADO.getCod(), listaPedidoLocales.get(posicion).getUid());
                                ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                            } finally {
                                ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                            }
                            Toast.makeText(getContext(), response.body().getMensaje(), Toast.LENGTH_LONG).show();//listaPedidoLocales.get(posicion).getDescCliente() + "Se registró el pedido correctamente "

                            listaPedidoLocales.get(posicion).setStatus(STATUSSINCRONIZACION.SINCRONIZADO.getCod());
                            pedidoLocalRecyclerAdapter.notifyDataSetChanged();
                            cargarPedidosLocal(listaPedidoLocales);
                            progressDialog.dismiss();
                            if (posicion == listaPedidoLocales.size() - 1) {
                                SnackAlert sa = new SnackAlert(getActivity());
                                sa.setTitle("ALERTA");
                                sa.setMessage("FINALIZADO");
                                sa.setType(SnackAlert.SUCCESS);
                                sa.show();
                            } else {
                                sincronizarPreventa(posicion + 1);
                            }

                        } else {
                            progressDialog.dismiss();
                            Snackbar snackbar = Snackbar.make(getView(), response.body().getMensaje(), Snackbar.LENGTH_INDEFINITE);
                            View snackbarView = snackbar.getView();
                            TextView tv = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                            tv.setMaxLines(3);
                            snackbar.setAction("Continuar", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (posicion == listaPedidoLocales.size() - 1) {
                                        SnackAlert sa = new SnackAlert(getActivity());
                                        sa.setTitle("ALERTA");
                                        sa.setMessage("FINALIZADO");
                                        sa.setType(SnackAlert.SUCCESS);
                                        sa.show();
                                    } else {
                                        sincronizarPreventa(posicion + 1);
                                    }
                                }
                            });
                            snackbar.show();
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
                public void onFailure(Call<JsonRespuesta<Integer>> call, Throwable t) {
                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(getView(),  t.getMessage(), Snackbar.LENGTH_INDEFINITE);
                    View snackbarView = snackbar.getView();
                    TextView tv = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setMaxLines(3);
                    snackbar.setAction("Continuar", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (posicion == listaPedidoLocales.size() - 1) {
                                SnackAlert sa = new SnackAlert(getActivity());
                                sa.setTitle("ALERTA");
                                sa.setMessage("FINALIZADO");
                                sa.setType(SnackAlert.SUCCESS);
                                sa.show();
                            } else {
                                sincronizarPreventa(posicion + 1);
                            }
                        }
                    });
                    snackbar.show();
                }
            });
        } else {
            progressDialog.dismiss();

            if (posicion == listaPedidoLocales.size() - 1) {
                SnackAlert sa = new SnackAlert(getActivity());
                sa.setTitle("NO HAY PEDIDOS POR SINCRONIZAR");
                sa.setMessage("FINALIZADO");
                sa.setType(SnackAlert.SUCCESS);
                sa.show();
            } else {
                sincronizarPreventa(posicion + 1);
            }

        }
    }


    public Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(getActivity(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int yyyy, int mm, int dd) {

                    List<PreVenta> lista = ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().listarPedidosLocales(getFechaHoy(), null, session.getUsuario());
                    cargarPedidosLocal(lista);
                }
            }, year, month, day);
        }
        return null;
    }

    public String getFechaHoy() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
       return getFormatoFecha(year, month, day);
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

    PedidoLocalRecyclerAdapter pedidoLocalRecyclerAdapter;

    public void cargarPedidosLocal(List<PreVenta> pedidosLocales) {
        recicladorPedidoLocal.setVisibility(View.VISIBLE);
        recicladorPedidoLocal.setHasFixedSize(true);
        lManagerPedido = new LinearLayoutManager(getContext());
        recicladorPedidoLocal.setLayoutManager(lManagerPedido);
        pedidoLocalRecyclerAdapter = new PedidoLocalRecyclerAdapter(pedidosLocales, getContext(), getActivity());
        recicladorPedidoLocal.setAdapter(pedidoLocalRecyclerAdapter);
        recicladorPedidoLocal.addItemDecoration(new SimpleDividerItemDecorator(getActivity()));
    }

}


