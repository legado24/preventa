package com.legado.preventagps.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.common.internal.Objects;
import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.legado.preventagps.R;

import com.legado.preventagps.adapter.vendedor.DireccionRecyclerAdapter;
import com.legado.preventagps.adapter.vendedor.SpinnerSimpleAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.dialogs.MyDialogClienteSuccess;
import com.legado.preventagps.dialogs.MyDialogLocalMapaEdit;
import com.legado.preventagps.dialogs.MyDialogProgress;
import com.legado.preventagps.dialogs.MyDialogRegistrarCumpleaños;
import com.legado.preventagps.enums.CLIENTEENUM;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.ClienteAlta;
import com.legado.preventagps.modelo.LocalCliente;
import com.legado.preventagps.modelo.PaqueteAlta;

import com.legado.preventagps.util.CustomEditText;
import com.legado.preventagps.util.PaqueteUtilLocales;
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
import cn.pedant.SweetAlert.SweetAlertDialog;
import it.beppi.tristatetogglebutton_library.TriStateToggleButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AuditarClienteFragment extends Fragment  implements MyDialogLocalMapaEdit.OnDialogFinishCallback , MyDialogClienteSuccess.OnDialogFinishCliente {
    private static final String TAG = AuditarClienteFragment.class.getName();
    //    @BindView(R.id.txtDniRuc)
//    CustomEditText txtDniRuc;
    @BindView(R.id.txtCodCliente)
    EditText txtCodCliente;
    @BindView(R.id.txtDescCliente)
    EditText txtDescCliente;


    @BindView(R.id.spinnerDirFormato1)
    Spinner spinnerDirFormato1;
    @BindView(R.id.txtDireccion1)
    EditText txtDireccion1;

    @BindView(R.id.txtDireccion2)
    EditText txtDireccion2;
    @BindView(R.id.txtDireccion3)
    EditText txtDireccion3;
    @BindView(R.id.txtDireccion4)
    EditText txtDireccion4;

    @BindView(R.id.spinnerDirFormato2)
    Spinner spinnerDirFormato2;
    @BindView(R.id.txtDireccion5)
    EditText txtDireccion5;
    @BindView(R.id.txtDireccion6)
    EditText txtDireccion6;


    @BindView(R.id.txtReferencia)
    EditText txtReferencia;
    @BindView(R.id.spinnerObservaciones)
    Spinner spinnerObservaciones;

    @BindView(R.id.estadoCliente)
    TriStateToggleButton estadoCliente;
    @BindView(R.id.txtTelefono)
    EditText txtTelefono;
    @BindView(R.id.txtCelular)
    EditText txtCelular;
    @BindView(R.id.txtEmail)
    EditText txtEmail;
    @BindView(R.id.swDeuda)
    Switch swDeuda;
    @BindView(R.id.spinnerSexo)
    Spinner spinnerSexo;
    @BindView(R.id.txtFechaFacturacion)
    TextView txtFechaFacturacion;
    @BindView(R.id.spinnerGiroNegocio)
    Spinner spinnerGiroNegocio;
    @BindView(R.id.spinnerTipoCliente)
    Spinner spinnerTipoCliente;
    @BindView(R.id.recicladorDirecciones)
    RecyclerView recicladorDirecciones;
    @BindView(R.id.scrollView1)
    ScrollView scrollView;
    @BindView(R.id.btnActualizarCliente)
    Button btnActualizarCliente;

    @BindView(R.id.txtDireccOld)
    TextView txtDireccOld;



    MyDialogProgress progressConsulta;
    RecyclerView.LayoutManager lManagerPedido;
    DireccionRecyclerAdapter adapter;

    SessionUsuario sessionUsuario;
    MyDialogProgress dialogActualizar;
    private int year, month, day;
    private String codCliente;
    private MyDialogLocalMapaEdit.OnDialogFinishCallback  listenerUpdate;
    private  MyDialogClienteSuccess.OnDialogFinishCliente listenerUpdateCliente;
    private boolean mSpinnerInitialized;

//    private ClienteAlta clienteAltaSession;

    public AuditarClienteFragment(String codigoCliente) {
        this.codCliente = codigoCliente;
    }

    public static AuditarClienteFragment newInstance(String codCliente) {

        AuditarClienteFragment fragment = new AuditarClienteFragment(codCliente);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_auditar_cliente, container, false);
        listenerUpdate=this;
        listenerUpdateCliente=this;
        ButterKnife.bind(this, rootView);
        getActivity().setTitle(Html.fromHtml("<font color='#FFFFFF'>"
                + "ACTUALIZAR CLIENTE"
                + "</font>"));
        sessionUsuario = new SessionUsuario(getActivity());
        txtCodCliente.setEnabled(false);
        txtDescCliente.setEnabled(false);

         cargarDatosClienteExiste(codCliente);
        toUpperDireccion();

        btnActualizarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar()){
                    ClienteAlta clienteAlta=new ClienteAlta();
                    clienteAlta.setCodCliente(txtCodCliente.getText().toString());
                    clienteAlta.setDescripcion(txtDescCliente.getText().toString());
                    clienteAlta.setCodCanal(sessionUsuario.getPaqueteUsuario().getCodCanal());
                    ArrayList tc= (ArrayList) spinnerTipoCliente.getSelectedItem();
                    clienteAlta.setCodTipoCliente(tc.get(0).toString());
                    ArrayList giro= (ArrayList) spinnerGiroNegocio.getSelectedItem();
                    clienteAlta.setCodGiroNegocio(giro.get(0).toString());
                    String formFinal="";
                    String dirFinal="";
                    if(!txtDireccion1.getText().toString().isEmpty()){
                        ArrayList form1= (ArrayList) spinnerDirFormato1.getSelectedItem();
                        formFinal=formFinal+"_"+form1.get(0).toString();
                        dirFinal=dirFinal+"_"+txtDireccion1.getText();
                    }else{
                        formFinal=formFinal+"_"+"XXXX";
                        dirFinal=dirFinal+"_"+"XXXX";
                    }
                    if(!txtDireccion2.getText().toString().isEmpty()){
                        formFinal=formFinal+"_"+"NRO.";
                        dirFinal=dirFinal+"_"+txtDireccion2.getText();
                    }else{
                        formFinal=formFinal+"_"+"XXXX";
                        dirFinal=dirFinal+"_"+"XXXX";
                    }
                    if(!txtDireccion3.getText().toString().isEmpty()){
                        formFinal=formFinal+"_"+"MZ.";
                        dirFinal=dirFinal+"_"+txtDireccion3.getText();
                    }else{
                        formFinal=formFinal+"_"+"MZ.";
                        dirFinal=dirFinal+"_"+"XXXX";
                    }
                    if(!txtDireccion4.getText().toString().isEmpty()){
                        formFinal=formFinal+"_"+"LT.";
                        dirFinal=dirFinal+"_"+txtDireccion4.getText();
                    }else{
                        formFinal=formFinal+"_"+"LT.";
                        dirFinal=dirFinal+"_"+"XXXX";
                    }

                    if(!txtDireccion5.getText().toString().isEmpty()){
                        formFinal=formFinal+"_"+"HU.";
                        dirFinal=dirFinal+"_"+txtDireccion5.getText();
                    }else{
                        formFinal=formFinal+"_"+"HU.";
                        dirFinal=dirFinal+"_"+"XXXX";
                    }

                    if(!txtDireccion6.getText().toString().isEmpty()){
                        ArrayList form2= (ArrayList) spinnerDirFormato2.getSelectedItem();
                        formFinal=formFinal+"_"+form2.get(0).toString();
                        dirFinal=dirFinal+"_"+txtDireccion6.getText();
                    }else{
                        formFinal=formFinal+"_"+"XXXX";
                        dirFinal=dirFinal+"_"+"XXXX";
                    }


                    clienteAlta.setFormatoDireccion(formFinal);
                    clienteAlta.setNewDirecion(dirFinal);

                    clienteAlta.setReferencia(txtReferencia.getText().toString());
                    ArrayList obs= (ArrayList) spinnerObservaciones.getSelectedItem();
                    clienteAlta.setObservaciones(obs.get(0).toString());

                    clienteAlta.setEmail(txtEmail.getText().toString());
                    clienteAlta.setTelefonoFijo(txtTelefono.getText().toString());
                    clienteAlta.setCelular(txtCelular.getText().toString());
                    clienteAlta.setUsuarioRegistra(sessionUsuario.getUsuario());

                    String status="";
                    if(estadoCliente.getToggleStatus().toString().equals("off")){
                        status="I";
                    }else if(estadoCliente.getToggleStatus().toString().equals("mid")){
                        status="P";
                    }else if(estadoCliente.getToggleStatus().toString().equals("on")){
                        status="A";
                    }
                    clienteAlta.setStatus(status);

                    List<LocalCliente> locales=adapter.getDirecciones();
                    clienteAlta.setLocales(locales);
                    Gson gson = new Gson();
                    String clienteJson = gson.toJson(clienteAlta);
                    Log.wtf("assas",clienteJson);
                    showDialog(clienteAlta);
                }

            }
        });


        return rootView;
    }



    private void cargarSpinnerTipos(String codGiro,String codTipo){
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("codGiro", codGiro);
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().getTiposByGiro(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if (response.body().getEstado() == 1) {
                    ArrayList lista = (ArrayList) response.body().getData();



                    ArrayList listaTiposMod=new ArrayList();
                    ArrayList itemSeleccione=new ArrayList();
                    itemSeleccione.add("XXXX");
                    itemSeleccione.add("-SELECCIONE-");
                    listaTiposMod.add(itemSeleccione);
                    listaTiposMod.addAll(lista);

                    SpinnerSimpleAdapter autoCompleteSimpleAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, listaTiposMod);
                    spinnerTipoCliente.setAdapter(autoCompleteSimpleAdapter);
                    for(int j=0;j<listaTiposMod.size();j++){
                        ArrayList tipo =  ((ArrayList) listaTiposMod.get(j));
                        if(tipo.get(0).equals(codTipo)){
                            spinnerTipoCliente.setSelection(j);
                            break;
                        }
                    }


                }
            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
            }
        });
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
                            @Override
                            public void run() {
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
                if (response.body().getEstado() == -1) {
                    Toast.makeText(getContext(), response.body().getMensaje(), Toast.LENGTH_LONG).show();
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText( response.body().getMensaje())
                            .show();


                    dialogActualizar.dismiss();

                } else {
                    dialogActualizar.dismiss();

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    MyDialogClienteSuccess dialogSuccess = new MyDialogClienteSuccess(clienteAlta.getCodCliente(),listenerUpdateCliente);
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
            DatePickerDialog dpD = new DatePickerDialog(getActivity(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int yyyy, int mm, int dd) {
                    txtFechaFacturacion.setText(getFormatoFecha(yyyy, mm, dd));
                }
            }, year, month, day);

            dpD.getDatePicker().getTouchables().get(0).performClick();
            return dpD;
        }
        return null;
    }

    public String getFormatoFecha(int yyyy, int mm, int dd) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            //date = dateFormat.parse(dd + "-" + (mm + 1) + "-" + yyyy);
            date = dateFormat.parse(yyyy + "-" + (mm + 1) + "-" + dd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outDate = dateFormat.format(date);

        return outDate;
    }


    private void toUpperDireccion() {
        txtDireccion1.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    txtDireccion1.setText(s);
                }
                txtDireccion1.setSelection(txtDireccion1.getText().length());
            }
        });
        txtDireccion2.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    txtDireccion2.setText(s);
                }
                txtDireccion2.setSelection(txtDireccion2.getText().length());
            }
        });
        txtDireccion3.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    txtDireccion3.setText(s);
                }
                txtDireccion3.setSelection(txtDireccion3.getText().length());
            }
        });
        txtDireccion4.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    txtDireccion4.setText(s);
                }
                txtDireccion4.setSelection(txtDireccion4.getText().length());
            }
        });
        txtDireccion5.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    txtDireccion5.setText(s);
                }
                txtDireccion5.setSelection(txtDireccion5.getText().length());
            }
        });


        txtDireccion6.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    txtDireccion6.setText(s);
                }
                txtDireccion6.setSelection(txtDireccion6.getText().length());
            }
        });
        txtReferencia.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    txtReferencia.setText(s);
                }
                txtReferencia.setSelection(txtReferencia.getText().length());
            }
        });
    }



    private void cargarDatosClienteExiste(final String codCliente) {
        Log.wtf(TAG, codCliente);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        progressConsulta = new MyDialogProgress();
        progressConsulta.show(ft, "dialog");
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario", sessionUsuario.getPaqueteUsuario().getUsuario());
        dataConsulta.put("nroDoc", codCliente);
        Call<JsonRespuesta> dataActualizar = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().cargarDatosClienteUpdate(dataConsulta);
        dataActualizar.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if (response.body().getEstado() == 1) {
                    ArrayList lista = (ArrayList) response.body().getData();
                    ArrayList listaDatAux = (ArrayList) lista.get(0);
                    ArrayList listaDataCliente = (ArrayList) listaDatAux.get(0);
                    ArrayList listaRptaLocales = (ArrayList) lista.get(1);
                    ArrayList listaRptaGiros = (ArrayList) lista.get(2);
                    // ArrayList listaRptaTipos = (ArrayList) lista.get(3);
                    ArrayList listaRptaFormDir = (ArrayList) lista.get(4);
                    ArrayList listaRptaObserv = (ArrayList) lista.get(5);
                    List<LocalCliente> listaLocalesCliente = new ArrayList<>();
                    for (int i = 0; i < listaRptaLocales.size(); i++) {
                        Gson gson = new Gson();
                        JsonObject jsonObject = gson.toJsonTree(listaRptaLocales.get(i)).getAsJsonObject();
                        LocalCliente lc = gson.fromJson(jsonObject, LocalCliente.class);
                        listaLocalesCliente.add(lc);
                    }

                    ArrayList forms1 = new ArrayList<>();
                    ArrayList forms2 = new ArrayList<>();
                    for (int i = 0; i < listaRptaFormDir.size(); i++) {
                        ArrayList form = (ArrayList) listaRptaFormDir.get(i);
                        if (form.get(2).toString().equals("1.0")) {
                            ArrayList obj = new ArrayList();
                            obj.add(form.get(0).toString());
                            obj.add(form.get(1).toString());

                            forms1.add(obj);
                        } else if (form.get(2).toString().equals("2.0")) {
                            ArrayList obj = new ArrayList();
                            obj.add(form.get(0).toString());
                            obj.add(form.get(1).toString());
                            forms2.add(obj);
                        }

                    }

                    PaqueteUtilLocales pul=new PaqueteUtilLocales();
                    pul.setObservaciones(listaRptaObserv);
                    pul.setFormato1(forms1);
                    pul.setFormato2(forms2);
                    sessionUsuario.guardarPaqueteUtilLocales(null);
                    sessionUsuario.guardarPaqueteUtilLocales(pul);

                    txtCodCliente.setText(codCliente);
                    txtDescCliente.setText(listaDataCliente.get(0).toString());
                    txtTelefono.setText(listaDataCliente.get(3) == null ? "" : listaDataCliente.get(3).toString());
                    txtCelular.setText(listaDataCliente.get(4) == null ? "" : listaDataCliente.get(4).toString());
                    txtEmail.setText(listaDataCliente.get(7) == null ? "" : listaDataCliente.get(7).toString());
                    txtReferencia.setText(listaDataCliente.get(18) == null ? "" : listaDataCliente.get(18).toString());
                    txtDireccOld.setText(listaDataCliente.get(1).toString());
                    cargarSpinnersObservaciones(listaRptaObserv, listaDataCliente.get(19)==null?"XXXX":listaDataCliente.get(19).toString());
                    cargarSpinnersGenero(listaDataCliente.get(5).toString());
                    if (listaDataCliente.get(17).toString().equals("A")) {
                        //estadoCliente.setToggleOn(true);
                        estadoCliente.setToggleOn();
                        estadoCliente.setOnColor(getResources().getColor(R.color.material_green_700));
//                        estadoCliente.setSpotColor(getResources().getColor(R.color.material_red_A200));
                        estadoCliente.setBackgroundColor(getResources().getColor(R.color.material_green_700));
                    } else if (listaDataCliente.get(17).toString().equals("P")) {
                        //  estadoCliente.setToggleMid(true);
                        estadoCliente.setToggleMid();
                        estadoCliente.setMidColor(getResources().getColor(R.color.material_blue_600));
                        estadoCliente.setBackgroundColor(getResources().getColor(R.color.material_blue_600));
                    } else if (listaDataCliente.get(17).toString().equals("I")) {
                        //estadoCliente.setToggleOff(true);
                        estadoCliente.setToggleOff();
                        estadoCliente.setOffColor(getResources().getColor(R.color.material_red_A200));
                        estadoCliente.setBackgroundColor(getResources().getColor(R.color.material_red_A200));
                    }
                    if (new Double(listaDataCliente.get(20).toString()) > 0) {
                        swDeuda.setChecked(true);
                        estadoCliente.setEnabled(false);
                    }

                    ArrayList listaRptaGirosMod=new ArrayList();
                    if(listaDataCliente.get(8)==null){// SI TIPO ES NULO
                        ArrayList itemSeleccione=new ArrayList();
                        itemSeleccione.add("XXXX");
                        itemSeleccione.add("-SELECCIONE-");
                        listaRptaGirosMod.add(itemSeleccione);
                        for (int i = 0; i <listaRptaGiros.size() ; i++) {
                            listaRptaGirosMod.add(listaRptaGiros.get(i));
                        }
                    }else{
                        listaRptaGirosMod.addAll(listaRptaGiros);
                    }



                    SpinnerSimpleAdapter giroAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, listaRptaGirosMod);
                    giroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerGiroNegocio.setAdapter(giroAdapter);

                    String codGiroSeleccionado="";
                    if(listaDataCliente.get(9)!=null) {// SI GIRO DIFIERE D ENULO
                        codGiroSeleccionado=listaDataCliente.get(9).toString();
                    }else{
                        codGiroSeleccionado="XXXX";

                    }
//
                    for(int j=0;j<listaRptaGirosMod.size();j++){
                        ArrayList giro =  ((ArrayList) listaRptaGirosMod.get(j));
                        if(giro.get(0).equals(codGiroSeleccionado)){
                            spinnerGiroNegocio.setSelection(j);
                            break;
                        }
                    }

                    spinnerGiroNegocio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                            ArrayList a = (ArrayList) spinnerGiroNegocio.getSelectedItem();//(Object[]) adapterView.getItemAtPosition(i);
                            if(a.get(0).toString().equals("XXXX")){
                                cargarSpinnerTipos("","");
                            }else{
                                cargarSpinnerTipos(a.get(0).toString(),listaDataCliente.get(8)!=null?listaDataCliente.get(8).toString():"");
                            }

                        }

                        public void onNothingSelected(AdapterView<?> adapterView) {
                            return;
                        }
                    });



                    if(listaDataCliente.get(22)!=null){
                        String[] arrForm=listaDataCliente.get(22).toString().split("_");
                        String[] arrValForm=listaDataCliente.get(23).toString().split("_");


                        txtDireccion1.setText(arrValForm[1].toString().equals("XXXX")?"":arrValForm[1].toString());

                        cargarSpinnersFormato1(forms1, arrForm[1].toString());
                        cargarSpinnersFormato2(forms2, arrForm[6].toString());

                        txtDireccion2.setText(arrValForm[2].toString().equals("XXXX")?"":arrValForm[2].toString());
                        txtDireccion3.setText(arrValForm[3].toString().equals("XXXX")?"":arrValForm[3].toString());
                        txtDireccion4.setText(arrValForm[4].toString().equals("XXXX")?"":arrValForm[4].toString());
                        txtDireccion5.setText(arrValForm[5].toString().equals("XXXX")?"":arrValForm[5].toString());
                        txtDireccion6.setText(arrValForm[6].toString().equals("XXXX")?"":arrValForm[6].toString());

                    }else{
                        cargarSpinnersFormato1(forms1,"");
                        cargarSpinnersFormato2(forms2,"");
                    }


                    txtFechaFacturacion.setText(listaDataCliente.get(21)==null?"":listaDataCliente.get(21).toString());


                    recicladorDirecciones.setHasFixedSize(true);
                    lManagerPedido = new LinearLayoutManager(getContext());
                    recicladorDirecciones.setLayoutManager(lManagerPedido);
                    adapter = new DireccionRecyclerAdapter(listaLocalesCliente, codCliente, (AppCompatActivity) getActivity(),listenerUpdate);
                    recicladorDirecciones.setAdapter(adapter);
                  recicladorDirecciones.addItemDecoration(new SimpleDividerItemDecorator(getActivity()));
////

                } else {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.txtMensajeServidorCaido))
                            .show();
                }
                progressConsulta.dismiss();
            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.txtMensajeConexion))
                        .show();
                progressConsulta.dismiss();
            }
        });

    }



    private boolean validar() {
        boolean band = true;

        if (txtReferencia.getText().toString().equals("")) {
            txtReferencia.setError(Html.fromHtml("<font color='red'>Ingrese referencia.</font>"));
            band = false;
        } else {
            txtReferencia.setError(null);
        }

        ArrayList obs= (ArrayList) spinnerObservaciones.getSelectedItem();
        LinearLayout ll = (LinearLayout) spinnerObservaciones.getSelectedView(); // get the parent layout view
        TextView tv = (TextView) ll.getChildAt(0); // get the child text view
        if (obs.get(0).toString().equals("XXXX")) {
            tv.setError(Html.fromHtml("<font color='red'>Seleccione observación</font>"));
            band = false;
        } else {
            tv.setError(null);
        }

        ArrayList giro= (ArrayList) spinnerGiroNegocio.getSelectedItem();
        LinearLayout lgiro = (LinearLayout) spinnerGiroNegocio.getSelectedView(); // get the parent layout view
        TextView tvgiro = (TextView) lgiro.getChildAt(0); // get the child text view
        if (giro.get(0).toString().equals("XXXX")) {
            tvgiro.setError(Html.fromHtml("<font color='red'>Seleccione Giro</font>"));
            band = false;
        } else {
            tvgiro.setError(null);
        }


        ArrayList tipo= (ArrayList) spinnerTipoCliente.getSelectedItem();
        LinearLayout ltipo = (LinearLayout) spinnerTipoCliente.getSelectedView(); // get the parent layout view
        TextView tvtipo= (TextView) ltipo.getChildAt(0); // get the child text view
        if (tipo.get(0).toString().equals("XXXX")) {
            tvtipo.setError(Html.fromHtml("<font color='red'>Seleccione Tipo cliente</font>"));
            band = false;
        } else {
            tvtipo.setError(null);
        }



        return band;

    }



    private void cargarSpinnersGenero(String genero) {
        ArrayList<String> listarGenero = new ArrayList<>();
        listarGenero.add("M");
        listarGenero.add("F");
        ArrayAdapter simpleAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, listarGenero);
        spinnerSexo.setAdapter(simpleAdapter);
        for(int j=0;j<listarGenero.size();j++){
            if(listarGenero.get(j).equals(genero)){
                spinnerSexo.setSelection(j);
                break;
            }
        }
    }



    private void cargarSpinnersObservaciones(ArrayList listaObservaciones, String codValor) {

        ArrayList listaObservMod=new ArrayList();
        ArrayList itemSeleccione=new ArrayList();
        itemSeleccione.add("XXXX");
        itemSeleccione.add("-SELECCIONE-");
        listaObservMod.add(itemSeleccione);
        listaObservMod.addAll(listaObservaciones);

        SpinnerSimpleAdapter observacionAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, listaObservMod);
        spinnerObservaciones.setAdapter(observacionAdapter);
        for(int j=0;j<listaObservMod.size();j++){
            ArrayList obs = (ArrayList) listaObservMod.get(j);
            if(obs.get(0).equals(codValor)){
                spinnerObservaciones.setSelection(j);
                break;
            }
        }
    }


    private void cargarSpinnersFormato1(ArrayList listaFormato1, String codValor) {
        SpinnerSimpleAdapter formato1Adapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, listaFormato1);
        spinnerDirFormato1.setAdapter(formato1Adapter);

        for(int j=0;j<listaFormato1.size();j++){
            ArrayList form1 = (ArrayList) listaFormato1.get(j);
            if(form1.get(0).equals(codValor)){
                spinnerDirFormato1.setSelection(j);
                break;
            }
        }
    }

    private void cargarSpinnersFormato2(ArrayList listaFormato2, String codValor) {
        SpinnerSimpleAdapter formato1Adapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, listaFormato2);
        spinnerDirFormato2.setAdapter(formato1Adapter);
        for(int j=0;j<listaFormato2.size();j++){
            ArrayList form2 = (ArrayList) listaFormato2.get(j);
            if(form2.get(0).equals(codValor)){
                spinnerDirFormato2.setSelection(j);
                break;
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(Html.fromHtml("<font color='#FFFFFF'>"
                + "ACTUALIZAR CLIENTE"
                + "</font>"));
    }


    @Override
    public void refreshRecyclerView() {

        Log.wtf("as","LLAMAR AL ACTUALIZAR");
        Log.wtf(TAG, codCliente);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        progressConsulta = new MyDialogProgress();
        progressConsulta.show(ft, "dialog");
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario", sessionUsuario.getPaqueteUsuario().getUsuario());
        dataConsulta.put("nroDoc", codCliente);
        Call<JsonRespuesta> dataActualizar = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().cargarDatosClienteUpdate(dataConsulta);
        dataActualizar.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if (response.body().getEstado() == 1) {
                    ArrayList lista = (ArrayList) response.body().getData();

                    ArrayList listaRptaLocales = (ArrayList) lista.get(1);

                    List<LocalCliente> listaLocalesCliente = new ArrayList<>();
                    for (int i = 0; i < listaRptaLocales.size(); i++) {
                        Gson gson = new Gson();
                        JsonObject jsonObject = gson.toJsonTree(listaRptaLocales.get(i)).getAsJsonObject();
                        LocalCliente lc = gson.fromJson(jsonObject, LocalCliente.class);
                        listaLocalesCliente.add(lc);
                    }


                    recicladorDirecciones.setHasFixedSize(true);
                    lManagerPedido = new LinearLayoutManager(getContext());
                    recicladorDirecciones.setLayoutManager(lManagerPedido);
                    adapter = new DireccionRecyclerAdapter(listaLocalesCliente, codCliente, (AppCompatActivity) getActivity(),listenerUpdate);
                    recicladorDirecciones.setAdapter(adapter);
                    recicladorDirecciones.addItemDecoration(new SimpleDividerItemDecorator(getActivity()));
//

                } else {

                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.txtMensajeServidorCaido))
                            .show();

                }
                progressConsulta.dismiss();
            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.txtMensajeConexion))
                        .show();
                progressConsulta.dismiss();
            }
        });
    }

    @Override
    public void reloadDataCliente(String codCliente) {
        cargarDatosClienteExiste(codCliente);
    }
}
