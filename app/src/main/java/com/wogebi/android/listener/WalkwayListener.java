package com.wogebi.android.listener;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.wogebi.android.db.WalkwayDBHelper;
import com.wogebi.android.entity.WalkwayEntity;
import com.wogebi.android.log.L;
import com.wogebi.android.service.WalkwayService;
import com.wogebi.android.utils.DateTimeUtils;


public class WalkwayListener extends LocationListener
{
    private static final String TAG = "WalkwayListener";

    public WalkwayListener(Context context, Handler handler)
    {
        super(context, handler);
    }

    @Override
    protected void process()
    {
        if (isLocationAvailable(local.code))
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    int result = save(local.code, local.lat, local.lng, local.radius, local.time);
                    Message message = new Message();
                    message.what = result;
                    handler.sendMessage(message);
                }
            });

            send(WalkwayService.MSG_SEND_SERVICE);
        }
        else if (isLocationUnavailable(local.code))
        {
            error();
        }
    }

    private int save(int code, double lat, double lng, float radius, String time)
    {
        L.i(TAG, "save - 定位信息写入数据库: " + DateTimeUtils.getCurrentDateTime());
        WalkwayEntity entity = new WalkwayEntity(0, code, lat, lng, radius, time, DateTimeUtils.getDate(time));
        WalkwayDBHelper.insert(context, entity);
        return WalkwayService.MSG_SAVE_DATABASE;
    }
}
