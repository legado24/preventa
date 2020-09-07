package com.legado.preventagps.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.legado.preventagps.R;
import com.roughike.bottombar.BottomBar;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class MyDialogCalculando extends DialogFragment{

 private String texto;
        public static MyDialogCalculando newInstance(String texto) {
            MyDialogCalculando fragment= new MyDialogCalculando(texto);
             return  fragment;
        }

    public MyDialogCalculando(String texto) {
            this.texto=texto;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_dialog_calculando, container, false);
        final LottieAnimationView animationView= (LottieAnimationView) rootView.findViewById(R.id.loadingData);
        TextView txtLoading=rootView.findViewById(R.id.txtLoading);
        txtLoading.setText(texto);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        animationView.loop(true);
        animationView.playAnimation();
        return rootView;
    }


}
