package com.legado.preventagps.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.legado.preventagps.R;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.enums.CLIENTEENUM;
import com.legado.preventagps.fragments.ActualizarClienteFragment;
import com.legado.preventagps.fragments.DatosClienteFragment;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.ClienteAlta;
import com.legado.preventagps.modelo.LocalCliente;
import com.legado.preventagps.util.SessionUsuario;
import com.roughike.bottombar.BottomBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class MyDialogRegistrarCumpleaños extends DialogFragment{
    private static final String TAG = "MyDialogRegistrarCumpleaños";
    @BindView(R.id.txtFechaNacimiento)
    TextView txtFechaNacimiento;
    @BindView(R.id.btnRegistrar)
    Button btnRegistrar;
    @BindView(R.id.btnCerrar)
    Button btnCerrar;
    private int year, month, day;
    static BottomBar bottomBarDialog;
    SessionUsuario sessionUsuario;
    public static MyDialogRegistrarCumpleaños newInstance(BottomBar  bottomBar) {
        bottomBarDialog=bottomBar;
            MyDialogRegistrarCumpleaños fragment= new MyDialogRegistrarCumpleaños();
             return  fragment;
        }
    public static MyDialogRegistrarCumpleaños newInstance() {
        MyDialogRegistrarCumpleaños fragment= new MyDialogRegistrarCumpleaños();
        return  fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_dialog_registrarcumple, container, false);
        getDialog().setCanceledOnTouchOutside(false);
        ButterKnife.bind(this, rootView);
        sessionUsuario=new SessionUsuario(getActivity());
      //  getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final LottieAnimationView animationView= (LottieAnimationView) rootView.findViewById(R.id.alert);
        final Bundle args=getArguments();
        animationView.loop(false);
        animationView.playAnimation();
final String codCliente=getArguments().getString(CLIENTEENUM.CODCLIENTE.getClave());
        inicializarFechaHoy();

        txtFechaNacimiento.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      Dialog dialog=onCreateDialog(999);
                                                      if(dialog!=null){
                                                          dialog.show();
                                                      }
                                                  }
                                              }
        );
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClienteAlta clienteAlta=new ClienteAlta();
                clienteAlta.setCodCliente(codCliente);
                clienteAlta.setFechaNacimiento(txtFechaNacimiento.getText().toString());
                clienteAlta.setLocales(new ArrayList<LocalCliente>());
                Gson gson = new Gson();
                String clienteJson = gson.toJson(clienteAlta);
                Log.d("assas",clienteJson);
                registrarFechaCumple(clienteAlta);
            }
        });

        return rootView;
    }

    private void registrarFechaCumple(ClienteAlta clienteAlta) {
        clienteAlta.setOperacion(2);
        Call<JsonRespuesta<Integer>> call = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().registrarCliente(clienteAlta);
        call.enqueue(new Callback<JsonRespuesta<Integer>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<Integer>> call, Response<JsonRespuesta<Integer>> response) {
                if(response.body().getEstado()==-1){
                    Toast.makeText(getContext(),response.body().getMensaje(),Toast.LENGTH_LONG).show();
                    Snackbar.make(getView(), response.body().getMensaje(), Snackbar.LENGTH_INDEFINITE).setAction("Cerrar", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    }).show();

                }else{
                    dismiss();
                    if(bottomBarDialog==null){
                        ActualizarClienteFragment actualizarClienteFragment=new  ActualizarClienteFragment();
                        actualizarClienteFragment.setArguments(getArguments());
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                        ft.replace(R.id.contenedorFragmentAlta, actualizarClienteFragment, TAG);
                        ft.addToBackStack(null);
                        ft.commit();
                        dismiss();

                    }else {
                        DatosClienteFragment datosClienteFragment = DatosClienteFragment.newInstance(bottomBarDialog);
                        datosClienteFragment.setArguments(getArguments());
                        getFragmentManager().beginTransaction().replace(R.id.contenedorFragment, datosClienteFragment).
                                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

                    }
                }
            }

            @Override
            public void onFailure(Call<JsonRespuesta<Integer>> call, Throwable t) {
                Snackbar.make(getView(), "Problemas de conexion  ", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Cerrar", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).show();

            }
        });
    }

    public void inicializarFechaHoy(){
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -18);
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        txtFechaNacimiento.setText(getFormatoFecha(year,month,day));
    }
    public Dialog onCreateDialog(int id) {
        if (id == 999) {
            DatePickerDialog dpD= new DatePickerDialog(getActivity(),R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int yyyy, int mm, int dd) {
                    txtFechaNacimiento.setText( getFormatoFecha(yyyy,mm,dd));
                }
            }, year, month, day);

            dpD.getDatePicker().getTouchables().get(0).performClick();
            return  dpD;
        }
        return null;
    }
    public String getFormatoFecha(int yyyy,int mm,int dd) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            //date = dateFormat.parse(dd + "-" + (mm + 1) + "-" + yyyy);
            date = dateFormat.parse(yyyy+ "-" + (mm + 1) + "-" + dd );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outDate = dateFormat.format(date);

        return outDate;
    }
}
