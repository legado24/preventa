package com.legado.preventagps.adapter.vendedor;

import androidx.viewpager.widget.PagerAdapter;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.legado.preventagps.R;
import com.legado.preventagps.modelo.CardItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by __Adrian__ on 2/04/2019.
 */

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<CardItem> mData;
    private float mBaseElevation;

    public CardPagerAdapter() {
        this.mViews =new ArrayList<>();
        this.mData = new ArrayList<>();
    }

    public void addCardItem(CardItem item){
        mViews.add(null);
        mData.add(item);
    }

    @Override
    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int posicion) {
        return mViews.get(posicion);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view= LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_adapter,container,false);
        container.addView(view);
    bind(mData.get(position),view);
        CardView cardView=view.findViewById(R.id.cardView);
        if(mBaseElevation==0){
            mBaseElevation=cardView.getCardElevation();
        }
        cardView.setMaxCardElevation(mBaseElevation*MAX_ELEVATION_FACTOR);
        mViews.set(position,cardView);
        return  view;

    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
container.removeView((View)object);
        mViews.set(position,null);
    }


    private void bind(CardItem item,View view){
        TextView titleTextView=(TextView) view.findViewById(R.id.titleTextView);
        TextView contentTextView=(TextView) view.findViewById(R.id.contentTextView);
        titleTextView.setText(item.getmTitleResource());
        contentTextView.setText(item.getmTextResource());
    }

}
