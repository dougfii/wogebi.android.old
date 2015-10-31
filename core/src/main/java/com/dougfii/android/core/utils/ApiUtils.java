package com.dougfii.android.core.utils;

import android.os.Build;

/**
 * Created by momo on 15/10/31.
 */
public class ApiUtils {
    // SDK5 Android 2.0+
    public static boolean hasEclair() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR;
    }

    // SDK9 Android 2.3+
    public static boolean hasGingerBread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    // SDK11 Android 3.0+
    public static boolean hasHoneyComb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    // SDK16 Android 4.1+
    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    // SDK21 Android 5.0+
    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}