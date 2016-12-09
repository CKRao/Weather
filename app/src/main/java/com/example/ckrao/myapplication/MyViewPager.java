package com.example.ckrao.myapplication;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by clark on 2016/12/9.
 */

public class MyViewPager extends ViewPager {
    private float preX = 0;

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            preX = (int) ev.getX();
        } else {
            if (Math.abs((int) ev.getX() - preX) > 10) {
                return true;
            } else {
                preX = (int) ev.getX();
            }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
