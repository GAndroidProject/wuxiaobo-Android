package xiaoe.com.common.entitys;

public class AudioPlayEntity {
    private String TAG = "AudioPlayEntity";
    //表里的字段
    private String appId = null;
    private String resourceId = null;
    private String title = null;
    private String content = null;
    private String playUrl = null;
    private int currentPlayState = 0;//默认0,0-不是当前播放，1-当前播放
    private int state = 0;//默认0，0-正常，1-删除
    private String createAt = null;
    private String updateAt = null;
    private String columnId = null;
    private int index = -1;


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public int getCurrentPlayState() {
        return currentPlayState;
    }

    public void setCurrentPlayState(int currentPlayState) {
        this.currentPlayState = currentPlayState;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
