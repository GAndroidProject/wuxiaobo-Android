package xiaoe.com.common.entitys;

public class AudioPlayTable {
    private static final String TAG = "AudioPlayTable";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "audio_play";
    private static final String TYPE_TEXT = "TEXT";
    private static final String TYPE_VARCHAR_64 = "VARCHAR(64)";
    private static final String TYPE_VARCHAR_256 = "VARCHAR(256)";
    private static final String TYPE_INTEGER = "INTEGER";
    private static final String TYPE_DATETIME = "DATETIME";
    //表里的字段
    private static final String appId = "app_id";
    private static final String resourceId = "resource_id";
    private static final String columnId = "column_id";
    private static final String title = "title";
    private static final String content = "content";
    private static final String playUrl = "play_url";
    private static final String currentPlayState = "current_play_state";//默认0,0-不是当前播放，1-当前播放
    private static final String state = "state";//默认0，0-正常，1-删除
    private static final String hasBuy = "has_buy";//是否购买，默认0，0-未购买，1-已购买
    private static final String createAt = "create_at";
    private static final String updateAt = "update_at";
    public static final String CREATE_TABLE_SQL = "CREATE TABLE "+TABLE_NAME+" ("+
            appId+" "+TYPE_VARCHAR_64+" not null,"+
            resourceId+" "+TYPE_VARCHAR_64+" not null,"+
            columnId+" "+TYPE_VARCHAR_64+" default \"\","+
            title+" "+TYPE_VARCHAR_256+" default \"\","+
            content+" "+TYPE_TEXT+" default \"\","+
            playUrl+" "+TYPE_TEXT+" default \"\","+
            currentPlayState+" "+TYPE_INTEGER+" default 0,"+
            state+" "+TYPE_INTEGER+" default 0,"+
            hasBuy+" "+TYPE_INTEGER+" default 0,"+
            createAt+" "+TYPE_DATETIME+" default '0000-00-00 00:00:00',"+
            updateAt+" "+TYPE_DATETIME+" default '0000-00-00 00:00:00',"+
            "primary key ("+appId +","+ resourceId+"))";

    public static String getAppId() {
        return appId;
    }

    public static String getResourceId() {
        return resourceId;
    }

    public static String getTitle() {
        return title;
    }

    public static String getContent() {
        return content;
    }

    public static String getPlayUrl() {
        return playUrl;
    }

    public static String getCurrentPlayState() {
        return currentPlayState;
    }

    public static String getState() {
        return state;
    }

    public static String getCreateAt() {
        return createAt;
    }

    public static String getUpdateAt() {
        return updateAt;
    }

    public static String getColumnId() {
        return columnId;
    }

    public static String getHasBuy() {
        return hasBuy;
    }
}
