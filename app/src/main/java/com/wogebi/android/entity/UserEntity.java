package com.wogebi.android.entity;

import com.dougfii.android.core.entity.BaseEntity;

public class UserEntity extends BaseEntity
{
    private int id = 0;
    private int gid = 0;
    private int oid = 0;
    private String username;
    private String name;
    private String serial;
    private boolean sex;
    private String time;

    public UserEntity()
    {
        super();
    }

    public UserEntity(int id, int gid, int oid, String username, String name, String serial, boolean sex, String time)
    {
        super();
        this.id = id;
        this.gid = gid;
        this.oid = oid;
        this.username = username;
        this.name = name;
        this.serial = serial;
        this.sex = sex;
        this.time = time;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getGid()
    {
        return gid;
    }

    public void setGid(int gid)
    {
        this.gid = gid;
    }

    public int getOid()
    {
        return oid;
    }

    public void setOid(int oid)
    {
        this.oid = oid;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSerial()
    {
        return serial;
    }

    public void setSerial(String serial)
    {
        this.serial = serial;
    }

    public boolean getSex()
    {
        return sex;
    }

    public void setSex(boolean sex)
    {
        this.sex = sex;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public boolean isLogin()
    {
        return this.id > 0;
    }
}