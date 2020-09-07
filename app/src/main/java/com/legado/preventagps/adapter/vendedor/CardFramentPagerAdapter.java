package com.legado.preventagps.adapter.vendedor;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.cardview.widget.CardView;
import android.view.ViewGroup;

import com.legado.preventagps.fragments.CardFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by __Adrian__ on 2/04/2019.
 */

public class CardFramentPagerAdapter extends FragmentStatePagerAdapter implements CardAdapter {

    private List<CardFragment> mFragments;
    private float mBaseElevation;

    public CardFramentPagerAdapter(FragmentManager fm, float mBaseElevation) {
        super(fm);
        this.mFragments = new ArrayList<>();
        this.mBaseElevation = mBaseElevation;

        for (int i = 0; i <5 ; i++) {
            addCardFragment(new CardFragment());
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int posicion) {
        return mFragments.get(posicion).getmCardView();
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment=super.instantiateItem(container,position);
        mFragments.set(position,(CardFragment)fragment);
        return  fragment;
    }

    public void addCardFragment(CardFragment fragment){
        mFragments.add(fragment);

    }
}
