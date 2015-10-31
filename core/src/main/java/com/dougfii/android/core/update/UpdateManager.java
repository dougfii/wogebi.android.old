package com.dougfii.android.core.update;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.dougfii.android.core.log.L;
import com.dougfii.android.core.utils.AppUtils;
import com.dougfii.android.core.utils.HttpUtils;
import com.dougfii.android.core.utils.Utils;

import java.io.File;
import java.io.InputStream;

/**
 * Created by momo on 15/10/31.
 */
public class UpdateManager {
    private static final String TAG = "UpdateManager";

    public static final int MSG_XML_DOWNLOAD_START = 1; //XML下载
    public static final int MSG_XML_DOWNLOAD_COMPLETED = 2; //XML下载完成
    public static final int MSG_XML_DOWNLOAD_FAILED = 3; //XML下载失败
    public static final int MSG_UPDATE_PARSE = 4; //分析更新
    public static final int MSG_UPDATE_FORCE = 5; //强制更新
    public static final int MSG_UPDATE_ENABLE = 6; //发现更新
    public static final int MSG_UPDATE_DISABLE = 7; //没有更新
    public static final int MSG_UPDATE_FAILED = 8; //更新分析失败
    public static final int MSG_FILE_COMPLETED = 9; //文件下载完成
    public static final int MSG_FILE_FAILED = 10; //文件下载失败
    public static final int MSG_FILE_BEGIN = 11; //文件下载开始
    public static final int MSG_FILE_DOWNLOADING = 12; //文件下载中
    public static final int MSG_FILE_CANCEL = 13; //文件下载取消
    public static final int MSG_FILE_SDCARD_UNAVAILABLE = 14; //文件下载SD卡无效

    private Context context;
    private HttpUtils http = new HttpUtils();

    private String url; //XML下载URL
    private String path; //下载文件保存路径
    private String filename; //保存包文件名

    private InputStream stream = null;
    private UpdateVersion version = new UpdateVersion();

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_XML_DOWNLOAD_COMPLETED:
                    callback.onStateChanged(MSG_XML_DOWNLOAD_COMPLETED);
                    parse();
                    break;
                default:
                    break;
            }
        }
    };

    private HttpUtils.DownloadCallback downloadCallback = new HttpUtils.DownloadCallback() {
        @Override
        public void onStateChanged(int id) {
            switch (id) {
                case HttpUtils.SUCCESS:
                    callback.onStateChanged(MSG_FILE_COMPLETED);
                    install();
                    break;
                case HttpUtils.FAILED:
                    callback.onStateChanged(MSG_FILE_FAILED);
                    break;
                case HttpUtils.BEGIN:
                    callback.onStateChanged(MSG_FILE_BEGIN);
                    break;
                case HttpUtils.DOWNLOADING:
                    callback.onStateChanged(MSG_FILE_DOWNLOADING);
                    break;
                case HttpUtils.CANCEL:
                    callback.onStateChanged(MSG_FILE_CANCEL);
                    break;
                case HttpUtils.SDCARD_UNAVAILABLE:
                    callback.onStateChanged(MSG_FILE_SDCARD_UNAVAILABLE);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onProgressChanged(int size, int progress) {
            callback.onProgressChanged(size, progress);
        }
    };

    public interface UpdateCallback {
        void onStateChanged(int id);

        void onProgressChanged(int size, int progress);
    }

    private UpdateCallback callback;

    public UpdateManager(Context context, String url, String path, String filename, UpdateCallback callback) {
        this.context = context;
        this.url = url;
        this.path = path;
        this.filename = filename;
        this.callback = callback;
    }

    public Handler getHandler() {
        return handler;
    }

    public void start() {
        xml();
    }

    private void xml() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                stream = http.download(url);

                if (stream == null) {
                    handler.sendEmptyMessage(MSG_XML_DOWNLOAD_FAILED);
                    return;
                }

                handler.sendEmptyMessage(MSG_XML_DOWNLOAD_COMPLETED);


            }
        }).start();
    }

    // 检查软件是否有更新版本
    private void parse() {
        L.i(TAG, "parse ......");

        callback.onStateChanged(MSG_UPDATE_PARSE);
        handler.sendEmptyMessage(MSG_UPDATE_PARSE);

        int ver = AppUtils.getVersionCode(context);

        UpdateXmlService xml = new UpdateXmlService(); //解析XML文件,由于XML文件比较小，因此使用DOM方式进行解析
        version = xml.parse(stream);

        L.i(TAG, "服务器更新版本:" + version.getCode());

        if (version.isAvailable()) {
            int code = version.getCode();
            // 版本判断
            if (code > ver && code - ver > 0) {
                if (Utils.toInteger(code / 10000 - ver / 10000) > 0) {
                    L.i(TAG, "服务器更新版本: 强制更新");
                    handler.sendEmptyMessage(MSG_UPDATE_FORCE);
                    callback.onStateChanged(MSG_UPDATE_FORCE);
                } else {
                    L.i(TAG, "服务器更新版本: 发现更新");
                    handler.sendEmptyMessage(MSG_UPDATE_ENABLE);
                    callback.onStateChanged(MSG_UPDATE_ENABLE);
                }
            } else {
                L.i(TAG, "服务器更新版本: 没有更新");
                handler.sendEmptyMessage(MSG_UPDATE_DISABLE);
                callback.onStateChanged(MSG_UPDATE_DISABLE);
            }
        } else {
            L.i(TAG, "服务器更新版本: 更新检测失败");
            handler.sendEmptyMessage(MSG_UPDATE_FAILED);
            callback.onStateChanged(MSG_UPDATE_FAILED);
        }
    }

    public void download() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                http.download(version.getUrl(), path, filename, downloadCallback);
            }
        }).start();
    }

    public void downloadCancel() {
        http.downloadCancel();
    }

    //安装APK文件
    private void install() {
        File file = new File(path, filename);
        if (!file.exists()) {
            return;
        }

        // 通过Intent安装APK文件
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + file.toString()), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}