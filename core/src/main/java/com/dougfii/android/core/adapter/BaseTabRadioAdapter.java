package com.dougfii.android.core.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import com.dougfii.android.core.base.BaseFragment;

import java.util.List;

/**
 * Created by momo on 15/11/1.
 */
public abstract class BaseTabRadioAdapter implements RadioGroup.OnCheckedChangeListener {
    protected List<? extends BaseFragment> fragments;
    protected RadioGroup radioGroup;
    protected FragmentActivity fragmentActivity;
    protected int fragmentLayoutResId;
    protected int tabId = 0;

    public BaseTabRadioAdapter(FragmentActivity fragmentActivity, List<? extends BaseFragment> fragments, int fragmentLayoutResId, RadioGroup radioGroup) {
        this.fragmentActivity = fragmentActivity;
        this.fragments = fragments;
        this.fragmentLayoutResId = fragmentLayoutResId;
        this.radioGroup = radioGroup;

        FragmentTransaction ft = getFragmentTransaction();
        ft.add(fragmentLayoutResId, fragments.get(0));
        ft.commit();

        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            if (radioGroup.getChildAt(i).getId() == checkedId) {
                Fragment fragment = fragments.get(i);
                FragmentTransaction ft = getFragmentTransaction();

                getCurrentFragment().onPause();

                if (fragment.isAdded()) {
                    fragment.onResume();
                } else {
                    ft.add(fragmentLayoutResId, fragment);
                }

                showTab(i);

                ft.commit();
            }
        }
    }

    protected FragmentTransaction getFragmentTransaction() {
        return fragmentActivity.getSupportFragmentManager().beginTransaction();
    }

    protected void showTab(int idx) {
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            FragmentTransaction ft = getFragmentTransaction();

            if (idx == i) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }

            ft.commit();
        }

        tabId = idx;
    }

    public int getCurrentTabId() {
        return tabId;
    }

    public Fragment getCurrentFragment() {
        return fragments.get(tabId);
    }
}
