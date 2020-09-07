package com.legado.preventagps.fragments;


import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.legado.preventagps.R;
import com.legado.preventagps.adapter.vendedor.SpinnerSimpleAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.ClienteAlta;
import com.legado.preventagps.modelo.PaqueteAlta;
import com.legado.preventagps.util.SessionUsuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Alta2Fragment extends Fragment {
    @BindView(R.id.spinnerSede)
    Spinner spinnerSede;
    @BindView(R.id.spinnerEmpresa)
    Spinner spinnerEmpresa;
    @BindView(R.id.spinnerTipoCliente)
    Spinner spinnerTipoCliente;
    @BindView(R.id.spinnerGiros)
    Spinner spinnerGiros;

    @BindView(R.id.spinnerLp)
    Spinner spinnerLp;

    @BindView(R.id.btnRegistrar)
    Button btnRegistrar;

    SessionUsuario sessionUsuario;
    public Alta2Fragment() {
        // Required empty public constructor
    }
    public static Alta2Fragment newInstance(String param1, String param2) {
        Alta2Fragment fragment = new Alta2Fragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alta2, container, false);
        ButterKnife.bind(this, rootView);
        sessionUsuario=new SessionUsuario(getContext());

        loadDatosUsuarioAlta();
        loadSpinnerTipoCliente();
        loadSpinnerGiroCliente();

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClienteAlta clienteAlta=new ClienteAlta();
                clienteAlta.setCodCliente(sessionUsuario.getPaqueteAlta().getCodCliente());

                if(sessionUsuario.getPaqueteAlta().getDniRuc().length()==11){
                    clienteAlta.setRuc(sessionUsuario.getPaqueteAlta().getDniRuc());
                }else{
                    clienteAlta.setDni(sessionUsuario.getPaqueteAlta().getDniRuc());
                }

                clienteAlta.setDescripcion(sessionUsuario.getPaqueteAlta().getDescCliente());
                clienteAlta.setCodCanal("01");
                clienteAlta.setCodTipoCliente("01");
                clienteAlta.setCodGiroNegocio("01");
                clienteAlta.setUbigeo(sessionUsuario.getPaqueteAlta().getCodDist());
                clienteAlta.setDireccion(sessionUsuario.getPaqueteAlta().getDirFact());
                clienteAlta.setReferencia("REFERENCIA");
                clienteAlta.setSexo("V");
                clienteAlta.setFechaNacimiento("1986-01-24");
                clienteAlta.setEmail("aaa");
                clienteAlta.setTelefonoFijo(sessionUsuario.getPaqueteAlta().getTelefono());
                clienteAlta.setCelular(sessionUsuario.getPaqueteAlta().getCelular());
                clienteAlta.setUsuarioRegistra("ADRIAN");



                Gson gson = new Gson();
                String clienteJson = gson.toJson(clienteAlta);
                Log.d("assas",clienteJson);

                  registrarCliente(clienteAlta);


            }
        });
        return  rootView;
    }

    private void loadDatosUsuarioAlta() {
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario", sessionUsuario.getUsuario());
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getVendedorService().datosUsuarioAlta(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if(response.body().getEstado()==1){
                    ArrayList objeto= (ArrayList) response.body().getData();

                    ArrayList listaSedes=  (ArrayList) objeto.get(0);
                    ArrayList listaCanales=  (ArrayList) objeto.get(1);
                    ArrayList listaEmpresas=  (ArrayList) objeto.get(2);
                    for (int i = 0; i <listaSedes.size() ; i++) {
                        Log.d("DATOS SEDES", " datos=>>>" + ((ArrayList) listaSedes.get(0)).get(0));
                        Log.d("DATOS SEDES", " datos=>>>" + ((ArrayList) listaSedes.get(0)).get(1));
                    }
                    SpinnerSimpleAdapter empresaAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, listaEmpresas);
                    empresaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerEmpresa.setAdapter(empresaAdapter);


                    SpinnerSimpleAdapter sedeAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, listaSedes);
                    sedeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSede.setAdapter(sedeAdapter);
                    spinnerSede.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            ArrayList obj = (ArrayList) parentView.getItemAtPosition(position);
                            PaqueteAlta paq=  sessionUsuario.getPaqueteAlta();
                           // paq.setCodEmpresa(codEmpresaSelected[0]);
                          //  paq.setCodSede(obj.get(0).toString());
                            sessionUsuario.guardarPaqueteAlta(paq);

                            ArrayList objEmpresa = (ArrayList) spinnerEmpresa.getSelectedItem();
                            System.out.println("aaaa"+objEmpresa.get(0).toString());

                            loadSpinnerLp(objEmpresa.get(0).toString(),obj.get(0).toString());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }
                    });

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
    private void loadSpinnerTipoCliente() {
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().listTipoClientes();
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if(response.body().getEstado()==1){
                    SpinnerSimpleAdapter tipoAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, (ArrayList) response.body().getData());
                    tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTipoCliente.setAdapter(tipoAdapter);

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
    private void loadSpinnerGiroCliente() {
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().listGiroClientes();
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if(response.body().getEstado()==1){
                    SpinnerSimpleAdapter tipoAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, (ArrayList) response.body().getData());
                    tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerGiros.setAdapter(tipoAdapter);

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

//                showLoginError(t.getMessage());
            }
        });
    }
    private void loadSpinnerLp(String codEmpresa,String codSede) {
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("codEmpresa", codEmpresa);
        dataConsulta.put("codSede", codSede);
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().listLp(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if(response.body().getEstado()==1){
                    SpinnerSimpleAdapter tipoAdapter = new SpinnerSimpleAdapter(getActivity(), R.id.desc, (ArrayList) response.body().getData());
                    tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerLp.setAdapter(tipoAdapter);
                    spinnerLp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            ArrayList obj = (ArrayList) parentView.getItemAtPosition(position);
                            PaqueteAlta paq=  sessionUsuario.getPaqueteAlta();
                            paq.setCodLista(obj.get(0).toString());
                            sessionUsuario.guardarPaqueteAlta(paq);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }
                    });





                }else{
//                    Snackbar.make(getView(), response.body().getMensaje(), Snackbar.LENGTH_INDEFINITE).setAction("Cerrar", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                        }
//                    }).show();

                }
            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {

//                showLoginError(t.getMessage());
            }
        });
    }
    public void registrarCliente(ClienteAlta clientealta){
        Call<JsonRespuesta<Integer>> call = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getClienteService().registrarCliente(clientealta);
        call.enqueue(new Callback<JsonRespuesta<Integer>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<Integer>> call, Response<JsonRespuesta<Integer>> response) {
                if(response.body().getEstado()==-1){
                    Snackbar.make(getView(), response.body().getMensaje(), Snackbar.LENGTH_INDEFINITE).setAction("Cerrar", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    }).show();

                }else{

                    Toast.makeText(getActivity(), "Se Registró cliente el pedido",Toast.LENGTH_LONG).show();

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
}
