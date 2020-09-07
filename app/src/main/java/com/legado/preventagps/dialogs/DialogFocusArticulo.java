package com.legado.preventagps.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.legado.preventagps.R;
import com.legado.preventagps.adapter.vendedor.ArticuloRecyclerAdapter;
import com.legado.preventagps.api.ApiRetrofitShort;
import com.legado.preventagps.enums.PREVENTAENUM;
import com.legado.preventagps.fragments.CarritoFragment;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by __Adrian__ on 20/6/2017.
 */

public class DialogFocusArticulo extends DialogFragment {
    private static final String TAG = "DialogFocusArticulo";
    @BindView(R.id.recicladorArticulo)
    RecyclerView recicladorArticulo;
    @BindView(R.id.descArticulo)
    CustomEditText descArticulo;
    RecyclerView.LayoutManager lManagerArticulo;
    @BindView(R.id.txtPrecio)
    EditText txtPrecio;
    @BindView(R.id.txtStock)
    EditText txtStock;
    @BindView(R.id.txtCantidad)
    EditText txtCantidad;
    @BindView(R.id.btnAgregar)
    Button btnAgregar;
    Articulo articuloSelected;
    List<Articulo> listaArt = new ArrayList<>();
    List<CarritoCompras> listaCarrito = new ArrayList<>();
    SessionUsuario sessionUsuario;
    private TextView titleLista;
    private  Spinner spinnerVend;
    private  Spinner spinnerCond;
    private  Spinner spinnerAlmacenes;
    private CarritoCompras carritoCompras;

    MyDialogInfo.DialogInfoListener dialogInfoListener;



    public void setDialogInfoListener(MyDialogInfo.DialogInfoListener dialogInfoListener) {
        this.dialogInfoListener = dialogInfoListener;
    }

    public static DialogFocusArticulo newInstance(TextView titleLista, Spinner spinnerVend, Spinner spinnerCond, Spinner spinnerAlmacenes,CarritoCompras carritoCompras) {
        DialogFocusArticulo fragment = new DialogFocusArticulo(titleLista,spinnerVend,spinnerCond,spinnerAlmacenes,carritoCompras);
        return fragment;
    }

    public DialogFocusArticulo(TextView titleLista, Spinner spinnerVend, Spinner spinnerCond, Spinner spinnerAlmacenes,CarritoCompras carritoCompras) {
        this.titleLista = titleLista;
        this.spinnerVend = spinnerVend;
        this.spinnerCond = spinnerCond;
        this.spinnerAlmacenes=spinnerAlmacenes;
        this.carritoCompras=carritoCompras;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_focus_articulo, container, false);
        ButterKnife.bind(this, rootView);
        sessionUsuario = new SessionUsuario(rootView.getContext());
         descArticulo.requestFocus();
        mostrarIconoClear();
        keyArticulos(getArguments().getString(PREVENTAENUM.CODEMPRESA.getClave()), getArguments().getString(PREVENTAENUM.CODLISTA.getClave()), getArguments().getString(PREVENTAENUM.CODALMACEN.getClave()));
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

    public void keyArticulos(final String codEmpresa, final String codLista, final String codAlmacen) {

        descArticulo.setText(carritoCompras.getArticulo().getCodItem());
        txtCantidad.setText(carritoCompras.getCantidad().toString());
        listarArticulos(codEmpresa,carritoCompras.getArticulo().getCodItem(), codLista, codAlmacen);
    }


    private void listarArticulos(final String codEmpresa, final String filtro, final String codLista, final String codAlmacen) {
        //progressDialog.show();
        recicladorArticulo.setHasFixedSize(true);
        lManagerArticulo = new LinearLayoutManager(getActivity());
        recicladorArticulo.setLayoutManager(lManagerArticulo);
        final String upperFiltro = filtro.toUpperCase();
        Map<String, String> dataConsulta = new HashMap<>();
        dataConsulta.put("codEmpresa", codEmpresa);
        dataConsulta.put("codAlmacen", codAlmacen);
        dataConsulta.put("codLista", codLista);
        dataConsulta.put("filtro", upperFiltro);
        ArrayList obj = (ArrayList) spinnerCond.getSelectedItem();
        dataConsulta.put("condPago", obj.get(0).toString());
        dataConsulta.put("usuario", sessionUsuario.getUsuario());
        dataConsulta.put("codigo", sessionUsuario.getCodigoAplicacion());
        Call<JsonRespuesta<Articulo>> loginCall = ApiRetrofitShort.getInstance(sessionUsuario.getUrlPreventa()).getArticuloService().articulos(dataConsulta);
        loginCall.enqueue(new Callback<JsonRespuesta<Articulo>>() {
            @Override
            public void onResponse(Call<JsonRespuesta<Articulo>> call, Response<JsonRespuesta<Articulo>> response) {
                if (response.body().getEstado() !=3) {
                    listaArt = response.body().getData();
                    ArticuloRecyclerAdapter articuloRecyclerAdapter = new ArticuloRecyclerAdapter(listaArt, getActivity());
                    recicladorArticulo.setAdapter(articuloRecyclerAdapter);
//                     recicladorArticulo.addItemDecoration(new SimpleDividerItemDecorator(getActivity()));
                   // progressDialog.dismiss();
                } else {
                    //progressDialog.dismiss();
//                    lvProductos.setVisibility(View.GONE);
                    Toast.makeText(getContext(),response.body().getMensaje(),Toast.LENGTH_SHORT).show();

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
                Toast.makeText(getContext(), "Error de conexión " , Toast.LENGTH_LONG).show();
            }
        });

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

                    CarritoFragment carritoFragmentEdit = CarritoFragment.newInstance(titleLista, spinnerVend, spinnerCond,spinnerAlmacenes);
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
