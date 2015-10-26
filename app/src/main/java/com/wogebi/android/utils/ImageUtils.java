package com.wogebi.android.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils
{
    @Nullable
    public static Bitmap load(Context context, int resId)
    {
        try
        {
            return BitmapFactory.decodeResource(context.getResources(), resId);
        }
        catch (Exception e)
        {
            //
        }
        return null;
    }

    @Nullable
    public static Bitmap load(String filename)
    {
        try
        {
            return BitmapFactory.decodeFile(filename);
        }
        catch (Exception e)
        {
            //
        }
        return null;
    }

    @Nullable
    public static Bitmap load(ContentResolver cr, Uri uri)
    {
        try
        {
            return BitmapFactory.decodeStream(cr.openInputStream(uri));
        }
        catch (FileNotFoundException e)
        {
            //
        }
        return null;
    }

    @Nullable
    public static Bitmap resize(Bitmap bitmap, int width)
    {
        try
        {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            float sw = width / (float) w;
            float sh = (width * (float) h / (float) w) / (float) h;
            Matrix matrix = new Matrix();
            matrix.postScale(sh, sw);
            return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        }
        catch (Exception e)
        {
            //
        }
        return null;
    }

    @Nullable
    public static Bitmap resize(Bitmap bitmap, int width, int height)
    {
        try
        {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            float sw = width / (float) w;
            float sh = height / (float) h;
            Matrix matrix = new Matrix();
            matrix.postScale(sh, sw);
            return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        }
        catch (Exception e)
        {
            //
        }
        return null;
    }

    @Nullable
    public static Bitmap zoom(Bitmap bitmap, int width, int ratio)
    {
        try
        {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            float sw = width / (float) (w * ratio);
            float sh = width / (float) (h * ratio);
            Matrix matrix = new Matrix();
            matrix.postScale(sh, sw);
            return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        }
        catch (Exception e)
        {
            //
        }
        return null;
    }

    @Nullable
    public static Bitmap create(String filename, int width, int radio)
    {
        try
        {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true; //***** don't alloc bitmap ram
            BitmapFactory.decodeFile(filename, opts);
            int w = opts.outWidth;
            int h = opts.outHeight;

            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = false;
            newOpts.inSampleSize = radio;
            newOpts.outWidth = width / (w * radio);
            newOpts.outHeight = width / (h * radio);
            return BitmapFactory.decodeFile(filename, newOpts);
        }
        catch (Exception e)
        {
            //
        }
        return null;
    }

    public static boolean save(Bitmap bitmap, String path, String filename, int quality)
    {
        FileOutputStream output = null;
        try
        {
            FileUtils.createDir(path);
            File file = FileUtils.createFile(path + filename);

            if (file != null)
            {
                try
                {
                    output = new FileOutputStream(path + filename);
                    return bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output);
                }
                catch (FileNotFoundException e)
                {
                    //
                }
                finally
                {
                    try
                    {
                        if (output != null)
                        {
                            output.flush();
                            output.close();
                        }
                    }
                    catch (IOException e)
                    {
                        //
                    }
                }
            }
        }
        catch (Exception e)
        {
            //
        }
        return false;
    }

    public static boolean save(Bitmap bitmap, String path, String filename)
    {
        return save(bitmap, path, filename, 100);
    }

    public static String saveRandomUUID(Bitmap bitmap, String path)
    {
        String filename = UUIDUtils.generate() + ".jpg";
        return save(bitmap, path, filename) ? path + filename : null;
    }

    public static String saveRandomMd5(Bitmap bitmap, String path)
    {
        String filename = MD5Utils.generate() + ".jpg";
        return save(bitmap, path, filename) ? path + filename : null;
    }
}