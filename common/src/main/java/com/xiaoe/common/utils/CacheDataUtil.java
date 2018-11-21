package com.xiaoe.common.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiaoe.common.db.ISQLiteCallBack;
import com.xiaoe.common.entitys.CacheData;

import java.util.Arrays;
import java.util.List;

public class CacheDataUtil implements ISQLiteCallBack {
    private static final String TAG = "CacheDataUtil";
    public static final String TABLE_NAME = "data_cache";
    public static final String CREATE_TABLES_SQL = "CREATE TABLE "+TABLE_NAME+" ("+
            "app_id VARCHAR(64) not null, "+
            "resource_id VARCHAR(64) not null, "+
            "img_url TEXT default \"\", "+
            "img_url_compress TEXT default \"\", "+
            "content TEXT default \"\", "+
            "resource_list TEXT default \"\", "+//产品包资料列表
            "primary key (app_id, resource_id))";

    @Override
    public String getDatabaseName() {
        return TABLE_NAME;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public List<String> createTablesSQL() {
        return Arrays.asList(CREATE_TABLES_SQL);
    }

    @Override
    public <T> void assignValuesByEntity(String tableName, T entity, ContentValues values) {
        CacheData cacheData = (CacheData) entity;
        values.put("app_id", cacheData.getAppId());
        values.put("resource_id", cacheData.getResourceId());
        values.put("img_url", cacheData.getImgUrl());
        values.put("img_url_compress", cacheData.getImgUrlCompress());
        values.put("content", cacheData.getContent());
        values.put("resource_list", cacheData.getAppId());
    }

    @Override
    public Object newEntityByCursor(String tableName, Cursor cursor) {
        CacheData cacheData = new CacheData();
        cacheData.setAppId(cursor.getString(cursor.getColumnIndex("app_id")));
        cacheData.setResourceId(cursor.getString(cursor.getColumnIndex("resource_id")));
        cacheData.setImgUrl(cursor.getString(cursor.getColumnIndex("img_url")));
        cacheData.setImgUrlCompress(cursor.getString(cursor.getColumnIndex("img_url_compress")));
        cacheData.setContent(cursor.getString(cursor.getColumnIndex("content")));
        cacheData.setResourceList(cursor.getString(cursor.getColumnIndex("resource_list")));

        return cacheData;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
