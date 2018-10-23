package xiaoe.com.shop.business.audio.presenter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Arrays;
import java.util.List;

import xiaoe.com.common.entitys.AudioPlayTable;
import xiaoe.com.common.entitys.AudioPlayTableEntity;
import xiaoe.com.common.utils.ISQLiteCallBack;

public class AudioSQLiteUtil implements ISQLiteCallBack {
    private final String TAG = "AudioSQLiteUtil";


    @Override
    public String getDatabaseName() {
        return null;
    }

    @Override
    public int getVersion() {
        return AudioPlayTable.DATABASE_VERSION;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion){
            db.execSQL(AudioPlayTable.CREATE_TABLE_SQL);
        }
    }

    @Override
    public List<String> createTablesSQL() {
        return Arrays.asList(AudioPlayTable.TABLE_NAME);
    }

    @Override
    public <T> void assignValuesByEntity(String tableName, T entity, ContentValues values) {
        AudioPlayTableEntity tableEntity = (AudioPlayTableEntity) entity;
        values.put(AudioPlayTable.getAppId(), tableEntity.getAppId());
        values.put(AudioPlayTable.getResourceId(), tableEntity.getResourceId());
        values.put(AudioPlayTable.getTitle(), tableEntity.getTitle());
        values.put(AudioPlayTable.getContent(), tableEntity.getContent());
        values.put(AudioPlayTable.getPlayUrl(), tableEntity.getPlayUrl());
        values.put(AudioPlayTable.getCurrentPlayState(), tableEntity.getCurrentPlayState());
        values.put(AudioPlayTable.getState(), tableEntity.getState());
        values.put(AudioPlayTable.getUpdateAt(), tableEntity.getUpdateAt());
        if(tableEntity.getCreateAt() != null){
            values.put(AudioPlayTable.getCreateAt(), tableEntity.getCreateAt());
        }
    }

    @Override
    public <T> T newEntityByCursor(String tableName, Cursor cursor) {
        return null;
    }
}
