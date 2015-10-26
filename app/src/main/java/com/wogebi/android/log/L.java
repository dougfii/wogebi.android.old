package com.wogebi.android.log;

import android.util.Log;

public final class L
{
    private static volatile boolean DEBUG = false;
    private static volatile boolean LOGS = false;

    public static void debug(boolean debug)
    {
        DEBUG = debug;
    }

    public static void logs(boolean logs)
    {
        LOGS = logs;
    }

    // v
    public static void v(String tag, String msg)
    {
        if (LOGS)
        {
            Log.v(tag, msg);
        }
    }

    public static void v(String tag, Throwable tr)
    {
        v(tag, "", tr);
    }

    public static void v(String tag, String msg, Throwable tr)
    {
        if (LOGS)
        {
            Log.v(tag, msg, tr);
        }
    }

    public static void v(Object obj, String msg)
    {
        v(obj.getClass().getSimpleName(), msg);
    }

    public static void v(Object obj, Throwable tr)
    {
        v(obj.getClass().getSimpleName(), tr);
    }

    public static void v(Object obj, String msg, Object... args)
    {
        if (args.length > 0)
        {
            msg = String.format(msg, args);
        }
        v(obj.getClass().getSimpleName(), msg);
    }

    // d
    public static void d(String tag, String msg)
    {
        if (DEBUG)
        {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, Throwable tr)
    {
        d(tag, "", tr);
    }

    public static void d(String tag, String msg, Throwable tr)
    {
        if (DEBUG)
        {
            Log.d(tag, msg, tr);
        }
    }

    public static void d(Object obj, String msg)
    {
        d(obj.getClass().getSimpleName(), msg);
    }

    public static void d(Object obj, Throwable tr)
    {
        d(obj.getClass().getSimpleName(), tr);
    }

    public static void d(Object obj, String msg, Object... args)
    {
        if (args.length > 0)
        {
            msg = String.format(msg, args);
        }
        d(obj.getClass().getSimpleName(), msg);
    }

    // i
    public static void i(String tag, String msg)
    {
        if (LOGS)
        {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, Throwable tr)
    {
        i(tag, "", tr);
    }

    public static void i(String tag, String msg, Throwable tr)
    {
        if (LOGS)
        {
            Log.i(tag, msg, tr);
        }
    }

    public static void i(Object obj, String msg)
    {
        i(obj.getClass().getSimpleName(), msg);
    }

    public static void i(Object obj, Throwable tr)
    {
        i(obj.getClass().getSimpleName(), tr);
    }

    public static void i(Object obj, String msg, Object... args)
    {
        if (args.length > 0)
        {
            msg = String.format(msg, args);
        }
        i(obj.getClass().getSimpleName(), msg);
    }

    // w
    public static void w(String tag, String msg)
    {
        if (LOGS)
        {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr)
    {
        if (LOGS)
        {
            Log.w(tag, msg, tr);
        }
    }

    public static void w(String tag, Throwable tr)
    {
        if (LOGS)
        {
            Log.w(tag, tr);
        }
    }

    public static void w(Object obj, String msg)
    {
        w(obj.getClass().getSimpleName(), msg);
    }

    public static void w(Object obj, Throwable tr)
    {
        w(obj.getClass().getSimpleName(), tr);
    }

    public static void w(Object obj, String msg, Object... args)
    {
        if (args.length > 0)
        {
            msg = String.format(msg, args);
        }
        w(obj.getClass().getSimpleName(), msg);
    }

    // e
    public static void e(String tag, String msg)
    {
        if (LOGS)
        {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, Throwable tr)
    {
        e(tag, "", tr);
    }

    public static void e(String tag, String msg, Throwable tr)
    {
        if (LOGS)
        {
            Log.e(tag, msg, tr);
        }
    }

    public static void e(Object obj, String msg)
    {
        e(obj.getClass().getSimpleName(), msg);
    }

    public static void e(Object obj, Throwable tr)
    {
        e(obj.getClass().getSimpleName(), tr);
    }

    public static void e(Object obj, String msg, Object... args)
    {
        if (args.length > 0)
        {
            msg = String.format(msg, args);
        }
        e(obj.getClass().getSimpleName(), msg);
    }
}
