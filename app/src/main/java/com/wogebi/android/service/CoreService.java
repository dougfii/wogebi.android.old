package com.wogebi.android.service;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.dougfii.android.core.base.BaseService;
import com.dougfii.android.core.log.L;
import com.dougfii.android.core.utils.ServiceUtils;
import com.wogebi.android.AppApplication;

public class CoreService extends BaseService<AppApplication> {
    private static final String TAG = "CoreService";

    public static final String ACTION_CORE_SERVICE = "com.wogebi.com.wogebi.android.service.CoreService";
    public static final String PROCESS_NAME_CORE = "com.wogebi.com.wogebi.android:core";

    private IAliveService walkway = new IAliveService.Stub() {
        @Override
        public void alive() throws RemoteException {
            Intent intent = new Intent(getBaseContext(), WalkwayService.class);
            getBaseContext().startService(intent);
        }
    };

    private void keep() {
        boolean run = ServiceUtils.isProcessRunning(this, WalkwayService.PROCESS_NAME_WALKWAY);
        if (!run) {
            try {
                L.i(TAG, "重新启动 WalkwayService");
                walkway.alive();
            } catch (RemoteException e) {
                //
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // return (IBinder) walkway;
        return null;
    }

    @Override
    public void onLowMemory() {
        L.i(TAG, "onLowMemory");
        keep();
    }

    @Override
    public void onTrimMemory(int level) {
        L.i(TAG, "onTrimMemory");
        keep();
    }

    @Override
    public void onCreate() {
        L.i(TAG, "onCreate");
        keep();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.i(TAG, "onStartCommand");
        return START_STICKY;
    }
}