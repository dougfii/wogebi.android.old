package com.wogebi.android.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils
{
    private static final String HASH_ALGORITHM = "MD5";

    public static byte[] generate(byte[] bytes)
    {
        byte[] hash = null;
        try
        {
            MessageDigest md5 = MessageDigest.getInstance(HASH_ALGORITHM);
            md5.update(bytes);
            hash = md5.digest();
        }
        catch (NoSuchAlgorithmException e)
        {
            //
        }
        return hash;
    }

    public static String generate(String str)
    {
        return Utils.toString(generate(str.getBytes()));
    }

    public static String generate()
    {
        return generate(UUIDUtils.generate());
    }
}