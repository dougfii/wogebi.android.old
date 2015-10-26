package com.wogebi.android.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DensityUtils
{
    // 根据手机分辨率从dp转成px
    public static int dp2px(Context context, float dpValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    // 根据手机的分辨率从 px(像素) 的单位 转成为 dp
    public static int px2dp(Context context, float pxValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f) - 15;
    }

    // 获取手机状态栏高度
    public static int getStatusBarHeight(Context context)
    {
        Class<?> c;
        Object obj;
        java.lang.reflect.Field field;
        int x;
        int statusBarHeight = 0;
        try
        {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
            return statusBarHeight;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    //获取屏幕宽度（像素）
    public static int getScreenWidthPixels(Context context)
    {
        DisplayMetrics metric = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    // 获取屏幕高度（像素）
    public static int getScreenHeightPixels(Context context)
    {
        DisplayMetrics metric = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    // 获取屏幕密度DPI (120/160/240)
    public static int getScreenDensityDpi(Context context)
    {
        DisplayMetrics metric = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metric);
        return metric.densityDpi;
    }

    //获取屏幕密度 (0.75/1.0/1.5)
    public static float getScreenDensity(Context context)
    {
        DisplayMetrics metric = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metric);
        return metric.density;
    }
}
