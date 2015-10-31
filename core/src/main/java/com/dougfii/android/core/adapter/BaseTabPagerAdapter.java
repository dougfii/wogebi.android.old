package com.dougfii.android.core.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.dougfii.android.core.base.BaseFragment;

import java.util.List;

/**
 * Created by momo on 15/11/1.
 */
public abstract class BaseTabPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    protected FragmentActivity fragmentActivity;
    protected List<? extends BaseFragment> fragments;
    protected String[] titles;

    public BaseTabPagerAdapter(FragmentActivity fragmentActivity, List<? extends BaseFragment> fragments, String[] titles) {
        this.fragmentActivity = fragmentActivity;
        this.fragments = fragments;
        this.titles = titles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position % getCount()];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = fragments.get(position);
        if (!fragment.isAdded()) {
            FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
            ft.add(fragment, fragment.getClass().getSimpleName());
            ft.commit();
            // 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
            // 会在进程的主线程中，用异步的方式来执行。 如果想要立即执行这个等待中的操作，就要调用这个方法（只能在主线程中调用）。
            // 要注意的是，所有的回调和相关的行为都会在这个调用中被执行完成，因此要仔细确认这个方法的调用位置。
            fragmentActivity.getSupportFragmentManager().executePendingTransactions();
        }

        if (fragment.getView().getParent() == null) {
            container.addView(fragment.getView());
        }

        return fragment.getView();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(fragments.get(position).getView());
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        showPage(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    private void showPage(int i) {
        fragments.get(i).onPause();
        if (fragments.get(i).isAdded()) {
            fragments.get(i).onResume();
        }
    }
}
