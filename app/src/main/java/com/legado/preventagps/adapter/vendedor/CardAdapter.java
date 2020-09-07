package com.legado.preventagps.adapter.vendedor;

import androidx.cardview.widget.CardView;

/**
 * Created by __Adrian__ on 2/04/2019.
 */

public interface CardAdapter {
    int MAX_ELEVATION_FACTOR=8;
    float getBaseElevation();
    CardView getCardViewAt(int posicion);
    int getCount();
}
