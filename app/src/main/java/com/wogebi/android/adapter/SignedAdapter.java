package com.wogebi.android.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.wogebi.android.BaseApplication;
import com.wogebi.android.R;
import com.wogebi.android.entity.BaseEntity;
import com.wogebi.android.entity.WalkwayEntity;

public class SignedAdapter extends EntityAdapter
{
    public SignedAdapter(BaseApplication application, Context context, List<? extends BaseEntity> entities)
    {
        super(application, context, entities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewItem item = new ViewItem();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.activity_walkway_item, parent, false);

            item.id = (TextView) convertView.findViewById(R.id.walkway_item_id);
            item.code = (TextView) convertView.findViewById(R.id.walkway_item_code);
            item.lat = (TextView) convertView.findViewById(R.id.walkway_item_lat);
            item.lng = (TextView) convertView.findViewById(R.id.walkway_item_lng);
            item.time = (TextView) convertView.findViewById(R.id.walkway_item_time);
            item.date = (TextView) convertView.findViewById(R.id.walkway_item_date);

            convertView.setTag(item);
        }
        else
        {
            item = (ViewItem) convertView.getTag();
        }

        WalkwayEntity entity = (WalkwayEntity) getItem(position);
        item.id.setText(String.valueOf(entity.getId()));
        item.code.setText(String.valueOf(entity.getCode()));
        item.lat.setText(String.valueOf(entity.getLat()));
        item.lng.setText(String.valueOf(entity.getLng()));
        item.time.setText(entity.getTime());
        item.date.setText(entity.getDate());

        return convertView;
    }

    class ViewItem
    {
        TextView id;
        TextView code;
        TextView lat;
        TextView lng;
        TextView time;
        TextView date;
    }
}
