package com.legado.preventagps.dialogs;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.airbnb.lottie.LottieAnimationView;
import com.legado.preventagps.R;
import com.legado.preventagps.fragments.DatosClienteFragment;
import com.legado.preventagps.fragments.PreventaFragment;
import com.roughike.bottombar.BottomBar;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class MyDialogErrorPreventa extends DialogFragment{

static BottomBar bottomBarDialog;
    static MyDialogProgress dialogPreventaError;

        public static MyDialogErrorPreventa newInstance(BottomBar bottomBar, MyDialogProgress dialogPreventa) {
            MyDialogErrorPreventa fragment= new MyDialogErrorPreventa();
            bottomBarDialog=bottomBar;
            dialogPreventaError=dialogPreventa;
            return  fragment;
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_dialog_error, container, false);
         getDialog().setCanceledOnTouchOutside(false);

       getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final LottieAnimationView animationView= (LottieAnimationView) rootView.findViewById(R.id.error);
        final Bundle args=getArguments();
      animationView.loop(false);
        animationView.playAnimation();
       final ImageButton reintentar = (ImageButton) rootView.findViewById(R.id.reintentar);

        reintentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPreventaError.dismiss();
                PreventaFragment preventaFragment=PreventaFragment.newInstance(bottomBarDialog);
                preventaFragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.contenedorFragment,preventaFragment).
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                dismiss();
             }
        });

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,android.view.KeyEvent event) {
                if ((keyCode ==  android.view.KeyEvent.KEYCODE_BACK))
                {

                    dialogPreventaError.dismiss();
                    PreventaFragment preventaFragment=PreventaFragment.newInstance(bottomBarDialog);
                    preventaFragment.setArguments(args);
                    getFragmentManager().beginTransaction().replace(R.id.contenedorFragment,preventaFragment).
                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                    dismiss();
                    return true; // pretend we've processed it
                }
                else
                    return false; // pass on to be processed as normal
            }
        });



        return rootView;
    }


}
