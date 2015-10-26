package com.wogebi.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.wogebi.android.BaseActivity;
import com.wogebi.android.BaseFragment;
import com.wogebi.android.R;
import com.wogebi.android.adapter.TabMainAdapter;

public class MainActivity extends BaseActivity
{
    public List<BaseFragment> fragments = new ArrayList<>();
    private TabMainAdapter adapter;
    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews()
    {
        setContentView(R.layout.activity_main);

        RadioGroup tabs = (RadioGroup) findViewById(R.id.tabs);

        fragments.add(new TabHomeFragment(application, this, this));
        fragments.add(new TabMessageFragment(application, this, this));
        fragments.add(new TabDiscoveryFragment(application, this, this));
        fragments.add(new TabContactFragment(application, this, this));
        fragments.add(new TabPersonalFragment(application, this, this));

        adapter = new TabMainAdapter(this, fragments, R.id.content, tabs);
    }

    @Override
    protected void initEvents()
    {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fm = adapter.getCurrentFragment();
        if (fm != null)
        {
            fm.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void finish()
    {
        moveTaskToBack(true);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        if (keyCode == KeyEvent.KEYCODE_BACK)
//        {
//            //doubleClickExit();
//            //moveTaskToBack(false);
//            return true;
//        }
//
//        // return false;
//        return super.onKeyDown(keyCode, event);
//    }

    protected void doubleClickExit()
    {
        Timer timer;
        if (exit)
        {
            finish();
            System.exit(0);
        }
        else
        {
            exit = true;
            showToast(R.string.double_click_exit);
            timer = new Timer();
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    exit = false;
                }
            }, 2000);

        }
    }
}