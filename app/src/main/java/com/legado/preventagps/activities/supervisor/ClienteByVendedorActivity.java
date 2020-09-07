package com.legado.preventagps.activities.supervisor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.legado.preventagps.R;
import com.legado.preventagps.adapter.supervisor.ClienteByVendedorAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.dialogs.MyDialogCalculando;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Cliente;
import com.legado.preventagps.modelo.PaqueteCliente;
import com.legado.preventagps.util.PermissionUtils;
import com.legado.preventagps.util.SimpleDividerItemDecorator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClienteByVendedorActivity extends BaseActivityByVendedor {
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private RecyclerView recycler;
    private ClienteByVendedorAdapter adapter;
    private RecyclerView.LayoutManager lManager;
    private MyDialogCalculando dialogSecuencia;
     private boolean isNetworkLocation, isGPSLocation;
    private ContenedorListener contenedorListener;
    Integer permiteOffline;

    private ProgressDialog pd;

    public void setContenedorListener(ClienteByVendedorActivity.ContenedorListener contenedorListener) {
        this.contenedorListener = contenedorListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_by_vendedor);
        ButterKnife.bind(this);
        setupToolbar();
        pd = new ProgressDialog(this, R.style.AppTheme_MyDialog);
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        sessionUsuario.guardarPaqueteClienteByVendedor(null);
        cargarClientes(getIntent().getStringExtra("usuario"));
        CardView menu1 = (CardView) findViewById(R.id.menuByVendedor1);
        menu1.setCardBackgroundColor(Color.parseColor("#FF9800"));
        TextView txtDashboard=(TextView) findViewById(R.id.txtDashboard);
        txtDashboard.setText("Menu de "+getIntent().getStringExtra("descVendedor"));


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
                if (ActivityCompat.checkSelfPermission(ClienteByVendedorActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
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

    private void cargarClientes(String usuarioVendedor) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        dialogSecuencia = MyDialogCalculando.newInstance("CARGANDO CLIENTES");
        dialogSecuencia.show(ft, "dialog");
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario", usuarioVendedor);
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().clientesByDiaV2(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
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

                    recycler = (RecyclerView) findViewById(R.id.reciclador);
                    recycler.setHasFixedSize(true);
                    lManager = new LinearLayoutManager(getApplicationContext());
                    recycler.setLayoutManager(lManager);
                    adapter = new ClienteByVendedorAdapter(listaCliente, getApplicationContext());

                    recycler.setAdapter(adapter);
                    recycler.addItemDecoration(new SimpleDividerItemDecorator(ClienteByVendedorActivity.this));
                    LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler.getContext(), R.anim.layout_full_down);
                    recycler.setLayoutAnimation(controller);
                    recycler.getAdapter().notifyDataSetChanged();
                    recycler.scheduleLayoutAnimation();
                    PaqueteCliente pcv=new PaqueteCliente();
                    pcv.setClientesSecuenciados(listaCliente);
                    sessionUsuario.guardarPaqueteClienteByVendedor(pcv);
                    dialogSecuencia.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), response.body().getMensaje(), Toast.LENGTH_LONG).show();
                    dialogSecuencia.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                Log.wtf("as", t.toString());
                Toast.makeText(getApplicationContext(), "NO HAY CONEXION CON EL SERVIDOR", Toast.LENGTH_LONG).show();
                dialogSecuencia.dismiss();
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

                sessionUsuario.guardarIsTodoSincronizado(false);
                limpiarTablaClientes();
                limpiarArticulos();
                limpiarCondiciones();
                limpiarDatosUsuario();

                Intent intent4 = new Intent(this, ClienteByVendedorActivity.class);
                startActivity(intent4);
                finish();


                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void goToNavDrawerItem(int item) {
        switch (item) {
            case R.id.menuByVendedor0:
                Intent intent0 = new Intent(this, InicioByVendedorActivity.class);
                intent0.putExtra("usuario", getIntent().getStringExtra("usuario"));
                intent0.putExtra("codVendedor", getIntent().getStringExtra("codVendedor"));
                intent0.putExtra("descVendedor", getIntent().getStringExtra("descVendedor"));
                startActivity(intent0);
                finish();
                break;
            case R.id.menuByVendedor1:
                Intent intent1 = new Intent(this, ClienteByVendedorActivity.class);
                intent1.putExtra("usuario", getIntent().getStringExtra("usuario"));
                intent1.putExtra("codVendedor", getIntent().getStringExtra("codVendedor"));
                intent1.putExtra("descVendedor", getIntent().getStringExtra("descVendedor"));
                startActivity(intent1);
                 finish();
                break;




        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
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
                for (Cliente c :sessionUsuario.getPaqueteClienteByVendedor().getClientesSecuenciados()) {
                    if (c.getDescCliente().toLowerCase().contains(clienteText)) {
                        newClientList.add(c);
                    }
                }
                adapter.updateList(newClientList);
                adapter = new ClienteByVendedorAdapter(newClientList, getApplicationContext());
                recycler.setAdapter(adapter);
                recycler.addItemDecoration(new SimpleDividerItemDecorator(ClienteByVendedorActivity.this));
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
        if (dialogSecuencia != null) {
            dialogSecuencia.dismiss();
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        Intent intent4 = new Intent(this, MenuByVendedorActivity.class);
        intent4.putExtra("origen","PedidoInRutaByVendedor");
        intent4.putExtra("usuario",getIntent().getStringExtra("usuario"));
        intent4.putExtra("codVendedor",getIntent().getStringExtra("codVendedor"));
        intent4.putExtra("descVendedor",getIntent().getStringExtra("descVendedor"));
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
            startActivity(new Intent(this, ClienteByVendedorActivity.class));
            sessionUsuario.guardarBandSettings(false);
        }

        super.onResume();
     }


    public interface ContenedorListener {
        void showButtonPedido();
    }


}
