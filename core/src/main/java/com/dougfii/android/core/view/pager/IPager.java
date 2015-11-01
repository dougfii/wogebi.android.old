package com.dougfii.android.core.view.pager;

import android.support.v4.view.ViewPager;

/**
 * Created by momo on 15/11/1.
 */
public interface IPager extends ViewPager.OnPageChangeListener {
    void setViewPager(ViewPager view);

    void setViewPager(ViewPager view, int position);

    void setCurrentItem(int i);

    void setOnPageChangeListener(ViewPager.OnPageChangeListener l);

    void notifyDataSetChanged();
}
