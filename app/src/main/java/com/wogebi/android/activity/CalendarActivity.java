package com.wogebi.android.activity;

import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import com.dougfii.android.core.base.BaseActivity;
import com.dougfii.android.core.view.Topbar;
import com.dougfii.android.core.view.pager.TabPager;
import com.wogebi.android.AppApplication;
import com.wogebi.android.R;
import com.wogebi.android.adapter.TabCalendarAdapter;

public class CalendarActivity extends BaseActivity<AppApplication> {
    private static final String TAG = "CalendarActivity";

    public final String[] TITLES = new String[]{"我的日程", "全部日程"};

    private Topbar topbar;
    private TabPager tab;

    public List<CalendarSection> sections = new ArrayList<>();

    private int currentPage = 0;

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_calendar);

        topbar = (Topbar) findViewById(R.id.calendar_topbar);
        topbar.setLogo(true);
        topbar.setLine(true);
        topbar.setTitle(TITLES[0]);

        for (int i = 0; i < TITLES.length; i++) {
            sections.add(new CalendarSection(application, this, this, topbar, i));
        }

        ViewPager pager = (ViewPager) findViewById(R.id.calendar_pager);
        tab = (TabPager) findViewById(R.id.calendar_tab);
        TabCalendarAdapter adapter = new TabCalendarAdapter(this, sections, TITLES);
        pager.setAdapter(adapter);
        tab.setViewPager(pager);
    }

    @Override
    protected void initEvents() {
        tab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                topbar.onTopbarRefreshComplete();
                topbar.setTitle(TITLES[i]);
                currentPage = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        topbar.setOnTopbarRefreshClickListener(new Topbar.OnTopbarRefreshClickListener() {
            @Override
            public void onTopbarRefreshClick() {
                sections.get(currentPage).onHeaderManualRefresh();
            }
        });

        topbar.setOnTopbarAddClickListener(new Topbar.OnTopbarAddClickListener() {
            @Override
            public void onTopbarAddClick() {
                startActivity(CalendarAddActivity.class);
            }
        });
    }
}