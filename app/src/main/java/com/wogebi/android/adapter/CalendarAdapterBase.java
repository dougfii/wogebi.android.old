package com.wogebi.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dougfii.android.core.adapter.BaseEntityAdapter;
import com.dougfii.android.core.entity.BaseEntity;
import com.wogebi.android.AppApplication;
import com.wogebi.android.R;
import com.wogebi.android.entity.CalendarEntity;

import java.util.List;

public class CalendarAdapterBase extends BaseEntityAdapter
{
    public CalendarAdapterBase(AppApplication application, Context context, List<? extends BaseEntity> entities)
    {
        super(application, context, entities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewItem item = new ViewItem();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.section_calendar_item, parent, false);

            item.panel1 = (LinearLayout) convertView.findViewById(R.id.calendar_item_panel1);
            item.panel2 = (LinearLayout) convertView.findViewById(R.id.calendar_item_panel2);
            item.panel3 = (LinearLayout) convertView.findViewById(R.id.calendar_item_panel3);
            item.user = (TextView) convertView.findViewById(R.id.calendar_item_user);
            item.date = (TextView) convertView.findViewById(R.id.calendar_item_date);
            item.state = (TextView) convertView.findViewById(R.id.calendar_item_state);
            item.customer = (TextView) convertView.findViewById(R.id.calendar_item_customer);
            item.contacts = (TextView) convertView.findViewById(R.id.calendar_item_contacts);
            item.content = (TextView) convertView.findViewById(R.id.calendar_item_content);
            item.plan = (TextView) convertView.findViewById(R.id.calendar_item_plan);
            item.walkway = (TextView) convertView.findViewById(R.id.calendar_item_walkway);
            item.distance = (TextView) convertView.findViewById(R.id.calendar_item_distance);
            item.memo = (TextView) convertView.findViewById(R.id.calendar_item_memo);
            item.time = (TextView) convertView.findViewById(R.id.calendar_item_time);

            convertView.setTag(item);
        }
        else
        {
            item = (ViewItem) convertView.getTag();
        }

        CalendarEntity entity = (CalendarEntity) getItem(position);
        item.user.setText(entity.getUser());
        item.date.setText(entity.getDate());
        item.state.setText(entity.getState());
        item.customer.setText(entity.getCustomer());
        item.contacts.setText(entity.getContacts());
        item.content.setText(entity.getContent());
        item.plan.setText(entity.getPlan());
        item.walkway.setText(entity.getWalkway());
        item.distance.setText(entity.getDistance());
        item.memo.setText(entity.getMemo());
        item.time.setText(entity.getTime());

        if (entity.getPlan().equals(""))
        {
            item.panel1.setVisibility(View.GONE);
        }
        if (entity.getWalkway().equals("") && entity.getDistance().equals(""))
        {
            item.panel2.setVisibility(View.GONE);
        }
        if (entity.getMemo().equals(""))
        {
            item.panel3.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewItem
    {
        LinearLayout panel1;
        LinearLayout panel2;
        LinearLayout panel3;
        TextView user;
        TextView date;
        TextView state;
        TextView customer;
        TextView contacts;
        TextView content;
        TextView plan;
        TextView walkway;
        TextView distance;
        TextView memo;
        TextView time;
    }
}