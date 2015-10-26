package com.wogebi.android.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.wogebi.android.log.L;
import com.wogebi.android.service.WalkwayService;

public class WalkwayShutdownBroadcastReceiver extends BroadcastReceiver
{
    private static final String TAG = "WalkwayShutdownBroadcastReceiver";

    private static final String ACTION_WALKWAY_SHUTDOWN = "android.intent.action.ACTION_WALKWAY_SHUTDOWN";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(ACTION_WALKWAY_SHUTDOWN))
        {
            L.i(TAG, "shutdown restart");
            SharedPreferences sp = context.getSharedPreferences(WalkwayService.TAG, Context.MODE_PRIVATE);
            sp.edit().putInt(WalkwayService.EXIT_MODE, WalkwayService.EXIT_SHUTDOWN).commit();
            Intent i = new Intent(WalkwayService.ACTION_WALKWAY_SERVICE).setPackage(context.getPackageName());
            i.putExtra(WalkwayService.START_MODE, WalkwayService.START_RESTART);
            context.startService(i);
        }
    }
}
