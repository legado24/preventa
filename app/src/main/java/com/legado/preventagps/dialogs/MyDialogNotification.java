package com.legado.preventagps.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.legado.preventagps.R;
import com.legado.preventagps.fragments.ActualizarClienteFragment;
import com.legado.preventagps.modelo.PaqueteMensaje;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class MyDialogNotification extends DialogFragment{
    private static final String TAG = "MyDialogNotification";
    @BindView(R.id.txtTitleNotification)
    TextView txtTitleNotification;

    @BindView(R.id.txtBodyNotification)
    TextView txtBodyNotification;


    static PaqueteMensaje paqueteMensajeDialog;
        public static MyDialogNotification newInstance(PaqueteMensaje paqueteMensaje) {
            paqueteMensajeDialog=paqueteMensaje;
            MyDialogNotification fragment= new MyDialogNotification();
             return  fragment;
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_dialog_notification, container, false);
        ButterKnife.bind(this,rootView);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final LottieAnimationView animationView= (LottieAnimationView) rootView.findViewById(R.id.alert);
        final Bundle args=getArguments();
        animationView.loop(false);
        animationView.playAnimation();
        txtTitleNotification.setText(paqueteMensajeDialog.getNotification().getTitle());
        txtBodyNotification.setText(paqueteMensajeDialog.getNotification().getBody());

        return rootView;
    }


}
