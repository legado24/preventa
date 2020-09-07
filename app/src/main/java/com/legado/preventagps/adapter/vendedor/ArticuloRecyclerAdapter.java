package com.legado.preventagps.adapter.vendedor;


import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.legado.preventagps.R;
import com.legado.preventagps.modelo.Articulo;
import com.legado.preventagps.util.ArticuloItemClickListener;

import java.util.List;

/**
 * Created by __Adrian__ on 19/4/2017.
 */

public class ArticuloRecyclerAdapter extends RecyclerView.Adapter<ArticuloRecyclerAdapter.ArticuloRecyclerViewHolder> implements ArticuloItemClickListener {
    private static final String TAG = "ArticuloRecyclerAdapter";

    private List<Articulo> articulos;
    private FragmentActivity activity;

    public ArticuloRecyclerAdapter(List<Articulo> articulos, FragmentActivity activity) {
        this.articulos = articulos;
        this.activity = activity;

    }

    @Override
    public ArticuloRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_articulo, parent, false);
        return new ArticuloRecyclerViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(ArticuloRecyclerViewHolder holder, int position) {
        holder.codArticulo.setText(articulos.get(position).getCodItem());
        holder.descArticulo.setText(articulos.get(position).getDescItem());
//        holder.precio.setText(articulos.get(position).getPrecioBase().toString());
//        holder.stock.setText(articulos.get(position).getSaldo().toString());
        holder.um.setText(articulos.get(position).getUm());
        holder.itemView.setSelected(true);
        holder.itemView.setBackgroundResource(R.drawable.selector_row);

    }

    @Override
    public int getItemCount() {
        return articulos.size();
    }

    @Override
    public void onItemClick(View view, int position) {

    }


    public static class ArticuloRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView codArticulo;
        public TextView descArticulo;
//        public TextView precio;
//        public TextView stock;
        public TextView um;



        public ArticuloItemClickListener listener;

        public ArticuloRecyclerViewHolder(View itemView, ArticuloItemClickListener listener) {
            super(itemView);
            codArticulo = (TextView) itemView.findViewById(R.id.codArticulo);
            descArticulo = (TextView) itemView.findViewById(R.id.descArticulo);
//            precio= (TextView) itemView.findViewById(R.id.txtPrecio);
//            stock= (TextView) itemView.findViewById(R.id.txtStock);
           um= (TextView) itemView.findViewById(R.id.txtUnidadMedida);
            this.listener = listener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            listener.onItemClick(view, getAdapterPosition());
        }
    }


}




