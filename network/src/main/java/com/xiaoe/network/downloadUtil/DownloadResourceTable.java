package com.xiaoe.network.downloadUtil;

import android.content.ContentValues;
import android.database.Cursor;

import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.db.ISQLiteCallBack;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.DownloadResourceTableInfo;

import java.util.Arrays;
import java.util.List;

/**
 * 下载资源数据库操作类
 */
public class DownloadResourceTable implements ISQLiteCallBack {
    public static final String TABLE_NAME = "download_resource";
    public static final String CREATE_TABLE_SQL = "CREATE TABLE "+TABLE_NAME+"( "+
            "app_id VARCHAR(64) not null, "+
            "user_id VARCHAR(64) not null, "+
            "resource_id VARCHAR(64) not null, "+
            "parent_id VARCHAR(64) default \"\", " +
            "parent_type INTEGER default 0, " +
            "top_parent_id VARCHAR(64) default \"\", " +
            "top_parent_type INTEGER default 0, " +
            "title TEXT default \"\", "+
            "descs TEXT default \"\", "+
            "img_url TEXT default \"\", "+
            "resource_type INTEGER default 0, "+//1-音频，2-视频，3-专栏，4-大专栏
            "depth INTEGER default 0, "+//深度，大专栏-2，小专栏-1，单品-0
            "create_at DATETIME default '0000-00-00 00:00:00', "+
            "update_at DATETIME default '0000-00-00 00:00:00', "+
            "primary key (app_id, user_id, resource_id))";
    @Override
    public String getDatabaseName() {
        return null;
    }

    @Override
    public int getVersion() {
        return SQLiteUtil.DATABASE_VERSION;
    }


    @Override
    public List<String> createTablesSQL() {
        return Arrays.asList(CREATE_TABLE_SQL);
    }

    @Override
    public <T> void assignValuesByEntity(String tableName, T entity, ContentValues values) {
        DownloadResourceTableInfo tableInfo = (DownloadResourceTableInfo) entity;
        values.put("app_id", tableInfo.getAppId());
        values.put("user_id", CommonUserInfo.getLoginUserIdOrAnonymousUserId());
        values.put("resource_id", tableInfo.getResourceId());
        values.put("parent_id", tableInfo.getParentId());
        values.put("parent_type", tableInfo.getParentType());
        values.put("top_parent_id", tableInfo.getTopParentId());
        values.put("top_parent_type", tableInfo.getTopParentType());
        values.put("title", tableInfo.getTitle());
        values.put("descs", tableInfo.getDesc());
        values.put("img_url", tableInfo.getImgUrl());
        values.put("resource_type", tableInfo.getResourceType());
        values.put("depth", tableInfo.getDepth());
        values.put("create_at", tableInfo.getCreateAt());
        values.put("update_at", tableInfo.getUpdateAt());
    }

    @Override
    public Object newEntityByCursor(String tableName, Cursor cursor) {
        DownloadResourceTableInfo tableInfo = new DownloadResourceTableInfo();
        tableInfo.setAppId(cursor.getString(cursor.getColumnIndex("app_id")));
        tableInfo.setResourceId(cursor.getString(cursor.getColumnIndex("resource_id")));
        tableInfo.setParentId(cursor.getString(cursor.getColumnIndex("parent_id")));
        tableInfo.setParentType(cursor.getInt(cursor.getColumnIndex("parent_type")));
        tableInfo.setTopParentId(cursor.getString(cursor.getColumnIndex("top_parent_id")));
        tableInfo.setTopParentType(cursor.getInt(cursor.getColumnIndex("top_parent_type")));
        tableInfo.setTitle(cursor.getString(cursor.getColumnIndex("title")));
        tableInfo.setDesc(cursor.getString(cursor.getColumnIndex("descs")));
        tableInfo.setImgUrl(cursor.getString(cursor.getColumnIndex("img_url")));
        tableInfo.setResourceType(cursor.getInt(cursor.getColumnIndex("resource_type")));
        tableInfo.setDepth(cursor.getInt(cursor.getColumnIndex("depth")));
        return tableInfo;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
