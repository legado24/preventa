package com.legado.preventagps.util;

import androidx.viewpager.widget.ViewPager;
import androidx.cardview.widget.CardView;
import android.view.View;

import com.legado.preventagps.adapter.vendedor.CardAdapter;

/**
 * Created by __Adrian__ on 2/04/2019.
 */

public class ShadowTransformer implements ViewPager.OnPageChangeListener,ViewPager.PageTransformer {
    private ViewPager mViewPager;
    private CardAdapter mAdapter;
    private float mLastOffset;
    private boolean mScalingEnabled;

    public ShadowTransformer(ViewPager viewPager,CardAdapter adapter) {
    this.mViewPager=viewPager;
        viewPager.addOnPageChangeListener(this);
        this.mAdapter=adapter;
    }

    public void enableScaling(boolean enabled){
        if(mScalingEnabled&& !enabled){

            CardView currentCard=mAdapter.getCardViewAt(mViewPager.getCurrentItem());
            if(currentCard!=null){
                currentCard.animate().scaleY(1);
                currentCard.animate().scaleX(1);

            }

        }else if(!mScalingEnabled&&enabled){
            CardView currentCard=mAdapter.getCardViewAt(mViewPager.getCurrentItem());

            if(currentCard!=null){
                currentCard.animate().scaleY(1.1f);
                currentCard.animate().scaleX(1.1f);

            }

        }
        mScalingEnabled=enabled;

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        int realCurrentPosition;
        int nextPosition;
        float baseElevation=mAdapter.getBaseElevation();
        float realOffset;
        boolean goingLeft=mLastOffset>positionOffset;


        if(goingLeft){
            realCurrentPosition=position+1;
            nextPosition=position;
            realOffset=1-positionOffset;
        }else{
            nextPosition=position+1;
            realCurrentPosition=position;
            realOffset=positionOffset;
        }

        if(nextPosition>mAdapter.getCount()-1||realCurrentPosition>mAdapter.getCount()-1){
            return;
        }

        CardView currentCard=mAdapter.getCardViewAt(realCurrentPosition);


        if(currentCard!=null){
            if(mScalingEnabled){
                currentCard.setScaleX((float)(1+0.1*(1-realOffset)));
                currentCard.setScaleY((float)(1+0.1*(1-realOffset)));
           }
            currentCard.setCardElevation(baseElevation+baseElevation*(CardAdapter.MAX_ELEVATION_FACTOR-1)*(1-realOffset));

        }
        CardView nextCard=mAdapter.getCardViewAt(nextPosition);

        if(nextCard!=null){
            if(mScalingEnabled){
                nextCard.setScaleX((float)(1+0.1*(realOffset)));
                nextCard.setScaleY((float)(1+0.1*(realOffset)));
            }
            nextCard.setCardElevation(baseElevation+baseElevation*(CardAdapter.MAX_ELEVATION_FACTOR-1)*(realOffset));

        }
        mLastOffset=positionOffset;






    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void transformPage(View page, float position) {

    }
}
