package com.xiaoe.common.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xiaoe.common.entitys.AudioPlayTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 一个通用的SQLite，通过简单的配置快速搭建一个数据库存储的方案；
 */

public final class SQLiteUtil extends SQLiteOpenHelper {

    private final String TAG = "SQLiteUtil";
    public static final int DATABASE_VERSION = 2;
    private static final  String DATABASE_NAME = "xiaoeshop.db";
    private static SQLiteUtil INSTANCE;
    private java.util.concurrent.Semaphore semaphoreTransaction = new java.util.concurrent.Semaphore(1);
    private AtomicInteger mOpenCounter = new AtomicInteger();
    private SQLiteDatabase mDB;
    /**
     * ConcurrentHashMap可以高并发操作，线程安全，高效
     */
    private ConcurrentHashMap<String, ISQLiteCallBack> sqlCallBack;//缓存下载进度集合



    private SQLiteUtil(Context context, ISQLiteCallBack callBack) {
        super(context, DATABASE_NAME, null, callBack.getVersion());
//        this.callBack = callBack;
    }

    public static SQLiteUtil init(@NonNull Context context, @NonNull ISQLiteCallBack callBack) {
        if(INSTANCE == null){
            synchronized (SQLiteUtil.class){
                if(INSTANCE == null){
                    INSTANCE = new SQLiteUtil(context, callBack);
                }
            }
        }
        if(INSTANCE.sqlCallBack == null){
            INSTANCE.sqlCallBack = new ConcurrentHashMap<String, ISQLiteCallBack>();
        }
        if(!TextUtils.isEmpty(callBack.getTableName())){
            INSTANCE.sqlCallBack.put(callBack.getTableName(), callBack);
        }
        return INSTANCE;
    }

    private SQLiteDatabase getSQLiteDataBase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            mDB = INSTANCE.getWritableDatabase();
        }
        return mDB;
    }

    private void closeSQLiteDatabase(){
        if(mOpenCounter.decrementAndGet() == 0){
            mDB.close();
        }
    }

    public <T> void insert(String tableName, T entity) {
        if(sqlCallBack.get(tableName) == null){
            return;
        }
        mDB = getWritableDatabase();
        ContentValues values = new ContentValues();
        sqlCallBack.get(tableName).assignValuesByEntity(tableName, entity, values);
        mDB.insert(tableName, null, values);
        values.clear();
        closeSQLiteDatabase();
    }


    public <T> void insert(String tableName, List<T> entities) {
        if(sqlCallBack.get(tableName) == null){
            return;
        }
        mDB= getWritableDatabase();
        try {
            semaphoreTransaction.acquire();
            mDB.beginTransaction();
            ContentValues values = new ContentValues();
            for (T entity : entities) {
                INSTANCE.sqlCallBack.get(tableName).assignValuesByEntity(tableName, entity, values);
                long lo = mDB.insert(tableName, null, values);
                values.clear();
            }
            mDB.setTransactionSuccessful();
        } catch (InterruptedException e) {
//            e.printStackTrace();
        } finally {
            mDB.endTransaction();
            semaphoreTransaction.release();
            closeSQLiteDatabase();
        }
    }

    public <T> void update(String tableName, T entity, String whereClause, String[] whereArgs) {
        if(sqlCallBack.get(tableName) == null){
            return;
        }
        mDB = getWritableDatabase();
        ContentValues values = new ContentValues();
        sqlCallBack.get(tableName).assignValuesByEntity(tableName, entity, values);
        mDB.update(tableName, values, whereClause, whereArgs);
        values.clear();
        closeSQLiteDatabase();
    }


    public <T> List<T> query(String tableName, @NonNull String queryStr, @Nullable String[] whereArgs) {
        mDB = getReadableDatabase();
        Cursor cursor = mDB.rawQuery(queryStr, whereArgs);
        List<T> lists = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                ISQLiteCallBack callBack = sqlCallBack.get(tableName);
                if(callBack == null){
                    continue;
                }
                T entity = callBack.newEntityByCursor(tableName, cursor);
                if (entity != null) {
                    lists.add(entity);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        closeSQLiteDatabase();
        return lists;
    }

    public void deleteFrom(String tableName) {

        mDB = getWritableDatabase();
        String sql = "DELETE FROM " + tableName;
        mDB.execSQL(sql);
        closeSQLiteDatabase();
    }

    // delete的适用场合是涉及到删除的对象数量较少时。
    // 当删除多条数据时（例如：500条），通过循环的方式来一个一个的删除需要12s，而使用execSQL语句结合(delete from table id in("1", "2", "3"))的方式只需要50ms
    public void delete(String tableName, String whereClause, String[] whereArgs) {
        mDB = getWritableDatabase();
        mDB.delete(tableName, whereClause, whereArgs);
        closeSQLiteDatabase();
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
        mDB = getWritableDatabase();
        mDB.execSQL(sql);
        closeSQLiteDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(Map.Entry<String ,ISQLiteCallBack> entry : INSTANCE.sqlCallBack.entrySet() ){
            for (String sql : entry.getValue().createTablesSQL()){
                db.execSQL(sql);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //根据数据版本号更新做不同的操作
        if(newVersion > oldVersion && newVersion == AudioPlayTable.DATABASE_VERSION){
            //列如，对音频表新增字段操作
        }
        if (oldVersion == 1) { // 数据库版本为 1 的数据版本
            try {
                sqLiteDatabase.beginTransaction();
                // 将表名改为临时表
                String downloadResource = "ALTER TABLE download_resource RENAME TO download_resource_temp";
                sqLiteDatabase.execSQL(downloadResource);

                // 创建新表
                // 新下载资源表创建
                String createNewDRTable = "CREATE TABLE download_resource (" +
                        "app_id VARCHAR(64) not null, " +
                        "user_id VARCHAR(64) not null, " +
                        "resource_id VARCHAR(64) not null, " +
                        "parent_id VARCHAR(64) default \"\", " +
                        "parent_type INTEGER default 0, " +
                        "top_parent_id VARCHAR(64) default \"\", " +
                        "top_parent_type INTEGER default 0, " +
                        "title TEXT default \"\", " +
                        "descs TEXT default \"\", " +
                        "img_url TEXT default \"\", " +
                        "resource_type INTEGER default 0, " +//1-音频，2-视频，3-专栏，4-大专栏
                        "depth INTEGER default 0, " +//深度，大专栏-2，小专栏-1，单品-0
                        "create_at DATETIME default '0000-00-00 00:00:00', " +
                        "update_at DATETIME default '0000-00-00 00:00:00', " +
                        "primary key (app_id, user_id, resource_id))";
                sqLiteDatabase.execSQL(createNewDRTable);

                Cursor cursor = sqLiteDatabase.rawQuery("select * from download_resource_temp", null);

                if (cursor.moveToFirst()) {
                    // 有数据则导入数据
                    String insertDRSql = "INSERT INTO download_resource (app_id,user_id,resource_id) " +
                            "SELECT app_id,user_id,resource_id from download_resource_temp";
                    sqLiteDatabase.execSQL(insertDRSql);
                    cursor.close();
                }

                // 删除临时表
                String deleteDRSql = "DROP TABLE download_resource_temp";
                sqLiteDatabase.execSQL(deleteDRSql);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (sqLiteDatabase != null) {
                    sqLiteDatabase.endTransaction();
                }
            }
        }
    }

    public boolean tabIsExist(String tabName){
        boolean result = false;
        if(tabName == null){
            return false;
        }
        mDB = getWritableDatabase();
        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from sqlite_master where type ='table' and name = '"+tabName.trim()+"'";
            cursor = mDB.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }
            cursor.close();
            closeSQLiteDatabase();
        } catch (Exception e) {
            if(cursor != null){
                cursor.close();
            }
            closeSQLiteDatabase();
        }
        return result;
    }
}
