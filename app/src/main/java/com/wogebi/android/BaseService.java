package com.wogebi.android;

import android.app.Service;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseService extends Service
{
    protected BaseApplication application;
    protected List<AsyncTask<Void, Void, Boolean>> tasks = new ArrayList<>();

    @Override
    public void onCreate()
    {
        super.onCreate();
        application = (BaseApplication) getApplication();
    }

    @Override
    public void onDestroy()
    {
        clearTasks();
        super.onDestroy();
    }

    protected void addTask(AsyncTask<Void, Void, Boolean> task)
    {
        tasks.add(task.execute());
    }

    protected void clearTasks()
    {
        for (AsyncTask<Void, Void, Boolean> task : tasks)
        {
            if (task != null && !task.isCancelled())
            {
                task.cancel(true);
            }
        }

        tasks.clear();
    }
}
