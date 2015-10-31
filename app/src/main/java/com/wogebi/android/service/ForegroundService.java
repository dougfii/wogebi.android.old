package com.wogebi.android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.dougfii.android.core.log.L;
import com.dougfii.android.core.utils.ApiUtils;
import com.wogebi.android.activity.MainActivity;

public class ForegroundService extends Service
{
    private static final String TAG = "ForegroundService";

    private boolean mReflectFlg = false;

    private static final int NOTIFICATION_ID = 1; // 如果id设置为0,会导致不能设置为前台service
    private static final Class<?>[] mSetForegroundSignature = new Class[]{boolean.class};
    private static final Class<?>[] mStartForegroundSignature = new Class[]{int.class, Notification.class};
    private static final Class<?>[] mStopForegroundSignature = new Class[]{boolean.class};

    private NotificationManager mNM;
    private Method mSetForeground;
    private Method mStartForeground;
    private Method mStopForeground;
    private Object[] mSetForegroundArgs = new Object[1];
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];

    @Override
    public void onCreate()
    {
        super.onCreate();
        L.i(TAG, "onCreate");

        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        try
        {
            mStartForeground = ForegroundService.class.getMethod("startForeground", mStartForegroundSignature);
            mStopForeground = ForegroundService.class.getMethod("stopForeground", mStopForegroundSignature);
        }
        catch (NoSuchMethodException e)
        {
            mStartForeground = mStopForeground = null;
        }

        try
        {
            mSetForeground = getClass().getMethod("setForeground",
                    mSetForegroundSignature);
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalStateException(
                    "OS doesn't have Service.startForeground OR Service.setForeground!");
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);


        //NotificationUtils notificationUtils = new NotificationUtils(this, R.mipmap.logo, "Foreground Service Start", "Foreground Service", "Make this service run in the foreground.", pendingIntent, Notification.FLAG_AUTO_CANCEL, Notification.DEFAULT_SOUND);

        //Notification notification = notificationUtils.getNotification();

        //startForegroundCompat(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        L.i(TAG, "onStartCommand");

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        L.i(TAG, "onDestroy");

        stopForegroundCompat(NOTIFICATION_ID);
    }

    void invokeMethod(Method method, Object[] args)
    {
        try
        {
            method.invoke(this, args);
        }
        catch (InvocationTargetException e)
        {
            // Should not happen.
            L.w("ApiDemos", "Unable to invoke method", e);
        }
        catch (IllegalAccessException e)
        {
            // Should not happen.
            L.w("ApiDemos", "Unable to invoke method", e);
        }
    }

    /**
     * This is a wrapper around the new startForeground method, using the older
     * APIs if it is not available.
     */
    void startForegroundCompat(int id, Notification notification)
    {
        if (mReflectFlg)
        {
            // If we have the new startForeground API, then use it.
            if (mStartForeground != null)
            {
                mStartForegroundArgs[0] = Integer.valueOf(id);
                mStartForegroundArgs[1] = notification;
                invokeMethod(mStartForeground, mStartForegroundArgs);
                return;
            }

            // Fall back on the old API.
            mSetForegroundArgs[0] = Boolean.TRUE;
            invokeMethod(mSetForeground, mSetForegroundArgs);
            mNM.notify(id, notification);
        }
        else
        {
            /* 还可以使用以下方法，当sdk大于等于5时，调用sdk现有的方法startForeground设置前台运行，
             * 否则调用反射取得的sdk level 5（对应Android 2.0）以下才有的旧方法setForeground设置前台运行 */

            if (ApiUtils.hasEclair())
            {
                startForeground(id, notification);
            }
            else
            {
                // Fall back on the old API.
                mSetForegroundArgs[0] = Boolean.TRUE;
                invokeMethod(mSetForeground, mSetForegroundArgs);
                mNM.notify(id, notification);
            }
        }
    }

    /**
     * This is a wrapper around the new stopForeground method, using the older
     * APIs if it is not available.
     */
    void stopForegroundCompat(int id)
    {
        if (mReflectFlg)
        {
            // If we have the new stopForeground API, then use it.
            if (mStopForeground != null)
            {
                mStopForegroundArgs[0] = Boolean.TRUE;
                invokeMethod(mStopForeground, mStopForegroundArgs);
                return;
            }

            // Fall back on the old API.  Note to cancel BEFORE changing the
            // foreground state, since we could be killed at that point.
            mNM.cancel(id);
            mSetForegroundArgs[0] = Boolean.FALSE;
            invokeMethod(mSetForeground, mSetForegroundArgs);
        }
        else
        {
            /* 还可以使用以下方法，当sdk大于等于5时，调用sdk现有的方法stopForeground停止前台运行，
             * 否则调用反射取得的sdk level 5（对应Android 2.0）以下才有的旧方法setForeground停止前台运行 */

            if (ApiUtils.hasEclair())
            {
                stopForeground(true);
            }
            else
            {
                // Fall back on the old API.  Note to cancel BEFORE changing the
                // foreground state, since we could be killed at that point.
                mNM.cancel(id);
                mSetForegroundArgs[0] = Boolean.FALSE;
                invokeMethod(mSetForeground, mSetForegroundArgs);
            }
        }
    }

}