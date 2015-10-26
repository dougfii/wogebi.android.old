package com.wogebi.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import com.wogebi.android.BaseApplication;
import com.wogebi.android.entity.BaseEntity;

public abstract class EntityAdapter extends BaseAdapter
{
    protected BaseApplication application;
    protected Context context;
    protected LayoutInflater inflater;
    protected List<? extends BaseEntity> entities = new ArrayList<>();

    public EntityAdapter(BaseApplication application, Context context, List<? extends BaseEntity> entities)
    {
        this.application = application;
        this.context = context;
        inflater = LayoutInflater.from(context);
        if (entities != null)
        {
            this.entities = entities;
        }
    }

    @Override
    public int getCount()
    {
        return entities.size();
    }

    @Override
    public Object getItem(int position)
    {
        return entities.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return null;
    }

    public List<? extends BaseEntity> getEntities()
    {
        return entities;
    }
}