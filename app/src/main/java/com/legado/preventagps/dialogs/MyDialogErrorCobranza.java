package com.legado.preventagps.dialogs;

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
import com.legado.preventagps.activities.vendedor.VoucherCobranzaActivity;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class MyDialogErrorCobranza extends DialogFragment{

         public static MyDialogErrorCobranza newInstance() {
            MyDialogErrorCobranza fragment= new MyDialogErrorCobranza();
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
                Intent intent0 = new Intent(getActivity(), VoucherCobranzaActivity.class);
                startActivity(intent0);
                getActivity().finish();

             }
        });


        return rootView;
    }


}
