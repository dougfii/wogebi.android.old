package com.wogebi.android.model;

import com.wogebi.android.entity.UserEntity;

public class Model
{
    public static UserEntity My = null;

    public static boolean IsLogin()
    {
        return My != null && My.isLogin();
    }
}
