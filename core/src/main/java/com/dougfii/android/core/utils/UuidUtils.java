package com.dougfii.android.core.utils;

import android.support.annotation.Nullable;

import java.util.UUID;

/**
 * Created by momo on 15/10/31.
 */
public class UuidUtils {
    @Nullable
    public static String generate() {
        return UUID.randomUUID().toString();
    }
}
