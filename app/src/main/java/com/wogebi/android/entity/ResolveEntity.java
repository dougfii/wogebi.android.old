package com.wogebi.android.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.wogebi.android.log.L;

public class ResolveEntity
{
    private static final String TAG = "ResolveEntity";

    public ResultEntity<UserEntity> getUser(String json)
    {
        ResultEntity<UserEntity> ret = null;
        UserEntity user = new UserEntity();

        try
        {
            JSONObject obj = new JSONObject(json);

            int code = obj.getInt("code");
            String msg = obj.getString("msg");
            JSONObject data = obj.getJSONObject("data");

            user.setId(data.getInt("id"));
            user.setGid(data.getInt("gid"));
            user.setOid(data.getInt("oid"));
            user.setUsername(data.getString("username"));
            user.setName(data.getString("name"));
            user.setSerial(data.getString("serial"));
            user.setSex(data.getBoolean("sex"));
            user.setTime(data.getString("time"));

            ret = new ResultEntity<UserEntity>(code, msg, user);
        }
        catch (JSONException e)
        {
            L.i(TAG, e.getMessage());
        }

        return ret;
    }

    public ResultsEntity<CalendarEntity> getCalendarList(String json)
    {
        ResultsEntity<CalendarEntity> ret = null;
        List<CalendarEntity> calendars = new ArrayList<CalendarEntity>();

        try
        {
            JSONObject obj = new JSONObject(json);

            int code = obj.getInt("code");
            String msg = obj.getString("msg");
            JSONArray data = obj.getJSONArray("data");

            for (int i = 0; i < data.length(); i++)
            {
                JSONObject item = data.getJSONObject(i);
                CalendarEntity calendar = new CalendarEntity();

                calendar.setId(item.getInt("id"));
                calendar.setUid(item.getInt("uid"));
                calendar.setUser(item.getString("user"));
                calendar.setDate(item.getString("date"));
                calendar.setStateId(item.getInt("stateid"));
                calendar.setState(item.getString("state"));
                calendar.setCustomer(item.getString("customer"));
                calendar.setContacts(item.getString("contacts"));
                calendar.setContent(item.getString("content"));
                calendar.setPlan(item.getString("plan"));
                calendar.setWalkway(item.getString("walkway"));
                calendar.setDistance(item.getString("distance"));
                calendar.setMemo(item.getString("memo"));
                calendar.setTime(item.getString("time"));

                calendars.add(calendar);
            }

            ret = new ResultsEntity<CalendarEntity>(code, msg, calendars);
        }
        catch (JSONException e)
        {
            L.i(TAG, e.getMessage());
        }

        return ret;
    }


    public ResultEntity<SimpleEntity> doSignedAdd(String json)
    {
        return getSimple(json);
    }

    public ResultEntity<SimpleEntity> doWalkwayAdd(String json)
    {
        return getSimple(json);
    }

    public ResultEntity<SimpleEntity> getSimple(String json)
    {
        ResultEntity<SimpleEntity> ret = null;
        try
        {
            JSONObject obj = new JSONObject(json);

            int code = obj.getInt("code");
            String msg = obj.getString("msg");

            ret = new ResultEntity<SimpleEntity>(code, msg, null);
        }
        catch (JSONException e)
        {
            L.i(TAG, e.getMessage());
        }

        return ret;
    }
}
