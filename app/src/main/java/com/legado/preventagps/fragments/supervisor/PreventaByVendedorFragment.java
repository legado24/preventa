package com.legado.preventagps.fragments.supervisor;


import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.legado.preventagps.R;
import com.legado.preventagps.adapter.vendedor.SpinnerSimpleAdapter;
import com.legado.preventagps.adapter.vendedor.SpinnerVendedorCustomAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.dialogs.DialogFocusArticulo;
import com.legado.preventagps.dialogs.MyDialogErrorPreventa;
import com.legado.preventagps.dialogs.MyDialogInfo;
import com.legado.preventagps.dialogs.MyDialogProgress;
import com.legado.preventagps.enums.PREVENTAENUM;
import com.legado.preventagps.fragments.CarritoFragment;
import com.legado.preventagps.fragments.DialogFindArticulo;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Articulo;
import com.legado.preventagps.modelo.CarritoCompras;
import com.legado.preventagps.modelo.PaqueteCarrito;
import com.legado.preventagps.util.Metodos;
import com.legado.preventagps.util.SessionUsuario;
import com.legado.preventagps.util.UtilAndroid;
import com.pd.chocobar.ChocoBar;
import com.roughike.bottombar.BottomBar;
import com.tuanfadbg.snackalert.SnackAlert;

import java.math.BigDecimal;
import java.util.ArrayList;
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
public class PreventaByVendedorFragment extends Fragment {

    private static final String TAG = "PreventaByVendedorFragment";

    @BindView(R.id.descArticulo)
    ImageView descArticulo;
    @BindView(R.id.btnFoco)
    ImageView btnFoco;
    @BindView(R.id.spinnerVend)
    Spinner spinnerVend;
    @BindView(R.id.spinnerCond)
    Spinner spinnerCond;
    @BindView(R.id.spinnerAlmacenes)
    Spinner spinnerAlmacenes;
    @BindView(R.id.titleLista)
    TextView titleLista;

    MyDialogInfo.DialogInfoListener dialogInfoListener;
    MyDialogProgress dialogPreventa;
    static BottomBar bottomBarDialog;
    private SessionUsuario sessionUsuario;
    private  String correlativo;
    List<CarritoCompras> listaCarrito = new ArrayList<>();


    String codCliente;
    public void setDialogInfoListener(MyDialogInfo.DialogInfoListener dialogInfoListener) {
        this.dialogInfoListener = dialogInfoListener;
    }

    public static PreventaByVendedorFragment newInstance(BottomBar bottomBar) {
        PreventaByVendedorFragment fragment= new PreventaByVendedorFragment();
        bottomBarDialog = bottomBar;
        return  fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_preventa, container, false);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialogPreventa = new MyDialogProgress();
        dialogPreventa.show(ft, "dialog");
        correlativo=Metodos.generarCorrelativo();
        ButterKnife.bind(this,rootView);
        sessionUsuario=new SessionUsuario(getContext());
        sessionUsuario.guardarBandSugerido(true);
        codCliente=  getArguments().getString(PREVENTAENUM.CODCLIENTE.getClave());
        String descCliente=  getArguments().getString(PREVENTAENUM.DESCCLIENTE.getClave());
        String dirCliente=  getArguments().getString(PREVENTAENUM.DIRCLIENTE.getClave());
        final String codLista=  getArguments().getString(PREVENTAENUM.CODLISTA.getClave());
        final String codEmpresa= getArguments().getString(PREVENTAENUM.CODEMPRESA.getClave());
        String codLocal=  getArguments().getString(PREVENTAENUM.CODLOCAL.getClave());
        String codRuta=  getArguments().getString(PREVENTAENUM.CODRUTA.getClave());
        Integer secuencia= getArguments().getInt(PREVENTAENUM.SECUENCIA.getClave());
        final Integer tieneDeudaVencida=getArguments().getBoolean("deudavencida")==false?0:1;
         obtenerDatosUsuario(sessionUsuario.getUsuario(),codLista,tieneDeudaVencida,codCliente);//online
         buscarArticulo();
         agregarSugeridosBtnFoco();
         spinnerVend.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                 ArrayList objVend = (ArrayList) spinnerVend.getSelectedItem();
                 getArguments().putString(PREVENTAENUM.CODALMACEN.getClave(),objVend.get(10).toString());
                 PaqueteCarrito paqueteCarrito=new PaqueteCarrito();
                 paqueteCarrito.setListaCarrito(new ArrayList<CarritoCompras>());
                 sessionUsuario.guardarPaqueteCarrito(paqueteCarrito);
                 obtenerAlmacenes(objVend.get(0).toString(),objVend.get(8).toString());
                 obtenerCondicionesDepago(codLista,objVend.get(7).toString(),tieneDeudaVencida,codCliente);
             }

             @Override
             public void onNothingSelected(AdapterView<?> parentView) {
                 // your code here
             }
         });
         spinnerCond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 PaqueteCarrito paqueteCarrito=new PaqueteCarrito();
                 paqueteCarrito.setListaCarrito(new ArrayList<CarritoCompras>());
                 sessionUsuario.guardarPaqueteCarrito(paqueteCarrito);
//                correlativo=Metodos.generarCorrelativo();//AGREGRE
             }

             @Override
             public void onNothingSelected(AdapterView<?> adapterView) {

             }
         });
         spinnerAlmacenes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 ArrayList objAlm = (ArrayList) spinnerAlmacenes.getSelectedItem();
                 getArguments().putString(PREVENTAENUM.CODALMACEN.getClave(),objAlm.get(0).toString());
                 PaqueteCarrito paqueteCarrito=new PaqueteCarrito();
                 paqueteCarrito.setListaCarrito(new ArrayList<CarritoCompras>());
                 sessionUsuario.guardarPaqueteCarrito(paqueteCarrito);

             }

             @Override
             public void onNothingSelected(AdapterView<?> adapterView) {

             }
         });
        return rootView;
    }

    private void obtenerAlmacenes(String codEmpresa, String codLocalidad) {
        final Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("codEmpresa", codEmpresa);
        dataConsulta.put("codLocalidad", codLocalidad);
         Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getAlmacenService().almacenesByLocalidad(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if(response.body().getEstado()==1) {
                    SpinnerSimpleAdapter spinnerSimpleAdapter=new SpinnerSimpleAdapter(getActivity(),R.layout.list_simplespinner_item,(ArrayList) response.body().getData());
                    spinnerAlmacenes.setAdapter(spinnerSimpleAdapter);
                    dialogPreventa.dismiss();
                }else{

                }

            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                MyDialogErrorPreventa newFragment = MyDialogErrorPreventa.newInstance(bottomBarDialog,dialogPreventa);
                newFragment.setArguments(getArguments());
                newFragment.show(ft, "dialog");
            }
        });

    }


    private void obtenerDatosUsuario(String usuario,final String codLista,final Integer tieneDeuda,String codCliente) {
        final Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario", usuario);
        Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getVendedorService().datosUsuario(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if(response.body().getEstado()==1){
                    SpinnerVendedorCustomAdapter spinnerVendedorCustomAdapter=new SpinnerVendedorCustomAdapter(getActivity(),R.layout.list_datos_vend_item,(ArrayList) response.body().getData());
                    spinnerVend.setAdapter(spinnerVendedorCustomAdapter);
                    ArrayList obj= (ArrayList) spinnerVend.getSelectedItem();
                    obtenerCondicionesDepago(codLista,obj.get(0).toString(),tieneDeuda,codCliente);
                }else{

                }
              //  dialogPreventa.dismiss();
            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                MyDialogErrorPreventa newFragment = MyDialogErrorPreventa.newInstance(bottomBarDialog,dialogPreventa);
                newFragment.setArguments(getArguments());
                newFragment.show(ft, "dialog");
            }
        });

    }

    private void obtenerCondicionesDepago(String codLista,String codVendedor,Integer deuda,String codCliente){
        final Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("codLista", codLista);
        dataConsulta.put("codVendedor", codVendedor);
        dataConsulta.put("deuda", deuda.toString());
        dataConsulta.put("codCliente", codCliente);
         Call<JsonRespuesta> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getVendedorService().condiciones(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta>() {
            @Override
            public void onResponse(Call<JsonRespuesta> call, Response<JsonRespuesta> response) {
                if(response.body().getEstado()==1) {
                    SpinnerSimpleAdapter spinnerSimpleAdapter=new SpinnerSimpleAdapter(getActivity(),R.layout.list_simplespinner_item,(ArrayList) response.body().getData());
                    spinnerCond.setAdapter(spinnerSimpleAdapter);

                   // obtenerFocus();
                    //dialogPreventa.dismiss();
                }else{

                }

            }

            @Override
            public void onFailure(Call<JsonRespuesta> call, Throwable t) {

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                MyDialogErrorPreventa newFragment = MyDialogErrorPreventa.newInstance(bottomBarDialog,dialogPreventa);
                newFragment.setArguments(getArguments());
                newFragment.show(ft, "dialog");
            }
        });


    }

    private void obtenerFocus() {
         Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("usuario", sessionUsuario.getUsuario());
        Call<JsonRespuesta<CarritoCompras>> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getVendedorService().focusByUsuario(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta<CarritoCompras>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<CarritoCompras>> call, Response<JsonRespuesta<CarritoCompras>> response) {
                if(response.body().getEstado()==1){
                    if(response.body().getData().isEmpty()){
                               FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                               DialogFindArticulo newFragment = DialogFindArticulo.newInstance(titleLista,spinnerVend,spinnerCond,spinnerAlmacenes);
                               newFragment.setDialogInfoListener(dialogInfoListener);
                                getArguments().putString("correlativo",correlativo);
                                newFragment.setArguments(getArguments());
                                newFragment.show(ft, "find");
                    }else{
                        CarritoCompras carritoCompras=response.body().getData().get(0);
                        if(!encontrarRepetido(carritoCompras.getArticulo().getCodItem())){
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            DialogFocusArticulo newFragment = DialogFocusArticulo.newInstance(titleLista,spinnerVend,spinnerCond,spinnerAlmacenes,carritoCompras);
                            newFragment.setDialogInfoListener(dialogInfoListener);
                            getArguments().putString("correlativo",correlativo);
                            newFragment.setArguments(getArguments());
                            newFragment.show(ft, "find");
                        }else{
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            DialogFindArticulo newFragment = DialogFindArticulo.newInstance(titleLista,spinnerVend,spinnerCond,spinnerAlmacenes);
                            newFragment.setDialogInfoListener(dialogInfoListener);
                            getArguments().putString("correlativo",correlativo);
                            newFragment.setArguments(getArguments());
                            newFragment.show(ft, "find");

                        }


                    }

                }else{

                }
                //  dialogPreventa.dismiss();
            }

            @Override
            public void onFailure(Call<JsonRespuesta<CarritoCompras>> call, Throwable t) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                MyDialogErrorPreventa newFragment = MyDialogErrorPreventa.newInstance(bottomBarDialog,dialogPreventa);
                newFragment.setArguments(getArguments());
                newFragment.show(ft, "dialog");
            }
        });

    }
    public boolean encontrarRepetido(String codArticulo) {
        boolean band = false;
        if (sessionUsuario.getPaqueteCarrito() == null) {
            return band;
        } else {
            List<CarritoCompras> pedidos = sessionUsuario.getPaqueteCarrito().getListaCarrito();
            for (int i = 0; i < pedidos.size(); i++) {
                if (codArticulo.equals(pedidos.get(i).getArticulo().getCodItem())) {
                    band = true;
                    break;
                }
            }
            return band;
        }

    }

    private void buscarArticulo() {
        descArticulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    DialogFindByVendedorArticulo newFragment = DialogFindByVendedorArticulo.newInstance(titleLista,spinnerVend,spinnerCond,spinnerAlmacenes);
                    newFragment.setDialogInfoListener(dialogInfoListener);
                    getArguments().putString("correlativo",correlativo);
                    newFragment.setArguments(getArguments());
                    newFragment.show(ft, "find");
            }
        });


    }

    private void agregarSugeridosBtnFoco(){

        btnFoco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList sugeridos=getArguments().getParcelableArrayList("listaSugeridos");
                if(sugeridos.size()>0){
                    if(sessionUsuario.getBandSugerido()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Sugeridos del dia!");
                        builder.setMessage("Se agregarán los articulos sugeridos establecidos en la pantalla anterior");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addArticulosSugeridos(getArguments().getString(PREVENTAENUM.CODEMPRESA.getClave()), getArguments().getString(PREVENTAENUM.CODLISTA.getClave()), getArguments().getString(PREVENTAENUM.CODALMACEN.getClave()));
                            }
                        });
                        builder.show();
                    }else{
                        ChocoBar.builder().setBackgroundColor(getResources().getColor(R.color.colorWarning)).setTextSize(12)
                                .setTextColor(Color.parseColor("#FFFFFF"))
                                .setTextTypefaceStyle(Typeface.ITALIC)
                                .setText("YA SE AGREGARON ARTICULOS SUGERIDOS!!")
                                .setMaxLines(4)
                                .centerText()
                                .setActionTextTypefaceStyle(Typeface.BOLD)
                                .setIcon(R.drawable.ic_warning_white_48dp)
                                .setActivity(getActivity())
                                .setDuration(ChocoBar.LENGTH_SHORT).build().show();
                    }

                }else{
                    ChocoBar.builder().setBackgroundColor(getResources().getColor(R.color.colorInfo)).setTextSize(12)
                            .setTextColor(Color.parseColor("#FFFFFF"))
                            .setTextTypefaceStyle(Typeface.ITALIC)
                            .setText("CLIENTE NO TIENE ARTICULOS SUGERIDOS!!")
                            .setMaxLines(4)
                            .centerText()
                            .setActionTextTypefaceStyle(Typeface.BOLD)
                            .setIcon(R.drawable.ic_info_outline_white_48dp)
                            .setActivity(getActivity())
                            .setDuration(ChocoBar.LENGTH_SHORT).build().show();
                }
            }
        });

    }

    private Integer getCantidad(String codItem){
        ArrayList sugeridos=getArguments().getParcelableArrayList("listaSugeridos");
         for (int i = 0; i <sugeridos.size() ; i++) {
           ArrayList sug= (ArrayList) sugeridos.get(i);
            if(sug.get(0).toString().equals(codItem)){
                return new Double(sug.get(2).toString()).intValue();
            }

        }
        return null;

    }

    private void addArticulosSugeridos(final String codEmpresa, final String codLista, final String codAlmacen) {
        ArrayList sugeridos=getArguments().getParcelableArrayList("listaSugeridos");

        List<String> codigos=new ArrayList<>();
        for (int i = 0; i <sugeridos.size() ; i++) {
            ArrayList objSugerido= (ArrayList) sugeridos.get(i);
            codigos.add(objSugerido.get(0).toString());
        }

        String codigosString=UtilAndroid.ConvertirArrayinString(codigos);
        ArrayList obj = (ArrayList) spinnerCond.getSelectedItem();
        if(getArguments().getBoolean(PREVENTAENUM.ISONLINE.getClave())) {
            Map<String, String> dataConsulta = new HashMap<>();
            dataConsulta.put("codEmpresa", codEmpresa);
            dataConsulta.put("codAlmacen", codAlmacen);
            dataConsulta.put("codLista", codLista);
            dataConsulta.put("codigosArt", codigosString);
            dataConsulta.put("condPago", obj.get(0).toString());
            dataConsulta.put("usuario", sessionUsuario.getUsuario());
            dataConsulta.put("codigo", sessionUsuario.getCodigoAplicacion());
            Call<JsonRespuesta<Articulo>> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getArticuloService().addArticulosSugeridos(dataConsulta);
            loginCall.enqueue(new Callback<JsonRespuesta<Articulo>>() {
                @Override
                public void onResponse(Call<JsonRespuesta<Articulo>> call, Response<JsonRespuesta<Articulo>> response) {
                    if (response.body().getEstado() != 3) {
                        List<Articulo> listaArt = response.body().getData();
                        for (int i = 0; i < listaArt.size(); i++) {
                            Articulo art = listaArt.get(i);
                            for (int j = 0; j <sugeridos.size() ; j++) {
                                ArrayList objSugerido= (ArrayList) sugeridos.get(j);
                                if(art.getCodItem().equals(objSugerido.get(0).toString())){
                                    art.setTipo(objSugerido.get(3).toString());//add flag sugerido/focus
                                    break;
                                }
                            }
                            CarritoCompras carritoCompras = new CarritoCompras();
                            carritoCompras.setArticulo(art);

                            Integer cantidadSugerida = getCantidad(art.getCodItem());
                            carritoCompras.setCantidad(cantidadSugerida);
                            BigDecimal cant = new BigDecimal(cantidadSugerida.toString());
                            carritoCompras.setImporte(cant.multiply(art.getPrecioSugerido()));
                            if (sessionUsuario.getPaqueteCarrito() == null) {
                                listaCarrito.add(carritoCompras);
                                PaqueteCarrito paqueteCarrito = new PaqueteCarrito();
                                paqueteCarrito.setListaCarrito(listaCarrito);
                                sessionUsuario.guardarPaqueteCarrito(paqueteCarrito);
                            } else {
                                listaCarrito = sessionUsuario.getPaqueteCarrito().getListaCarrito();
                                listaCarrito.add(carritoCompras);
                                PaqueteCarrito paqueteCarrito = new PaqueteCarrito();
                                paqueteCarrito.setListaCarrito(listaCarrito);
                                sessionUsuario.guardarPaqueteCarrito(paqueteCarrito);
                            }

                        }

                       sessionUsuario.guardarBandSugerido(false);

                        CarritoFragment carritoFragmentEdit = CarritoFragment.newInstance(titleLista, spinnerVend, spinnerCond, spinnerAlmacenes);
                        carritoFragmentEdit.setDialogInfoListener(dialogInfoListener);
                        carritoFragmentEdit.setArguments(getArguments());

                        FragmentTransaction ftCarrito = getFragmentManager().beginTransaction();
                        ftCarrito.replace(R.id.content_carritocompras, carritoFragmentEdit, TAG);
                        ftCarrito.commit();

                    } else {
                        SnackAlert sa = new SnackAlert(getActivity());
                        sa.setTitle("ALERTA!!");
                        sa.setMessage("USUARIO BLOQUEADO POR HORA");
                        sa.setType(SnackAlert.WARNING);
                        sa.show();



                    }
                }

                @Override
                public void onFailure(Call<JsonRespuesta<Articulo>> call, Throwable t) {
                    // closeKeyboard();
                    //progressDialog.dismiss();
//                Snackbar.make(getView(), "Error de conexión!!", Snackbar.LENGTH_INDEFINITE)
//                        .setAction("reintentar", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//
//                             listarArticulos(codEmpresa,filtro,codLista,codAlmacen);
//
//                            }
//                        }).show();
                    //Toast.makeText(getContext(), "Error de conexión " , Toast.LENGTH_LONG).show();
                }
            });
        }else{
           List<Articulo> articulos= ApiSqlite.getInstance(getContext()).getOperacionesBaseDatos().getSugeridosArticulos(codCliente,codigosString,codLista,obj.get(0).toString());

            for (int i = 0; i < articulos.size(); i++) {
                Articulo art = articulos.get(i);
                CarritoCompras carritoCompras = new CarritoCompras();
                carritoCompras.setArticulo(art);
                Integer cantidadSugerida = getCantidad(art.getCodItem());
                carritoCompras.setCantidad(cantidadSugerida);
                BigDecimal cant = new BigDecimal(cantidadSugerida.toString());
                carritoCompras.setImporte(cant.multiply(art.getPrecioSugerido()));
                if (sessionUsuario.getPaqueteCarrito() == null) {
                    listaCarrito.add(carritoCompras);
                    PaqueteCarrito paqueteCarrito = new PaqueteCarrito();
                    paqueteCarrito.setListaCarrito(listaCarrito);
                    sessionUsuario.guardarPaqueteCarrito(paqueteCarrito);
                } else {
                    listaCarrito = sessionUsuario.getPaqueteCarrito().getListaCarrito();
                    listaCarrito.add(carritoCompras);
                    PaqueteCarrito paqueteCarrito = new PaqueteCarrito();
                    paqueteCarrito.setListaCarrito(listaCarrito);
                    sessionUsuario.guardarPaqueteCarrito(paqueteCarrito);
                }

            }

            sessionUsuario.guardarBandSugerido(false);
            getArguments().putString("correlativo",correlativo);
            CarritoFragment carritoFragmentEdit = CarritoFragment.newInstance(titleLista, spinnerVend, spinnerCond, spinnerAlmacenes);
            carritoFragmentEdit.setDialogInfoListener(dialogInfoListener);
            carritoFragmentEdit.setArguments(getArguments());

            FragmentTransaction ftCarrito = getFragmentManager().beginTransaction();
            ftCarrito.replace(R.id.content_carritocompras, carritoFragmentEdit, TAG);
            ftCarrito.commit();
        }
    }

}
