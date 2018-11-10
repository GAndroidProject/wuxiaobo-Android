package xiaoe.com.common.entitys;

/**
 * 最近更新列表项
 */
public class RecentUpdateListItem {

    // item 标题
    private String listTitle;
    // item 图标
    private String listPlayState;
    // item 资源 id
    private String listResourceId;
    // item 是否为游客
    private boolean listIsFormUser;

    private String appId;
    private String imgUrl;
    private String imgUrlCompress;
    private String startAt;
    private String tryM3u8Url;
    private String tryAudioUrl;
    private int audioLength;
    private int resourceType;
    private int videoLength;
    private String audioUrl;
    private String audioCompressUrl;
    private String m3u8Url;

    private String columnId = "";
    private String bigColumnId = "";
    private String mColumnTitle = "";

    public RecentUpdateListItem() {

    }

    public RecentUpdateListItem(String listTitle, String listPlayState) {

        this.listTitle = listTitle;
        this.listPlayState = listPlayState;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }

    public void setListPlayState(String listPlayState) {
        this.listPlayState = listPlayState;
    }

    public String getListTitle() {
        return listTitle;
    }

    public String getListPlayState() {
        return listPlayState;
    }

    public void setListResourceId(String listResourceId) {
        this.listResourceId = listResourceId;
    }

    public String getListResourceId() {
        return listResourceId;
    }


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgUrlCompress() {
        return imgUrlCompress;
    }

    public void setImgUrlCompress(String imgUrlCompress) {
        this.imgUrlCompress = imgUrlCompress;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getTryM3u8Url() {
        return tryM3u8Url;
    }

    public void setTryM3u8Url(String tryM3u8Url) {
        this.tryM3u8Url = tryM3u8Url;
    }

    public String getTryAudioUrl() {
        return tryAudioUrl;
    }

    public void setTryAudioUrl(String tryAudioUrl) {
        this.tryAudioUrl = tryAudioUrl;
    }

    public int getAudioLength() {
        return audioLength;
    }

    public void setAudioLength(int audioLength) {
        this.audioLength = audioLength;
    }

    public int getResourceType() {
        return resourceType;
    }

    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
    }

    public int getVideoLength() {
        return videoLength;
    }

    public void setVideoLength(int videoLength) {
        this.videoLength = videoLength;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getAudioCompressUrl() {
        return audioCompressUrl;
    }

    public void setAudioCompressUrl(String audioCompressUrl) {
        this.audioCompressUrl = audioCompressUrl;
    }

    public String getM3u8Url() {
        return m3u8Url;
    }

    public void setM3u8Url(String m3u8Url) {
        this.m3u8Url = m3u8Url;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getBigColumnId() {
        return bigColumnId;
    }

    public void setBigColumnId(String bigColumnId) {
        this.bigColumnId = bigColumnId;
    }

    public boolean isListIsFormUser() {
        return listIsFormUser;
    }

    public void setListIsFormUser(boolean listIsFormUser) {
        this.listIsFormUser = listIsFormUser;
    }

    public String getColumnTitle() {
        return mColumnTitle;
    }

    public void setColumnTitle(String mColumnTitle) {
        this.mColumnTitle = mColumnTitle;
    }
}
