package com.legado.preventagps.dialogs;


import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;
import com.legado.preventagps.R;
import com.legado.preventagps.fragments.CobranzaFragment;
import com.legado.preventagps.fragments.PreventaFragment;
import com.roughike.bottombar.BottomBar;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class MyDialogAlertaMayorista extends DialogFragment{

        static BottomBar bottomBarDialog;
    static ActionBar actionBarDialog;
    MyDialogInfo.DialogInfoListener dialogInfoListener;

    public void setDialogInfoListener(MyDialogInfo.DialogInfoListener dialogInfoListener) {
        this.dialogInfoListener = dialogInfoListener;
    }

    public static MyDialogAlertaMayorista newInstance(BottomBar bottomBar, ActionBar actionBar) {
            MyDialogAlertaMayorista fragment= new MyDialogAlertaMayorista();
            bottomBarDialog=bottomBar;
            actionBarDialog=actionBar;
            return  fragment;
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_dialog_alert_mayorista, container, false);
        final LottieAnimationView animationView= (LottieAnimationView) rootView.findViewById(R.id.alert);
      animationView.loop(false);
        animationView.playAnimation();
       final Button continuar = (Button) rootView.findViewById(R.id.continuar);
        final Button dismiss = (Button) rootView.findViewById(R.id.dismiss);
        final Button cobrar = (Button) rootView.findViewById(R.id.goCobranza);
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
                preventaFragment.setArguments(args);
                dismiss();
                getFragmentManager().beginTransaction().replace(R.id.contenedorFragment,preventaFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            }
        });


        cobrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionBarDialog.setHomeAsUpIndicator(R.drawable.ic_menu);
                actionBarDialog.setTitle(
                        Html.fromHtml("<font color='#FFFFFF'>"
                                + "REALIZAR COBRANZA"
                                + "</font>"));
                actionBarDialog.setDisplayHomeAsUpEnabled(true);
                bottomBarDialog.setDefaultTab(R.id.tab_cobranza);
                bottomBarDialog.getTabWithId(R.id.tab_preventa).setEnabled(false);
                CobranzaFragment cobranzaFragment=new CobranzaFragment();
                cobranzaFragment.setArguments(getArguments());
                dismiss();
                getFragmentManager().beginTransaction().replace(R.id.contenedorFragment,cobranzaFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            }
        });
        return rootView;
    }


}
