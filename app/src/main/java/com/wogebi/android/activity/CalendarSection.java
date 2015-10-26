package com.wogebi.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import com.wogebi.android.BaseApplication;
import com.wogebi.android.BaseFragment;
import com.wogebi.android.R;
import com.wogebi.android.adapter.CalendarAdapter;
import com.wogebi.android.entity.CalendarEntity;
import com.wogebi.android.entity.ResolveEntity;
import com.wogebi.android.entity.ResultsEntity;
import com.wogebi.android.log.L;
import com.wogebi.android.model.Constants;
import com.wogebi.android.model.Model;
import com.wogebi.android.utils.HttpUtils;
import com.wogebi.android.utils.Utils;
import com.wogebi.android.view.PullToRefreshListView;
import com.wogebi.android.view.Topbar;

public class CalendarSection extends BaseFragment implements AdapterView.OnItemClickListener, PullToRefreshListView.OnHeaderRefreshListener, PullToRefreshListView.OnHeaderManualRefreshListener, PullToRefreshListView.OnHeaderCancelListener, PullToRefreshListView.OnFooterRefreshListener
{
    private static final String TAG = "CalendarSection";

    private Topbar topbar;
    private PullToRefreshListView lv;
    private List<CalendarEntity> entities = new ArrayList<>();
    private CalendarAdapter adapter;
    private int index;

    private static final String URL = Constants.URL_SERVER + Constants.MODEL_CALENDAR_LIST;
    private ResultsEntity<CalendarEntity> ret;
    private int count = 0;
    private int pages = 0;
    private int page = 1;

    public CalendarSection()
    {
        super();
    }

    @SuppressLint("ValidFragment")
    public CalendarSection(BaseApplication application, Activity activity, Context context, Topbar topbar, int index)
    {
        super(application, activity, context);
        this.topbar = topbar;
        this.index = index;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.section_calendar, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initViews()
    {
        lv = (PullToRefreshListView) findViewById(R.id.calendar_listview);
        adapter = new CalendarAdapter(application, context, entities);
        lv.setAdapter(adapter);
    }

    @Override
    protected void initEvents()
    {
        lv.setOnItemClickListener(this);
        lv.setOnHeaderRefreshListener(this);
        lv.setOnHeaderManualRefreshListener(this);
        lv.setOnHeaderCancelListener(this);
        lv.setOnFooterRefreshListener(this);

        loadList(true);
    }

    @Override
    public void onHeaderRefresh()
    {
        loadList(true);
    }

    @Override
    public void onFooterRefresh()
    {
        loadList(false);
    }

    @Override
    public void onHeaderManualRefresh()
    {
        lv.onHeaderManualRefresh();
    }

    @Override
    public void onHeaderCancel()
    {
        clearTasks();
        lv.onHeaderRefreshComplete();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
    }

    private void loadList(final boolean direct)
    {
        L.i(TAG, index + " Direct:" + direct + " Size:" + entities.size() + " Count:" + count);
//        if (!direct && entities.size() >= count)
//        {
//            lv.onFooterNoMore();
//            return;
//        }

        L.i(TAG, "*** " + index + " *** CurrentPage:" + page);
        addTask(new AsyncTask<Void, Void, Boolean>()
        {
            @Override
            protected Boolean doInBackground(Void... params)
            {
                try
                {
                    if (direct)
                    {
                        page = 1;
                        entities.clear();
                    }

                    final String url = URL + "&uid=" + Model.My.getId() + "&page=" + page + "&power=" + index;
                    L.i(TAG, url);

                    HttpUtils http = new HttpUtils();
                    String json = http.get(url);
                    L.i(TAG, json);

                    ret = (new ResolveEntity()).getCalendarList(json);
                    if (ret != null && !ret.getMsg().equals(""))
                    {
                        count = Utils.toInteger(ret.getMsg());
                        pages = Utils.getPageCount(count, Constants.DEFAULT_PAGE_SIZE);
                        L.i(TAG, index + " Count:" + count + " PageCount:" + pages + " CurrentPage:" + page);

                        if (page > 0 && pages >= page)
                        {
                            if (ret.getData() != null && ret.getData().size() > 0)
                            {
                                for (int i = ret.getData().size() - 1; i >= 0; i--)
                                {
                                    if (!entities.contains(ret.getData().get(i)))
                                    {
                                        if (direct)
                                        {
                                            entities.add(0, ret.getData().get(i));
                                        }
                                        else
                                        {
                                            entities.add(ret.getData().get(i));
                                        }
                                    }
                                }
                            }
                        }
                    }

                    return true;
                }
                catch (Exception ex)
                {
                    //
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                super.onPostExecute(result);
                lv.onHeaderRefreshComplete();
                topbar.onTopbarRefreshComplete();

                if (result && ret != null && ret.getCode() == 1)
                {
                    page++;

                    if (page < 1)
                    {
                        page = 1;
                    }

                    if (page > pages)
                    {
                        page = pages;
                    }

                    L.i(TAG, index + " NextPage:" + page);


                    if (entities.size() >= count)
                    {
                        lv.onFooterNoMore();
                    }
                    else
                    {
                        lv.onFooterReset();
                    }

                    if (count == 0)
                    {
                        showToast(getString(R.string.no_data));
                    }

                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}