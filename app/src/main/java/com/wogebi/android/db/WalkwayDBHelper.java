package com.wogebi.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import com.wogebi.android.entity.WalkwayEntity;

public class WalkwayDBHelper extends SQLiteOpenHelper
{
    private static final String DB_NAME = "gerp.db";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "walkway";

    private static WalkwayDBHelper instance;

    private WalkwayDBHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static WalkwayDBHelper getInstance(Context context)
    {
        if (instance == null)
        {
            synchronized (WalkwayDBHelper.class)
            {
                if (instance == null)
                {
                    instance = new WalkwayDBHelper(context);
                }
            }
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String sql = "create table " + DB_TABLE + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "code INT DEFAULT 0 ," +
                "lat REAL DEFAULT 0 ," +
                "lng REAL DEFAULT 0 ," +
                "radius REAL DEFAULT 0 ," +
                "time DATETIME DEFAULT '' ," +
                "date DATE DEFAULT(date('now','localtime')) ," +
                "timestamp DATETIME DEFAULT(datetime('now','localtime'))" +
                ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String sql = "drop table " + DB_TABLE + ";";
        db.execSQL(sql);
    }

    public static void insert(Context context, WalkwayEntity entity)
    {
        SQLiteDatabase db = WalkwayDBHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("code", entity.getCode());
        values.put("lat", entity.getLat());
        values.put("lng", entity.getLng());
        values.put("radius", entity.getRadius());
        values.put("time", entity.getTime());
        db.insert(DB_TABLE, null, values);
        db.close();
    }

    public static void update(Context context, int id)
    {
        SQLiteDatabase db = WalkwayDBHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("code", 0);
        db.update(DB_TABLE, values, "id=" + id, null);
        db.close();
    }

    public static void delete(Context context, int id)
    {
        SQLiteDatabase db = WalkwayDBHelper.getInstance(context).getWritableDatabase();
        db.delete(DB_TABLE, "id<=" + id, null);
        db.close();
    }

    public static List<WalkwayEntity> query(Context context, String order)
    {
        SQLiteDatabase db = WalkwayDBHelper.getInstance(context).getReadableDatabase();
        List<WalkwayEntity> entities = new ArrayList<>();

        Cursor cursor = db.query(DB_TABLE, null, null, null, null, null, order);
        if (cursor.moveToFirst())
        {
            do
            {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int code = cursor.getInt(cursor.getColumnIndex("code"));
                double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
                double lng = cursor.getDouble(cursor.getColumnIndex("lng"));
                float radius = cursor.getFloat(cursor.getColumnIndex("radius"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String date = cursor.getString(cursor.getColumnIndex("date"));

                WalkwayEntity entity = new WalkwayEntity(id, code, lat, lng, radius, time, date);
                entities.add(entity);
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return entities;
    }
}
