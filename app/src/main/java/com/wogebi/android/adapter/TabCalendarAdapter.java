package com.wogebi.android.adapter;

import android.support.v4.app.FragmentActivity;

import com.dougfii.android.core.adapter.BaseTabPagerAdapter;
import com.dougfii.android.core.base.BaseFragment;

import java.util.List;

public class TabCalendarAdapter extends BaseTabPagerAdapter {
    public TabCalendarAdapter(FragmentActivity fragmentActivity, List<? extends BaseFragment> fragments, String[] titles) {
        super(fragmentActivity, fragments, titles);
    }
}
