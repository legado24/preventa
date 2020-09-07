package com.legado.preventagps.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.legado.preventagps.R;
import com.legado.preventagps.adapter.vendedor.AutoCompleteSimpleAdapter;
import com.legado.preventagps.adapter.vendedor.DireccionRecyclerAdapter;
import com.legado.preventagps.adapter.vendedor.SpinnerSimpleAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.dialogs.MyDialogClienteSuccess;
import com.legado.preventagps.dialogs.MyDialogProgress;
import com.legado.preventagps.dialogs.MyDialogRegistrarCumpleaños;
import com.legado.preventagps.enums.CLIENTEENUM;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.ClienteAlta;
import com.legado.preventagps.modelo.LocalCliente;
import com.legado.preventagps.modelo.PaqueteAlta;
import com.legado.preventagps.util.CustomEditText;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.SimpleDividerItemDecorator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActualizarClienteFragment extends Fragment {
    private static final String TAG = ActualizarClienteFragment.class.getName();
    @BindView(R.id.txtDniRuc)
    CustomEditText txtDniRuc;
    @BindView(R.id.txtDescripcion)
    EditText txtDescripcion;
    @BindView(R.id.txtDireccFact)
    EditText txtDireccFact;

    @BindView(R.id.txtCodCliente)
    EditText txtCodCliente;
    @BindView(R.id.spinnerDpto)
    AutoCompleteTextView spinnerDpto;
    @BindView(R.id.spinnerProv)
    AutoCompleteTextView spinnerProv;
    @BindView(R.id.spinnerDist)
    AutoCompleteTextView spinnerDist;

    @BindView(R.id.scrollView1)
    ScrollView scrollView;

    @BindView(R.id.txtTelefono)
    EditText txtTelefono;

    @BindView(R.id.txtCelular)
    EditText txtCelular;

    @BindView(R.id.txtEmail)
    EditText txtEmail;

    @BindView(R.id.spinnerTipoCliente)
    Spinner spinnerTipoCliente;

    @BindView(R.id.spinnerGiroCliente)
    Spinner spinnerGiroCliente;
    @BindView(R.id.spinnerLp)
    Spinner spinnerLp;

    @BindView(R.id.opciones_sexo)
    RadioGroup opciones_sexo;


    @BindView(R.id.txtFechaNacimiento)
    TextView txtFechaNacimiento;

    @BindView(R.id.btnActualizarCliente)
    Button btnActualizarCliente;

    @BindView(R.id.recicladorDirecciones)
    RecyclerView recicladorDirecciones;
    MyDialogProgress progressConsulta;
    RecyclerView.LayoutManager lManagerPedido;
    DireccionRecyclerAdapter adapter;

    SessionUsuario sessionUsuario;

    private int year, month, day;
    MyDialogProgress dialogActualizar;
    public ActualizarClienteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_actualizar_cliente, container, false);
        ButterKnife.bind(this, rootView);
        getActivity().setTitle( Html.fromHtml("<font color='#FFFFFF'>"
                + "ACTUALIZAR CLIENTE"
                + "</font>"));
        inicializarFechaHoy();
        sessionUsuario=new SessionUsuario(getActivity());
        txtDniRuc.setEnabled(false);
        cargarDatosClienteExiste(getArguments().getString(CLIENTEENUM.CODCLIENTE.getClave()));
        btnActualizarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar()){
                    ClienteAlta clienteAlta=new ClienteAlta();
                    clienteAlta.setCodCliente(txtCodCliente.getText().toString());
                    clienteAlta.setDescripcion(txtDescripcion.getText().toString());
                    clienteAlta.setCodCanal(sessionUsuario.getPaqueteUsuario().getCodCanal());
                    ArrayList tc= (ArrayList) spinnerTipoCliente.getSelectedItem();
                    clienteAlta.setCodTipoCliente(tc.get(0).toString());
                    ArrayList giro= (ArrayList) spinnerGiroCliente.getSelectedItem();
                    clienteAlta.setCodGiroNegocio(giro.get(0).toString());
                    clienteAlta.setUbigeo(sessionUsuario.getPaqueteAlta().getCodDist());
                    clienteAlta.setDireccion(txtDireccFact.getText().toString());
                    clienteAlta.setReferencia("REFERENCIA");
                    int selectedId = opciones_sexo.getCheckedRadioButtonId();
                    RadioButton radioButton = (RadioButton) rootView.findViewById(selectedId);
                    clienteAlta.setSexo(radioButton.getText().toString());
                    clienteAlta.setFechaNacimiento(txtFechaNacimiento.getText().toString());
                    clienteAlta.setEmail(txtEmail.getText().toString());
                    clienteAlta.setTelefonoFijo(txtTelefono.getText().toString());
                    clienteAlta.setCelular(txtCelular.getText().toString());
                    clienteAlta.setUsuarioRegistra(sessionUsuario.getUsuario());
                    ArrayList lp= (ArrayList) spinnerLp.getSelectedItem();
                    clienteAlta.setCodLp(lp.get(0).toString());
                    List<LocalCliente> locales=adapter.getDirecciones();
                    clienteAlta.setLocales(locales);
                    Gson gson = new Gson();
                    String clienteJson = gson.toJson(clienteAlta);
                    Log.d("assas",clienteJson);
                     showDialog(clienteAlta);
                }

            }
        });

        spinnerDpto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Object[] item =(Object[]) adapterView.getItemAtPosition(position);
                PaqueteAlta paq=  sessionUsuario.getPaqueteAlta();
                if(paq==null){
                    paq=new PaqueteAlta();
                    paq.setCodDpto(item[0].toString());
                    paq.setDescDpto(item[1].toString());
                    sessionUsuario.guardarPaqueteAlta(paq);
                }

                loadSpinnerProv(item[0].toString());
                spinnerDist.setText("");
                spinnerProv.setText("");
                spinnerProv.requestFocus();
            }
        });

        spinnerProv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Object[] item =(Object[]) adapterView.getItemAtPosition(position);
                PaqueteAlta paq=  sessionUsuario.getPaqueteAlta();
                paq.setCodProv(item[0].toString());
                paq.setDescProv(item[1].toString());
                sessionUsuario.guardarPaqueteAlta(paq);
                loadSpinnerDistrito(item[0].toString());
                spinnerDist.setText("");
                spinnerDist.requestFocus();
            }
        });


        spinnerDist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Object[] item =(Object[]) adapterView.getItemAtPosition(position);
                PaqueteAlta paq=  sessionUsuario.getPaqueteAlta();
                paq.setCodDist(item[0].toString());
                paq.setDescDist(item[1].toString());
                sessionUsuario.guardarPaqueteAlta(paq);
                loadSpinnerDistrito(item[0].toString());

            }
        });

        spinnerLp.setEnabled(false);
        return  rootView;
    }


    public void showDialog(final ClienteAlta clientealta) {
        new AlertDialog.Builder(getContext())
                .setTitle("CONFIRMACION")
                .setMessage("Desea actualizar el Cliente?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        dialogActualizar = new MyDialogProgress();
                        dialogActualizar.show(ft, "dialog");
                        new Handler().postDelayed(new Runnable() {
                            @Override public void run() {
                                actualizarCliente(clientealta);
                            }
                        }, 1000);
                    }
                }).setNegativeButton(android.R.string.no, null).show();
    }

    private void actualizarCliente(ClienteAlta clienteAlta) {
        clienteAlta.setOperacion(3);
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
                    dialogActualizar.dismiss();

                }else{
                    dialogActualizar.dismiss();

                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                    MyDialogClienteSuccess dialogSuccess= new MyDialogClienteSuccess();
                        dialogSuccess.show(ft, "dialog");


                }
            }

            @Override
            public void onFailure(Call<JsonRespuesta<Integer>> call, Throwable t) {
                dialogActualizar.dismiss();
                Snackbar.make(getView(), "Problemas de conexion  ", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Cerrar", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).show();

            }
        });
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
             date = dateFormat.parse(yyyy+ "-" + (mm + 1) + "-" + dd );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outDate = dateFormat.format(date);

        return outDate;
    }
    public void inicializarFechaHoy(){
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -18);
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        txtFechaNacimiento.setText(getFormatoFecha(year,month,day));
    }


    private void cargarDatosClienteExiste(final String codCliente) {
        Log.wtf(TAG,codCliente);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        progressConsulta= new MyDialogProgress();
        progressConsulta.show(ft, "dialog");
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("nroDoc",codCliente);
        dataConsulta.put("usuario",sessionUsuario.getUsuario());
        Call<JsonRespuesta> dataActualizar = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().cargarDatosClienteUpdate(dataConsulta);
        dataActualizar.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if(response.body().getEstado()==1){
                    ArrayList lista= (ArrayList) response.body().getData();
                    ArrayList listaDatAux = (ArrayList) lista.get(0);
                    ArrayList listaDataCliente=(ArrayList) listaDatAux.get(0);
                    ArrayList listaRptaLocales = (ArrayList) lista.get(1);
                    List<LocalCliente> listaLocalesCliente =new ArrayList<>();
                    for (int i = 0; i <listaRptaLocales.size() ; i++) {
                        Gson gson=new Gson();
                        JsonObject jsonObject = gson.toJsonTree(listaRptaLocales.get(i)).getAsJsonObject();
                        LocalCliente lc=gson.fromJson(jsonObject, LocalCliente.class);
                        listaLocalesCliente.add(lc);
                    }
                    Log.wtf(TAG,listaDataCliente.toString());
                    txtTelefono.setText(listaDataCliente.get(3)==null?"":listaDataCliente.get(3).toString());
                    txtCelular.setText(listaDataCliente.get(4)==null?"":listaDataCliente.get(4).toString());
                    txtEmail.setText(listaDataCliente.get(7)==null?"":listaDataCliente.get(7).toString());
                     txtCodCliente.setText(codCliente);
                    txtDescripcion.setText(listaDataCliente.get(0).toString());
                    txtDireccFact.setText(listaDataCliente.get(1).toString());
                    spinnerDpto.setText(listaDataCliente.get(11).toString());
                    spinnerProv.setText(listaDataCliente.get(13).toString());
                    spinnerDist.setText(listaDataCliente.get(15).toString());
                    if(listaDataCliente.get(6)==null){
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        MyDialogRegistrarCumpleaños dialogDatosCumple = MyDialogRegistrarCumpleaños.newInstance();
                        Bundle args=getArguments();
                        args.putString(CLIENTEENUM.CODCLIENTE.getClave(),codCliente);
                        dialogDatosCumple.setArguments(args);
                        dialogDatosCumple.show(ft, "dialog");
                    }

                    txtFechaNacimiento.setText(listaDataCliente.get(6)==null?"":listaDataCliente.get(6).toString());

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

                    recicladorDirecciones.setHasFixedSize(true);
                    lManagerPedido = new LinearLayoutManager(getContext());
                    recicladorDirecciones.setLayoutManager(lManagerPedido);
                    //adapter=new DireccionRecyclerAdapter(listaLocalesCliente,(AppCompatActivity) getActivity());
                    recicladorDirecciones.setAdapter(adapter);
                    recicladorDirecciones.addItemDecoration(new SimpleDividerItemDecorator(getActivity()));
                    cargarDatosAlta(listaDataCliente);

                }else{

                }

            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                progressConsulta.dismiss();
            }
        });



    }
    private void cargarDatosAlta(final ArrayList listaDataCliente) {
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario",sessionUsuario.getUsuario());
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().cargarDatosAlta(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if(response.body().getEstado()==1){
                    ArrayList lista= (ArrayList) response.body().getData();

                    ArrayList listaDptos= (ArrayList) lista.get(0);
                    ArrayList listaGiros= (ArrayList) lista.get(1);
                    ArrayList listaTipos= (ArrayList) lista.get(2);
                    ArrayList listaLp= (ArrayList) lista.get(3);
                    AutoCompleteSimpleAdapter autoCompleteSimpleAdapter = new AutoCompleteSimpleAdapter(getContext(), R.layout.list_simplespinner_item, listaDptos);
                    spinnerDpto.setAdapter(autoCompleteSimpleAdapter);

                    for(int i=0;i<listaDptos.size();i++){
                        ArrayList dpto= (ArrayList) listaDptos.get(i);
                        if(dpto.get(0).toString().equals(listaDataCliente.get(15).toString())){
                            spinnerDpto.setSelection(i);
                            break;
                        }
                    }




                    SpinnerSimpleAdapter giroAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc,listaGiros);
                    giroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerGiroCliente.setAdapter(giroAdapter);
                    for(int j=0;j<listaGiros.size();j++){
                        ArrayList giro= (ArrayList) listaGiros.get(j);
                        if(giro.get(0).toString().equals(listaDataCliente.get(9).toString())){
                            spinnerGiroCliente.setSelection(j);
                            break;
                        }
                    }

                    SpinnerSimpleAdapter tipoAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc,listaTipos);
                    tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTipoCliente.setAdapter(tipoAdapter);
                    for(int i=0;i<listaTipos.size();i++){
                        ArrayList tipo= (ArrayList) listaTipos.get(i);
                        if(tipo.get(0).toString().equals(listaDataCliente.get(8).toString())){
                            spinnerTipoCliente.setSelection(i);
                            break;
                        }
                    }


                    SpinnerSimpleAdapter lpAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, listaLp);
                    lpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerLp.setAdapter(lpAdapter);
                     boolean encontro=false;
                    for(int i=0;i<listaLp.size();i++){
                        ArrayList lp= (ArrayList) listaLp.get(i);
                        if(lp.get(0).toString().equals(listaDataCliente.get(16).toString())){
                            spinnerLp.setSelection(i);
                            encontro=true;
                           break;
                        }
                    }
                    if(!encontro){
                        spinnerLp.setEnabled(false);
                        btnActualizarCliente.setEnabled(false);
                       ArrayList lpOther=new ArrayList();
                        lpOther.add(listaDataCliente.get(16).toString());
                        lpOther.add("LISTA NO EDITABLE");
                        listaLp.add(lpOther);
                        for (int i = 0; i <listaLp.size() ; i++) {
                            ArrayList lp= (ArrayList) listaLp.get(i);
                            if(lp.get(0).toString().equals(listaDataCliente.get(16).toString())){
                                spinnerLp.setSelection(i);
                                 break;
                            }
                        }
                    }else{
                        if(sessionUsuario.getPaqueteUsuario().getCodPerfil().equals("05")||sessionUsuario.getPaqueteUsuario().getCodPerfil().equals("03")){
                            spinnerLp.setEnabled(true);
                        }
                    }

                    RadioButton rMasculino=getView().findViewById(R.id.radio_masculino);
                    RadioButton rFemenino=getView().findViewById(R.id.radio_femenino);
                    if(rMasculino.getText().equals(listaDataCliente.get(5).toString())){
                        rMasculino.setChecked(true);
                        rFemenino.setChecked(false);

                    }else if (rFemenino.getText().equals(listaDataCliente.get(5).toString())){
                        rMasculino.setChecked(false);
                        rFemenino.setChecked(true);
                    }




                    String codDist=listaDataCliente.get(14).toString();
                    PaqueteAlta paqueteAlta=new PaqueteAlta();
                    paqueteAlta.setCodDist(codDist);
                    sessionUsuario.guardarPaqueteAlta(paqueteAlta);
                    loadSpinnerProvincia(codDist);

                    progressConsulta.dismiss();




                }else{


                }
                progressConsulta.dismiss();
            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                progressConsulta.dismiss();
            }
        });

    }
    private boolean validar() {
        boolean band = true;

        if (txtCodCliente.getText().toString().equals("")) {
            txtCodCliente.setError(Html.fromHtml("<font color='red'>CODIGO NECESARIO</font>"));
            band = false;
        } else {
            txtCodCliente.setError(null);
        }
        if (txtDireccFact.getText().toString().equals("")) {
            txtDireccFact.setError(Html.fromHtml("<font color='red'>Ingrese direccion.</font>"));
            band = false;
        } else {
            txtDireccFact.setError(null);
        }

        if (spinnerDpto.getText().toString().equals("")) {
            spinnerDpto.setError(Html.fromHtml("<font color='red'>Ingrese Departamento.</font>"));
            band = false;
        } else {
            spinnerDpto.setError(null);
        }
        if (spinnerProv.getText().toString().equals("")) {
            spinnerProv.setError(Html.fromHtml("<font color='red'>Ingrese Provincia.</font>"));
            band = false;
        } else {
            spinnerProv.setError(null);
        }
        if (spinnerDist.getText().toString().equals("")) {
            spinnerDist.setError(Html.fromHtml("<font color='red'>Ingrese Distrito.</font>"));
            band = false;
        } else {
            spinnerDist.setError(null);
        }


        return band;

    }

    private void loadSpinnerProvincia(final String codDistrito) {
        String codDep=codDistrito.substring(0,2);
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("tipoUbic", "PROVI");
        dataConsulta.put("filtro", codDep);
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().datosUbicacionGeo(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if(response.body().getEstado()==1){
                    AutoCompleteSimpleAdapter autoCompleteSimpleAdapter = new AutoCompleteSimpleAdapter(getContext(), R.layout.list_simplespinner_item, (ArrayList) response.body().getData());
                    spinnerProv.setAdapter(autoCompleteSimpleAdapter);
                    loadSpinnerDistrito(codDistrito.substring(0,4));

                }else{

                    CoordinatorLayout coordinatorLayout=(CoordinatorLayout)getView().findViewById(R.id.coordinatorLayout);
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Text", Snackbar.LENGTH_LONG);
                    View view = snackbar.getView();
                    CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    snackbar.show();

                }
            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
            }
        });
    }
    private void loadSpinnerDistrito(String codProv) {
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("filtro", codProv);
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().datosUbicacionGeo(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if(response.body().getEstado()==1){
                    AutoCompleteSimpleAdapter autoCompleteSimpleAdapter = new AutoCompleteSimpleAdapter(getContext(), R.layout.list_simplespinner_item, (ArrayList) response.body().getData());
                    spinnerDist.setAdapter(autoCompleteSimpleAdapter);
                }else{

                    Snackbar.make(getView(), response.body().getMensaje(), Snackbar.LENGTH_INDEFINITE).setAction("Cerrar", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    }).show();
                }
            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
            }
        });
    }

    private void loadSpinnerProv(String codDep) {
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("tipoUbic", "PROVI");
        dataConsulta.put("filtro", codDep);
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().datosUbicacionGeo(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if(response.body().getEstado()==1){
                    AutoCompleteSimpleAdapter autoCompleteSimpleAdapter = new AutoCompleteSimpleAdapter(getContext(), R.layout.list_simplespinner_item, (ArrayList) response.body().getData());
                    spinnerProv.setAdapter(autoCompleteSimpleAdapter);

                }else{

                    CoordinatorLayout coordinatorLayout=(CoordinatorLayout)getView().findViewById(R.id.coordinatorLayout);
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Text", Snackbar.LENGTH_LONG);
                    View view = snackbar.getView();
                    CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    snackbar.show();

                }
            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle( Html.fromHtml("<font color='#FFFFFF'>"
                + "ACTUALIZAR CLIENTE"
                + "</font>"));
    }
}
