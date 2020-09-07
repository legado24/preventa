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

public class DropDownCondicionAdapter extends RecyclerView.Adapter<DropDownCondicionAdapter.CondicionViewHolder> {

    private final ViewActions actionViewDelegate;
    private int cantidad;

    public DropDownCondicionAdapter(final ViewActions actionViewDelegate, int cantidad) {
        this.actionViewDelegate = actionViewDelegate;
        this.cantidad=cantidad;
    }

    @Override
    public CondicionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_condicion_drop_down, parent, false);
        return new CondicionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CondicionViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return cantidad;
    }

    class CondicionViewHolder extends RecyclerView.ViewHolder {

        private final TextView condicionTitle;


        public CondicionViewHolder(View itemView) {
            super(itemView);
            condicionTitle = (TextView) itemView.findViewById(R.id.condicionTitle);
            itemView.setBackgroundDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.stand_drop_down_selector));
            itemView.setOnClickListener(condicionViewItemClickListener);
        }

        public void bind(int position) {
            condicionTitle.setText(actionViewDelegate.getCondicionTitle(position));
            itemView.setSelected(actionViewDelegate.getSelectedCondicion()== position);
        }

        private final View.OnClickListener condicionViewItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lastSelectedPosition = actionViewDelegate.getSelectedCondicion();
                actionViewDelegate.setSelectedCondicion(getAdapterPosition());
                notifyItemChanged(lastSelectedPosition);
                notifyItemChanged(getAdapterPosition());
                actionViewDelegate.collapseDropDown();
            }
        };
    }

    public interface ViewActions {
        void collapseDropDown();
        void setSelectedCondicion(int standId);
        String getCondicionTitle(int standId);

        int getSelectedCondicion();
    }
}
