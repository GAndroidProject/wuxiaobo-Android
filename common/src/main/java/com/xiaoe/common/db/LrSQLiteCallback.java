package com.xiaoe.common.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.xiaoe.common.entitys.LearningRecord;
import com.xiaoe.common.entitys.LrEntity;

import java.util.Arrays;
import java.util.List;

/**
 * 学习记录数据处理
 */
public class LrSQLiteCallback implements ISQLiteCallBack{

    public static final String TABLE_NAME_LR = "lr_table"; // 学习记录表

    private static final String TYPE_TEXT = " TEXT";
    private static final String SEP_COMMA = ",";

    // 建表 sql
    public static final String TABLE_SCHEMA_LR =
            "CREATE TABLE " + TABLE_NAME_LR + " (" +
            LrEntity.COLUMN_NAME_LR_ID + TYPE_TEXT + " PRIMARY KEY " + SEP_COMMA +
            LrEntity.COLUMN_NAME_LR_TYPE + TYPE_TEXT + SEP_COMMA +
            LrEntity.COLUMN_NAME_LR_TITLE + TYPE_TEXT + SEP_COMMA +
            LrEntity.COLUMN_NAME_LR_IMG + TYPE_TEXT + SEP_COMMA +
            LrEntity.COLUMN_NAME_LR_DESC + TYPE_TEXT + ")";


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
        return Arrays.asList(TABLE_SCHEMA_LR);
    }

    @Override
    public <T> void assignValuesByEntity(String tableName, T entity, ContentValues values) {
        switch (tableName) {
            case TABLE_NAME_LR:
                if (entity instanceof LearningRecord) {
                    LearningRecord lr = (LearningRecord) entity;
                    values.put(LrEntity.COLUMN_NAME_LR_ID, lr.getLrId());
                    values.put(LrEntity.COLUMN_NAME_LR_TYPE, lr.getLrType());
                    values.put(LrEntity.COLUMN_NAME_LR_TITLE, lr.getLrTitle());
                    values.put(LrEntity.COLUMN_NAME_LR_IMG, lr.getLrImg());
                    values.put(LrEntity.COLUMN_NAME_LR_DESC, lr.getLrDesc());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public Object newEntityByCursor(String tableName, Cursor cursor) {
        switch (tableName) {
            case TABLE_NAME_LR:
                LearningRecord lr = new LearningRecord();
                lr.setLrId(cursor.getString(cursor.getColumnIndex(LrEntity.COLUMN_NAME_LR_ID)));
                lr.setLrType(cursor.getString(cursor.getColumnIndex(LrEntity.COLUMN_NAME_LR_TYPE)));
                lr.setLrTitle(cursor.getString(cursor.getColumnIndex(LrEntity.COLUMN_NAME_LR_TITLE)));
                lr.setLrImg(cursor.getString(cursor.getColumnIndex(LrEntity.COLUMN_NAME_LR_IMG)));
                lr.setLrDesc(cursor.getString(cursor.getColumnIndex(LrEntity.COLUMN_NAME_LR_DESC)));
                return lr;
            default:
                break;
        }
        return null;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME_LR;
    }
}
