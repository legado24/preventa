package com.legado.preventagps.dialogs;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.legado.preventagps.R;
import com.roughike.bottombar.BottomBar;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class MyDialogProgress extends DialogFragment{

static BottomBar bottomBarDialog;
        public static MyDialogProgress newInstance(BottomBar bottomBar) {
            MyDialogProgress fragment= new MyDialogProgress();
            bottomBarDialog=bottomBar;
            return  fragment;
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_dialog_loading, container, false);
        final LottieAnimationView animationView= (LottieAnimationView) rootView.findViewById(R.id.loadingData);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        animationView.loop(true);
        animationView.playAnimation();
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(android.content.DialogInterface dialog, int keyCode,android.view.KeyEvent event) {
                if ((keyCode ==  android.view.KeyEvent.KEYCODE_BACK))
                {


                    return true; // pretend we've processed it
                }
                else
                    return false; // pass on to be processed as normal
            }
        });

        return rootView;
    }



}
