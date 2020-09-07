package com.legado.preventagps.modelo;

/**
 * Created by __Adrian__ on 2/04/2019.
 */

public class CardItem {
    private int mTextResource;
    private int mTitleResource;

    public CardItem(int mTitleResource,int mTextResource) {
        this.mTextResource = mTextResource;
        this.mTitleResource = mTitleResource;
    }

    public int getmTextResource() {
        return mTextResource;
    }

    public int getmTitleResource() {
        return mTitleResource;
    }
}
