package com.legado.preventagps.activities.vendedor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.legado.preventagps.R;
import com.legado.preventagps.adapter.vendedor.ClienteAdapter;
import com.legado.preventagps.api.ApiRetrofitLong;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.dialogs.MyDialogCalculando;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Articulo;
import com.legado.preventagps.modelo.Cliente;
import com.legado.preventagps.modelo.PaqueteUsuario;
import com.legado.preventagps.util.PermissionUtils;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.SimpleDividerItemDecorator;
import com.legado.preventagps.util.UtilAndroid;
import com.pd.chocobar.ChocoBar;
import com.tuanfadbg.snackalert.SnackAlert;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClienteActivity extends BaseActivity {
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private RecyclerView recycler;
    private ClienteAdapter adapter;
    private RecyclerView.LayoutManager lManager;
     private boolean isNetworkLocation, isGPSLocation;
    private ContenedorListener contenedorListener;
    private ProgressDialog pd;

    TextView txtCantidadClientes;

    public void setContenedorListener(ContenedorListener contenedorListener) {
        this.contenedorListener = contenedorListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);
        ButterKnife.bind(this);
        txtCantidadClientes=(TextView) findViewById(R.id.txtCantCliente);
        sessionUsuario = new SessionUsuario(this);
        setupToolbar();
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        //mientras exista data local con la fecha del sistema, no consulta al servidor,solo local
        if (!ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().listarClientes(UtilAndroid.fechaDevice("yyyy-MM-dd")).isEmpty() &&getIntent().getIntExtra("newRegistro",0)==0) {
            recycler = (RecyclerView) findViewById(R.id.reciclador);
            recycler.setHasFixedSize(true);
            lManager = new LinearLayoutManager(getApplicationContext());
            recycler.setLayoutManager(lManager);
            adapter = new ClienteAdapter(ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().listarClientes(UtilAndroid.fechaDevice("yyyy-MM-dd")), getApplicationContext());
            recycler.setAdapter(adapter);
            recycler.addItemDecoration(new SimpleDividerItemDecorator(ClienteActivity.this));
            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler.getContext(), R.anim.layout_full_down);
            recycler.setLayoutAnimation(controller);
            recycler.getAdapter().notifyDataSetChanged();
            recycler.scheduleLayoutAnimation();
            txtCantidadClientes.setText(adapter.getItemCount()+"");
        } else {
            limpiarTablaClientes();
            limpiarDatosUsuario();
            limpiarCondiciones();
            limpiarArticulos();
            limpiarFocusSugeridos();
            cargarClientesOffline();
        }
        CardView menu1 = (CardView) findViewById(R.id.menu1);
        menu1.setCardBackgroundColor(Color.parseColor("#FF9800"));

    }




    private void cargarClientes(String codigo,String texto) {
        pd.setMessage(texto);
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario", sessionUsuario.getUsuario());
        Call<JsonRespuesta> loginCall = ApiRetrofitLong.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().clientesByDiaV2(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if(response.body()!=null) {
                    if (response.body().getEstado() == 1) {
                        ArrayList lista = (ArrayList) response.body().getData();
                        ArrayList listaDataCliente = (ArrayList) lista.get(1);
                        List<Cliente> listaCliente = new ArrayList<>();
                        for (int i = 0; i < listaDataCliente.size(); i++) {
                            Gson gson = new Gson();
                            JsonObject jsonObject = gson.toJsonTree(listaDataCliente.get(i)).getAsJsonObject();
                            Cliente lc = gson.fromJson(jsonObject, Cliente.class);
                            listaCliente.add(lc);
                        }
                        for (int i = 0; i < listaCliente.size(); i++) {
                            listaCliente.get(i).setSecuencia(i);
                        }

                        try {
                            ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
                            for (int i = 0; i < listaCliente.size(); i++) {
                                Cliente cl = listaCliente.get(i);
                                ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().insertarCliente((Cliente) cl);
                            }
                            ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                        } finally {
                            ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                        }

                        recycler = (RecyclerView) findViewById(R.id.reciclador);
                        recycler.setHasFixedSize(true);
                        lManager = new LinearLayoutManager(getApplicationContext());
                        recycler.setLayoutManager(lManager);

                        if (codigo.equals("X")) {
                            sessionUsuario.guardarIsOnlyOnline(true);
                        }
                        adapter = new ClienteAdapter(listaCliente, getApplicationContext());
                        recycler.setAdapter(adapter);
                        recycler.addItemDecoration(new SimpleDividerItemDecorator(ClienteActivity.this));
                        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler.getContext(), R.anim.layout_full_down);
                        recycler.setLayoutAnimation(controller);
                        recycler.getAdapter().notifyDataSetChanged();
                        recycler.scheduleLayoutAnimation();
                        txtCantidadClientes.setText(adapter.getItemCount() + "");
                        pd.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), response.body().getMensaje(), Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }
                }else{
                    pd.dismiss();
                    new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.txtMensajeServidorCaido))
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                new SweetAlertDialog(ClienteActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.txtMensajeConexion))
                        .show();
                pd.dismiss();
             }
        });
    }


    private void initGoogleMapLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                super.onLocationResult(result);
                mCurrentLocation = result.getLocations().get(0);
                if (mCurrentLocation != null) {
                    Log.e("Location(Lat)==", "" + mCurrentLocation.getLatitude());
                    Log.e("Location(Long)==", "" + mCurrentLocation.getLongitude());
                }
                sessionUsuario.guardarUbicacion(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }
            @Override
            public void onLocationAvailability(LocationAvailability availability) {
                //boolean isLocation = availability.isLocationAvailable();
            }
        };
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        //To get location information only once here
        mLocationRequest.setNumUpdates(3);

        LocationManager mListener = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (mListener != null) {
            isGPSLocation = mListener.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkLocation = mListener.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        if (isGPSLocation) {
            //Accuracy is a top priority regardless of battery consumption
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        } else if (isNetworkLocation) {
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        } else {
            //Device location is not set
            PermissionUtils.LocationSettingDialog a = PermissionUtils.LocationSettingDialog.newInstance();
            a.show(this.getSupportFragmentManager(), "Setting");
            a.setCancelable(false);
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        /**
         * Stores the type of location service the client wants to use. Also used for positioning.
         */
        LocationSettingsRequest mLocationSettingsRequest = builder.build();

        Task<LocationSettingsResponse> locationResponse = mSettingsClient.checkLocationSettings(mLocationSettingsRequest);
        locationResponse.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.e("Response", "Successful acquisition of location information!!");
                //
                if (ActivityCompat.checkSelfPermission(ClienteActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            }
        });
        //When the location information is not set and acquired, callback
        locationResponse.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.e("onFailure", "Location environment check");
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Check location setting";
                        Log.e("onFailure", errorMessage);
                }
            }
        });
    }

    private void cargarClientesOffline() {
        pd.setMessage("CARGANDO CLIENTES");
        pd.show();
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario", sessionUsuario.getUsuario());
        Call<JsonRespuesta<Cliente>> loginCall = ApiRetrofitLong.getInstance(sessionUsuario.getUrlPreventa()).getOfflineService().clientesByDiaOffLine(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta<Cliente>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<Cliente>> call, Response<JsonRespuesta<Cliente>> response) {
                if(response.body().getEstado()!=null){
                    if (response.body().getEstado() == 1) {
                        List<Cliente> listaCliente = response.body().getData();
                        if(listaCliente.size()>0){
                            if(listaCliente.get(0).getCodCliente().equals("X")){
                                PaqueteUsuario pu= sessionUsuario.getPaqueteUsuario();
                                pu.setIgnoreGps(Integer.parseInt(listaCliente.get(0).getEstadoGeoSis()));
                                sessionUsuario.guardarPaqueteUsuario(pu);
                                cargarClientes(listaCliente.get(0).getCodCliente(),listaCliente.get(0).getDescCliente());
                            }else{
                                try {
                                    ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
                                    for (int i = 0; i < listaCliente.size(); i++) {
                                        Cliente cl = listaCliente.get(i);
                                        ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().insertarCliente((Cliente) cl);// INSERTA CLIENTES
                                    }
                                    ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                                } finally {
                                    ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                                }
                                recycler = (RecyclerView) findViewById(R.id.reciclador);
                                recycler.setHasFixedSize(true);
                                lManager = new LinearLayoutManager(getApplicationContext());
                                recycler.setLayoutManager(lManager);
                                adapter = new ClienteAdapter(listaCliente, getApplicationContext());
                                recycler.setAdapter(adapter);
                                recycler.addItemDecoration(new SimpleDividerItemDecorator(ClienteActivity.this));
                                LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler.getContext(), R.anim.layout_full_down);
                                recycler.setLayoutAnimation(controller);
                                recycler.getAdapter().notifyDataSetChanged();
                                recycler.scheduleLayoutAnimation();
                                txtCantidadClientes.setText(adapter.getItemCount()+"");
                                ChocoBar.builder().setBackgroundColor(getResources().getColor(R.color.colorSuccess)).setTextSize(12)
                                        .setTextColor(Color.parseColor("#FFFFFF"))
                                        .setTextTypefaceStyle(Typeface.ITALIC)
                                        .setText("SE CARGARON LOS CLIENTES CORRECTAMENTE!!")
                                        .setMaxLines(4)
                                        .centerText()
                                        .setActionTextSize(12)
                                        .setActionTextTypefaceStyle(Typeface.BOLD)
                                        .setIcon(R.drawable.ic_check_circle_white_48dp)
                                        .setActivity(ClienteActivity.this)
                                        .setDuration(ChocoBar.LENGTH_SHORT).build().show();
                                //OFFLINE
                                cargarDatosUsuarioOffline();
                            }

                        }else{
                            ChocoBar.builder().setBackgroundColor(getResources().getColor(R.color.colorError)).setTextSize(12)
                                    .setTextColor(Color.parseColor("#FFFFFF"))
                                    .setTextTypefaceStyle(Typeface.ITALIC)
                                    .setText("NO TIENE CLIENTES ASIGNADOS!!")
                                    .setMaxLines(4)
                                    .centerText()
                                    .setActionText("Cerrar")
                                    .setActionClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(R.color.amber900))
                                    .setActionTextSize(12)
                                    .setActionTextTypefaceStyle(Typeface.BOLD)
                                    .setIcon(R.drawable.ic_error_outline_white_48dp)
                                    .setActivity(ClienteActivity.this)
                                    .setDuration(ChocoBar.LENGTH_INDEFINITE).build().show();
                            pd.dismiss();
                        }





                    } else {

                        new SweetAlertDialog(ClienteActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText(response.body().getMensaje())
                                .show();
                        pd.dismiss();
                    }
                }else{
                    new SweetAlertDialog(ClienteActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.txtMensajeServidorCaido))
                            .show();
                    pd.dismiss();
                }

            }

            @Override
            public void onFailure(Call<JsonRespuesta<Cliente>> call, Throwable t) {
                new SweetAlertDialog(ClienteActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.txtMensajeConexion))
                        .show();
                pd.dismiss();
            }
        });
    }

    private void cargarOnlyClientesOffline() {
        this.invalidateOptionsMenu();
        pd.setMessage("CARGANDO SOLO CLIENTES CON CAMBIOS");
        pd.show();
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario", sessionUsuario.getUsuario());
        Call<JsonRespuesta> loginCall = ApiRetrofitLong.getInstance(sessionUsuario.getUrlPreventa()).getOfflineService().onlyClientesByDiaOffLine(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if(response.body()!=null){
                    if (response.body().getEstado() == 1) {
                        ArrayList listaRpta = (ArrayList) response.body().getItem();
                        ArrayList arrayClientesNew = (ArrayList) listaRpta.get(0);
                        ArrayList arrayClientesOld = (ArrayList) listaRpta.get(1);
                        List<Cliente> listaClientesNew = new ArrayList<>();
                        for (int i = 0; i < arrayClientesNew.size(); i++) {
                            Gson gson = new Gson();
                            JsonObject jsonObject = gson.toJsonTree(arrayClientesNew.get(i)).getAsJsonObject();
                            Cliente cliente = gson.fromJson(jsonObject, Cliente.class);
                            listaClientesNew.add(cliente);
                        }

                        List<Cliente> listaClientesOld = new ArrayList<>();
                        for (int i = 0; i < arrayClientesOld.size(); i++) {
                            Gson gson = new Gson();
                            JsonObject jsonObject = gson.toJsonTree(arrayClientesOld.get(i)).getAsJsonObject();
                            Cliente cliente = gson.fromJson(jsonObject, Cliente.class);
                            listaClientesOld.add(cliente);
                        }


                        if (listaClientesNew.size() == 0 && listaClientesOld.size() == 0) {
                            ChocoBar.builder().setBackgroundColor(getResources().getColor(R.color.colorWarning)).setTextSize(12)
                                    .setTextColor(Color.parseColor("#FFFFFF"))
                                    .setTextTypefaceStyle(Typeface.ITALIC)
                                    .setText("NO SE ENCUENTRAN CAMBIOS EN CLIENTES!!")
                                    .setMaxLines(4)
                                    .centerText()
                                    .setActionTextSize(12)
                                    .setActionTextTypefaceStyle(Typeface.BOLD)
                                    .setIcon(R.drawable.ic_check_circle_white_48dp)
                                    .setActivity(ClienteActivity.this)
                                    .setDuration(ChocoBar.LENGTH_SHORT).build().show();
                            pd.dismiss();

                        } else {
                            if (listaClientesNew.size() > 0) {
                                try {
                                    ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
                                    for (int i = 0; i < listaClientesNew.size(); i++) {
                                        Cliente cl = listaClientesNew.get(i);
                                        long cantNew = ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().getCountClienteNuevo(cl.getCodCliente(), cl.getCodLocal());
                                        if (cantNew == 0) {
                                            ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().insertarCliente(cl);// INSERTA CLIENTE nuevo
                                        } else {
                                            ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().updateClienteOldClienteNew(cl);// INSERTA CLIENTE nuevo
                                        }
                                    }
                                    ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                                } finally {
                                    ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                                }

                            }

                            if (listaClientesOld.size() > 0) {
                                try {
                                    ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
                                    for (int i = 0; i < listaClientesOld.size(); i++) {
                                        Cliente cl = listaClientesOld.get(i);
                                        ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().deleteCliente(cl);// Delete CLIENTE
                                    }
                                    ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                                } finally {
                                    ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                                }

                            }

                            recycler = (RecyclerView) findViewById(R.id.reciclador);
                            recycler.setHasFixedSize(true);
                            lManager = new LinearLayoutManager(getApplicationContext());
                            recycler.setLayoutManager(lManager);
                            adapter = new ClienteAdapter(ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().listarClientes(UtilAndroid.fechaDevice("yyyy-MM-dd")), getApplicationContext());
                            recycler.setAdapter(adapter);
                            recycler.addItemDecoration(new SimpleDividerItemDecorator(ClienteActivity.this));
                            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler.getContext(), R.anim.layout_full_down);
                            recycler.setLayoutAnimation(controller);
                           // recycler.getAdapter().notifyDataSetChanged();
                            recycler.scheduleLayoutAnimation();

                            ChocoBar.builder().setBackgroundColor(getResources().getColor(R.color.colorSuccess)).setTextSize(12)
                                    .setTextColor(Color.parseColor("#FFFFFF"))
                                    .setTextTypefaceStyle(Typeface.ITALIC)
                                    .setText("SE ACTUALIZARON " + listaClientesNew.size() + " CLIENTES CORRECTAMENTE!!")
                                    .setMaxLines(4)
                                    .centerText()
                                    .setActionTextSize(12)
                                    .setActionTextTypefaceStyle(Typeface.BOLD)
                                    .setIcon(R.drawable.ic_check_circle_white_48dp)
                                    .setActivity(ClienteActivity.this)
                                    .setDuration(ChocoBar.LENGTH_SHORT).build().show();
                            pd.dismiss();

                        }
                        //


                    } else {
                        Toast.makeText(getApplicationContext(), response.body().getMensaje(), Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }
                }else{
                    new SweetAlertDialog(ClienteActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.txtMensajeServidorCaido))
                            .show();
                    pd.dismiss();

                }


            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                ChocoBar.builder().setBackgroundColor(getResources().getColor(R.color.colorError)).setTextSize(12)
                        .setTextColor(Color.parseColor("#FFFFFF"))
                        .setTextTypefaceStyle(Typeface.ITALIC)
                        .setText("NO SE CARGARON LOS CLIENTES , PORFAVOR VOLVER A INICIAR LA JORNADA!!")
                        .setMaxLines(4)
                        .centerText()
                        .setActionText("Cerrar")
                        .setActionClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.amber900))
                        .setActionTextSize(12)
                        .setActionTextTypefaceStyle(Typeface.BOLD)
                        .setIcon(R.drawable.ic_error_outline_white_48dp)
                        .setActivity(ClienteActivity.this)
                        .setDuration(ChocoBar.LENGTH_INDEFINITE).build().show();

                pd.dismiss();
            }
        });
    }

    private void cargarDatosUsuarioOffline() {
        pd.setMessage("CARGANDO DATOS USUARIO");
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario", sessionUsuario.getUsuario());
        Call<JsonRespuesta> loginCall = ApiRetrofitLong.getInstance(sessionUsuario.getUrlPreventa()).getOfflineService().datosUsuarioOffline(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if (response.body().getEstado() == 1) {
                    ArrayList lista = (ArrayList) response.body().getData();
                    ArrayList datosUsuario = (ArrayList) lista.get(0);
                    ArrayList condicionesPago = (ArrayList) lista.get(1);
                    try {
                        ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
                        for (int i = 0; i < datosUsuario.size(); i++) {
                            ArrayList datos = (ArrayList) datosUsuario.get(i);
                            ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().insertarDatosUsuario(datos);//INSERTAR DATOS DE USUARIO
                        }
                        for (int i = 0; i < condicionesPago.size(); i++) {
                            ArrayList condiciones = (ArrayList) condicionesPago.get(i);
                            ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().insertarCondicionesPago(condiciones);//INSERTAR CONDICIONES DE PAGO
                        }
                        ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                    } finally {
                        ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                    }

                    ChocoBar.builder().setBackgroundColor(getResources().getColor(R.color.colorSuccess)).setTextSize(12)
                            .setTextColor(Color.parseColor("#FFFFFF"))
                            .setTextTypefaceStyle(Typeface.ITALIC)
                            .setText("SE CARGARON LOS DATOS DE USUARIO CORRECTAMENTE!!")
                            .setMaxLines(50)
                            .centerText()
                            .setActionTextSize(12)
                            .setActionTextTypefaceStyle(Typeface.BOLD)
                            .setIcon(R.drawable.ic_check_circle_white_48dp)
                            .setActivity(ClienteActivity.this)
                            .setDuration(ChocoBar.LENGTH_SHORT).build().show();

                    cargarArticulosOffline();


                } else {

//                    Snackbar.make(btnRegistrar.getRootView(), response.body().getMensaje(), Snackbar.LENGTH_INDEFINITE).setAction("Cerrar", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                        }
//                    }).show();
                }
            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {

                ChocoBar.builder().setBackgroundColor(getResources().getColor(R.color.colorError)).setTextSize(12)
                        .setTextColor(Color.parseColor("#FFFFFF"))
                        .setTextTypefaceStyle(Typeface.ITALIC)
                        .setText("NO SE CARGARON LOS DATOS DE USUARIO , PORFAVOR VOLVER A INICIAR LA JORNADA!!")
                        .setMaxLines(4)
                        .centerText()
                        .setActionText("Cerrar")
                        .setActionClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.amber900))
                        .setActionTextSize(12)
                        .setActionTextSize(12)
                        .setActionTextTypefaceStyle(Typeface.BOLD)
                        .setIcon(R.drawable.ic_error_outline_white_48dp)
                        .setActivity(ClienteActivity.this)
                        .setDuration(ChocoBar.LENGTH_INDEFINITE).build().show();


                pd.dismiss();


            }
        });
    }

    private void cargarArticulosOffline() {
        pd.setMessage("CARGANDO MAESTRO DE ARTICULOS/FOCUS/SUGERIDOS");
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario", sessionUsuario.getUsuario());
        Call<JsonRespuesta> loginCall = ApiRetrofitLong.getInstance(sessionUsuario.getUrlPreventa()).getOfflineService().articulosOffLine(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if (response.body().getEstado() == 1) {
                    ArrayList lista = (ArrayList) response.body().getData();
                    ArrayList articulos = (ArrayList) lista.get(0);
                    ArrayList sugeridos = (ArrayList) lista.get(1);
                    ArrayList focus = (ArrayList) lista.get(2);

                    List<Articulo> listaArticulos = new ArrayList<>();
                    for (int i = 0; i < articulos.size(); i++) {
                        Gson gson = new Gson();
                        JsonObject jsonObject = gson.toJsonTree(articulos.get(i)).getAsJsonObject();
                        Articulo articulo = gson.fromJson(jsonObject, Articulo.class);
                        listaArticulos.add(articulo);
                    }

                    try {
                        ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();
                        for (int i = 0; i < listaArticulos.size(); i++) {
                            Articulo art = listaArticulos.get(i);
                            ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().insertarArticulos(art);//INSERTAR ARTICULOS
                        }


                        for (int i = 0; i < sugeridos.size(); i++) {
                            ArrayList obj = (ArrayList) sugeridos.get(i);
                            ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().insertarSugeridoLocal(obj);//INSERTAR SUGERIDO

                        }
///movi
                        for (int i = 0; i < focus.size(); i++) {
                            ArrayList obj = (ArrayList) focus.get(i);
                            ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().insertarFocusLocal(obj);//INSERTAR FOCUS

                        }


                        ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                    } finally {
                        ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                    }


                    pd.dismiss();
                    sessionUsuario.guardarIsTodoSincronizado(true);
                    ChocoBar.builder().setBackgroundColor(getResources().getColor(R.color.colorSuccess)).setTextSize(12)
                            .setTextColor(Color.parseColor("#FFFFFF"))
                            .setTextTypefaceStyle(Typeface.ITALIC)
                            .setText("SE CARGO TODA LA DATA CORRECTAMENTE!!")
                            .setMaxLines(4)
                            .centerText()
                            .setActionText("Cerrar")
                            .setActionClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.amber900))
                            .setActionTextSize(12)
                            .setActionTextTypefaceStyle(Typeface.BOLD)
                            .setActionTextSize(12)
                            .setActionTextTypefaceStyle(Typeface.BOLD)
                            .setIcon(R.drawable.ic_check_circle_white_48dp)
                            .setActivity(ClienteActivity.this)
                            .setDuration(ChocoBar.LENGTH_INDEFINITE).build().show();





                } else {

                    pd.dismiss();
                    SnackAlert sa = new SnackAlert(ClienteActivity.this);
                    sa.setTitle("ERROR!!");
                    sa.setMessage("ERROR AL CARGAR LOS ARTICULOS,\n VUELVA A INTENTAR PORFAVOR!!");
                    sa.setType(SnackAlert.ERROR);
                    sa.show();

                }
            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                pd.dismiss();
                ChocoBar.builder().setBackgroundColor(getResources().getColor(R.color.colorError)).setTextSize(12)
                        .setTextColor(Color.parseColor("#FFFFFF"))
                        .setTextTypefaceStyle(Typeface.ITALIC)
                        .setText("NO SE CARGARON LOS ARTICULOS, PORFAVOR VOLVER A INICIAR LA JORNADA!!")
                        .setMaxLines(4)
                        .centerText()
                        .setActionText("Cerrar")
                        .setActionClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.amber900))
                        .setActionTextSize(12)
                        .setActionTextTypefaceStyle(Typeface.BOLD)
                        .setIcon(R.drawable.ic_error_outline_white_48dp)
                        .setActivity(ClienteActivity.this)
                        .setDuration(ChocoBar.LENGTH_INDEFINITE).build().show();

            }
        });
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setTitle(
                Html.fromHtml("<font color='#FFFFFF'>"
                        + "CLIENTES"
                        + "</font>"));
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                openDrawer();
                return true;
            case R.id.action_logout:
                logout(sessionUsuario);
                return true;

            case R.id.jornada:


                new SweetAlertDialog(ClienteActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Desea cargar nuevamente toda la data para la toma de pedidos?")
                        //.setContentText("SSSS")
                        .setConfirmText("SI")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                sessionUsuario.guardarIsTodoSincronizado(false);
                                sessionUsuario.guardarIsOnlyOnline(false);
                                limpiarTablaClientes();
                                limpiarArticulos();
                                limpiarCondiciones();
                                limpiarDatosUsuario();
                                Intent intent4 = new Intent(ClienteActivity.this, ClienteActivity.class);
                                startActivity(intent4);
                                finish();

                            }
                        })
                        .setCancelButton("NO", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();


                return true;

            case R.id.onlyClientes:
if(sessionUsuario.getIsTodoSincronizado()&&!sessionUsuario.getIsOnlyOnline()){

    new SweetAlertDialog(ClienteActivity.this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Desea cargar/actualizar solo clientes ?")
            //.setContentText("SSSS")
            .setConfirmText("SI")
            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();
                    cargarOnlyClientesOffline();

                }
            })
            .setCancelButton("NO", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();
                }
            })
            .show();
}else{
    new SweetAlertDialog(ClienteActivity.this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Alerta")
            .setContentText("ESTA OPCIÓN SOLO ESTA DISPONIBLE CUANDO  TODA LA DATA ESTA CARGADA")
            .show();
}

                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void goToNavDrawerItem(int item) {
        switch (item) {
            case R.id.menu0:
                Intent intent0 = new Intent(this, InicioActivity.class);
                startActivity(intent0);
                 break;
            case R.id.menu1:
                Intent intent1 = new Intent(this, MenuPreventaActivity.class);
                startActivity(intent1);
                 break;
            case R.id.menu2:
                Intent intent2 = new Intent(this, ContenedorAltaActivity.class);
                startActivity(intent2);
                 break;
            case R.id.menu3:
                Intent intent3 = new Intent(this, MenuCobranzaActivity.class);
                startActivity(intent3);
                 break;
            case R.id.menu4:
                Intent intent4 = new Intent(this, MenuBandejasActivity.class);
                startActivity(intent4);
                 break;


        }
    }

    SearchView mSearchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_cliente, menu);
        MenuItem mSearch = menu.findItem(R.id.action_search);
         mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint(Html.fromHtml("<font  color='#FFFFFF'>"
                + "BUSCAR CLIENTE"
                + "</font>"));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String clienteText = newText.toLowerCase();
                List<Cliente> newClientList = new ArrayList<Cliente>();
                for (Cliente c : ApiSqlite.getInstance(ClienteActivity.this.getApplicationContext()).getOperacionesBaseDatos().listarClientes(UtilAndroid.fechaDevice("yyyy-MM-dd"))) {
                    if (c.getDescCliente().toLowerCase().contains(clienteText)) {
                        newClientList.add(c);
                    }
                }
                if(adapter!=null){
                    adapter.updateList(newClientList);
                    adapter = new ClienteAdapter(newClientList, getApplicationContext());
                    recycler.setAdapter(adapter);
                    recycler.addItemDecoration(new SimpleDividerItemDecorator(ClienteActivity.this));

                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != PermissionUtils.REQUEST_CODE) {
            return;
        }
        if (PermissionUtils.isPermissionGranted(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, grantResults)) {
            initGoogleMapLocation();
        } else {
            Toast.makeText(this, "Stop apps without permission to use location information", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        //  LocalBroadcastManager.getInstance(this).unregisterReceiver(br);
        super.onPause();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        if (dialogSecuencia != null) {
//            dialogSecuencia.dismiss();
//        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        Intent intent4 = new Intent(this, MenuPreventaActivity.class);
        intent4.putExtra("origen", "pedidoEnRuta");
        startActivity(intent4);
        finish();
        // openDrawer();
    }

    @Override
    protected void onRestart() {
        if (contenedorListener != null) {
            contenedorListener.showButtonPedido();
        }
        super.onRestart();
    }

    @Override
    protected void onResume() {
        if (sessionUsuario.getBandSettings()) {
            startActivity(new Intent(this, ClienteActivity.class));
            sessionUsuario.guardarBandSettings(false);
        }
        super.onResume();
    }


    public interface ContenedorListener {
        void showButtonPedido();
    }


}
