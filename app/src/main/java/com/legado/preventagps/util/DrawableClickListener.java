package com.legado.preventagps.util;

/**
 * Created by __Adrian__ on 22/3/2017.
 */

public interface DrawableClickListener {
    public static enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT };
    public void onClick(DrawablePosition target);
}
