package com.example.ckrao.myapplication.Adapter;


import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by clark on 2016/12/3.
 */

public class MyViewPagerAdapter extends PagerAdapter {
    private List<View> mViews;
    private String[] mTitles = {"主界面", "多城市"};

    public MyViewPagerAdapter(List<View> viewList) {
        mViews = viewList;
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
