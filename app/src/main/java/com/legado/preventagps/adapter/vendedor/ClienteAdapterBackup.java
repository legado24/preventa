package com.legado.preventagps.adapter.vendedor;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.tonywills.loadingbutton.LoadingButton;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.legado.preventagps.R;
import com.legado.preventagps.activities.vendedor.ClienteActivity;
import com.legado.preventagps.activities.vendedor.ContenedorActivity;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.dialogs.MyDialogClienteMapa;
import com.legado.preventagps.dialogs.MyDialogInfo;
import com.legado.preventagps.dialogs.MyDialogProgress;
import com.legado.preventagps.enums.CLIENTEENUM;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Cliente;
import com.legado.preventagps.modelo.NoPedido;
import com.legado.preventagps.util.OnMapAndViewReadyListener;
import com.legado.preventagps.util.PermissionUtils;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.UtilRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ClienteAdapterBackup extends RecyclerView.Adapter<ClienteAdapterBackup.ClienteViewHolder> implements MyDialogClienteMapa.EditDialogClientListener, MyDialogInfo.DialogInfoListener {
    public List<Cliente> items;
    SessionUsuario sessionUsuario;
    Context c;
    MyDialogClienteMapa.EditDialogClientListener editDialogClientListener;
    MyDialogInfo.DialogInfoListener dialogInfoListener;
    public ClienteAdapterBackup(List<Cliente> items, Context context) {
        this.items = items;
        editDialogClientListener = this;
        dialogInfoListener = this;
        this.c = context;
        sessionUsuario = new SessionUsuario(c);
    }

    public List<Cliente> getItems() {
        return items;
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

        if (items.get(i).getEstadoGeoSis().equals("0")) {
            viewHolder.secuencia.setText("SIN GEOLOCALIZAR");
            viewHolder.llGeolocalizar.setVisibility(View.VISIBLE);
            viewHolder.llVerGeo.setVisibility(View.GONE);
        } else {
            viewHolder.secuencia.setText("GEOLOCALIZADO");
            viewHolder.llGeolocalizar.setVisibility(View.GONE);
            viewHolder.llVerGeo.setVisibility(View.GONE);
            viewHolder.btnPreventa.setEnabled(true);
            viewHolder.btnNoPedido.setEnabled(true);
        }
        if (items.get(i).getEstadoJornada().equals("NEW")) {
            viewHolder.itemView.setBackgroundColor(c.getResources().getColor(R.color.material_blue_400));
        } else if (items.get(i).getEstadoJornada().equals("N")) {
            viewHolder.itemView.setBackgroundColor(c.getResources().getColor(R.color.material_red_400));
        } else if (items.get(i).getEstadoJornada().equals("P")) {
            viewHolder.itemView.setBackgroundColor(c.getResources().getColor(R.color.material_green_400));
        } else {
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
        }
//       int countAux=0;
//        if(sessionUsuario.getPaqueteClienteAux()!=null){
//            if(sessionUsuario.getPaqueteClienteAux().getClientesSecuenciados()!=null){
//                System.out.println("pasados"+sessionUsuario.getPaqueteClienteAux().getClientesSecuenciados().size());
//                countAux=sessionUsuario.getPaqueteClienteAux().getClientesSecuenciados().size();
//            }
//
//        }

//        if(items.get(i).getEstadoJornada().equals("0")){
////            if(items.get(i).getFlagDespachado()==1){
////                 viewHolder.itemView.setBackgroundColor(c.getResources().getColor(R.color.material_green_500));
////            }else if(items.get(i).getFlagDespachado()==-1){
////                viewHolder.itemView.setBackgroundColor(c.getResources().getColor(R.color.material_red_400));
////            }else if(items.get(i).getFlagDespachado()==-2){
////                viewHolder.itemView.setBackgroundColor(c.getResources().getColor(R.color.material_blue_400));
////            }else{
//                viewHolder.itemView.setBackgroundColor(Color.WHITE);
////                if(i==0){
////                    viewHolder.btnPreventa.setEnabled(true);
////                    viewHolder.btnNoPedido.setEnabled(true);
////                    viewHolder.itemView.setBackgroundColor(c.getResources().getColor(R.color.white));
////                }else{
////                    viewHolder.btnPreventa.setEnabled(false);
////                    viewHolder.btnNoPedido.setEnabled(false);
////                    viewHolder.itemView.setBackgroundColor(c.getResources().getColor(R.color.material_grey_500));
////                }
//            //}
//        }else{
//
//           if(items.get(i).getEstadoJornada().equals("N")){
//               viewHolder.itemView.setBackgroundColor(c.getResources().getColor(R.color.material_red_400));
//           } if(items.get(i).getEstadoJornada().equals("P")){
//                viewHolder.itemView.setBackgroundColor(c.getResources().getColor(R.color.material_green_500));
//            }else{
//
//               viewHolder.itemView.setBackgroundColor(Color.WHITE);
//           }
////            if(items.get(i).getFlagDespachado()!=null){
////                viewHolder.btnPreventa.setEnabled(true);
////                viewHolder.btnNoPedido.setEnabled(true);
////                viewHolder.itemView.setBackgroundColor(c.getResources().getColor(R.color.white));
////            }else{
////
////                if(i==items.size()-(items.size()-countAux)){
////                    viewHolder.btnPreventa.setEnabled(true);
////                    viewHolder.btnNoPedido.setEnabled(true);
////                    viewHolder.itemView.setBackgroundColor(c.getResources().getColor(R.color.white));
////                }else {
////                    viewHolder.btnPreventa.setEnabled(false);
////                    viewHolder.btnNoPedido.setEnabled(false);
////                    viewHolder.itemView.setBackgroundColor(c.getResources().getColor(R.color.material_grey_500));
////                }
////            }
//        }


    }

    @Override
    public void updateRecycler(List<Cliente> lista) {
        updateList(lista);
    }

    @Override
    public void updateRecyclerSuccess(List<Cliente> list) {
        updateList(list);
    }

    public static class ClienteViewHolder extends RecyclerView.ViewHolder implements MyDialogClienteMapa.EditDialogClientHolderListener, ClienteActivity.ContenedorListener, GoogleMap.OnMyLocationButtonClickListener,
            GoogleMap.OnMarkerClickListener,
            GoogleMap.OnInfoWindowClickListener,
            GoogleMap.OnMarkerDragListener,
            SeekBar.OnSeekBarChangeListener,
            GoogleMap.OnInfoWindowLongClickListener,
            GoogleMap.OnInfoWindowCloseListener,
            OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {
        public TextView nombre;
        public TextView codCliente;
        public TextView secuencia;
        public TextView direccion;
        public TextView codRuta;
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
        private LocationRequest mLocationRequest;
        private LocationCallback mLocationCallback;
        private Location mCurrentLocation;
        private boolean isNetworkLocation, isGPSLocation;
        private FusedLocationProviderClient mFusedLocationClient;
        MyDialogClienteMapa.EditDialogClientListener editDialogClientListener;
        MyDialogClienteMapa.EditDialogClientHolderListener editDialogClientHolderListener;
        ClienteActivity.ContenedorListener contenedorListener;
        MyDialogInfo.DialogInfoListener dialogInfoListener;
        AppCompatActivity activity = (AppCompatActivity) itemView.getContext();

        public ClienteViewHolder(final View v, final List<Cliente> lista, MyDialogClienteMapa.EditDialogClientListener editDialogClientListener, final MyDialogInfo.DialogInfoListener dialogInfoListener) {
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
//            secuencia = (TextView) v.findViewById(R.id.txtSecuencia);
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
                            } else if (v.getContext().getClass().getName().equals("com.legado.preventagps.activities.vendedor.CobranzaOutActivity")) {
//                                ((CobranzaOutActivity) v.getContext()).setContenedorListener(contenedorListener);
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
                            intent.putExtra("onlycobranza", false);
                            btnPreventa.setLoading(false);
                            v.getContext().startActivity(intent);
                        }
                    }, 500);

                }
            });

            btnNoPedido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                            showDialog(noPedido, v, position, cliente);

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
//                            List<Cliente> list = sessionUsuario.getPaqueteCliente().getClientesSecuenciados();
//                            for (int q = 0; q < list.size(); q++) {
//                                if (list.get(q).getCodCliente().equals(cliente.getCodCliente()) && list.get(q).getCodLocal().equals(cliente.getCodLocal())) {
//                                    cliente.setCoordenadas(list.get(q).getCoordenadas());
//                                    break;
//                                }
//
//                            }
                            checkMyPermissionLocation(cliente);
                        }
                    }, 500);
                }
            });

            btnVerGeo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnVerGeo.setEnabled(false);
                    imgVerGeo.setEnabled(false);
                    imgVerGeo.setVisibility(View.GONE);
                    btnVerGeo.setLoading(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            final int position = getAdapterPosition();
                            final Cliente cliente = lista.get(position);
//                            List<Cliente> list = sessionUsuario.getPaqueteCliente().getClientesSecuenciados();
//                            for (int q = 0; q < list.size(); q++) {
//                                if (list.get(q).getCodCliente().equals(cliente.getCodCliente()) && list.get(q).getCodLocal().equals(cliente.getCodLocal())) {
//                                    cliente.setCoordenadas(list.get(q).getCoordenadas());
//                                    break;
//                                }
//                            }
                            checkMyPermissionLocation(cliente);
                        }
                    }, 500);
                }
            });
        }

        private void checkMyPermissionLocation(Cliente cliente) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                //Permission Check
                PermissionUtils.requestPermission(activity);
            } else {
                initGoogleMapLocation(cliente);
            }
        }


        private void initGoogleMapLocation(final Cliente cliente) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
            SettingsClient mSettingsClient = LocationServices.getSettingsClient(activity);
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult result) {
                    super.onLocationResult(result);
                    mCurrentLocation = result.getLocations().get(0);
                    if (mCurrentLocation != null) {
                        Log.e("Location(Lat)==", "" + mCurrentLocation.getLatitude());
                        Log.e("Location(Long)==", "" + mCurrentLocation.getLongitude());
                    }
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
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                }

                //Locatio nMeaning that all relevant information is available
                @Override
                public void onLocationAvailability(LocationAvailability availability) {
                    //boolean isLocation = availability.isLocationAvailable();
                }
            };
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(5000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setNumUpdates(3);
            LocationManager mListener = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
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

                a.show(activity.getSupportFragmentManager(), "Setting");
                a.setCancelable(false);

            }

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            /**
             * Stores the type of location service the client wants to use. Also used for positioning.
             */
            LocationSettingsRequest mLocationSettingsRequest = builder.build();

            Task<LocationSettingsResponse> locationResponse = mSettingsClient.checkLocationSettings(mLocationSettingsRequest);
            locationResponse.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    Log.e("Response", "Successful acquisition of location information!!");
                    //
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                }
            });
            //When the location information is not set and acquired, callback
            locationResponse.addOnFailureListener(activity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.e("onFailure", "Location environment check");

                            PermissionUtils.LocationSettingDialog a = PermissionUtils.LocationSettingDialog.newInstance();

                            a.show(activity.getSupportFragmentManager(), "Setting");
                            a.setCancelable(false);
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = "Check location setting";
                            Log.e("onFailure", errorMessage);
                    }
                }
            });
        }

        private void checkMyPermissionLocation(final NoPedido noPedido, final View v, final Integer position, final Cliente cliente) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                //Permission Check
                PermissionUtils.requestPermission(activity);
            } else {
                initGoogleLocation(noPedido, v, position, cliente);
            }
        }

        private void initGoogleLocation(final NoPedido noPedido, final View v, final Integer position, final Cliente cliente) {
            //   enableMyLocation();
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
            SettingsClient mSettingsClient = LocationServices.getSettingsClient(activity);
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult result) {
                    super.onLocationResult(result);
                    mCurrentLocation = result.getLocations().get(0);
                    if (mCurrentLocation != null) {
                        Log.e("Location(Lat)==", "" + mCurrentLocation.getLatitude());
                        Log.e("Location(Long)==", "" + mCurrentLocation.getLongitude());
                    }

                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                    LatLng latLngYo = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    noPedido.setCoordenadasNoPedido(latLngYo.latitude + "," + latLngYo.longitude);
                    registrarNoPedid(noPedido, v, position, cliente);
                    Log.wtf("COORDENADA REGISTRO", noPedido.getCoordenadasNoPedido());
                    //Toast.makeText(itemView.getContext(), "COOEDENADA "+noPedido.getCoordenadasNoPedido(), Toast.LENGTH_LONG).show();
                    dialogRegistrarMotivo.dismiss();
                }

                //Locatio nMeaning that all relevant information is available
                @Override
                public void onLocationAvailability(LocationAvailability availability) {
                    //boolean isLocation = availability.isLocationAvailable();
                }
            };
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(4000);
            mLocationRequest.setFastestInterval(4000);
            mLocationRequest.setNumUpdates(4);
            LocationManager mListener = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
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
                a.show(activity.getSupportFragmentManager(), "Setting");
                a.setCancelable(false);

            }

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            /**
             * Stores the type of location service the client wants to use. Also used for positioning.
             */
            LocationSettingsRequest mLocationSettingsRequest = builder.build();

            Task<LocationSettingsResponse> locationResponse = mSettingsClient.checkLocationSettings(mLocationSettingsRequest);
            locationResponse.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    Log.e("Response", "Successful acquisition of location information!!");
                    //
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                }
            });
            //When the location information is not set and acquired, callback
            locationResponse.addOnFailureListener(activity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.e("onFailure", "Location environment check");
                            PermissionUtils.LocationSettingDialog a = PermissionUtils.LocationSettingDialog.newInstance();
                            a.show(activity.getSupportFragmentManager(), "Setting");
                            a.setCancelable(false);
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = "Check location setting";
                            Log.e("onFailure", errorMessage);
                    }
                }
            });
        }

        private void registrarUbicacionNoPedido(final NoPedido noPedido, final View v, final Integer position, final Cliente cliente) {
            checkMyPermissionLocation(noPedido, v, position, cliente);
        }

        public void registrarNoPedid(final NoPedido noPedido, final View v, final Integer position, final Cliente cliente) {
            Call<JsonRespuesta<Integer>> call = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getVentaService().registrarNoPedido(noPedido);
            call.enqueue(new Callback<JsonRespuesta<Integer>>() {
                @Override
                public void onResponse(Call<JsonRespuesta<Integer>> call, Response<JsonRespuesta<Integer>> response) {
                    if (response.body().getEstado() == -1) {
                        Snackbar.make(v, response.body().getMensaje(), Snackbar.LENGTH_INDEFINITE).setAction("Cerrar", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        }).show();
                     } else {
//                        List<Cliente> clientes = sessionUsuario.getPaqueteCliente().getClientesSecuenciados();
//                        for (int i = 0; i < clientes.size(); i++) {
//                            if (clientes.get(i).getCodCliente().equals(cliente.getCodCliente()) && clientes.get(i).getCodLocal().equals(cliente.getCodLocal())) {
//                                clientes.get(i).setEstadoJornada("N");
//                                break;
//                            }
//                        }
//                        PaqueteCliente paq = new PaqueteCliente();
//                        paq.setClientesSecuenciados(clientes);
//                        sessionUsuario.guardarPaqueteCliente(paq);
//
                        try {
                            ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().getDb().beginTransaction();

                            ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().updateClienteEstadoJornada("N",cliente.getCodCliente(),cliente.getCodLocal());

                            ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().getDb().setTransactionSuccessful();
                        } finally {
                            ApiSqlite.getInstance(activity.getApplicationContext()).getOperacionesBaseDatos().getDb().endTransaction();
                        }
                        Intent intent = new Intent(v.getContext(), ClienteActivity.class);
                        v.getContext().startActivity(intent);


                    }
                }

                @Override
                public void onFailure(Call<JsonRespuesta<Integer>> call, Throwable t) {
//                    Snackbar.make(getView(), "Problemas de conexion  ", Snackbar.LENGTH_INDEFINITE)
//                            .setAction("Cerrar", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//
//                                }
//                            }).show();
//                    dialogRegistrarLocal.dismiss();
                }
            });
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
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    registrarUbicacionNoPedido(noPedido, v, position, cliente);
                                }
                            }, 1000);
                        }
                    }).setNegativeButton(android.R.string.no, null).show();
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

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onInfoWindowClick(Marker marker) {

        }

        @Override
        public void onInfoWindowClose(Marker marker) {

        }

        @Override
        public void onInfoWindowLongClick(Marker marker) {

        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            return false;
        }

        @Override
        public void onMarkerDragStart(Marker marker) {

        }

        @Override
        public void onMarkerDrag(Marker marker) {

        }

        @Override
        public void onMarkerDragEnd(Marker marker) {

        }

        @Override
        public boolean onMyLocationButtonClick() {
            enableMyLocation();
            return true;
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {

        }

        private void enableMyLocation() {
            if (ContextCompat.checkSelfPermission(itemView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission to access the location is missing.
                PermissionUtils.requestPermission(activity);
            }
//
//            mLocationRequest = new LocationRequest();
//            mLocationRequest.setInterval(4000);
//            mLocationRequest.setFastestInterval(4000);
//            mLocationRequest.setNumUpdates(4);


            LocationManager locationManager = (LocationManager)
                    activity.getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();
            Location yo = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));
               /* if(yo==null){
                    Toast.makeText(itemView.getContext(), "COOEDENADA "+yo.getLatitude()+"-"+yo.longitude, Toast.LENGTH_LONG).show();
                    dialogRegistrarMotivo.dismiss();

                }else{*/
            LatLng latLngYo = new LatLng(yo.getLatitude(), yo.getLongitude());
            Log.wtf("COORDENADA REGISTRO", latLngYo.latitude + "-" + latLngYo.longitude);
            Toast.makeText(itemView.getContext(), "COOEDENADA " + latLngYo.latitude + "-" + latLngYo.longitude, Toast.LENGTH_LONG).show();
            dialogRegistrarMotivo.dismiss();


               /* }*/

        }
    }


}
