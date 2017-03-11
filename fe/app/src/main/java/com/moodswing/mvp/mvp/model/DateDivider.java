package com.moodswing.mvp.mvp.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Matthew on 2017-03-08.
 */

public class DateDivider extends RecyclerView.ItemDecoration{

    private Drawable dDivider;

    public DateDivider(Context context, int resId) {
        dDivider = ContextCompat.getDrawable(context, resId);
    }

//    @Override
//    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
//            drawSeparator(c, parent);
//    }
//
//    public void drawSeparator(Canvas c, RecyclerView parent) {
//        final int left = parent.getPaddingLeft();
//        final int right = parent.getWidth() - parent.getPaddingRight();
//        final int childCount = parent.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            final View child = parent.getChildAt(i);
//            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
//            final int top = child.getBottom() + params.bottomMargin;
//            final int bottom = top + dDivider.getIntrinsicHeight();
//            dDivider.setBounds(left, top, right, bottom);
//            dDivider.draw(c);
//        }
//    }
//
//    @Override
//    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            outRect.set(0, 0, 0, dDivider.getIntrinsicHeight());
//    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int bottom = child.getTop() + params.topMargin ;
            int top =  bottom - dDivider.getIntrinsicHeight();

            dDivider.setBounds(left, top, right, bottom);
            dDivider.draw(c);
        }
    }
}
