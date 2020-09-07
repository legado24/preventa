package com.legado.preventagps.dialogs.supervisor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.legado.preventagps.R;
import com.legado.preventagps.dialogs.MyDialogInfo;
import com.legado.preventagps.fragments.CarritoFragment;
import com.legado.preventagps.fragments.supervisor.CarritoByVendedorFragment;
import com.legado.preventagps.modelo.CarritoCompras;
import com.legado.preventagps.modelo.PaqueteCarrito;
import com.legado.preventagps.util.SessionUsuario;

import java.math.BigDecimal;
import java.util.List;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class DialogEditByVendedorCarrito extends DialogFragment {
    private static final String TAG = "MyDialogEdit";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    static CarritoByVendedorFragment carritoFragmentEdit;
    static CarritoCompras carritoComprasDialog;
    static TextView titleListDialog;
    static Spinner spinnerVendFindDialog;
    static Spinner spinnerCondFindDialog;
    static Spinner spinnerAlmacenesFindDialog;
    private SessionUsuario sessionUsuario;
    static int positionDialog;
    static TextView txtMontoDialog;
    MyDialogInfo.DialogInfoListener dialogInfoListener;

    public void setDialogInfoListener(MyDialogInfo.DialogInfoListener dialogInfoListener) {
        this.dialogInfoListener = dialogInfoListener;
    }

    public static DialogEditByVendedorCarrito newInstance(CarritoCompras carritoCompras, int position , CarritoByVendedorFragment carritoFragment, TextView titleList, Spinner spinnerVendFind, Spinner spinnerCondFind, TextView txtMonto, Spinner spinnerAlmacenFind) {
        carritoComprasDialog=carritoCompras;
        titleListDialog=titleList;
        spinnerVendFindDialog=spinnerVendFind;
        spinnerCondFindDialog=spinnerCondFind;
        positionDialog=position;
        txtMontoDialog=txtMonto;
        spinnerAlmacenesFindDialog=spinnerAlmacenFind;
        DialogEditByVendedorCarrito fragment = new DialogEditByVendedorCarrito();
        return fragment;
    }

  EditText txtCantidad;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_dialog_edit, container, false);
        getDialog().setTitle("Edit Dialog");
        sessionUsuario=new SessionUsuario(getContext());
        final Bundle args =getArguments();
         args.putString(ARG_PARAM1, carritoComprasDialog.getCantidad().toString());
        args.putString(ARG_PARAM2, carritoComprasDialog.getArticulo().getCodItem());
        args.putInt(ARG_PARAM3, positionDialog);
        txtCantidad = (EditText) rootView.findViewById(R.id.txtCantidadEdit);
        txtCantidad.setText(getArguments().getString(ARG_PARAM1));
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
                if(validar()){
                    actualizar(args);
                }




            }
        });

        return rootView;
    }

    private void actualizar(Bundle args) {
        List<CarritoCompras>lista= sessionUsuario.getPaqueteCarrito().getListaCarrito();
        for (int i = 0; i <lista.size() ; i++) {
            if(carritoComprasDialog.getArticulo().getCodItem().equals(lista.get(i).getArticulo().getCodItem())){
                lista.get(i).setCantidad(new Integer(txtCantidad.getText().toString()));
                BigDecimal cant=new BigDecimal(txtCantidad.getText().toString());
                lista.get(i).setImporte(cant.multiply(lista.get(i).getArticulo().getPrecioSugerido()));
                break;
            }
        }
        PaqueteCarrito paq=new PaqueteCarrito();
        paq.setListaCarrito(lista);
        sessionUsuario.guardarPaqueteCarrito(paq);
        BigDecimal montoVenta=new BigDecimal(0);
        for (int i = 0; i <lista.size() ; i++) {
            montoVenta=montoVenta.add(lista.get(i).getImporte());
        }
        txtMontoDialog.setText(montoVenta.toString());
        CarritoByVendedorFragment  carritoFragmentEdit=CarritoByVendedorFragment.newInstance(titleListDialog,spinnerVendFindDialog,spinnerCondFindDialog,spinnerAlmacenesFindDialog);
        carritoFragmentEdit.setArguments(args);
        carritoFragmentEdit.setDialogInfoListener(dialogInfoListener);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_carritocompras, carritoFragmentEdit,TAG);
        ft.commit();
        dismiss();
    }

    private boolean validar() {
        boolean band = true;
        if (txtCantidad.getText().toString().equals("")) {
            txtCantidad.setError("Cantidad");
            band = false;
        } else {
            if (new Integer(txtCantidad.getText().toString()).equals(0)) {
                txtCantidad.setError("DEBE SER MAYOR A 0");
                band = false;
            } else {
                txtCantidad.setError(null);
            }
        }
        return band;

    }

}
