package com.wogebi.android.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wogebi.android.log.L;
import com.wogebi.android.service.WalkwayService;

public class WalkwayBootBroadcastReceiver extends BroadcastReceiver
{
    private static final String TAG = "WalkwayBootBroadcastReceiver";

    private static final String ACTION_WALKWAY_BOOT = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(ACTION_WALKWAY_BOOT))
        {
            L.i(TAG, "boot start");
            Intent i = new Intent(WalkwayService.ACTION_WALKWAY_SERVICE).setPackage(context.getPackageName());
            i.putExtra(WalkwayService.START_MODE, WalkwayService.START_BOOT);
            context.startService(i);
        }
    }
}
