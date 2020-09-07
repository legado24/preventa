package com.legado.preventagps.dialogs;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.legado.preventagps.R;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.modelo.Cliente;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.UtilAndroid;

import java.util.List;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class MyDialogInfo extends DialogFragment {
SessionUsuario sessionUsuario;
static Activity goActivityDialog;
    private DialogInfoListener dialogInfoListener;


    public void setDialogInfoListener(DialogInfoListener dialogInfoListener) {
        this.dialogInfoListener = dialogInfoListener;
    }

    public static MyDialogInfo newInstance(Activity goActivity) {
        goActivityDialog=goActivity;
        MyDialogInfo fragment= new MyDialogInfo();
        return  fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_dialog_info, container, false);
        sessionUsuario=new SessionUsuario(getActivity());
        final LottieAnimationView animationView= (LottieAnimationView) rootView.findViewById(R.id.check);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        animationView.loop(false);
        animationView.playAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
//                List<Cliente> clientesSec = sessionUsuario.getPaqueteCliente().getClientesSecuenciados();
//                PaqueteCliente paqueteCliente = new PaqueteCliente();
//                paqueteCliente.setClientesSecuenciados(clientesSec);
//                sessionUsuario.guardarPaqueteCliente(paqueteCliente);
                dismiss();
                getActivity().finish();
                dialogInfoListener.updateRecyclerSuccess(ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().listarClientes(UtilAndroid.fechaDevice("yyyy-MM-dd")));
               dismiss();
            }
        },1000);

        return rootView;
    }

    public interface DialogInfoListener {
        void updateRecyclerSuccess(List<Cliente> list);

    }
}
