package com.legado.preventagps.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.legado.preventagps.R;
import com.legado.preventagps.adapter.vendedor.ArticuloRecyclerAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.api.ApiSqlite;
import com.legado.preventagps.dialogs.MyDialogInfo;
import com.legado.preventagps.enums.PREVENTAENUM;
import com.legado.preventagps.json.JsonRespuesta;
import com.legado.preventagps.modelo.Articulo;
import com.legado.preventagps.modelo.CarritoCompras;
import com.legado.preventagps.modelo.PaqueteCarrito;
import com.legado.preventagps.util.CustomEditText;
import com.legado.preventagps.util.DrawableClickListener;
import com.legado.preventagps.util.RecyclerItemClickListener;
import com.legado.preventagps.util.SessionUsuario;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class DialogFindArticulo extends DialogFragment {
    private static final String TAG = "DialogFindArticulo";
    @BindView(R.id.recicladorArticulo)
    RecyclerView recicladorArticulo;
    @BindView(R.id.descArticulo)
    CustomEditText descArticulo;
    RecyclerView.LayoutManager lManagerArticulo;
    @BindView(R.id.txtPrecio)
    EditText txtPrecio;
    @BindView(R.id.txtStock)
    EditText txtStock;
    @BindView(R.id.txtDisponible)
    EditText txtDisponible;


    @BindView(R.id.swRetenido)
    Switch swRetenido;

    @BindView(R.id.lblPiso)
    TextView lblPiso;
    @BindView(R.id.txtPiso)
    EditText txtPiso;
    @BindView(R.id.txtCantidad)
    EditText txtCantidad;
    @BindView(R.id.btnAgregar)
    Button btnAgregar;
    Articulo articuloSelected;
    List<Articulo> listaArt = new ArrayList<>();
    List<CarritoCompras> listaCarrito = new ArrayList<>();
    SessionUsuario sessionUsuario;
    static TextView titleListaCount;
    static Spinner spinnerVendFind;
    static Spinner spinnerCondFind;
    static Spinner spinnerAlmacenesFind;

    MyDialogInfo.DialogInfoListener dialogInfoListener;

    public void setDialogInfoListener(MyDialogInfo.DialogInfoListener dialogInfoListener) {
        this.dialogInfoListener = dialogInfoListener;
    }

    public static DialogFindArticulo newInstance(TextView titleLista, Spinner spinnerVend, Spinner spinnerCond,Spinner spinnerAlmacenes) {
        titleListaCount = titleLista;
        spinnerVendFind = spinnerVend;
        spinnerCondFind = spinnerCond;
        spinnerAlmacenesFind=spinnerAlmacenes;
        DialogFindArticulo fragment = new DialogFindArticulo();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_find_articulos, container, false);
        ButterKnife.bind(this, rootView);
        sessionUsuario = new SessionUsuario(rootView.getContext());
         descArticulo.requestFocus();
        mostrarIconoClear();
        //if(getArguments().getBoolean(PREVENTAENUM.ISONLINE.getClave())){
            keyArticulos(getArguments().getString(PREVENTAENUM.CODEMPRESA.getClave()), getArguments().getString(PREVENTAENUM.CODLISTA.getClave()), getArguments().getString(PREVENTAENUM.CODALMACEN.getClave()));

       // }else{
//            ArrayList obj = (ArrayList) spinnerCondFind.getSelectedItem();
//             keyArticulosOffline(getArguments().getString(PREVENTAENUM.CODEMPRESA.getClave()), getArguments().getString(PREVENTAENUM.CODLISTA.getClave()), getArguments().getString(PREVENTAENUM.CODALMACEN.getClave()),obj.get(0).toString());

       // }
        limpiarArticulos();
        selectedItemRecyclerArticulo();
        Button dismiss = (Button) rootView.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        agregarArticulo();
        showKeyboard();
        return rootView;
    }

    private void keyArticulosOffline(String codEmpresa, String codLista, String codAlmacen,String condicionPago) {
        recicladorArticulo.setHasFixedSize(true);
        lManagerArticulo = new LinearLayoutManager(getActivity());
        recicladorArticulo.setLayoutManager(lManagerArticulo);
        descArticulo.addTextChangedListener(new TextWatcher() {
            String ba = "";

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                //mostrarIconoClear();
                if (descArticulo.getText().length() >= ba.length()) {
                    if (cs.toString().length() >= 4) {
                      listaArt= ApiSqlite.getInstance(getActivity().getApplicationContext()).getOperacionesBaseDatos().listaArticulo(cs.toString(), codLista, codAlmacen,condicionPago);
                      ArticuloRecyclerAdapter articuloRecyclerAdapter = new ArticuloRecyclerAdapter(listaArt, getActivity());
                        recicladorArticulo.setAdapter(articuloRecyclerAdapter);
                    }

                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                mostrarIconoClear();
                ba = arg0.toString();
                //  recicladorArticulo.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
    }

    public void keyArticulos(final String codEmpresa, final String codLista, final String codAlmacen) {
        descArticulo.addTextChangedListener(new TextWatcher() {
            String ba = "";

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                //mostrarIconoClear();
                if (descArticulo.getText().length() >= ba.length()) {
                    if (cs.toString().length() >= 4) {
                        listarArticulos(codEmpresa, cs.toString(), codLista, codAlmacen);
                    }

                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                mostrarIconoClear();
                ba = arg0.toString();
              //  recicladorArticulo.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable arg0) {
//                if (descArticulo.getText().toString().length() <= 0) {
//                  recicladorArticulo.setVisibility(View.GONE);
//
//                }
            }
        });
    }


    private void listarArticulos(final String codEmpresa, final String filtro, final String codLista, final String codAlmacen) {
        //progressDialog.show();
       try{
           recicladorArticulo.setHasFixedSize(true);
           lManagerArticulo = new LinearLayoutManager(getActivity());
           recicladorArticulo.setLayoutManager(lManagerArticulo);
           final String upperFiltro = filtro.toUpperCase();
           Map<String, String> dataConsulta = new HashMap<>();
           dataConsulta.put("codEmpresa", codEmpresa);
           dataConsulta.put("codAlmacen", codAlmacen);
           dataConsulta.put("codLista", codLista);
           dataConsulta.put("filtro", upperFiltro);
           ArrayList obj = (ArrayList) spinnerCondFind.getSelectedItem();
           dataConsulta.put("condPago", obj.get(0).toString());
           dataConsulta.put("usuario", sessionUsuario.getUsuario());
           dataConsulta.put("codigo", sessionUsuario.getCodigoAplicacion());
           dataConsulta.put("codCanal", sessionUsuario.getPaqueteUsuario().getCodCanal());
           Call<JsonRespuesta<Articulo>> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getArticuloService().articulos(dataConsulta);
           loginCall.enqueue(new Callback<JsonRespuesta<Articulo>>() {
               @Override
               public void onResponse(Call<JsonRespuesta<Articulo>> call, Response<JsonRespuesta<Articulo>> response) {
                   if(response.body()!=null){
                       if (response.body().getEstado() !=3) {
                           listaArt = response.body().getData();
                           ArticuloRecyclerAdapter articuloRecyclerAdapter = new ArticuloRecyclerAdapter(listaArt, getActivity());
                           recicladorArticulo.setAdapter(articuloRecyclerAdapter);

                       } else {

                           Toast.makeText(getContext(),response.body().getMensaje(),Toast.LENGTH_SHORT).show();

                       }
                   }else{

                       if(sessionUsuario.getIsOnlyOnline()){
                           new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                   .setTitleText("Oops...")
                                   .setContentText(getString(R.string.txtMensajeServidorCaido))
                                   .show();
                       }else {
                           ArrayList obj = (ArrayList) spinnerCondFind.getSelectedItem();
                           keyArticulosOffline(getArguments().getString(PREVENTAENUM.CODEMPRESA.getClave()), getArguments().getString(PREVENTAENUM.CODLISTA.getClave()), getArguments().getString(PREVENTAENUM.CODALMACEN.getClave()), obj.get(0).toString());
                       }
                   }

               }

               @Override
               public void onFailure(Call<JsonRespuesta<Articulo>> call, Throwable t) {

                   if(sessionUsuario.getIsOnlyOnline()){
                       new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                               .setTitleText("Oops...")
                               .setContentText(getString(R.string.txtMensajeConexion))
                               .show();
                   }else{
                       ArrayList obj = (ArrayList) spinnerCondFind.getSelectedItem();
                       keyArticulosOffline(getArguments().getString(PREVENTAENUM.CODEMPRESA.getClave()), getArguments().getString(PREVENTAENUM.CODLISTA.getClave()), getArguments().getString(PREVENTAENUM.CODALMACEN.getClave()),obj.get(0).toString());

                   }


               }
           });

       }catch(Exception ex){
           new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                   .setTitleText("ERROR")
                   .setContentText("NO se han cargado los datos de empresa ,almacen o condicion de pago")
                   .show();
       }

    }

    public void selectedItemRecyclerArticulo() {

        recicladorArticulo.addOnItemTouchListener(new RecyclerItemClickListener(getActivity().getApplicationContext(), new RecyclerItemClickListener.SimpleOnItemClickListener() {
            @Override
            public void onItemClick(View childView, int position) {
                articuloSelected = listaArt.get(position);

                if (!encontrarRepetido(articuloSelected.getCodItem())) {
                    descArticulo.setText(articuloSelected.getDescItem());
                    txtPrecio.setText(articuloSelected.getPrecioSugerido().toString());
                    txtStock.setText(articuloSelected.getSaldo().toString());
                    txtPiso.setText(articuloSelected.getPiso().toString());
                    swRetenido.setChecked(articuloSelected.getIsRestringido()==1?true:false);
                    if(articuloSelected.getIsRestringido()==1){
                        swRetenido.setBackground(getResources().getDrawable(R.color.red_btn_bg_color));
                    }else{
                        swRetenido.setBackground(getResources().getDrawable(R.color.gray_btn_bg_color));
                    }
                    //Integer disponible=new Integer(articuloSelected.getSaldo().toString())-new Integer(articuloSelected.getPiso().toString());
                    txtDisponible.setText(articuloSelected.getDisponibleCanal().toString());

                    txtCantidad.requestFocus();

                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInputFromInputMethod(txtCantidad.getWindowToken(), 0);
                    recicladorArticulo.setVisibility(View.GONE);
                   // recicladorArticulo.setAdapter(null);

                } else {
                    recicladorArticulo.setVisibility(View.GONE);
                    //closeKeyboard();
                    Snackbar.make(getView(), "Este articulo ya fue agregado!! Porfavor seleccione otro", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Cerrar", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    descArticulo.setText("");
                                    txtCantidad.setText("");
                                    txtPrecio.setText("");
                                    txtStock.setText("");
                                    showKeyboard();
                                    descArticulo.requestFocus();
                                    // txtStock.requestFocus();
                                    recicladorArticulo.setAdapter(null);
                                    recicladorArticulo.setVisibility(View.VISIBLE);

                                }
                            }).show();
                }
             // validar();
                //recicladorArticulo.setVisibility(View.GONE);

            }

            @Override
            public void onItemLongPress(View childView, int position) {
                super.onItemLongPress(childView, position);
            }
        }));
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

    private boolean validar() {
        boolean band = true;
        if (descArticulo.getText().toString().equals("")) {
            descArticulo.setError("Articulo");
            band = false;
        } else {
            descArticulo.setError(null);
        }
        if (txtPrecio.getText().toString().equals("")) {
            txtPrecio.setError("Precio");
            band = false;
        } else {
            txtPrecio.setError(null);
        }

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

            if(new Integer(txtCantidad.getText().toString())>new Integer(txtDisponible.getText().toString())){
                txtCantidad.setError("CANTIDAD EXCEDE AL DISPONIBLE DE VENTA PARA SU CANAL");
                band = false;
            }else{
                txtCantidad.setError(null);

            }
        }



        return band;

    }

    public void agregarArticulo() {
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validar()) {
                    CarritoCompras carritoCompras = new CarritoCompras();
                    carritoCompras.setArticulo(articuloSelected);
                    carritoCompras.setCantidad(Integer.parseInt(txtCantidad.getText().toString()));
                    BigDecimal cant = new BigDecimal(txtCantidad.getText().toString());
                    carritoCompras.setImporte(cant.multiply(articuloSelected.getPrecioSugerido()));
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

                    CarritoFragment carritoFragmentEdit = CarritoFragment.newInstance(titleListaCount, spinnerVendFind, spinnerCondFind,spinnerAlmacenesFind);
                    carritoFragmentEdit.setDialogInfoListener(dialogInfoListener);
                    carritoFragmentEdit.setArguments(getArguments());

                    FragmentTransaction ftCarrito = getFragmentManager().beginTransaction();
                    ftCarrito.replace(R.id.content_carritocompras, carritoFragmentEdit, TAG);
                    ftCarrito.commit();
                    dismiss();
                    //closeKeyboard();


                }

            }

        });
    }

    private void limpiarArticulos() {
        descArticulo.setDrawableClickListener(new DrawableClickListener() {
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case LEFT:
                        break;
                    case RIGHT:

                        descArticulo.setText("");
                        txtCantidad.setText("");
                        txtPrecio.setText("");
                        txtStock.setText("");
                        descArticulo.requestFocus();
                       // txtStock.requestFocus();
                        recicladorArticulo.setAdapter(null);
                        recicladorArticulo.setVisibility(View.VISIBLE);

                        break;
                    default:
                        break;
                }
            }

        });
    }

    //para android 4
    public void mostrarIconoClear() {
        Drawable errorIcon = getResources().getDrawable(android.R.drawable.ic_delete);
        errorIcon.setBounds(new Rect(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight()));
        descArticulo.setError(null, errorIcon);
    }

    public void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

//    public void closeKeyboard() {
//        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//    }

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
