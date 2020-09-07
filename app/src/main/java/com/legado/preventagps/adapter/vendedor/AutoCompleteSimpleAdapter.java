package com.legado.preventagps.adapter.vendedor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.legado.preventagps.R;

import java.util.ArrayList;

/**
 * Created by __Adrian__ on 15/04/2019.
 */

public class AutoCompleteSimpleAdapter extends ArrayAdapter<Object[]> {
    private final String MY_DEBUG_TAG = "CustomerAdapter";
    private ArrayList items;
    private ArrayList itemsAll;
    private ArrayList  suggestions;
    private int viewResourceId;

    public AutoCompleteSimpleAdapter(Context context, int viewResourceId, ArrayList items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll =(ArrayList) items.clone();
        this.suggestions = new ArrayList<Object[]>();
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        Object[] dpto = (Object[]) items.get(position);
        if (dpto != null) {
            TextView customerNameLabel = (TextView) v.findViewById(R.id.desc);
            if (customerNameLabel != null) {
//              Log.i(MY_DEBUG_TAG, "getView Customer Name:"+customer.getName());
                customerNameLabel.setText(dpto[1].toString());
            }
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            Object[] o= (Object[]) resultValue;
            return o[1].toString();
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();
                for (Object customer : itemsAll) {
                    ArrayList obj= (ArrayList) customer;
                    if(obj.get(1).toString().toLowerCase().startsWith(constraint.toString().toLowerCase())){
                        suggestions.add(customer);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList filteredList = (ArrayList) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (Object c : filteredList) {
                   ArrayList cc= (ArrayList) c;
                    Object aa[]=new Object[2];
                    aa[0]=cc.get(0).toString();
                    aa[1]=cc.get(1).toString();
                    add(aa);
                }
                notifyDataSetChanged();
            }
        }
    };

}