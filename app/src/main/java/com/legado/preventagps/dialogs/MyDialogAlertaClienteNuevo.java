package com.legado.preventagps.dialogs;


import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.legado.preventagps.R;
import com.legado.preventagps.fragments.CobranzaFragment;
import com.legado.preventagps.fragments.PreventaFragment;
import com.roughike.bottombar.BottomBar;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class MyDialogAlertaClienteNuevo extends DialogFragment{

        static BottomBar bottomBarDialog;
    static ActionBar actionBarDialog;
    MyDialogInfo.DialogInfoListener dialogInfoListener;

    public void setDialogInfoListener(MyDialogInfo.DialogInfoListener dialogInfoListener) {
        this.dialogInfoListener = dialogInfoListener;
    }

    public static MyDialogAlertaClienteNuevo newInstance(BottomBar bottomBar, ActionBar actionBar) {
            MyDialogAlertaClienteNuevo fragment= new MyDialogAlertaClienteNuevo();
            bottomBarDialog=bottomBar;
            actionBarDialog=actionBar;
            return  fragment;
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_dialog_alert_cliente_nuevo, container, false);
        final LottieAnimationView animationView= (LottieAnimationView) rootView.findViewById(R.id.alert);
      animationView.loop(false);
        animationView.playAnimation();
       final Button continuar = (Button) rootView.findViewById(R.id.continuar);
        final Button dismiss = (Button) rootView.findViewById(R.id.dismiss);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomBarDialog.setDefaultTab(R.id.tab_datos);
                dismiss();
            }
        });
        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreventaFragment preventaFragment=new PreventaFragment();
                preventaFragment.setDialogInfoListener(dialogInfoListener);
                Bundle args=getArguments();
                args.putBoolean("deudavencida",true);
                preventaFragment.setArguments(args);
                dismiss();
                getFragmentManager().beginTransaction().replace(R.id.contenedorFragment,preventaFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            }
        });


         return rootView;
    }


}
