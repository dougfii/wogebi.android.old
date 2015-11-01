package com.wogebi.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dougfii.android.core.base.BaseFragment;
import com.dougfii.android.core.view.Topbar;
import com.wogebi.android.AppApplication;
import com.wogebi.android.R;

public class TabContactFragment extends BaseFragment<AppApplication> {
    public TabContactFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public TabContactFragment(AppApplication application, Activity activity, Context context) {
        super(application, activity, context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab_contact, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initViews() {
        Topbar topbar = (Topbar) findViewById(R.id.contact_topbar);
        topbar.setLogo(true);
        topbar.setLine(false);
        topbar.setTitle(getString(R.string.tab_contact));
    }

    @Override
    protected void initEvents() {
    }
}