package com.wogebi.android.utils;

import android.support.annotation.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils
{
    public final static String UTF8 = "UTF-8";

    @Nullable
    public static String toString(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
        {
            sb.append(b);
        }
        return sb.toString();
    }

    public static String toString(Object obj)
    {
        try
        {
            return obj == null ? "" : obj.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }

    public static int toInteger(Object obj)
    {
        try
        {
            return obj == null ? 0 : Integer.valueOf(obj.toString());
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    public static long toDecimal(Object obj)
    {
        try
        {
            return obj == null ? 0 : Long.valueOf(obj.toString());
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    public static float toFloat(Object obj)
    {
        try
        {
            return obj == null ? 0 : Float.valueOf(obj.toString());
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    public static double toDouble(Object obj)
    {
        try
        {
            return obj == null ? 0 : Double.valueOf(obj.toString());
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    // 下将字节数组换成成16进制的字符串
    public static String bytesToHex(byte[] bytes)
    {
        // 首先初始化一个字符数组，用来存放每个16进制字符
        char[] hexs = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] ret = new char[bytes.length * 2];
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : bytes)
        {
            ret[index++] = hexs[b >>> 4 & 0xf];
            ret[index++] = hexs[b & 0xf];
        }
        // 字符数组组合成字符串返回
        return new String(ret);
    }

    public static boolean isBlank(String str)
    {
        Pattern p = Pattern.compile("/[^\\\\s]/");
        Matcher m = p.matcher(str);
        return m.find();
    }

    public static boolean isUsername(String str)
    {
        Pattern p = Pattern.compile("^[a-zA-Z]{1}([a-zA-Z0-9]|[_]){3,19}$");
        Matcher m = p.matcher(str);
        return m.find();
    }

    public static boolean isPassword(String str)
    {
        Pattern p = Pattern.compile("^[\\w\\$#@%*&\\(\\)\\{\\}\\[\\]\\?\\<\\>\\.\\!]{6,20}$");
        Matcher m = p.matcher(str);
        return m.find();
    }

    public static boolean isMobile(String str)
    {

        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(str);
        return m.matches();

    }

    public static boolean IsSmsAuthCode(String str)
    {
        Pattern p = Pattern.compile("^\\d{4}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean isURL(String str)
    {
        Pattern p = Pattern.compile("^(https|http|www|ftp|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(str);
        return m.find();
    }

    public static int getPageCount(int count, int pagesize)
    {
        int pagecount = count / pagesize;
        if (count % pagesize != 0)
        {
            pagecount++;
        }
        return pagecount;
    }
}