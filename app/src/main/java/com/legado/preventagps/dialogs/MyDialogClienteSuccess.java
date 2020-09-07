package com.legado.preventagps.dialogs;

import android.content.Intent;
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
import com.legado.preventagps.activities.vendedor.ClienteActivity;
import com.legado.preventagps.activities.vendedor.ContenedorAltaActivity;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class MyDialogClienteSuccess extends DialogFragment {
    private MyDialogClienteSuccess.OnDialogFinishCliente listenerUpdateCliente;
    private String codigoCliente;
    public static MyDialogClienteSuccess newInstance(String codigoCliente, MyDialogClienteSuccess.OnDialogFinishCliente listenerUpdateCliente) {
        MyDialogClienteSuccess fragment = new MyDialogClienteSuccess(codigoCliente,listenerUpdateCliente);

        return fragment;
    }


    public MyDialogClienteSuccess() {


    }

    public MyDialogClienteSuccess(String codigoCliente,MyDialogClienteSuccess.OnDialogFinishCliente listenerUpdateCliente) {
        this.listenerUpdateCliente=listenerUpdateCliente;
        this.codigoCliente=codigoCliente;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_dialog_info, container, false);
        final LottieAnimationView animationView= (LottieAnimationView) rootView.findViewById(R.id.check);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        animationView.loop(false);
        animationView.playAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                Intent intent = new Intent(getActivity(), ClienteActivity.class);
                startActivity(intent);
                getActivity().finish();
                dismiss();
            }
        },1500);

        return rootView;
    }

    public interface OnDialogFinishCliente
    {
        void reloadDataCliente(String codCliente);
    }

}
