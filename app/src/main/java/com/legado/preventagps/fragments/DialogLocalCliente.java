package com.legado.preventagps.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.legado.preventagps.R;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class DialogLocalCliente extends DialogFragment {
    private static final String TAG = "DialogLocalCliente";

    public static DialogLocalCliente newInstance() {

        DialogLocalCliente fragment = new DialogLocalCliente();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_dialog_local, container, false);

        return rootView;
    }

        @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

}
