package com.legado.preventagps.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.legado.preventagps.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardFragment extends Fragment {


     private CardView mCardView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

       View view=inflater.inflate(R.layout.fragment_adapter, container, false);
       mCardView=(CardView)mCardView.findViewById(R.id.cardView);



        return inflater.inflate(R.layout.fragment_adapter, container, false);
    }

    public CardView getmCardView(){
        return mCardView;
    }
}
