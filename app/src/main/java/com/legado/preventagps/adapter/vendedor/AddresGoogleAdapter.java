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
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.legado.preventagps.R;
import com.legado.preventagps.activities.vendedor.ClienteActivity;
import com.legado.preventagps.activities.vendedor.ContenedorActivity;
import com.legado.preventagps.activities.vendedor.ContenedorAltaActivity;
 import com.legado.preventagps.api.ApiRetrofitLong;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.dialogs.MyDialogClienteMapa;
import com.legado.preventagps.dialogs.MyDialogInfo;
import com.legado.preventagps.dialogs.MyDialogProgress;
import com.legado.preventagps.enums.CLIENTEENUM;
import com.legado.preventagps.enums.STATUSSINCRONIZACION;
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


public class AddresGoogleAdapter extends RecyclerView.Adapter<AddresGoogleAdapter.AddresGoogleViewHolder> implements ItemAddressListener {
    public List<Address> items;
    SessionUsuario sessionUsuario;
    Context c;

    MapaAdressCallListener listener;

    public AddresGoogleAdapter(List<Address> items, Context context,  MapaAdressCallListener listener ) {
        this.items = items;
        this.c = context;
        this.listener=listener;
         sessionUsuario = new SessionUsuario(c);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateList(List<Address> address) {

            items = new ArrayList<>();
            items.addAll(address);
            notifyDataSetChanged();

    }

    @Override
    public AddresGoogleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.addressgoogle_card, viewGroup, false);
        AddresGoogleViewHolder ch = new AddresGoogleViewHolder(v, items,this);
        return ch;
    }

    @Override
    public void onBindViewHolder(AddresGoogleViewHolder viewHolder, final int i) {
        viewHolder.txtAddress.setText(items.get(i).getAddressLine(i));
//        viewHolder.codCliente.setText(items.get(i).getCodCliente());




    }

    @Override
    public void onItemClick(View view, int position) {
         Log.wtf("coordeandas",items.get(position).getLatitude()+","+items.get(position).getLongitude());
         LatLng latLng=new LatLng(items.get(position).getLatitude(),items.get(position).getLongitude());
         listener.moverPuntoEnMapa(latLng);
    }


    public static class AddresGoogleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
         List<Address> lista;
        AppCompatActivity activity = (AppCompatActivity) itemView.getContext();
        TextView txtAddress;
        ItemAddressListener listener;

        public AddresGoogleViewHolder(final View v, final List<Address> lista, ItemAddressListener listener) {
            super(v);
             this.lista = lista;
             txtAddress=v.findViewById(R.id.txtAddress);
             this.listener=listener;
            v.setOnClickListener(this);

        }



        @Override
        public void onClick(View view) {
            listener.onItemClick(view,getAdapterPosition());
        }
    }


}
interface ItemAddressListener{
    void onItemClick(View view, int position);
}

