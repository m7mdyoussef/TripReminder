package com.example.tripreminder2021.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class MyViewPagerAdapter extends PagerAdapter {

    private LayoutInflater layoutInflater;
    private int[] layouts;
    private Context context;

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
    public  MyViewPagerAdapter(Context context1 ,int [] layouts1)
    {
        context=context1;
        layouts=layouts1;
    }

    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layouts[position], container, false);
        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
