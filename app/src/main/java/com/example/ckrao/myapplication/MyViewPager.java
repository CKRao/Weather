package com.example.ckrao.myapplication;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by clark on 2016/12/9.
 */

public class MyViewPager extends ViewPager {
    private float preY = 0;

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            preY = (int) ev.getY();
        } else {
            if (Math.abs((int) ev.getY() - preY) > 10) {
                return false;
            } else {
                preY = (int) ev.getY();
            }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
