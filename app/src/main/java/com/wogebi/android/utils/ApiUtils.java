package com.wogebi.android.utils;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

public class ApiUtils
{
    // SDK5 Android 2.0+
    public static boolean hasEclair()
    {
        return VERSION.SDK_INT >= VERSION_CODES.ECLAIR;
    }

    // SDK9 Android 2.3+
    public static boolean hasGingerBread()
    {
        return VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
    }

    // SDK11 Android 3.0+
    public static boolean hasHoneyComb()
    {
        return VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
    }

    // SDK16 Android 4.1+
    public static boolean hasJellyBean()
    {
        return VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
    }

    // SDK21 Android 5.0+
    public static boolean hasLollipop()
    {
        return VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP;
    }
}