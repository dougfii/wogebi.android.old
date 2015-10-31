package com.wogebi.android.entity;

import com.dougfii.android.core.entity.BaseEntity;

import java.io.Serializable;

public class CalendarEntity extends BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    private int id;
    private int uid;
    private String user;
    private String date;
    private int stateId;
    private String state;
    private String customer;
    private String contacts;
    private String content;
    private String plan;
    private String walkway;
    private String distance;
    private String memo;
    private String time;

    public CalendarEntity()
    {
        super();
    }

    public CalendarEntity(int id, int uid, String user, String date, int stateId, String state, String customer, String contacts, String content, String plan, String walkway, String distance, String memo, String time)
    {
        this.id = id;
        this.uid = uid;
        this.user = user;
        this.date = date;
        this.stateId = stateId;
        this.state = state;
        this.customer = customer;
        this.contacts = contacts;
        this.content = content;
        this.plan = plan;
        this.walkway = walkway;
        this.distance = distance;
        this.memo = memo;
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

    public int getUid()
    {
        return uid;
    }

    public void setUid(int uid)
    {
        this.uid = uid;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public int getStateId()
    {
        return stateId;
    }

    public void setStateId(int stateId)
    {
        this.stateId = stateId;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getCustomer()
    {
        return customer;
    }

    public void setCustomer(String customer)
    {
        this.customer = customer;
    }

    public String getContacts()
    {
        return contacts;
    }

    public void setContacts(String contacts)
    {
        this.contacts = contacts;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getPlan()
    {
        return plan;
    }

    public void setPlan(String plan)
    {
        this.plan = plan;
    }

    public String getWalkway()
    {
        return walkway;
    }

    public void setWalkway(String walkway)
    {
        this.walkway = walkway;
    }

    public String getDistance()
    {
        return distance;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
    }

    public String getMemo()
    {
        return memo;
    }

    public void setMemo(String memo)
    {
        this.memo = memo;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }
}
