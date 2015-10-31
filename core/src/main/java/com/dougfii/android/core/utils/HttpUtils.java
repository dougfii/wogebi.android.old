package com.dougfii.android.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by momo on 15/10/31.
 */
public class HttpUtils {
    private static final String TAG = "HttpUtils";

    public static final int SUCCESS = 1; //成功
    public static final int FAILED = 2; //下载失败
    public static final int BEGIN = 3; //下载开始
    public static final int DOWNLOADING = 4; //下载中
    public static final int CANCEL = 5; //下载用户取消
    public static final int FILE_SIZE = 6; //下载文件总尺寸
    public static final int SDCARD_UNAVAILABLE = 7; //SD卡不可用
    public static final int MANUAL_CANCEL = 8; //手动取消下载

    private static final int CONNECT_TIMEOUT = 5 * 1000; //连接超时
    private static final int READ_TIMEOUT = 5 * 1000; //数据读取超时
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private static final String AND = "&";
    private static final String QM = "?";
    private static final String EQ = "=";
    private static final String BOUNDARY = "----------------doyouloveme";

    private boolean cancel = false;


    public interface DownloadCallback {
        void onStateChanged(int id);

        void onProgressChanged(int size, int progress);
    }

    public String get(String addr) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;

        try {
            URL url = new URL(addr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setRequestMethod(GET);
            conn.setRequestProperty("Content-Type", "text/html; charset=UTF-8");

            if (conn.getResponseCode() == 200) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
        } catch (Exception e) {
            //
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                //
            }
        }

        return sb.toString();
    }

    public String post(String addr, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;

        try {
            URL url = new URL(addr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setRequestMethod(POST);
            conn.setDoInput(true); //允许输入流，即允许下载
            conn.setDoOutput(true); //允许输出流，即允许上传

            String param = params(params);

            OutputStream output = conn.getOutputStream();
            output.write(param.getBytes(Utils.UTF8));
            output.close();

            if (conn.getResponseCode() == 200) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

        } catch (Exception e) {
            //
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                //
            }
        }

        return sb.toString();
    }

    public String put(String addr, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;

        try {
            URL url = new URL(addr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setRequestMethod(PUT);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            String param = params(params);
            OutputStream os = conn.getOutputStream();
            os.write(param.getBytes(Utils.UTF8));
            os.close();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);

            }
        } catch (Exception e) {
            //
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                //
            }
        }

        return sb.toString();
    }

    public int delete(String addr) {
        try {
            URL url = new URL(addr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setRequestMethod(DELETE);
            conn.setDoOutput(true);

            if (conn.getResponseCode() == 200) {
                return SUCCESS;
            }
        } catch (Exception e) {
            //
        }

        return FAILED;
    }

    public InputStream download(String addr) {
        InputStream stream = null;

        try {
            URL url = new URL(addr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.connect();

            stream = conn.getInputStream(); //创建输入流
        } catch (Exception e) {
            //
        }

        return stream;
    }

    public void download(String addr, String path, String filename, DownloadCallback callback) {
        InputStream input = null;
        FileOutputStream output = null;
        boolean finished = false;

        try {
            // 判断SD卡是否存在，并且是否具有读写权限
            if (!FileUtils.isSdCardExist()) {
                callback.onStateChanged(SDCARD_UNAVAILABLE);
            }

            URL url = new URL(addr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.connect();

            int size = conn.getContentLength(); //获取文件大小
            input = conn.getInputStream(); //创建输入流

            callback.onStateChanged(FILE_SIZE);
            callback.onProgressChanged(size, 0);

            FileUtils.createDir(path); //判断文件目录是否存在

            File file = new File(path, filename);
            output = new FileOutputStream(file);
            byte buf[] = new byte[1024]; //缓存
            int progress = 0;

            callback.onStateChanged(BEGIN);
            do {
                int read = input.read(buf);
                progress += read;

                // 更新进度
                callback.onStateChanged(DOWNLOADING);
                callback.onProgressChanged(size, progress);

                if (read <= 0)//下载完成
                {
                    finished = true;
                    break;
                }

                output.write(buf, 0, read); //写入文件
            }
            while (!cancel);// 点击取消就停止下载.
        } catch (Exception e) {
            //
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (Exception e) {
                //
            }
        }

        if (cancel) {
            callback.onStateChanged(CANCEL);
        } else if (finished) {
            callback.onStateChanged(SUCCESS);
        } else {
            callback.onStateChanged(FAILED);
        }
    }

    public String params(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (String key : params.keySet()) {
            sb.append(AND).append(key).append(EQ).append(params.get(key));
        }

        return sb.toString();
    }

    public void downloadCancel() {
        cancel = true;
    }
}