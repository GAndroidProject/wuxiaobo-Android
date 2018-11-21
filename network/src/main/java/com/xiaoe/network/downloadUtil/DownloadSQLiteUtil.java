package com.xiaoe.network.downloadUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xiaoe.common.db.ISQLiteCallBack;
import com.xiaoe.common.entitys.DownloadTableInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一个通用的SQLite，通过简单的配置快速搭建一个数据库存储的方案；
 */

public final class DownloadSQLiteUtil extends SQLiteOpenHelper {

    private final String TAG = "SQLiteUtil";
    private static final String DATABASE_NAME = "xiaoeshop_download.db";
    private DownloadSQLiteUtil INSTANCE;
    SQLiteDatabase mDB = null;
    /**
     * ConcurrentHashMap可以高并发操作，线程安全，高效
     */
    private ConcurrentHashMap<String, ISQLiteCallBack> sqlCallBack;//缓存下载进度集合



    public DownloadSQLiteUtil(Context context, ISQLiteCallBack callBack) {
        super(context, DATABASE_NAME, null, callBack.getVersion());
        if(sqlCallBack == null){
            sqlCallBack = new ConcurrentHashMap<String, ISQLiteCallBack>();
        }
        if(!TextUtils.isEmpty(callBack.getTableName())){
            sqlCallBack.put(callBack.getTableName(), callBack);
        }
        INSTANCE = this;
        mDB = INSTANCE.getWritableDatabase();
    }

    private SQLiteDatabase getDB(){
        if(mDB == null || !mDB.isOpen()){
            mDB = INSTANCE.getWritableDatabase();
        }
        return mDB;
    }

    public <T> void insert(String tableName, T entity) {
        if(INSTANCE.sqlCallBack.get(tableName) == null){
            return;
        }
        SQLiteDatabase db = getDB();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            INSTANCE.sqlCallBack.get(tableName).assignValuesByEntity(tableName, entity, values);
            db.insert(tableName, null, values);
            values.clear();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }



    public <T> void update(String tableName, T entity, String whereClause, String[] whereArgs) {
        if(INSTANCE.sqlCallBack.get(tableName) == null){
            return;
        }
        SQLiteDatabase db = getDB();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            INSTANCE.sqlCallBack.get(tableName).assignValuesByEntity(tableName, entity, values);
            db.update(tableName, values, whereClause, whereArgs);
            values.clear();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }


    public <T> List<T> query(String tableName, @NonNull String queryStr, @Nullable String[] whereArgs) {
        SQLiteDatabase db = getDB();
        Cursor cursor = db.rawQuery(queryStr, whereArgs);
        try {
            List<T> lists = new ArrayList<>(cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    ISQLiteCallBack callBack = INSTANCE.sqlCallBack.get(tableName);
                    if(callBack == null){
                        continue;
                    }
                    T entity = callBack.newEntityByCursor(tableName, cursor);
                    if (entity != null) {
                        lists.add(entity);
                    }
                } while (cursor.moveToNext());
            }
            return lists;
        } finally {
            cursor.close();
            db.close();
        }
    }

    public void deleteFrom(String tableName) {

        SQLiteDatabase db = getDB();
        db.beginTransaction();
        try {
            String sql = "DELETE FROM " + tableName;
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // delete的适用场合是涉及到删除的对象数量较少时。
    // 当删除多条数据时（例如：500条），通过循环的方式来一个一个的删除需要12s，而使用execSQL语句结合(delete from table id in("1", "2", "3"))的方式只需要50ms
    public void delete(String tableName, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getDB();
        db.beginTransaction();
        try {
            db.delete(tableName, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /*
     * 当操作数据较多时，直接使用sql语句或许效率更高
     *
     * 执行sql语句（例如: String sql = "delete from tableName where mac in ('24:71:89:0A:DD:82', '24:71:89:0A:DD:83','24:71:89:0A:DD:84')"）
     * 注意：db.execSQL文档中有说明"the SQL statement to be executed. Multiple statements separated by semicolons are not supported."，
     * 也就是说通过分号分割的多个statement操作是不支持的。
     *
     */
    public void execSQL(String sql) {
        SQLiteDatabase db = getDB();
        db.beginTransaction();
        try {
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(Map.Entry<String ,ISQLiteCallBack> entry : sqlCallBack.entrySet() ){
            for (String sql : entry.getValue().createTablesSQL()){
                db.execSQL(sql);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        for(Map.Entry<String ,ISQLiteCallBack> entry : sqlCallBack.entrySet() ){
            entry.getValue().onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        }
    }

    public boolean tabIsExist(String tabName){
        boolean result = false;
        if(tabName == null){
            return false;
        }
        SQLiteDatabase db = getDB();
        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from sqlite_master where type ='table' and name = '"+tabName.trim()+"'";
            cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            if(cursor != null){
                cursor.close();
            }
            if(db != null){
                db.close();
            }
        }
        return result;
    }

    public void dbClose(){
        if(mDB != null && mDB.isOpen()){
            mDB.close();
        }
    }


    public void updateDownloadInfo(DownloadTableInfo downloadTableInfo){
        String whereSQL = "app_id=? and resource_id=?";
        String[] whereVal = {downloadTableInfo.getAppId(), downloadTableInfo.getResourceId()};
//        SQLiteUtil.update(TABLE_NAME, downloadTableInfo, whereSQL, whereVal);
        update(DownloadFileConfig.TABLE_NAME, downloadTableInfo, whereSQL, whereVal);
    }

    public void insertDownloadInfo(DownloadTableInfo downloadTableInfo){
        insert(DownloadFileConfig.TABLE_NAME, downloadTableInfo);
    }
}
