package com.wogebi.android;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.RemoteException;
import android.os.SystemClock;

import com.baidu.mapapi.SDKInitializer;

import com.wogebi.android.broadcast.WalkwayAlarmBroadcastReceiver;
import com.wogebi.android.log.CrashHandler;
import com.wogebi.android.log.L;
import com.wogebi.android.service.IAliveService;
import com.wogebi.android.service.WalkwayService;
import com.wogebi.android.utils.ServiceUtils;

public class BaseApplication extends Application
{
    private static final String TAG = "BaseApplication";

    private static BaseApplication instance;

    private static final int DELAY_ALARM = 3 * 60 * 1000; //闹钟定时

    public static BaseApplication getInstance()
    {
        if (instance == null)
        {
            instance = new BaseApplication();
        }

        return instance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        //TODO:开启调试
        L.debug(true);
        L.logs(true);

        init();
    }

    private void init()
    {
        CrashHandler.getInstance().init(this);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        //注意：在SDK各功能组件使用之前都需要调用
        //因此我们建议该方法放在Application的初始化方法中
        SDKInitializer.initialize(this);

        keep();
        //alarm();
    }

    private IAliveService walkway = new IAliveService.Stub()
    {
        @Override
        public void alive() throws RemoteException
        {
            Intent intent = new Intent(getBaseContext(), WalkwayService.class);
            intent.putExtra(WalkwayService.START_MODE, WalkwayService.START_MANUAL);
            getBaseContext().startService(intent);
        }
    };

    private void keep()
    {
        boolean run = ServiceUtils.isProcessRunning(this, WalkwayService.PROCESS_NAME_WALKWAY);
        if (!run)
        {
            try
            {
                L.i(TAG, "keep WalkwayService");
                walkway.alive();
            }
            catch (RemoteException e)
            {
                //
            }
        }
    }

    private void alarm()
    {
        L.i(TAG, "alarm walkway");

        Intent intent = new Intent(WalkwayAlarmBroadcastReceiver.ACTION_WALKWAY_ALARM).setPackage(getPackageName());
        intent.putExtra(WalkwayService.START_MODE, WalkwayService.START_ALARM);

        PendingIntent i = PendingIntent.getBroadcast(this, 0, intent, 0);

        long first = SystemClock.elapsedRealtime();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, first, DELAY_ALARM, i);

        L.i(TAG, "alarm walkway service");
    }
}
