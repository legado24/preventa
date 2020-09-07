package com.legado.preventagps.adapter.vendedor;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.legado.preventagps.R;
import com.legado.preventagps.dialogs.MyDialogEditSugerido;
import com.legado.preventagps.util.ArticuloItemClickListener;
import com.legado.preventagps.util.SessionUsuario;

import java.util.ArrayList;

/**
 * Created by __Adrian__ on 19/4/2017.
 */

public class FocusAndSugeridoRecyclerAdapter extends RecyclerView.Adapter<FocusAndSugeridoRecyclerAdapter.ArticuloRecyclerViewHolder> implements ArticuloItemClickListener, MyDialogEditSugerido.SugeridoComunicador {
    private static final String TAG = "FocusAndSugeridoRecyclerAdapter";
    private  ArrayList listaFocusAndSugeridos;
     private FragmentActivity activity;
    MyDialogEditSugerido.SugeridoComunicador sugeridoComunicador;
    private SessionUsuario sessionUsuario;

    public FocusAndSugeridoRecyclerAdapter(ArrayList listaFocusAndSugeridos, FragmentActivity activity) {
         this.listaFocusAndSugeridos = listaFocusAndSugeridos;
        this.sessionUsuario=new SessionUsuario(activity);
        this.activity = activity;
        this.sugeridoComunicador=this;

    }

    @Override
    public ArticuloRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_sugerido, parent, false);
        return new ArticuloRecyclerViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(ArticuloRecyclerViewHolder holder, int position) {
        ArrayList objFocusAndSugerido= (ArrayList) listaFocusAndSugeridos.get(position);
       // holder.codArticulo.setText(objSugerido.get(0).toString());
        holder.descArticulo.setText(objFocusAndSugerido.get(1).toString());
        Double d=new Double(objFocusAndSugerido.get(2).toString());
        holder.cantidad.setText(d.intValue()+"");
        holder.itemView.setSelected(true);
        holder.itemView.setBackgroundResource(R.drawable.selector_row);

        holder.imagenTipo.setVisibility(View.GONE);
        if(objFocusAndSugerido.get(3).toString()!=null){
            if(objFocusAndSugerido.get(3).toString().equals("F")){
                holder.imagenTipo.setVisibility(View.VISIBLE);
                holder.imagenTipo.setBackgroundResource(R.drawable.ic_focus_item_sugerido);
            }else if(objFocusAndSugerido.get(3).toString().equals("S")){
                holder.imagenTipo.setVisibility(View.VISIBLE);
                holder.imagenTipo.setBackgroundResource(R.drawable.ic_item_sugerido);
            }else {
                holder.imagenTipo.setVisibility(View.GONE);
            }



        }


        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                MyDialogEditSugerido newFragment = MyDialogEditSugerido.newInstance((ArrayList) listaFocusAndSugeridos.get(position),position);
          newFragment.setSugeridoComunicador(sugeridoComunicador);
//                newFragment.setArguments(args);
                newFragment.show(ft, "edit");
            }
        });

        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItemRecycler(position);

            }
        });


    }

    @Override
    public int getItemCount() {
        return listaFocusAndSugeridos.size();
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void updateRecycler(int position,String newCantidad) {
        ArrayList item= (ArrayList) listaFocusAndSugeridos.get(position);
        item.set(2,new Integer(newCantidad));
        listaFocusAndSugeridos.set(position,item);
        notifyItemChanged(position);
        sessionUsuario.guardarArrayListSugeridos(listaFocusAndSugeridos);
    }

    public ArrayList getItemsRecycler(){
        return this.listaFocusAndSugeridos;
    }

    @Override
    public void deleteItemRecycler(int position) {
        listaFocusAndSugeridos.remove(position);
     notifyItemRemoved(position);
     notifyItemRangeChanged(position, listaFocusAndSugeridos.size());
        sessionUsuario.guardarArrayListSugeridos(listaFocusAndSugeridos);
    }



    public static class ArticuloRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        public TextView codArticulo;
        public TextView descArticulo;
        public TextView cantidad;
        public ImageView btnEdit;
        public ImageView btnEliminar;
        public ImageView imagenTipo;


        public ArticuloItemClickListener listener;

        public ArticuloRecyclerViewHolder(View itemView, ArticuloItemClickListener listener) {
            super(itemView);
            //codArticulo = (TextView) itemView.findViewById(R.id.codArticulo);
            descArticulo = (TextView) itemView.findViewById(R.id.descArticulo);
            cantidad= (TextView) itemView.findViewById(R.id.txtCantidad);
            btnEdit=   (ImageView) itemView.findViewById(R.id.btnEditar);
            btnEliminar= (ImageView) itemView.findViewById(R.id.btnEliminar);
            imagenTipo=(ImageView) itemView.findViewById(R.id.imagenTipo);
            this.listener = listener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            listener.onItemClick(view, getAdapterPosition());
        }



    }


}




