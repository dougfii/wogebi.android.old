package com.dougfii.android.core.utils;

import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by momo on 15/10/31.
 */
public class FileUtils {
    private static String path;

    static {
        if (isSdCardExist() && isRootDirExist()) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        } else {
            //path = BaseApplication.getFilesDir().getAbsolutePath() + File.separator;
        }
    }

    public static String getPath() {
        return path;
    }

    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean isRootDirExist() {

        return Environment.getExternalStorageDirectory().exists();
    }

    // 目录是否存在
    public static boolean isDirExist(String path) {
        File dir = new File(path);
        return dir.exists();
    }

    // 创建目录
    public static boolean createDir(String path) {
        File dir = new File(path);
        return !dir.exists() && dir.mkdirs();
    }

    // 文件是否存在
    public static boolean isFileExist(String filename) {
        File file = new File(filename);
        return file.exists();
    }

    // 创建文件
    @Nullable
    public static File createFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                return null;
            }
        }
        return file;
    }

    // 删除文件夹
    public static boolean delDir(String path) {
        delFiles(path);
        File file = new File(path);
        return file.delete();
    }

    // 删除文件
    public static boolean delFiles(String path) {
        File file = new File(path);

        if (!file.exists()) {
            return false;
        }
        if (!file.isDirectory()) {
            return false;
        }

        String[] fs = file.list();
        File f;
        for (String str : fs) {
            if (path.endsWith(File.separator)) {
                f = new File(path + str);
            } else {
                f = new File(path + File.separator + str);
            }

            if (f.isFile()) {
                f.delete();
            }

            if (f.isDirectory()) {
                delFiles(path + File.separator + str);
                delDir(path + File.separator + str);
            }
        }

        return true;
    }

    //将文本内容写入到文件
    public static File write(String path, String filename, String str) {
        return write(path, filename, str.getBytes());
    }

    //将字节缓冲写入到文件
    @Nullable
    public static File write(String path, String filename, byte[] buffer) {
        File file;
        OutputStream output = null;

        try {
            createDir(path);
            file = createFile(path + filename);
            if (file != null) {
                output = new FileOutputStream(file);
                output.write(buffer);
                output.flush();
            }

            return file;
        } catch (Exception e) {
            //
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (Exception e) {
                //
            }
        }

        return null;
    }

    // 将输入流写入到文件
    @Nullable
    public static File write(String path, String filename, InputStream input) {
        File file;
        OutputStream output = null;

        try {
            createDir(path);
            file = createFile(path + filename);
            if (file != null) {
                output = new FileOutputStream(file);
                byte buffer[] = new byte[4 * 1024];
                while ((input.read(buffer)) != -1) {
                    output.write(buffer);
                }
                output.flush();
            }

            return file;
        } catch (Exception e) {
            //
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (Exception e) {
                //
            }
        }

        return null;
    }
}
