package com.legado.preventagps.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.legado.preventagps.R;
import com.legado.preventagps.util.SessionUsuario;

import java.util.ArrayList;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class MyDialogEditSugerido extends DialogFragment {

    static ArrayList sugerido;

    private SessionUsuario sessionUsuario;
    private int position;
    SugeridoComunicador sugeridoComunicador;


  public void setSugeridoComunicador(SugeridoComunicador sugeridoComunicador){
      this.sugeridoComunicador=sugeridoComunicador;
  }
    public static MyDialogEditSugerido newInstance(ArrayList sugerido, int position) {
        MyDialogEditSugerido fragment = new MyDialogEditSugerido(sugerido,position);
        return fragment;
    }

    public MyDialogEditSugerido(ArrayList sugerido,int position) {
        this.sugerido=sugerido;
        this.position=position;
     }
    EditText txtCantidad;
     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_dialog_edit, container, false);
        getDialog().setTitle("Edit Dialog");
        sessionUsuario=new SessionUsuario(getContext());
         txtCantidad = (EditText) rootView.findViewById(R.id.txtCantidadEdit);
        txtCantidad.setText(new Double(sugerido.get(2).toString()).intValue()+"");
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        txtCantidad.requestFocus();
        Button dismiss = (Button) rootView.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        Button btnActualizar = (Button) rootView.findViewById(R.id.btnActualizar);
         btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(validar()){
//               actualizar(getArguments());
//                }
                sugeridoComunicador.updateRecycler(position,txtCantidad.getText().toString());

                dismiss();

            }
        });


        return rootView;
    }

public interface SugeridoComunicador{
    void updateRecycler(int position,String newCantidad);
    void deleteItemRecycler(int position);
}

}
