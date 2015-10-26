package com.wogebi.android.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import com.wogebi.android.BaseActivity;
import com.wogebi.android.R;
import com.wogebi.android.adapter.SignedAdapter;
import com.wogebi.android.db.WalkwayDBHelper;
import com.wogebi.android.entity.WalkwayEntity;
import com.wogebi.android.view.Topbar;

public class WalkwayListActivity extends BaseActivity
{
    private static final String TAG = "WalkwayListActivity";

    private static final String TITILE = "数据浏览";

    private Topbar topbar;
    private Button refresh;

    private SignedAdapter adapter;
    private List<WalkwayEntity> entities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews()
    {
        setContentView(R.layout.activity_walkway_list);

        topbar = (Topbar) findViewById(R.id.walkway_list_topbar);
        topbar.setLogo(true);
        topbar.setLine(true);
        topbar.setTitle(TITILE);

        refresh = (Button) findViewById(R.id.walkway_list_refresh);

        ListView lv = (ListView) findViewById(R.id.walkway_list_view);
        adapter = new SignedAdapter(application, this, entities);
        lv.setAdapter(adapter);
    }

    @Override
    protected void initEvents()
    {
        refresh.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                topbar.setTitle(TITILE);

                addTask(new AsyncTask<Void, Void, Boolean>()
                {
                    @Override
                    protected Boolean doInBackground(Void... params)
                    {
                        entities.clear();

                        try
                        {
                            entities.addAll(WalkwayDBHelper.query(getApplicationContext(), "id DESC"));
                        }
                        catch (Exception e)
                        {
                            return false;
                        }

                        return true;
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean)
                    {
                        super.onPostExecute(aBoolean);

                        if (aBoolean || entities == null)
                        {
                            topbar.setTitle(TITILE + " (" + entities.size() + ")");
                            adapter.notifyDataSetChanged();
                        }
                        else
                        {
                            showToast("读取数据错误");
                        }
                    }
                });
            }
        });
    }
}