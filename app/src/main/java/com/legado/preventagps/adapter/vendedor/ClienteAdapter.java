package com.legado.preventagps.adapter.vendedor;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.tonywills.loadingbutton.LoadingButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.legado.preventagps.R;
import com.legado.preventagps.activities.vendedor.ClienteActivity;
import com.legado.preventagps.activities.vendedor.ContenedorActivity;
import com.legado.preventagps.activities.vendedor.ContenedorAltaActivity;
import com.legado.preventagps.activities.vendedor.EditarLocalClienteActivity;
import com.legado.preventagps.api.ApiRetrofitLong;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.dialogs.MyDialogClienteMapa;
import com.legado.preventagps.dialogs.MyDialogInfo;
import com.legado.preventagps.dialogs.MyDialogProgress;
import com.legado.preventagps.enums.CLIENTEENUM;
import com.legado.preventagps.enums.STATUSSINCRONIZACION;
import com.legado.preventagps.fragments.DialogFullscreenFragment;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Cliente;
import com.legado.preventagps.modelo.ClienteAlta;
import com.legado.preventagps.modelo.LocalCliente;
import com.legado.preventagps.modelo.NoPedido;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.UtilAndroid;
import com.legado.preventagps.util.UtilRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> implements MyDialogClienteMapa.EditDialogClientListener, MyDialogInfo.DialogInfoListener {
    public List<Cliente> items;
    SessionUsuario sessionUsuario;
    Context c;
    MyDialogClienteMapa.EditDialogClientListener editDialogClientListener;
    MyDialogInfo.DialogInfoListener dialogInfoListener;

    public ClienteAdapter(List<Cliente> items, Context context) {

        this.items = items;
        editDialogClientListener = this;
        dialogInfoListener = this;
        this.c = context;
         sessionUsuario = new SessionUsuario(c);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateList(List<Cliente> clientes) {

            items = new ArrayList<>();
            items.addAll(clientes);
            notifyDataSetChanged();


    }

    @Override
    public ClienteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cliente_card, viewGroup, false);
        ClienteViewHolder ch = new ClienteViewHolder(v, items, editDialogClientListener, dialogInfoListener);
        return ch;
    }

    @Override
    public void onBindViewHolder(ClienteViewHolder viewHolder, final int i) {
        viewHolder.nombre.setText(items.get(i).getDescCliente());
        viewHolder.codCliente.setText(items.get(i).getCodCliente());
        viewHolder.direccion.setText(items.get(i).getDireccion());
        viewHolder.btnPreventa.setEnabled(false);
        viewHolder.btnNoPedido.setEnabled(false);

        viewHolder.newTelefono.setText(items.get(i).getNewTelefono());


        if (items.get(i).getEstadoGeoSis().equals("0") &sessionUsuario.getPaqueteUsuario().getIgnoreGps().equals(0)) {
             viewHolder.llGeolocalizar.setVisibility(View.VISIBLE);
            viewHolder.llVerGeo.setVisibility(View.GONE);
        } else {
            //viewHolder.secuencia.setText("GEOLOCALIZADO");
            viewHolder.llGeolocalizar.setVisibility(View.GONE);
            viewHolder.llVerGeo.setVisibility(View.GONE);
            viewHolder.btnPreventa.setEnabled(true);
            viewHolder.btnNoPedido.setEnabled(true);
        }

        if (items.get(i).getStatus().equals("P")) {
             viewHolder.codCliente.setTextColor(c.getResources().getColor(R.color.white));
            viewHolder.nombre.setTextColor(c.getResources().getColor(R.color.main_color_grey_300));
            viewHolder.direccion.setTextColor(c.getResources().getColor(R.color.white));
            viewHolder.codCliente.setTextColor(c.getResources().getColor(R.color.white));
            viewHolder.itemView.setBackgroundColor(c.getResources().getColor(R.color.material_blue_400));
        } else    if (items.get(i).getEstadoJornada().equals("N")) {
            //viewHolder.secuencia.setTextColor(c.getResources().getColor(R.color.white));
            viewHolder.codCliente.setTextColor(c.getResources().getColor(R.color.white));
            viewHolder.nombre.setTextColor(c.getResources().getColor(R.color.main_color_grey_300));
            viewHolder.direccion.setTextColor(c.getResources().getColor(R.color.white));
            viewHolder.codCliente.setTextColor(c.getResources().getColor(R.color.white));
            viewHolder.itemView.setBackgroundColor(c.getResources().getColor(R.color.material_red_A200));
        } else if (items.get(i).getEstadoJornada().equals("P")) {
            //viewHolder.secuencia.setTextColor(c.getResources().getColor(R.color.white));
            viewHolder.codCliente.setTextColor(c.getResources().getColor(R.color.white));
            viewHolder.nombre.setTextColor(c.getResources().getColor(R.color.main_color_grey_300));
            viewHolder.direccion.setTextColor(c.getResources().getColor(R.color.white));
            viewHolder.codCliente.setTextColor(c.getResources().getColor(R.color.white));
            viewHolder.itemView.setBackgroundColor(c.getResources().getColor(R.color.material_green_A700));
        } else {
            viewHolder.codCliente.setTextColor(c.getResources().getColor(R.color.main_color_grey_900));
            viewHolder.nombre.setTextColor(c.getResources().getColor(R.color.main_color_grey_900));
            viewHolder.direccion.setTextColor(c.getResources().getColor(R.color.main_color_grey_900));
            viewHolder.codCliente.setTextColor(c.getResources().getColor(R.color.main_color_grey_900));
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
         }

    }

    @Override
    public void updateRecycler(List<Cliente> lista) {
        updateList(lista);
    }

    @Override
    public void updateRecyclerSuccess(List<Cliente> list) {
        updateList(list);
    }

    public static class ClienteViewHolder extends RecyclerView.ViewHolder implements MyDialogClienteMapa.EditDialogClientHolderListener, ClienteActivity.ContenedorListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
    {
        private static GoogleApiClient mGoogleApiClient;
        private LatLng latLng;
        private boolean stateMap;
        private Location location;
        private LocationRequest mLocationRequest;
        private static final int REQUEST_CHECK_SETTINGS = 0x1;
        private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
        private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";

        public TextView nombre;
        public TextView codCliente;
      //  public TextView secuencia;
        public TextView direccion;
        public TextView newTelefono;

        public LoadingButton btnPreventa;
        public LoadingButton btnNoPedido;
        public LoadingButton btnGeolocalizar;
        public LoadingButton btnVerGeo;
        public LinearLayout llVerGeo;
        public LinearLayout llGeolocalizar;
        public List<Cliente> lista;
        public ImageView img1;
        public ImageView imgGeo;
        public ImageView imgVerGeo;
        SessionUsuario sessionUsuario;
        MyDialogProgress dialogRegistrarMotivo;
        MyDialogClienteMapa.EditDialogClientListener editDialogClientListener;
        MyDialogClienteMapa.EditDialogClientHolderListener editDialogClientHolderListener;
        ClienteActivity.ContenedorListener contenedorListener;
        MyDialogInfo.DialogInfoListener dialogInfoListener;
        AppCompatActivity activity = (AppCompatActivity) itemView.getContext();



        public ClienteViewHolder(final View v, final List<Cliente> lista, final MyDialogClienteMapa.EditDialogClientListener editDialogClientListener, final MyDialogInfo.DialogInfoListener dialogInfoListener) {
            super(v);
             this.lista = lista;
            this.editDialogClientListener = editDialogClientListener;
            editDialogClientHolderListener = this;
            contenedorListener = this;
            this.dialogInfoListener = dialogInfoListener;
            sessionUsuario = new SessionUsuario(v.getContext());
            codCliente = (TextView) v.findViewById(R.id.txtCodCliente);
            direccion=(TextView) v.findViewById(R.id.txtDireccion);
            nombre = (TextView) v.findViewById(R.id.txtDescripcion);
            newTelefono=(TextView) v.findViewById(R.id.txtTelefono);
             btnPreventa = (LoadingButton) v.findViewById(R.id.btnPreventa);
            btnNoPedido = (LoadingButton) v.findViewById(R.id.btnNoPedido);
            btnGeolocalizar = (LoadingButton) v.findViewById(R.id.btnGeolocalizar);
            btnVerGeo = (LoadingButton) v.findViewById(R.id.btnVerGeo);
            llGeolocalizar = (LinearLayout) v.findViewById(R.id.llGeolocalizar);
            llVerGeo = (LinearLayout) v.findViewById(R.id.llVerGeo);
            btnPreventa.getTextView().setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10.f);
            img1 = (ImageView) v.findViewById(R.id.img1);
            imgGeo = (ImageView) v.findViewById(R.id.imgGeo);
            imgVerGeo = (ImageView) v.findViewById(R.id.imgVerGeo);

            btnPreventa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    int position = getAdapterPosition();
                    if(lista.get(position).getIsAuditado().equals(new Integer(0)) && sessionUsuario.getIsOnlyOnline()){

                        //Dialog dialog= showDialogGoEditar(lista.get(position));
                        Intent intent = new Intent(v.getContext(), ContenedorAltaActivity.class);
                        System.out.println(v.getContext().getClass().getName());
                        sessionUsuario.guardarDeudaVencida(null);
                        sessionUsuario.guardarDeudaNoVencida(null);
                        intent.putExtra("codCliente", lista.get(position).getCodCliente());
                        intent.putExtra("codLocal", lista.get(position).getCodLocal());
                        //sessionUsuario.guardarClienteAlta(null);
                        v.getContext().startActivity(intent);

//                        ((AppCompatButton) dialog.findViewById(R.id.bt_continuar)).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                                img1.setVisibility(View.GONE);
//
//                                        int position = getAdapterPosition();
//                                        Intent intent = new Intent(v.getContext(), ContenedorActivity.class);
//                                        System.out.println(v.getContext().getClass().getName());
//                                        if (v.getContext().getClass().getName().equals("com.legado.preventagps.activities.vendedor.ClienteActivity")) {
//                                            ((ClienteActivity) v.getContext()).setContenedorListener(contenedorListener);
//                                        }
//                                        ContenedorActivity.setDialogInfoListener(dialogInfoListener);
//                                        sessionUsuario.guardarDeudaVencida(null);
//                                        sessionUsuario.guardarDeudaNoVencida(null);
//                                        intent.putExtra("codCliente", lista.get(position).getCodCliente());
//                                        intent.putExtra("descCliente", lista.get(position).getDescCliente());
//                                        intent.putExtra("dirCliente", lista.get(position).getDireccion());
//                                        intent.putExtra("codRuta", lista.get(position).getCodRuta());
//                                        intent.putExtra("codLocal", lista.get(position).getCodLocal());
//                                        intent.putExtra("codLista", lista.get(position).getCodLista());
//                                        intent.putExtra("codEmpresa", lista.get(position).getCodEmpresa());
//                                        intent.putExtra("nrosecuencia", lista.get(position).getSecuencia());
//                                        intent.putExtra("statusCliente", lista.get(position).getStatus());
//                                        intent.putExtra("onlycobranza", false);
//
//                                        if (!sessionUsuario.getIsOnlyOnline()) {
//                                            //OFFLINE
//                                            intent.putExtra("fechaCumple", lista.get(position).getFechaCumple());
//                                            intent.putExtra("tipoNegocio", lista.get(position).getTipoNegocio());
//                                            intent.putExtra("descListaPrecios", lista.get(position).getCodLista() + "-" + lista.get(position).getDescListaPrecios());
//                                            intent.putExtra("limiteCredito", lista.get(position).getLimiteCredito().toString());
//                                            intent.putExtra("deudaVencida", lista.get(position).getDeudaVencida().toString());
//                                            intent.putExtra("deudaNoVencida", lista.get(position).getDeudaNoVencida().toString());
//                                            intent.putExtra("antiguedad", lista.get(position).getAntiguedad());
//                                            intent.putExtra("velocidadPago", lista.get(position).getVelocidadPago().toString());
//                                            intent.putExtra("cantidadPedidos", lista.get(position).getCantidadPedidos().toString());
//
//                                        }
//                                        v.getContext().startActivity(intent);
//                                         img1.setVisibility(View.VISIBLE);
//                                        btnPreventa.setLoading(false);
//
//                            }
//                        });


                    }else {
                        img1.setVisibility(View.GONE);
                        btnPreventa.setLoading(true);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                int position = getAdapterPosition();
                                Intent intent = new Intent(v.getContext(), ContenedorActivity.class);
                                System.out.println(v.getContext().getClass().getName());
                                if (v.getContext().getClass().getName().equals("com.legado.preventagps.activities.vendedor.ClienteActivity")) {
                                    ((ClienteActivity) v.getContext()).setContenedorListener(contenedorListener);
                                }
                                ContenedorActivity.setDialogInfoListener(dialogInfoListener);
                                sessionUsuario.guardarDeudaVencida(null);
                                sessionUsuario.guardarDeudaNoVencida(null);
                                intent.putExtra("codCliente", lista.get(position).getCodCliente());
                                intent.putExtra("descCliente", lista.get(position).getDescCliente());
                                intent.putExtra("dirCliente", lista.get(position).getDireccion());
                                intent.putExtra("codRuta", lista.get(position).getCodRuta());
                                intent.putExtra("codLocal", lista.get(position).getCodLocal());
                                intent.putExtra("codLista", lista.get(position).getCodLista());
                                intent.putExtra("codEmpresa", lista.get(position).getCodEmpresa());
                                intent.putExtra("nrosecuencia", lista.get(position).getSecuencia());
                                intent.putExtra("statusCliente", lista.get(position).getStatus());
                                intent.putExtra("onlycobranza", false);

                                if (!sessionUsuario.getIsOnlyOnline()) {
                                    //OFFLINE
                                    intent.putExtra("fechaCumple", lista.get(position).getFechaCumple());
                                    intent.putExtra("tipoNegocio", lista.get(position).getTipoNegocio());
                                    intent.putExtra("descListaPrecios", lista.get(position).getCodLista() + "-" + lista.get(position).getDescListaPrecios());
                                    intent.putExtra("limiteCredito", lista.get(position).getLimiteCredito().toString());
                                    intent.putExtra("deudaVencida", lista.get(position).getDeudaVencida().toString());
                                    intent.putExtra("deudaNoVencida", lista.get(position).getDeudaNoVencida().toString());
                                    intent.putExtra("antiguedad", lista.get(position).getAntiguedad());
                                    intent.putExtra("velocidadPago", lista.get(position).getVelocidadPago().toString());
                                    intent.putExtra("cantidadPedidos", lista.get(position).getCantidadPedidos().toString());

                                }


                                btnPreventa.setLoading(false);
                                //img1.setVisibility(View.VISIBLE);
                                v.getContext().startActivity(intent);
                            }
                        }, 200);
                    }
                }
            });

            btnNoPedido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));//Registrar el receptor de difusión para comprobar el estado del GPS.

                    initGoogleAPIClient();
                    checkPermissions();
                   buildGoogleApiClient();
                    final int position = getAdapterPosition();
                    final Cliente cliente = lista.get(position);
                    final NoPedido noPedido = new NoPedido();
                    noPedido.setCodCliente(cliente.getCodCliente());
                    noPedido.setDescCliente(cliente.getDescCliente());
                    noPedido.setCodRuta(cliente.getCodRuta());
                    noPedido.setCodEmpresa(cliente.getCodEmpresa());
                    noPedido.setDireccion(cliente.getDireccion());
                    noPedido.setCodLocal(cliente.getCodLocal());
                    noPedido.setUsuario(sessionUsuario.getUsuario());
                    AlertDialog.Builder b = new AlertDialog.Builder(view.getContext());
                    b.setTitle("MOTIVO NO PEDIDO");
                    final String[] types = UtilRepository.listarMotivoNoPedido();
                    b.setItems(types, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            switch (which) {
                                case 0:
                                    noPedido.setDescMotivo(types[0]);
                                    break;
                                case 1:
                                    noPedido.setDescMotivo(types[1]);
                                    break;
                                case 2:
                                    noPedido.setDescMotivo(types[2]);
                                    break;
                                case 3:
                                    noPedido.setDescMotivo(types[3]);
                                    break;
                                case 4:
                                    noPedido.setDescMotivo(types[4]);
                                    break;
                                case 5:
                                    noPedido.setDescMotivo(types[5]);
                                    break;
                                case 6:
                                    noPedido.setDescMotivo(types[6]);
                                    break;
                                case 7:
                                    noPedido.setDescMotivo(types[7]);
                                    break;
                                case 8:
                                    noPedido.setDescMotivo(types[8]);
                                    break;
                                case 9:
                                    noPedido.setDescMotivo(types[9]);
                                    break;
                                case 10:
                                    noPedido.setDescMotivo(types[10]);
                                    break;
                            }

                            Gson gson = new Gson();
                            String lcJson = gson.toJson(noPedido);
                            Log.d("assas", lcJson);

                            if(latLng!=null){
                                showDialog(noPedido, v, position, cliente);
                            }else{

                                Snackbar.make(v, "DEBE ACEPTAR PERMITIR SU UBICACION  ", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("REINTENTAR", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                showSettingDialog();
                                            }
                                        }).show();
                            }



                        }

                    });
                    b.show();
                }
            });

            btnGeolocalizar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    btnGeolocalizar.setEnabled(false);
                    imgGeo.setEnabled(false);
                    imgGeo.setVisibility(View.GONE);
                    btnGeolocalizar.setLoading(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            final int position = getAdapterPosition();
                            final Cliente cliente = lista.get(position);
                            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                            MyDialogClienteMapa newFragment = MyDialogClienteMapa.newInstance();
                            Bundle args = new Bundle();
                            newFragment.setEditDialogClientListener(editDialogClientListener);
                            newFragment.setEditDialogClientHolderListener(editDialogClientHolderListener);
                            args.putString(CLIENTEENUM.CODCLIENTE.getClave(), cliente.getCodCliente());
                            args.putString("descCliente", cliente.getDescCliente());
                            args.putString("codLocal", cliente.getCodLocal());
                            args.putString("coordenadas", cliente.getCoordenadas());
                            newFragment.setArguments(args);
                            newFragment.show(ft, "find");
                        }
                    }, 500);
                }
            });





        }

        private Runnable sendUpdatesToUI = new Runnable() {
            public void run() {
                showSettingDialog();
            }
        };
        private BroadcastReceiver gpsLocationReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                //Si la acción es la ubicación
                if (intent.getAction().matches(BROADCAST_ACTION)) {
                    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                    //Compruebe si el GPS está encendido o apagado
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Log.e("About GPS", "GPS is Enabled in your device");
                    } else {
                        //Si el GPS está apagado, muestre el diálogo de ubicación
                        new Handler().postDelayed(sendUpdatesToUI, 10);
                        Log.e("About GPS", "GPS is Disabled in your device");
                        //dismiss();
                    }

                }
            }
        };

private void initGoogleAPIClient() {
     if(activity!=null){
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
}
        private void registrarUbicacionNoPedido(final NoPedido noPedido, final View v, final Integer position, final Cliente cliente) {
            noPedido.setCoordenadasNoPedido(latLng.latitude + "," + latLng.longitude);
             registrarNoPedid(noPedido, v, position, cliente);
           Log.wtf("COORDENADA REGISTRO", noPedido.getCoordenadasNoPedido());
        }

        private void registrarNoPedidOffline(NoPedido noPedido, View v, Integer position, Cliente cliente) {

            try {
                ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();


                ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().insertarNoPedidoLocal(noPedido);
                ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().updateClienteEstadoJornada("N",cliente.getCodCliente(),cliente.getCodLocal());
                ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
            } finally {
                ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
            }

            dialogRegistrarMotivo.dismiss();

            SweetAlertDialog sa=  new SweetAlertDialog(activity);
            sa.setCancelable(false);
            sa.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                     Intent intent = new Intent(v.getContext(), ClienteActivity.class);
                    v.getContext().startActivity(intent);
                }
            });
            sa.setTitleText("Se registró en modo Offline, no olvides sincronizar!")
                    .show();

        }

        public void registrarNoPedid(final NoPedido noPedido, final View v, final Integer position, final Cliente cliente) {

            Call<JsonRespuesta<Integer>> call = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getVentaService().registrarNoPedido(noPedido);
            call.enqueue(new Callback<JsonRespuesta<Integer>>() {
                @Override
                public void onResponse(Call<JsonRespuesta<Integer>> call, Response<JsonRespuesta<Integer>> response) {

                    if(response.body()!=null){
                        if (response.body().getEstado() == -1) {
                            Snackbar.make(v, response.body().getMensaje(), Snackbar.LENGTH_INDEFINITE).setAction("Cerrar", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            }).show();
                        } else {
                            try {
                                ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();

                                ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().updateClienteEstadoJornada("N",cliente.getCodCliente(),cliente.getCodLocal());

                                ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                            } finally {
                                ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                            }
//
                            dialogRegistrarMotivo.dismiss();
                            Intent intent = new Intent(v.getContext(), ClienteActivity.class);
                            v.getContext().startActivity(intent);
                        }
                    }else{

                        modoOffline(noPedido,v,position,cliente);
                    }

                }

                @Override
                public void onFailure(Call<JsonRespuesta<Integer>> call, Throwable t) {
                    modoOffline(noPedido,v,position,cliente);
                }
            });
        }

        public void modoOffline(NoPedido noPedido,View v,Integer position,Cliente cliente){
            Gson gson = new Gson();
            String lcJson = gson.toJson(noPedido);
            Log.wtf("PEDIDO OFFLINE", lcJson);
            noPedido.setStatusNoPedido(STATUSSINCRONIZACION.PENDIENTE.getCod());
            registrarNoPedidOffline(noPedido, v, position, cliente);
        }

        public void showDialog(final NoPedido noPedido, final View v, final Integer position, final Cliente cliente) {
            new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                    .setTitle("CONFIRMACION")
                    .setMessage("Desea registrar el motivo?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            FragmentTransaction ft = ((AppCompatActivity) v.getContext()).getSupportFragmentManager().beginTransaction();
                            dialogRegistrarMotivo = new MyDialogProgress();
                            dialogRegistrarMotivo.show(ft, "dialog");
                            registrarUbicacionNoPedido(noPedido,v,position,cliente);
                        }
                    }).setNegativeButton(android.R.string.no, null).show();
        }
        private boolean isTelefonoValido(String nroTelefono) {
            return nroTelefono.length() >= 6 &&nroTelefono.length() <= 9;
        }

        private Dialog showDialogTelefono(Cliente c) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_telefono);
            dialog.setCancelable(true);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;



             final TextView titleTelefono = (TextView) dialog.findViewById(R.id.txtDialogTitleTelefono);
            titleTelefono.setText(c.getDescCliente());
//            final AppCompatRatingBar rating_bar = (AppCompatRatingBar) dialog.findViewById(R.id.rating_bar);
            ((ImageButton) dialog.findViewById(R.id.bt_cerrar)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
//
            final EditText editTextTelefono = (EditText) dialog.findViewById(R.id.etNroTelefono);
            ((AppCompatButton) dialog.findViewById(R.id.bt_submit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nroTelefono = editTextTelefono.getText().toString().trim();
                    editTextTelefono.setError(null);
                     boolean cancel = false;
                    View focusView = null;
                    if (TextUtils.isEmpty(nroTelefono)) {
                        editTextTelefono.setError("Ingrese Teléfono");
                        focusView = editTextTelefono;
                        cancel = true;
                    }

                     if (!isTelefonoValido(nroTelefono)) {
                            editTextTelefono.setError("Número incorrecto");
                            focusView = editTextTelefono;
                            cancel = true;

                    }


                    if (cancel) {
                        focusView.requestFocus();
                    } else {

                        new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("CONFIRMACIÓN")
                                .setContentText("Usted registrará el  numero telefónico  " + nroTelefono + " , para el cliente " + c.getDescCliente() + " ,una vez registrado no se podrá editar. Está seguro de registrarlo?")
                                .setConfirmText("SI")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        ClienteAlta clienteAlta=new ClienteAlta();
                                        clienteAlta.setCodCliente(c.getCodCliente());
                                        clienteAlta.setCelular(nroTelefono);
                                        clienteAlta.setLocales(new ArrayList<LocalCliente>());
                                        clienteAlta.setUsuarioRegistra(sessionUsuario.getUsuario());
                                        registrarNumeroTelefónico(clienteAlta,c,getAdapterPosition());
                                        sweetAlertDialog.dismissWithAnimation();
                                        dialog.dismiss();

                                    }
                                })
                                .setCancelText("NO")
                                .show();
                    }
                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);
            return dialog;
        }
        private Dialog showDialogGoEditar(Cliente c) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_goeditar);
            dialog.setCancelable(true);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


//            final AppCompatRatingBar rating_bar = (AppCompatRatingBar) dialog.findViewById(R.id.rating_bar);
            ((ImageButton) dialog.findViewById(R.id.bt_cerrar)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


            ((AppCompatButton) dialog.findViewById(R.id.btnActualizarCliente)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ContenedorAltaActivity.class);
                    System.out.println(v.getContext().getClass().getName());
                    sessionUsuario.guardarDeudaVencida(null);
                    sessionUsuario.guardarDeudaNoVencida(null);
                    intent.putExtra("codCliente", c.getCodCliente());
                    intent.putExtra("codLocal", c.getCodLocal());
                    //sessionUsuario.guardarClienteAlta(null);
                    v.getContext().startActivity(intent);
                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);
            return dialog;
        }


        private void registrarNumeroTelefónico(ClienteAlta clienteAlta,Cliente c,Integer position) {
            clienteAlta.setOperacion(5);
            Call<JsonRespuesta<Integer>> call = ApiRetrofitLong.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().registrarCliente(clienteAlta);
            call.enqueue(new Callback<JsonRespuesta<Integer>>() {
                @Override
                public void onResponse(Call<JsonRespuesta<Integer>> call, Response<JsonRespuesta<Integer>> response) {
                    if(response.body().getEstado()==-1){
                        Toast.makeText(activity,response.body().getMensaje(),Toast.LENGTH_LONG).show();


                    }else{
                        Toast.makeText(activity,response.body().getMensaje(),Toast.LENGTH_LONG).show();

                        try {
                            ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();

                            ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().updateAfterRegistrarTelefono(clienteAlta.getCelular(),clienteAlta.getCodCliente());

                            ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                        } finally {
                            ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                        }

                        img1.setVisibility(View.GONE);

                        Intent intent = new Intent(activity.getApplicationContext(), ContenedorActivity.class);
                        System.out.println(activity.getApplicationContext().getClass().getName());
                        if (activity.getApplicationContext().getClass().getName().equals("com.legado.preventagps.activities.vendedor.ClienteActivity")) {
                            ((ClienteActivity) activity.getApplicationContext()).setContenedorListener(contenedorListener);
                        }
                        ContenedorActivity.setDialogInfoListener(dialogInfoListener);
                        sessionUsuario.guardarDeudaVencida(null);
                        sessionUsuario.guardarDeudaNoVencida(null);
                        intent.putExtra("codCliente", c.getCodCliente());
                        intent.putExtra("descCliente", c.getDescCliente());
                        intent.putExtra("dirCliente", c.getDireccion());
                        intent.putExtra("codRuta", c.getCodRuta());
                        intent.putExtra("codLocal", c.getCodLocal());
                        intent.putExtra("codLista",c.getCodLista());
                        intent.putExtra("codEmpresa", c.getCodEmpresa());
                        intent.putExtra("nrosecuencia", c.getSecuencia());
                        intent.putExtra("statusCliente", c.getStatus());
                        intent.putExtra("onlycobranza", false);

                        if (!sessionUsuario.getIsOnlyOnline()) {
                            //OFFLINE
                            intent.putExtra("fechaCumple", c.getFechaCumple());
                            intent.putExtra("tipoNegocio", c.getTipoNegocio());
                            intent.putExtra("descListaPrecios", c.getCodLista() + "-" + c.getDescListaPrecios());
                            intent.putExtra("limiteCredito", c.getLimiteCredito().toString());
                            intent.putExtra("deudaVencida", c.getDeudaVencida().toString());
                            intent.putExtra("deudaNoVencida", c.getDeudaNoVencida().toString());
                            intent.putExtra("antiguedad", c.getAntiguedad());
                            intent.putExtra("velocidadPago", c.getVelocidadPago().toString());
                            intent.putExtra("cantidadPedidos", c.getCantidadPedidos().toString());

                        }

                        lista.get(position).setNewTelefono(clienteAlta.getCelular());
                        editDialogClientListener.updateRecycler(ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().listarClientes(UtilAndroid.fechaDevice("yyyy-MM-dd")));

                        activity.getApplicationContext().startActivity(intent);
                       img1.setVisibility(View.VISIBLE);
//                        btnPreventa.setLoading(false);





                    }
                }

                @Override
                public void onFailure(Call<JsonRespuesta<Integer>> call, Throwable t) {

                    new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(activity.getString(R.string.txtMensajeConexion))
                            .show();
                }
            });
        }



        @Override
        public void showButtonGeolocalizar() {
            btnGeolocalizar.setLoading(false);
            imgGeo.setVisibility(View.VISIBLE);
        }

        @Override
        public void showButtonVerGeo() {
            btnVerGeo.setLoading(false);
            imgVerGeo.setVisibility(View.VISIBLE);

        }

        @Override
        public void showButtonPedido() {
            btnPreventa.setLoading(false);
            img1.setVisibility(View.VISIBLE);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        protected synchronized void buildGoogleApiClient() {
            Log.e("TAG", "buildGoogleApiClient");
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.reconnect();
            } else {
                mGoogleApiClient = new GoogleApiClient.Builder(activity)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
                mGoogleApiClient.connect();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.e("TAG", "onConnected");
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(9000);
            mLocationRequest.setFastestInterval(9000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            try {
                if (ContextCompat.checkSelfPermission(Objects.requireNonNull(activity), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.e("TAG", "onConnectionSuspended");
            mGoogleApiClient.connect(i);
        }

        private void checkPermissions() {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(activity,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)
                    requestLocationPermission();
                else
                    showSettingDialog();
            } else
                showSettingDialog();
        }

        private void requestLocationPermission() {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
            }
        }

        private void showSettingDialog() {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            Log.e("TAG", "SUCCESS");
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.e("TAG", "RESOLUTION_REQUIRED");
                            try {
                                status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Log.e("TAG", "GPS NO DISPONIBLE");
                            break;
                    }
                }
            });
        }
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e("TAG", "onLocationChanged");
            this.location = location;

            stateMap=true;//en el caso de que se ejecute el Handler y entre a onLocationChanged va volver verdadero stateMap y no volvera a pedir permisos de GPS
             latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Log.e("TAG", latLng.latitude+"-"+latLng.longitude);


        }

    }


}
