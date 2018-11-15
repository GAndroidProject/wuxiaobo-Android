package xiaoe.com.network.downloadUtil;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Arrays;
import java.util.List;

import xiaoe.com.common.db.ISQLiteCallBack;

public class RelationTable implements ISQLiteCallBack {
    //下载资源关系路径表
    public static final String TABLE_NAME = "relation_table";
    public static final String CREATE_TABLE_SQL = "CREATE TABLE "+TABLE_NAME+"( "+
            "app_id VARCHAR(64) not null,"+
            "id VARCHAR(512) not null,"+//md5(resource_id + 专栏id + 大专栏id)
            "resource_id VARCHAR(64) not null,"+//单品资源id
            "path TEXT default \"\","+//json格式{"topic":"","column":""}//如果未空，说明是单品
            "primary key (app_id, resource_id, id))";

    @Override
    public String getDatabaseName() {
        return null;
    }

    @Override
    public int getVersion() {
        return 0;
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
        RelationTableInfo relationTableInfo = (RelationTableInfo) entity;
        values.put("app_id",relationTableInfo.getAppId());
        values.put("id",relationTableInfo.getId());
        values.put("resource_id",relationTableInfo.getResourceId());
        values.put("path",relationTableInfo.getPath());
    }

    @Override
    public Object newEntityByCursor(String tableName, Cursor cursor) {
        RelationTableInfo relationTableInfo = new RelationTableInfo();

        relationTableInfo.setAppId(cursor.getString(cursor.getColumnIndex("app_id")));
        relationTableInfo.setId(cursor.getString(cursor.getColumnIndex("id")));
        relationTableInfo.setResourceId(cursor.getString(cursor.getColumnIndex("resource_id")));
        relationTableInfo.setPath(cursor.getString(cursor.getColumnIndex("path")));

        return relationTableInfo;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
