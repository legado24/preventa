package com.legado.preventagps.adapter.vendedor;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.legado.preventagps.R;

/**
 * Created by __Adrian__ on 20/03/2019.
 */

public class DropDownAdapter extends RecyclerView.Adapter<DropDownAdapter.StandViewHolder> {

    private final ViewActions actionViewDelegate;
    private int cantidad;

    public DropDownAdapter(final ViewActions actionViewDelegate,int cantidad) {
        this.actionViewDelegate = actionViewDelegate;
        this.cantidad=cantidad;
    }

    @Override
    public StandViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_stand_drop_down, parent, false);
        return new StandViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StandViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return cantidad;
    }

    class StandViewHolder extends RecyclerView.ViewHolder {

        private final TextView tipoPedido;
//        private final TextView mesa;
//        private final TextView codigo;
        private final TextView canal;

        public StandViewHolder(View itemView) {
            super(itemView);
            tipoPedido = (TextView) itemView.findViewById(R.id.tipoPedido);
//            mesa = (TextView) itemView.findViewById(R.id.mesa);
//            codigo= (TextView) itemView.findViewById(R.id.cod);
            canal=(TextView)itemView.findViewById(R.id.canal);
            itemView.setBackgroundDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.stand_drop_down_selector));
            itemView.setOnClickListener(standViewItemClickListener);
        }

        public void bind(int position) {
            tipoPedido.setText(actionViewDelegate.getTipoPedido(position));
//            mesa.setText(actionViewDelegate.getMesa(position));
//            codigo.setText(actionViewDelegate.getCodigo(position));
            canal.setText(actionViewDelegate.getCanal(position));
            itemView.setSelected(actionViewDelegate.getSelectedStand() == position);
        }

        private final View.OnClickListener standViewItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lastSelectedPosition = actionViewDelegate.getSelectedStand();
                actionViewDelegate.setSelectedStand(getAdapterPosition());
                notifyItemChanged(lastSelectedPosition);
                notifyItemChanged(getAdapterPosition());
                actionViewDelegate.collapseDropDown();
            }
        };
    }

    public interface ViewActions {
        void collapseDropDown();
        void setSelectedStand(int standId);
        String getTipoPedido(int standId);
        String getMesa(int standId);
        String getCodigo(int standId);
        String getCanal(int standId);
        int getSelectedStand();
    }
}
