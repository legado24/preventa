package com.legado.preventagps.adapter.vendedor;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.legado.preventagps.R;
import com.legado.preventagps.activities.vendedor.ContenedorActivity;
import com.legado.preventagps.modelo.Cliente;
import com.legado.preventagps.util.SessionUsuario;

import java.util.List;


public class ClienteCobranzaAdapter extends RecyclerView.Adapter<ClienteCobranzaAdapter.ClienteViewHolder> implements ItemClienteListener {
    public List<Cliente> items;
    SessionUsuario sessionUsuario;
    Context c;
    public ClienteCobranzaAdapter(List<Cliente> items, Context context) {
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
    public ClienteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cliente_cobranza_card, viewGroup, false);
        ClienteViewHolder ch = new ClienteViewHolder(v, this);
        return ch;
    }

    @Override
    public void onBindViewHolder(ClienteViewHolder viewHolder, final int i) {
        viewHolder.nombre.setText(items.get(i).getDescCliente());
        viewHolder.codCliente.setText(items.get(i).getCodCliente());
        viewHolder.direccion.setText(items.get(i).getDireccion());
        viewHolder.ruta.setText(items.get(i).getCodRuta()+"-"+items.get(i).getDescRuta());
        viewHolder.status.setText(items.get(i).getStatus());
        viewHolder.itemView.setSelected(true);

        if (items.get(i).getStatus().equals("INACTIVO")) {
            viewHolder.itemView.setBackgroundColor(c.getResources().getColor(R.color.material_red_400));
        } else  {
            viewHolder.itemView.setBackgroundResource(R.drawable.selector_row);
        }






    }

    @Override
    public void onItemClick(View v, int position) {
         Intent intent = new Intent(v.getContext(), ContenedorActivity.class);
        System.out.println(v.getContext().getClass().getName());
        sessionUsuario.guardarDeudaVencida(null);
        sessionUsuario.guardarDeudaNoVencida(null);
        intent.putExtra("codCliente", items.get(position).getCodCliente());
        intent.putExtra("descCliente", items.get(position).getDescCliente());
        intent.putExtra("dirCliente", items.get(position).getDireccion());
        intent.putExtra("codRuta", items.get(position).getCodRuta());
        intent.putExtra("codLocal", items.get(position).getCodLocal());
        intent.putExtra("codLista", items.get(position).getCodLista());
        intent.putExtra("codEmpresa", items.get(position).getCodEmpresa());
        intent.putExtra("nrosecuencia", items.get(position).getSecuencia());
        intent.putExtra("onlycobranza", true);




        v.getContext().startActivity(intent);
    }




    public static class ClienteViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        public TextView nombre;
        public TextView codCliente;
        public TextView secuencia;
        public TextView direccion;
        public TextView ruta;
        public TextView status;
        public List<Cliente> lista;
        SessionUsuario sessionUsuario;
        AppCompatActivity activity = (AppCompatActivity) itemView.getContext();
        public ItemClienteListener listener;

        public ClienteViewHolder(final View v,  ItemClienteListener listener) {
            super(v);
             sessionUsuario = new SessionUsuario(v.getContext());
            codCliente = (TextView) v.findViewById(R.id.txtCodCliente);
            direccion=(TextView) v.findViewById(R.id.txtDireccion);
            nombre = (TextView) v.findViewById(R.id.txtDescripcion);
//            secuencia = (TextView) v.findViewById(R.id.txtSecuencia);
            ruta=(TextView)v.findViewById(R.id.txtRuta);
            status=(TextView)v.findViewById(R.id.txtStatus);
            this.listener=listener;
            v.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            listener.onItemClick(view,getAdapterPosition());
        }
    }


}

interface ItemClienteListener{
    void onItemClick(View view, int position);
}


