package xiaoe.com.shop.business.login.presenter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Arrays;
import java.util.List;

import xiaoe.com.common.entitys.LoginUser;
import xiaoe.com.common.entitys.LoginUserEntity;
import xiaoe.com.common.utils.ISQLiteCallBack;

public class LoginSQLiteCallback implements ISQLiteCallBack {

    public static final String TABLE_NAME_USER = "user_table"; // 用户表（成功绑定手机的）

    private static final int DATABASE_VERSION = 1;
    private static final String TYPE_TEXT = " TEXT";
    private static final String SEP_COMMA = ",";

    // 建表 sql
    public static final String TABLE_SCHEMA_USER =
            "CREATE TABLE " + TABLE_NAME_USER + " (" +
                    LoginUserEntity.COLUMN_NAME_ROW_ID + TYPE_TEXT + " PRIMARY KEY " + SEP_COMMA +
                    LoginUserEntity.COLUMN_NAME_ID + TYPE_TEXT + SEP_COMMA +
                    LoginUserEntity.COLUMN_NAME_WX_OPEN_ID + TYPE_TEXT + SEP_COMMA +
                    LoginUserEntity.COLUMN_NAME_WX_UNION_ID + TYPE_TEXT + SEP_COMMA +
                    LoginUserEntity.COLUMN_NAME_API_TOKEN + TYPE_TEXT + SEP_COMMA +
                    LoginUserEntity.COLUMN_NAME_USER_ID + TYPE_TEXT + SEP_COMMA +
                    LoginUserEntity.COLUMN_NAME_WX_NICKNAME + TYPE_TEXT + SEP_COMMA +
                    LoginUserEntity.COLUMN_NAME_WX_AVATAR + TYPE_TEXT + SEP_COMMA +
                    LoginUserEntity.COLUMN_NAME_PHONE + TYPE_TEXT + SEP_COMMA +
                    LoginUserEntity.COLUMN_NAME_SHOP_ID + TYPE_TEXT + ")";

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
                db.execSQL(TABLE_SCHEMA_USER);
            case 1:
                break;
            default:
                break;
        }
    }

    @Override
    public List<String> createTablesSQL() {
        return Arrays.asList(TABLE_SCHEMA_USER);
    }

    @Override
    public <T> void assignValuesByEntity(String tableName, T entity, ContentValues values) {
        switch (tableName) {
            case TABLE_NAME_USER:
                if (entity instanceof LoginUser) {
                    LoginUser loginUser = (LoginUser) entity;
                    values.put(LoginUserEntity.COLUMN_NAME_ROW_ID, loginUser.getRowId());
                    values.put(LoginUserEntity.COLUMN_NAME_ID, loginUser.getId());
                    values.put(LoginUserEntity.COLUMN_NAME_WX_OPEN_ID, loginUser.getWxOpenId());
                    values.put(LoginUserEntity.COLUMN_NAME_WX_UNION_ID, loginUser.getWxUnionId());
                    values.put(LoginUserEntity.COLUMN_NAME_API_TOKEN, loginUser.getApi_token());
                    values.put(LoginUserEntity.COLUMN_NAME_USER_ID, loginUser.getUserId());
                    values.put(LoginUserEntity.COLUMN_NAME_WX_NICKNAME, loginUser.getWxNickname());
                    values.put(LoginUserEntity.COLUMN_NAME_WX_AVATAR, loginUser.getWxAvatar());
                    values.put(LoginUserEntity.COLUMN_NAME_PHONE, loginUser.getPhone());
                    values.put(LoginUserEntity.COLUMN_NAME_SHOP_ID, loginUser.getShopId());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public String getTableName() {
        return TABLE_NAME_USER;
    }

    @Override
    public Object newEntityByCursor(String tableName, Cursor cursor) {
        switch (tableName) {
            case TABLE_NAME_USER:
                LoginUser loginUser = new LoginUser();
                loginUser.setRowId(cursor.getString(cursor.getColumnIndex(LoginUserEntity.COLUMN_NAME_ROW_ID)));
                loginUser.setId(cursor.getString(cursor.getColumnIndex(LoginUserEntity.COLUMN_NAME_ID)));
                loginUser.setWxOpenId(cursor.getString(cursor.getColumnIndex(LoginUserEntity.COLUMN_NAME_WX_OPEN_ID)));
                loginUser.setWxUnionId(cursor.getString(cursor.getColumnIndex(LoginUserEntity.COLUMN_NAME_WX_UNION_ID)));
                loginUser.setApi_token(cursor.getString(cursor.getColumnIndex(LoginUserEntity.COLUMN_NAME_API_TOKEN)));
                loginUser.setUserId(cursor.getString(cursor.getColumnIndex(LoginUserEntity.COLUMN_NAME_USER_ID)));
                loginUser.setWxNickname(cursor.getString(cursor.getColumnIndex(LoginUserEntity.COLUMN_NAME_WX_NICKNAME)));
                loginUser.setWxAvatar(cursor.getString(cursor.getColumnIndex(LoginUserEntity.COLUMN_NAME_WX_AVATAR)));
                loginUser.setPhone(cursor.getString(cursor.getColumnIndex(LoginUserEntity.COLUMN_NAME_PHONE)));
                loginUser.setShopId(cursor.getString(cursor.getColumnIndex(LoginUserEntity.COLUMN_NAME_SHOP_ID)));
                return loginUser;
            default:
                break;
        }
        return null;
    }

}