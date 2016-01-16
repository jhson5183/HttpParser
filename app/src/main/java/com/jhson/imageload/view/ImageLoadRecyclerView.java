package com.jhson.imageload.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by jhson on 2016-01-16.
 */
public class ImageLoadRecyclerView extends RecyclerView{
    public ImageLoadRecyclerView(Context context) {
        super(context);
    }

    public ImageLoadRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageLoadRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public int getVerticalScrollOffset(){
        return super.computeVerticalScrollOffset();
    }
}
