package com.wogebi.android.update;

public class UpdateVersion
{
    private int code = 0;
    private String version = "";
    private String name = "";
    private String url = "";
    private String description = "";

    //是否分析完成或成功
    public boolean isAvailable()
    {
        return code > 0;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
