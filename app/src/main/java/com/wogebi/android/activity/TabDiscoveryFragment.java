package com.wogebi.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.dougfii.android.core.base.BaseFragment;
import com.dougfii.android.core.view.Topbar;
import com.wogebi.android.AppApplication;
import com.wogebi.android.R;

public class TabDiscoveryFragment extends BaseFragment<AppApplication> {
    public TabDiscoveryFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public TabDiscoveryFragment(AppApplication application, Activity activity, Context context) {
        super(application, activity, context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab_discovery, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initViews() {
        Topbar topbar = (Topbar) findViewById(R.id.discovery_topbar);
        topbar.setLogo(true);
        topbar.setLine(false);
        topbar.setTitle(getString(R.string.tab_discovery));
    }

    @Override
    protected void initEvents() {
    }

    private void test() {
        TableLayout table = (TableLayout) findViewById(R.id.mytable);
        for (int i = 0; i < 3; i++) {
            TableRow row = new TableRow(context);

            Button btn = new Button(context);
            btn.setText("Button " + i);

            row.addView(btn);

            table.addView(row, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }
}