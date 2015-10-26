package com.wogebi.android.listener;

import android.content.Context;
import android.os.Handler;

import com.wogebi.android.service.WalkwayService;

public class SignedListener extends LocationListener
{
    private static final String TAG = "SignedListener";

    public SignedListener(Context context, Handler handler)
    {
        super(context, handler);
    }

    @Override
    protected void process()
    {
        if (isLocationAvailable(local.code))
        {
            send(WalkwayService.MSG_SEND_CLIENT);
        }
        else if (isLocationUnavailable(local.code))
        {
            error();
        }
    }
}