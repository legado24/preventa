package com.legado.preventagps.dialogs;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.airbnb.lottie.LottieAnimationView;
import com.legado.preventagps.R;
import com.legado.preventagps.activities.vendedor.InicioActivity;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class MyDialogErrorInicio extends DialogFragment{

         public static MyDialogErrorInicio newInstance() {
            MyDialogErrorInicio fragment= new MyDialogErrorInicio();
             return  fragment;
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_dialog_error, container, false);
         getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.round_dialog);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final LottieAnimationView animationView= (LottieAnimationView) rootView.findViewById(R.id.error);
        final Bundle args=getArguments();
      animationView.loop(false);
        animationView.playAnimation();
       final ImageButton reintentar = (ImageButton) rootView.findViewById(R.id.reintentar);

        reintentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent0 = new Intent(getActivity(), InicioActivity.class);
//            intent.putExtra("provider", LocationManager.NETWORK_PROVIDER);
//            sessionUsuario.guardarProvider(LocationManager.NETWORK_PROVIDER);
                startActivity(intent0);
                getActivity().finish();

             }
        });

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(android.content.DialogInterface dialog, int keyCode,android.view.KeyEvent event) {
                if ((keyCode ==  android.view.KeyEvent.KEYCODE_BACK))
                {
                    Intent intent0 = new Intent(getActivity(), InicioActivity.class);
//            intent.putExtra("provider", LocationManager.NETWORK_PROVIDER);
//            sessionUsuario.guardarProvider(LocationManager.NETWORK_PROVIDER);
                    startActivity(intent0);
                    getActivity().finish();

//



                    return true; // pretend we've processed it
                }
                else
                    return false; // pass on to be processed as normal
            }
        });
        return rootView;
    }


}
