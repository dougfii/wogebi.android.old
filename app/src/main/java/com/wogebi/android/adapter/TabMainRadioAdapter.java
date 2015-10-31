package com.wogebi.android.adapter;

import android.support.v4.app.FragmentActivity;
import android.widget.RadioGroup;

import com.dougfii.android.core.adapter.BaseTabRadioAdapter;
import com.dougfii.android.core.base.BaseFragment;

import java.util.List;

public class TabMainRadioAdapter extends BaseTabRadioAdapter {

    public TabMainRadioAdapter(FragmentActivity fragmentActivity, List<? extends BaseFragment> fragments, int fragmentLayoutResId, RadioGroup radioGroup) {
        super(fragmentActivity, fragments, fragmentLayoutResId, radioGroup);
    }
}
