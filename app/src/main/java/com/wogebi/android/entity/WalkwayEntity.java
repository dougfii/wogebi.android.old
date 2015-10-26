package com.wogebi.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class WalkwayEntity extends BaseEntity implements Parcelable, Serializable
{
    private static final long serialVersionUID = 1L;

    private int id;
    private int code;
    private double lat;
    private double lng;
    private float radius;
    private String time;
    private String date;

    public WalkwayEntity()
    {
        super();
    }

    public WalkwayEntity(int id, int code, double lat, double lng, float radius, String time, String date)
    {
        super();
        this.id = id;
        this.code = code;
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
        this.time = time;
        this.date = date;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public double getLat()
    {
        return lat;
    }

    public void setLat(double lat)
    {
        this.lat = lat;
    }

    public double getLng()
    {
        return lng;
    }

    public void setLng(double lng)
    {
        this.lng = lng;
    }

    public float getRadius()
    {
        return radius;
    }

    public void setRadius(float radius)
    {
        this.radius = radius;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    @Override
    public String toString()
    {
        return id + "," + code + "," + lat + "," + lng + "," + radius + "," + time;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof WalkwayEntity)
        {
            WalkwayEntity obj = (WalkwayEntity) o;
            return String.valueOf(obj.getId()).equals(String.valueOf(this.getId()));
        }

        return super.equals(o);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeInt(code);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeFloat(radius);
        dest.writeString(time);
        dest.writeString(date);
    }

    public static final Parcelable.Creator<WalkwayEntity> CREATOR = new Parcelable.Creator<WalkwayEntity>()
    {
        @Override
        public WalkwayEntity createFromParcel(Parcel source)
        {
            return new WalkwayEntity(source.readInt(), source.readInt(), source.readDouble(), source.readDouble(), source.readFloat(), source.readString(), source.readString());
        }

        @Override
        public WalkwayEntity[] newArray(int size)
        {
            return new WalkwayEntity[size];
        }
    };
}
