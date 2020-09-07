package com.legado.preventagps.adapter.supervisor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.legado.preventagps.R;
import com.legado.preventagps.activities.supervisor.MenuByVendedorActivity;
import com.legado.preventagps.modelo.Cliente;
import com.legado.preventagps.util.SessionUsuario;

import java.util.ArrayList;
import java.util.List;


public class VendedorSupAdapter extends RecyclerView.Adapter<VendedorSupAdapter.VendedorSupViewHolder> implements ItemVendSupListener {
    public ArrayList items;
    SessionUsuario sessionUsuario;
    Context c;
    public VendedorSupAdapter(ArrayList items, Context context) {
        this.items = items;
        this.c = context;
        sessionUsuario = new SessionUsuario(c);
    }

    public List<Cliente> getItems() {
        return items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    @Override
    public VendedorSupViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.vendedor_sup_card, viewGroup, false);
        VendedorSupViewHolder ch = new VendedorSupViewHolder(v, this);
        return ch;
    }

    @Override
    public void onBindViewHolder(VendedorSupViewHolder viewHolder, final int i) {
        ArrayList item= (ArrayList) items.get(i);
        viewHolder.txtCodVendedor.setText(item.get(5).toString());
        viewHolder.txtDescVendedor.setText(item.get(1).toString());
        viewHolder.txtDescCanal.setText(item.get(8).toString());

    }

    @Override
    public void onItemClick(View v, int position) {
        ArrayList item= (ArrayList) items.get(position);
        Intent intent = new Intent(v.getContext(), MenuByVendedorActivity.class);
          intent.putExtra("codVendedor", item.get(5).toString());
        intent.putExtra("descVendedor", item.get(1).toString());
        intent.putExtra("usuario", item.get(0).toString());
        v.getContext().startActivity(intent);
    }




    public static class VendedorSupViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        public TextView txtCodVendedor;
        public TextView txtDescVendedor;
        public TextView txtDescCanal;
        SessionUsuario sessionUsuario;
         public ItemVendSupListener listener;

        public VendedorSupViewHolder(final View v,  ItemVendSupListener listener) {
            super(v);
            sessionUsuario = new SessionUsuario(v.getContext());
            this.listener=listener;
            v.setOnClickListener(this);
            txtCodVendedor = (TextView) v.findViewById(R.id.txtCodVendedor);
            txtDescVendedor = (TextView) v.findViewById(R.id.txtDescVendedor);
            txtDescCanal = (TextView) v.findViewById(R.id.txtDescCanal);
        }


        @Override
        public void onClick(View view) {
            listener.onItemClick(view,getAdapterPosition());
        }
    }


}

interface ItemVendSupListener{
    void onItemClick(View view, int position);
}


