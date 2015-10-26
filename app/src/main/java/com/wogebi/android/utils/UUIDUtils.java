package com.wogebi.android.utils;

import android.support.annotation.Nullable;

import java.util.UUID;

public class UUIDUtils
{
    @Nullable
    public static String generate()
    {
        return UUID.randomUUID().toString();
    }
}