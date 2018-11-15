package xiaoe.com.shop.business.audio.presenter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Arrays;
import java.util.List;

import xiaoe.com.common.entitys.AudioPlayEntity;
import xiaoe.com.common.entitys.AudioPlayTable;
import xiaoe.com.common.db.ISQLiteCallBack;

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
        return Arrays.asList(AudioPlayTable.CREATE_TABLE_SQL);
    }

    @Override
    public <T> void assignValuesByEntity(String tableName, T entity, ContentValues values) {
        AudioPlayEntity tableEntity = (AudioPlayEntity) entity;
        values.put(AudioPlayTable.getAppId(), tableEntity.getAppId());
        values.put(AudioPlayTable.getResourceId(), tableEntity.getResourceId());
        values.put(AudioPlayTable.getTitle(), tableEntity.getTitle());
        values.put(AudioPlayTable.getContent(), tableEntity.getContent());
        values.put(AudioPlayTable.getPlayUrl(), tableEntity.getPlayUrl());
        values.put(AudioPlayTable.getCurrentPlayState(), tableEntity.getCurrentPlayState());
        values.put(AudioPlayTable.getColumnId(), tableEntity.getColumnId());
        values.put(AudioPlayTable.getState(), tableEntity.getState());
        values.put(AudioPlayTable.getUpdateAt(), tableEntity.getUpdateAt());
        values.put(AudioPlayTable.getHasBuy(), tableEntity.getHasBuy());
        if(tableEntity.getCreateAt() != null){
            values.put(AudioPlayTable.getCreateAt(), tableEntity.getCreateAt());
        }
    }

    @Override
    public String getTableName() {
        return AudioPlayTable.TABLE_NAME;
    }

    @Override
    public Object newEntityByCursor(String tableName, Cursor cursor) {
        AudioPlayEntity entity = new AudioPlayEntity();
        int appIdIndex = cursor.getColumnIndex(AudioPlayTable.getAppId());
        entity.setAppId(cursor.getString(appIdIndex));

        int resourceIdIndex = cursor.getColumnIndex(AudioPlayTable.getResourceId());
        entity.setResourceId(cursor.getString(resourceIdIndex));

        int contentIndex = cursor.getColumnIndex(AudioPlayTable.getContent());
        entity.setContent(cursor.getString(contentIndex));

        int currentPlayStateIndex = cursor.getColumnIndex(AudioPlayTable.getCurrentPlayState());
        entity.setCurrentPlayState(cursor.getInt(currentPlayStateIndex));

        int playUrlIndex = cursor.getColumnIndex(AudioPlayTable.getPlayUrl());
        entity.setPlayUrl(cursor.getString(playUrlIndex));

        int stateIndex = cursor.getColumnIndex(AudioPlayTable.getState());
        entity.setState(cursor.getInt(stateIndex));

        int titleIndex = cursor.getColumnIndex(AudioPlayTable.getTitle());
        entity.setTitle(cursor.getString(titleIndex));

        int columnIdIndex = cursor.getColumnIndex(AudioPlayTable.getColumnId());
        entity.setColumnId(cursor.getString(columnIdIndex));

        int hasBuyIndex = cursor.getColumnIndex(AudioPlayTable.getHasBuy());
        entity.setHasBuy(cursor.getInt(hasBuyIndex));

        int createAtIndex = cursor.getColumnIndex(AudioPlayTable.getCreateAt());
        entity.setCreateAt(cursor.getString(createAtIndex));

        int updateAtIndex = cursor.getColumnIndex(AudioPlayTable.getUpdateAt());
        entity.setUpdateAt(cursor.getString(updateAtIndex));
        return entity;
    }
}
