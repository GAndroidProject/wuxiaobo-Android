package com.xiaoe.common.entitys;

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
    private String columnId = "";
    private String bigColumnId = "";
    private int price;
    private String imgUrl;
    private String imgUrlCompressed;

    private int totalDuration = 0;//总时长
    private String shareUrl = "";

    private int hasFavorite = 0;//是否收藏，0-未收藏，1-已收藏
    private int hasBuy = 0;// 是否已购买0-未购买，1-已购买

    private int playCount = 0;
    private int code = -2;//-2:正在请求详情（没有图文详情和播放地址），-1:只有播放地址，0:请求成功，1：请求失败
    private int index = -1;
    private boolean isPlay = false;
    private boolean isPlaying;
    private String productsTitle;
    private String flowId;

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

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getHasFavorite() {
        return hasFavorite;
    }

    public void setHasFavorite(int hasFavorite) {
        this.hasFavorite = hasFavorite;
    }

    public int getHasBuy() {
        return hasBuy;
    }

    public void setHasBuy(int hasBuy) {
        this.hasBuy = hasBuy;
    }

    public String getBigColumnId() {
        return bigColumnId;
    }

    public void setBigColumnId(String bigColumnId) {
        this.bigColumnId = bigColumnId;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgUrlCompressed() {
        return imgUrlCompressed;
    }

    public void setImgUrlCompressed(String imgUrlCompressed) {
        this.imgUrlCompressed = imgUrlCompressed;
    }

    public String getProductsTitle() {
        return productsTitle;
    }

    public void setProductsTitle(String productsTitle) {
        this.productsTitle = productsTitle;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }
}
