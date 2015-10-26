package com.wogebi.android.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wogebi.android.log.L;
import com.wogebi.android.service.WalkwayService;

public class WalkwayAlarmBroadcastReceiver extends BroadcastReceiver
{
    private static final String TAG = "WalkwayAlarmBroadcastReceiver";

    public static final String ACTION_WALKWAY_ALARM = "com.wogebi.com.wogebi.android.broadcast.WalkwayAlarmBroadcastReceiver.ACTION_WALKWAY_ALARM";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(ACTION_WALKWAY_ALARM))
        {
            L.i(TAG, "alarm start");
            Intent i = new Intent(WalkwayService.ACTION_WALKWAY_SERVICE).setPackage(context.getPackageName());
            i.putExtra(WalkwayService.START_MODE, WalkwayService.START_ALARM);
            context.startService(i);
        }
    }
}
