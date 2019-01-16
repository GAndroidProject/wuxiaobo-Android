package com.xiaoe.network.downloadUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xiaoe.common.app.CommonUserInfo;
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
    private SQLiteDatabase mDB = null;
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
//            entry.getValue().onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        }
        try {
            switch (oldVersion) {
                // 数据库版本为 1 的数据版本（wxb 二期迭代，已上线版本为 1，二期迭代上线后版本为 3）
                case 1:
                case 2:
                case 3:
                    // 此处需要注意不要使用 sqLiteDatabase.beginTransaction(); 这个方法是用于独立一个 sqLiteDatabase 对象来处理事务，完成后全局使用的 sqLiteDatabase 不能感知到变化
                    if (isTableExit(sqLiteDatabase, "download_file")) {
                        // 单纯新增字段的更新方式
                        updateDataByAdd(sqLiteDatabase);
                    } else {
                        onCreate(sqLiteDatabase);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据库升级，新增字段并进行数据迁移
     *
     * 新增字段为 parent_id、parent_type、top_parent_id、top_parent_type
     * @param sqLiteDatabase
     */
    private void updateDataByMigration(SQLiteDatabase sqLiteDatabase) {
        // 将表名改为临时表
        String downloadFile = "ALTER TABLE download_file RENAME TO download_file_temp";
        sqLiteDatabase.execSQL(downloadFile);

        // 创建新表
        // 新下载文件表创建
        String createNewDFTable = "CREATE TABLE download_file (" +
                "app_id VARCHAR(64) not null, " +
                "user_id VARCHAR(64) not null, " +
                "resource_id VARCHAR(64) not null, " +
                "id VARCHAR(512) not null, " +
                "column_id VARCHAR(64) default null, " +
                "big_column_id VARCHAR(64) default null, " +
                "parent_id VARCHAR(64) default \"-1\", " +
                "parent_type INTEGER default -1, " +
                "top_parent_id VARCHAR(64) default \"-1\"," +
                "top_parent_type INTEGER default -1," +
                "progress Long default 0," +
                "file_type INTEGER default 0," +
                "total_size Long default 0," +
                "local_file_path VARCHAR(512) default \"\"," +
                "file_name VARCHAR(512) default \"\"," +
                "file_download_url VARCHAR(512) default \"\"," +
                "download_state INTEGER default 0," +
                "title TEXT default \"\", " +
                "desc TEXT default \"\", " +
                "img_url TEXT default \"\", " +
                "resource_type INTEGER default 0, " +//1-音频，2-视频，3-专栏，4-大专栏
                "create_at DATETIME default '0000-00-00 00:00:00'," +
                "update_at DATETIME default '0000-00-00 00:00:00'," +
                "primary key (app_id, user_id, id))";
        sqLiteDatabase.execSQL(createNewDFTable);

        Cursor cursor = sqLiteDatabase.rawQuery("select * from download_file_temp", null);

        if (cursor.moveToFirst()) {
            // 有数据则导入数据
            String insertDFSql = "INSERT INTO download_file (app_id,user_id,resource_id,id,column_id,big_column_id,progress,file_type,total_size,local_file_path,file_name,file_download_url,download_state,title,desc,img_url,resource_type,create_at,update_at) " +
                    "SELECT app_id,user_id,resource_id,id,column_id,big_column_id,progress,file_type,total_size,local_file_path,file_name,file_download_url,download_state,title,desc,img_url,resource_type,create_at,update_at from download_file_temp";
            sqLiteDatabase.execSQL(insertDFSql);
            cursor.close();
        }

        // 删除临时表
        String deleteDFSql = "DROP TABLE download_file_temp";
        sqLiteDatabase.execSQL(deleteDFSql);
    }

    /**
     * 数据库升级，单纯添加字段形式
     *
     * 新增字段为 parent_id、parent_type、top_parent_id、top_parent_type
     * @param sqLiteDatabase sqLiteDatabase
     */
    private void updateDataByAdd(SQLiteDatabase sqLiteDatabase) {
        if (!isColumnExist(sqLiteDatabase, "download_file", "parent_id")) {
            String addParentIdColumn = "ALTER TABLE download_file ADD COLUMN parent_id VARCHAR(64) default \"-1\"";
            sqLiteDatabase.execSQL(addParentIdColumn);
        }
        if (!isColumnExist(sqLiteDatabase, "download_file", "parent_type")) {
            String addParentTypeColumn = "ALTER TABLE download_file ADD COLUMN parent_type INTEGER default -1";
            sqLiteDatabase.execSQL(addParentTypeColumn);
        }
        if (!isColumnExist(sqLiteDatabase, "download_file", "top_parent_id")) {
            String addTopParentIdColumn = "ALTER TABLE download_file ADD COLUMN top_parent_id VARCHAR(64) default \"-1\"";
            sqLiteDatabase.execSQL(addTopParentIdColumn);
        }
        if (!isColumnExist(sqLiteDatabase, "download_file", "top_parent_type")) {
            String addTopParentTypeColumn = "ALTER TABLE download_file ADD COLUMN top_parent_type INTEGER default -1";
            sqLiteDatabase.execSQL(addTopParentTypeColumn);
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
        String whereSQL = "app_id=? and user_id=? and resource_id=?";
        String[] whereVal = {downloadTableInfo.getAppId(), CommonUserInfo.getLoginUserIdOrAnonymousUserId(), downloadTableInfo.getResourceId()};
//        SQLiteUtil.update(TABLE_NAME, downloadTableInfo, whereSQL, whereVal);
        update(DownloadFileConfig.TABLE_NAME, downloadTableInfo, whereSQL, whereVal);
    }

    public void insertDownloadInfo(DownloadTableInfo downloadTableInfo){
        insert(DownloadFileConfig.TABLE_NAME, downloadTableInfo);
    }

    /**
     * 判断 tableName 是否存在，sqlite_master 是 sqlite 数据库默认产生的表，可以在里面查询
     * @param db
     * @param tableName
     * @return
     */
    private boolean isTableExit(SQLiteDatabase db, String tableName) {
        boolean abc = isColumnExist(db, "", "");
        try (Cursor cursor = db.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name=?", new String[]{tableName})) {
            boolean hasNext = cursor.moveToNext();
            return hasNext && cursor.getInt(0) > 0;
        }
    }

    /**
     * 判断列名是否存在
     * @param db         sqLiteDatabase
     * @param tableName  表名
     * @param columnName 列名
     * @return           返回列名是否存在
     */
    private boolean isColumnExist(SQLiteDatabase db, String tableName, String columnName) {
        try (Cursor cursor = db.rawQuery("SELECT count(*) FROM sqlite_master WHERE tbl_name = ? AND (sql LIKE ? OR sql LIKE ?);",
                new String[]{tableName, "%(" + columnName + "%", "%, " + columnName + " %"})) {
            boolean hasNext = cursor.moveToNext();
            return hasNext && cursor.getInt(0) > 0;
        }
    }
}
