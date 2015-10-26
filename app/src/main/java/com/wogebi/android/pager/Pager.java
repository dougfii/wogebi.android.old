package com.wogebi.android.pager;

import android.support.v4.view.ViewPager;

public interface Pager extends ViewPager.OnPageChangeListener
{
    void setViewPager(ViewPager view);

    void setViewPager(ViewPager view, int position);

    void setCurrentItem(int i);

    void setOnPageChangeListener(ViewPager.OnPageChangeListener l);

    void notifyDataSetChanged();
}
