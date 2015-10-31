package com.dougfii.android.core.base;

import android.app.Service;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by momo on 15/11/1.
 */
public abstract class BaseService<T extends BaseApplication> extends Service {
    protected T application;
    protected List<AsyncTask<Void, Void, Boolean>> tasks = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        application = (T) getApplication();
    }

    @Override
    public void onDestroy() {
        clearTasks();
        super.onDestroy();
    }

    protected void addTask(AsyncTask<Void, Void, Boolean> task) {
        tasks.add(task.execute());
    }

    protected void clearTasks() {
        for (AsyncTask<Void, Void, Boolean> task : tasks) {
            if (task != null && !task.isCancelled()) {
                task.cancel(true);
            }
        }

        tasks.clear();
    }
}
