package xiaoe.com.shop.business.search.presenter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Arrays;
import java.util.List;

import xiaoe.com.common.entitys.SearchHistory;
import xiaoe.com.common.entitys.SearchHistoryEntity;
import xiaoe.com.common.utils.ISQLiteCallBack;

public class SearchSQLiteCallback implements ISQLiteCallBack {

    public static final String TABLE_NAME_CONTENT = "search_history";

    private static final int DATABASE_VERSION = 1;
    private static final String TYPE_TEXT = " TEXT";
    private static final String SEP_COMMA = ",";

    // 建表 sql
    public static final String TABLE_SCHEMA_CONTENT =
            "CREATE TABLE " + TABLE_NAME_CONTENT + " (" +
            SearchHistoryEntity.COLUMN_NAME_ID + TYPE_TEXT + " PRIMARY KEY " + SEP_COMMA +
            SearchHistoryEntity.COLUMN_NAME_CONTENT + TYPE_TEXT + SEP_COMMA +
            SearchHistoryEntity.COLUMN_NAME_CREATE + TYPE_TEXT + ")";

    public SearchSQLiteCallback() {  }

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
        switch (oldVersion) {
            case 0:
                // 升级操作
                db.execSQL(TABLE_SCHEMA_CONTENT);
            case 1:
                break;
            default:
                break;
        }
    }

    @Override
    public List<String> createTablesSQL() {
        return Arrays.asList(TABLE_SCHEMA_CONTENT);
    }

    @Override
    public <T> void assignValuesByEntity(String tableName, T entity, ContentValues values) {
        switch (tableName) {
            case TABLE_NAME_CONTENT:
                if (entity instanceof SearchHistory) {
                    SearchHistory searchHistory = (SearchHistory) entity;
                    values.put(SearchHistoryEntity.COLUMN_NAME_ID, searchHistory.getmId());
                    values.put(SearchHistoryEntity.COLUMN_NAME_CONTENT, searchHistory.getmContent());
                    values.put(SearchHistoryEntity.COLUMN_NAME_CREATE, searchHistory.getmCreate());
                }
                break;
        }
    }

    @Override
    public Object newEntityByCursor(String tableName, Cursor cursor) {
        switch (tableName) {
            case TABLE_NAME_CONTENT:
                return new SearchHistory(
                        cursor.getString(cursor.getColumnIndex(SearchHistoryEntity.COLUMN_NAME_ID)),
                        cursor.getString(cursor.getColumnIndex(SearchHistoryEntity.COLUMN_NAME_CONTENT)),
                        cursor.getString(cursor.getColumnIndex(SearchHistoryEntity.COLUMN_NAME_CREATE))
                );
        }
        return null;
    }
}