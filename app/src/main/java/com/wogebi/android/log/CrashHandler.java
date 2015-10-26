package com.wogebi.android.log;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.widget.Toast;

import com.wogebi.android.R;
import com.wogebi.android.model.Constants;
import com.wogebi.android.utils.FileUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CrashHandler implements UncaughtExceptionHandler
{
    public static final String TAG = "CrashHandler";

    private static CrashHandler instance;
    private UncaughtExceptionHandler handler;
    private Context context;
    private Map<String, String> infos = new HashMap<String, String>();
    private DateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault());

    private CrashHandler()
    {
    }

    public static CrashHandler getInstance()
    {
        if (instance == null) instance = new CrashHandler();
        return instance;
    }

    public void init(Context context)
    {
        this.context = context;
        handler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        if (!process(ex) && handler != null)
        {
            handler.uncaughtException(thread, ex);
        }
        else
        {
            try
            {
                Thread.sleep(3000);
            }
            catch (InterruptedException e)
            {
                L.e(TAG, "error: ", e);
            }

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    //自定义错误处理
    private boolean process(Throwable ex)
    {
        if (ex == null) return false;

        collect(context);

        new Thread()
        {
            @Override
            public void run()
            {
                Looper.prepare();
                Toast.makeText(context, R.string.crash, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();

        save(ex);

        return true;
    }

    //收集设备参数信息
    private void collect(Context context)
    {
        try
        {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null)
            {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }

            Field[] fileds = Build.class.getDeclaredFields();
            for (Field filed : fileds)
            {
                try
                {
                    filed.setAccessible(true);
                    infos.put(filed.getName(), filed.get(null).toString());
                    L.d(TAG, filed.getName() + " : " + filed.get(null));
                }
                catch (Exception e)
                {
                    L.e(TAG, "an error occurred when collect crash info ", e);
                }
            }
        }
        catch (NameNotFoundException e)
        {
            L.e(TAG, "an error occurred when collect package info ", e);
        }
    }

    //保存错误信息
    private boolean save(Throwable ex)
    {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : infos.entrySet())
        {
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }

        Writer writer = new StringWriter();
        PrintWriter printer = new PrintWriter(writer);
        ex.printStackTrace(printer);

        Throwable cause = ex.getCause();
        while (cause != null)
        {
            cause.printStackTrace(printer);
            cause = cause.getCause();
        }

        printer.close();

        String result = writer.toString();
        sb.append(result);

        return write(sb.toString());
    }

    //写入文件
    private boolean write(String str)
    {
        try
        {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String filename = "crash-" + time + "-" + timestamp + ".log";

            FileUtils.write(Constants.DIR_CRASH, filename, str);

            return true;
        }
        catch (Exception e)
        {
            L.e(TAG, "an error occurred while writing file ", e);
        }

        return false;
    }
}
