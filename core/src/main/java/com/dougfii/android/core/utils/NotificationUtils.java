package com.dougfii.android.core.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

/**
 * Created by momo on 15/10/31.
 */
public class NotificationUtils {
    public final static int LOCATOR_UNAVAILABLE = 1; //定位器未开启(GPS和AGPS)
    public final static int NETWORK_UNAVAILABLE = 2; //网络未开启

    private Context context;
    private Notification notification;
    private NotificationManager notificationManager;

    public NotificationUtils(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void sendNetworkNotification(int icon, String ticker, String title, String content) {
        Intent intent = new Intent();
        if (ApiUtils.hasHoneyComb()) {
            //API 11及以上
            intent.setAction(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        } else {
            //API 11以下
            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//需要设置Intent.FLAG_ACTIVITY_NEW_TASK属性

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        notification = new NotificationCompat.Builder(context)
                .setSmallIcon(icon)
                .setTicker(ticker)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .build();

        notification.flags = Notification.FLAG_AUTO_CANCEL;//当用户点击 Clear 之后，能够清除该通知
        //notification.flags = Notification.FLAG_ONGOING_EVENT;//常驻通知栏
        notification.defaults = Notification.DEFAULT_SOUND; //将使用默认的声音来提醒用户

        notificationManager.notify(NETWORK_UNAVAILABLE, notification);
    }

    public void sendLocatorNotification(int icon, String ticker, String title, String content) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//需要设置Intent.FLAG_ACTIVITY_NEW_TASK属性

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        notification = new NotificationCompat.Builder(context)
                .setSmallIcon(icon)
                .setTicker(ticker)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .build();

        notification.flags = Notification.FLAG_AUTO_CANCEL;//当用户点击 Clear 之后，能够清除该通知
        //notification.flags = Notification.FLAG_ONGOING_EVENT;//常驻通知栏
        notification.defaults = Notification.DEFAULT_SOUND; //将使用默认的声音来提醒用户

        notificationManager.notify(LOCATOR_UNAVAILABLE, notification);
    }

    public void cancel(int id) {
        notificationManager.cancel(id);
    }
}