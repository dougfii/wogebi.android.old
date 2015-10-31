package com.wogebi.android.model;

import com.dougfii.android.core.utils.FileUtils;

import java.io.File;

public class Constants
{
    //update
    public static String UPDATE_XML = "http://gerp.greentown.cn/distribute/android.xml";
    public static final String UPDATE_FILENAME = "gerp.apk";
    //directory
    public static final String DIR_APP = FileUtils.getPath() + "wogebi" + File.separator;
    public static final String DIR_CRASH = DIR_APP + "log" + File.separator + "crash" + File.separator;    // 异常日志目录
    public static final String DIR_LOG = DIR_APP + "log" + File.separator + "log" + File.separator;    // 一般日志目录
    public static final String DIR_TEMP = DIR_APP + "temp" + File.separator;    // 临时目录
    public static final String DIR_CACHE = DIR_APP + "cache" + File.separator;  // 缓冲目录
    public static final String DIR_DOWNLOAD = DIR_APP + "download" + File.separator;

    public static String APPID = "44b64d9ddc59b6553410dad04a61d507";
    public static String SECRET = "03fa0c4cf680f9e7542a7cf11406d5d6";
    public static String TOKEN = "";
    public static String MODULE = "Portal";
    public static String URL_SERVER = "http://service.greentown.cn/?appkey=" + APPID + "&secret=" + SECRET + "&m=" + MODULE + "&a=";
    public static String MODEL_LOGIN = "Login";//登录
    public static String MODEL_SIGNED_ADD = "SignedAdd";//打卡签到
    public static String MODEL_WALKWAY_ADD = "WalkwayAdd";//行走路径
    public static String MODEL_CALENDAR_LIST = "CalendarList";
    public static String MODEL_CALENDAR_ADD = "CalendarAdd";

    public static int DEFAULT_PAGE_SIZE = 10;//默认分页尺寸
}
