package xiaoe.com.common.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

public interface ISQLiteCallBack {
    String getDatabaseName();

    int getVersion();

    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    List<String> createTablesSQL();

    <T> void assignValuesByEntity(String tableName, T entity, ContentValues values);

    <T> T newEntityByCursor(String tableName, Cursor cursor);

    String getTableName();
}
