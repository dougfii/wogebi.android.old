package com.wogebi.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wogebi.android.BaseApplication;
import com.wogebi.android.BaseFragment;
import com.wogebi.android.R;
import com.wogebi.android.view.Topbar;

public class TabMessageFragment extends BaseFragment
{
    public TabMessageFragment()
    {
        super();
    }

    @SuppressLint("ValidFragment")
    public TabMessageFragment(BaseApplication application, Activity activity, Context context)
    {
        super(application, activity, context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_tab_message, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initViews()
    {
        Topbar topbar = (Topbar) findViewById(R.id.message_topbar);
        topbar.setLogo(true);
        topbar.setLine(false);
        topbar.setTitle(getString(R.string.tab_message));
    }

    @Override
    protected void initEvents()
    {
    }
}