package com.dougfii.android.core.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.dougfii.android.core.log.L;

/**
 * Created by momo on 15/10/31.
 */
public class HardwareUtils {
    private static final String TAG = "HardwareUtils";

    public enum NetworkState {
        NONE, WIFI, GPRS
    }

    // 获取当前的网络状态
    public static NetworkState getNetworkState(Context context) {
        NetworkInfo.State wifi;
        NetworkInfo.State gprs;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        manager.getActiveNetworkInfo();
        wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if (wifi != null && gprs != null && NetworkInfo.State.CONNECTED != wifi && NetworkInfo.State.CONNECTED == gprs) {
            return NetworkState.GPRS;
        } else if (wifi != null && gprs != null && NetworkInfo.State.CONNECTED != wifi && NetworkInfo.State.CONNECTED != gprs) {
            return NetworkState.NONE;
        } else if (wifi != null && NetworkInfo.State.CONNECTED == wifi) {
            return NetworkState.WIFI;
        }
        return NetworkState.NONE;
    }

    //判断网络是否可用
    public static boolean isNetworkAvailable(Context context) {
        return getNetworkState(context) != NetworkState.NONE;
    }

    //判断GPS是否开启
    public static boolean isGpsAvailable(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    //判断AGPS是否开启
    public static boolean isAgpsAvailable(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    //判断定位器(GPS或AGPS)是否开启
    public static boolean isLocatorAvailable(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean agps = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || agps;
    }

    //强制帮用户打开GPS, Android4.0以上
    public static boolean openGps(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        intent.addCategory("android.intent.category.ALTERNATIVE");
        intent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, intent, 0).send();
            return true;
        } catch (PendingIntent.CanceledException e) {
            L.e(TAG, e.getCause());
        }

        return false;
    }
}