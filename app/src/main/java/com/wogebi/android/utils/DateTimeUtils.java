package com.wogebi.android.utils;

import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils
{
    public static final int DAY_MILLISECOND = 1000 * 60 * 60 * 24;//一天的毫秒数
    public static final String TIMEZONE = "GMT+8";

    public static String getCurrentDateTime()
    {
        Date d = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return f.format(d);
    }

    public static String getCurrentDate()
    {
        Date d = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        return f.format(d);
    }

    public static int getCurrentHour()
    {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    @Nullable
    public static String getDate(String s)
    {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            Date d = f.parse(s);
            return f.format(d);
        }
        catch (ParseException e)
        {
        }

        return null;
    }

    public static String formatDate(int year, int month, int day)
    {
        return String.format("%d-%02d-%02d", year, month, day);
    }

    public static CopmareResult compareDate(String s1, String s2)
    {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            long d1 = f.parse(s1).getTime() / DAY_MILLISECOND;
            long d2 = f.parse(s2).getTime() / DAY_MILLISECOND;

            if (d1 == d2)
            {
                return CopmareResult.Equal;
            }
            else if (d1 > d2)
            {
                return CopmareResult.GreaterThan;
            }
            else if (d1 < d2)
            {
                return CopmareResult.LessThan;
            }
        }
        catch (ParseException e)
        {
        }

        return CopmareResult.Unknown;
    }

    public enum CopmareResult
    {
        GreaterThan,//大于
        Equal,// 等于
        LessThan,//小于
        Unknown,//未知
    }
}