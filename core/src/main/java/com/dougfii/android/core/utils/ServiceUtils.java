package com.dougfii.android.core.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by momo on 15/10/31.
 */
public class ServiceUtils {
    private static final String TAG = "ServiceUtils";

    public static boolean isServiceRunning(Context context, String className) {
        boolean run = false;

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo info : infos) {
            if (info.service.getClassName().equals(className)) {
                run = true;
                break;
            }
        }

        return run;
    }

    public static boolean isProcessRunning(Context context, String className) {
        boolean run = false;

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.processName.equals(className)) {
                run = true;
                break;
            }
        }

        return run;
    }
}
