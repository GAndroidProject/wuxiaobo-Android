package xiaoe.com.network.downloadUtil;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import xiaoe.com.common.entitys.DownloadTableInfo;
import xiaoe.com.common.db.ISQLiteCallBack;
import xiaoe.com.common.db.SQLiteUtil;

/**
 * Created by Administrator on 2018/1/3.
 */

public class DownloadFileConfig implements ISQLiteCallBack {
    private static final String TAG = "DownloadFileConfig";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "download_file";
    public static final String APP_ID = "app_id";
    public static final String ID = "id";
    public static final String RESOURCE_ID = "resource_id";//不可为空
    public static final String COLUMN_ID = "column_id";
    public static final String BIG_COLUMN_ID = "big_column_id";
    public static final String PROGRESS = "progress";//下载进度
    public static final String TOTAL_SIZE = "total_size";//文件大小
    public static final String LOCAL_FILE_PATH = "local_file_path";//下载本地文件目录路径
    public static final String DOWNLOAD_STATE = "download_state";//0-等待，1-下载中，2-暂停，3-完成
    public static final String FILE_TYPE = "file_type";
    public static final String FILE_NAME= "file_name";
    public static final String FILE_DOWNLOAD_URL = "file_download_url";
    public static final String TITLE = "title";
    public static final String DESC = "desc";
    public static final String IMG_URL = "img_url";
    public static final String RESOURCE_TYPE = "resource_type";
    public static final String CREATE_AT = "create_at";
    public static final String UPDATE_AT = "update_at";
    private static DownloadFileConfig downloadFileConfig;

    public static final String CREATE_TABLE_SQL = "CREATE TABLE "+TABLE_NAME+" ("+
            APP_ID+" VARCHAR(64) not null,"+
            RESOURCE_ID+" VARCHAR(64) not null,"+
            ID+" VARCHAR(512) not null,"+
            COLUMN_ID+" VARCHAR(64) default null,"+
            BIG_COLUMN_ID+" VARCHAR(64) default null,"+
            PROGRESS+" Long default 0,"+
            FILE_TYPE+" INTEGER default 0,"+
            TOTAL_SIZE+" Long default 0,"+
            LOCAL_FILE_PATH+" VARCHAR(512) default \"\","+
            FILE_NAME+" VARCHAR(512) default \"\","+
            FILE_DOWNLOAD_URL+" VARCHAR(512) default \"\","+
            DOWNLOAD_STATE+" INTEGER default 0,"+
            TITLE+" TEXT default \"\", "+
            DESC+" TEXT default \"\", "+
            IMG_URL+" TEXT default \"\", "+
            RESOURCE_TYPE+" INTEGER default 0, "+//1-音频，2-视频，3-专栏，4-大专栏
            CREATE_AT+" DATETIME default '0000-00-00 00:00:00',"+
            UPDATE_AT+" DATETIME default '0000-00-00 00:00:00',"+
            "primary key ("+APP_ID +","+ ID+"))";
//    private static DownloadSQLiteUtil downloadSQLiteUtil;


    public static DownloadFileConfig getInstance(){
        if(downloadFileConfig == null){
            synchronized (DownloadFileConfig.class){
                if(downloadFileConfig == null){
                    downloadFileConfig = new DownloadFileConfig();
//                    downloadSQLiteUtil = new DownloadSQLiteUtil(XiaoeApplication.getmContext(), downloadFileConfig);
//                    if(!downloadSQLiteUtil.tabIsExist(TABLE_NAME)){
//                        downloadSQLiteUtil.execSQL(CREATE_TABLE_SQL);
//                        downloadSQLiteUtil.dbClose();
//                    }
                }
            }
        }
        return downloadFileConfig;
    }

//    public DownloadFileConfig insertDownloadInfo(DownloadTableInfo downloadTableInfo){
//        downloadSQLiteUtil.insert(TABLE_NAME, downloadTableInfo);
//
//        return downloadFileConfig;
//    }
//    public DownloadFileConfig updateDownloadInfo(DownloadTableInfo downloadTableInfo){
//        String whereSQL = APP_ID+"=? and "+RESOURCE_ID+"=?";
//        String[] whereVal = {downloadTableInfo.getAppId(), downloadTableInfo.getResourceId()};
////        SQLiteUtil.update(TABLE_NAME, downloadTableInfo, whereSQL, whereVal);
//        downloadSQLiteUtil.update(TABLE_NAME, downloadTableInfo, whereSQL, whereVal);
//        return downloadFileConfig;
//    }

    public List<DownloadTableInfo> getDownloadInfo(){
        String querySQL = "select * from "+TABLE_NAME;
        List<DownloadTableInfo> downloadTableInfos = SQLiteUtil.query(TABLE_NAME, querySQL, null);
        Log.d(TAG, "getDownloadInfo: "+downloadTableInfos.size());
        return downloadTableInfos;
    }

    @Override
    public String getDatabaseName() {
        return null;
    }

    @Override
    public int getVersion() {
        return DATABASE_VERSION;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public List<String> createTablesSQL() {
        return Arrays.asList(CREATE_TABLE_SQL);
    }


    @Override
    public <T> void assignValuesByEntity(String tableName, T entity, ContentValues values) {
        DownloadTableInfo downloadTableInfo = (DownloadTableInfo) entity;
        values.put(APP_ID, downloadTableInfo.getAppId());
        values.put(ID, downloadTableInfo.getId());
        values.put(RESOURCE_ID, downloadTableInfo.getResourceId());
        values.put(COLUMN_ID, downloadTableInfo.getColumnId());
        values.put(BIG_COLUMN_ID, downloadTableInfo.getBigColumnId());
        values.put(PROGRESS, downloadTableInfo.getProgress());
        values.put(TOTAL_SIZE, downloadTableInfo.getTotalSize());
        values.put(LOCAL_FILE_PATH, downloadTableInfo.getLocalFilePath());
        values.put(DOWNLOAD_STATE, downloadTableInfo.getDownloadState());
        values.put(FILE_TYPE, downloadTableInfo.getFileType());
        values.put(FILE_NAME, downloadTableInfo.getFileName());
        values.put(FILE_DOWNLOAD_URL, downloadTableInfo.getFileDownloadUrl());
        values.put(TITLE, downloadTableInfo.getTitle());
        values.put(DESC, downloadTableInfo.getDesc());
        values.put(IMG_URL, downloadTableInfo.getImgUrl());
        values.put(RESOURCE_TYPE, downloadTableInfo.getResourceType());
        if(downloadTableInfo.getCreateAt() != null){
            values.put(CREATE_AT, downloadTableInfo.getCreateAt());
        }
        if(downloadTableInfo.getUpdateAt() != null){
            values.put(UPDATE_AT, downloadTableInfo.getUpdateAt());
        }
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public Object newEntityByCursor(String tableName, Cursor cursor) {

        DownloadTableInfo downloadTableInfo = new DownloadTableInfo();
        int appIndex = cursor.getColumnIndex(APP_ID);
        downloadTableInfo.setAppId(cursor.getString(appIndex));

        int idIndex = cursor.getColumnIndex(ID);
        downloadTableInfo.setId(cursor.getString(idIndex));

        int resourceIndex = cursor.getColumnIndex(RESOURCE_ID);
        downloadTableInfo.setResourceId(cursor.getString(resourceIndex));

        int columnIdIndex = cursor.getColumnIndex(COLUMN_ID);
        downloadTableInfo.setColumnId(cursor.getString(columnIdIndex));

        int bigColumnIdIndex = cursor.getColumnIndex(BIG_COLUMN_ID);
        downloadTableInfo.setBigColumnId(cursor.getString(bigColumnIdIndex));

        int progressIndex = cursor.getColumnIndex(PROGRESS);
        downloadTableInfo.setProgress(cursor.getInt(progressIndex));

        int totalSizeIndex = cursor.getColumnIndex(TOTAL_SIZE);
        downloadTableInfo.setTotalSize(cursor.getInt(totalSizeIndex));

        int localFilePathIndex = cursor.getColumnIndex(LOCAL_FILE_PATH);
        downloadTableInfo.setLocalFilePath(cursor.getString(localFilePathIndex));

        int downloadStateIndex = cursor.getColumnIndex(DOWNLOAD_STATE);
        downloadTableInfo.setDownloadState(cursor.getInt(downloadStateIndex));

        int fileTypeIndex = cursor.getColumnIndex(FILE_TYPE);
        downloadTableInfo.setFileType(cursor.getInt(fileTypeIndex));

        int fileNameIndex = cursor.getColumnIndex(FILE_NAME);
        downloadTableInfo.setFileName(cursor.getString(fileNameIndex));

        int fileDownloadUrlIndex = cursor.getColumnIndex(FILE_DOWNLOAD_URL);
        downloadTableInfo.setFileDownloadUrl(cursor.getString(fileDownloadUrlIndex));

        int titleIndex = cursor.getColumnIndex(TITLE);
        downloadTableInfo.setTitle(cursor.getString(titleIndex));

        int descIndex = cursor.getColumnIndex(DESC);
        downloadTableInfo.setDesc(cursor.getString(descIndex));

        int imgUrlIndex = cursor.getColumnIndex(IMG_URL);
        downloadTableInfo.setImgUrl(cursor.getString(imgUrlIndex));

        int resourceTypeIndex = cursor.getColumnIndex(RESOURCE_TYPE);
        downloadTableInfo.setResourceType(cursor.getInt(resourceTypeIndex));

        return downloadTableInfo;
    }

//    public <T> List<T> query(String tableName, @NonNull String queryStr, @Nullable String[] whereArgs) {
//        return downloadSQLiteUtil.query(tableName, queryStr, whereArgs);
//    }
//
//    public void delete(String tableName, String whereClause, String[] whereArgs){
//        downloadSQLiteUtil.delete(tableName, whereClause, whereArgs);
//    }
//
//    public void execSQL(String sql){
//        downloadSQLiteUtil.execSQL(sql);
//    }
//
//    public void dbClose(){
//        downloadSQLiteUtil.dbClose();
//    }
}
