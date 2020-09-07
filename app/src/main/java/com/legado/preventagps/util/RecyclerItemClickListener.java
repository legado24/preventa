package com.legado.preventagps.util;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by __Adrian__ on 13/5/2017.
 */

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener{

    protected SimpleOnItemClickListener listener;
    private GestureDetector gestureDetector;
    private View childView;
    private int childViewPosition;

    public RecyclerItemClickListener(Context context, SimpleOnItemClickListener listener) {
       this.gestureDetector=new GestureDetector(context,new GestureListener());
        this.listener=listener;
    }



    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        childView=rv.findChildViewUnder(e.getX(),e.getY());
        childViewPosition=rv.getChildPosition(childView);
        return childView!=null&&gestureDetector.onTouchEvent(e);
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }


    public interface OnItemClickListener{
        public void onItemClick(View childView, int position);
        public void onItemLongPress(View childView, int position);
    }


    public static abstract class SimpleOnItemClickListener implements OnItemClickListener{
        @Override
        public void onItemClick(View childView, int position) {

        }

        @Override
        public void onItemLongPress(View childView, int position) {

        }
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if(childView!=null){
                listener.onItemClick(childView,childViewPosition);

            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if(childView!=null){
                listener.onItemLongPress(childView,childViewPosition);

            }
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
}
