package com.xiaoe.common.entitys;

import java.util.List;

/**
 * Created by Administrator on 2018/1/3.
 */

public class DownloadInfo {
    private static final String TAG = "DownloadInfo";
    private String resourceId;//资源id
    private String columnResourceId;//如果有所属专栏，则是所属专栏id，否则为空
    private String columnImageUrl;//如果有所属专栏，则是所属专栏海报地址，否则为空
    private String localColumnImageUrl;//如果有所属专栏，则是保存在本地的所属专栏海报地址，否则为空
    private String resourceImageUrl;//资源海报地址
    private String localResourceImageUrl;//保存在本地资源海报地址
    private String title;//资源标题
    private String columnTitle;//如果有所属专栏，则是所属专题标题，否则为空
    private String resourceDownloadPath;//资源下载地址
    private String localFilePath;//保存在本地下载资源路径
    private long fileSize;//文件大小
    private float progress;//已经下载的进度(0-1；0代表已下载0%，1代表已下载100%)
    private boolean downloadFinish = false;//是否下载完成
    private int downloadState = -1;//0代表正在下载，1代表暂停，2代表等待下载（未开始），3代表下载完成
    private List<DownloadInfo> mClassList;//课程列表，如果是专栏，否则无效
    private boolean isSelect = false;//是否选择状态
    private int playCount;
    private int commentCount;
    private int resourceType;//2代表音频，3代表视频
    private String StartAt;
    private long addTime;
    private long addColumnTime;
    private String resourceDetails;


    public String getResourceId() {
        return resourceId == null ? "" : resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getColumnResourceId() {
        return columnResourceId == null ? "" : columnResourceId;
    }

    public void setColumnResourceId(String columnResourceId) {
        this.columnResourceId = columnResourceId;
    }

    public String getColumnImageUrl() {
        return columnImageUrl == null ? "" : columnImageUrl;
    }

    public void setColumnImageUrl(String columnImageUrl) {
        this.columnImageUrl = columnImageUrl;
    }

    public String getLocalColumnImageUrl() {
        return localColumnImageUrl == null ? "" : localColumnImageUrl;
    }

    public void setLocalColumnImageUrl(String localColumnImageUrl) {
        this.localColumnImageUrl = localColumnImageUrl;
    }

    public String getResourceImageUrl() {
        return resourceImageUrl == null ? "" : resourceImageUrl;
    }

    public void setResourceImageUrl(String resourceImageUrl) {
        this.resourceImageUrl = resourceImageUrl;
    }

    public String getLocalResourceImageUrl() {
        return localResourceImageUrl == null ? "" : localResourceImageUrl;
    }

    public void setLocalResourceImageUrl(String localResourceImageUrl) {
        this.localResourceImageUrl = localResourceImageUrl;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColumnTitle() {
        return columnTitle == null ? "" : columnTitle;
    }

    public void setColumnTitle(String columnTitle) {
        this.columnTitle = columnTitle;
    }

    public String getResourceDownloadPath() {
        return resourceDownloadPath == null ? "" : resourceDownloadPath;
    }

    public void setResourceDownloadPath(String resourceDownloadPath) {
        this.resourceDownloadPath = resourceDownloadPath;
    }

    public String getLocalFilePath() {
        return localFilePath == null ? "" : localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public boolean isDownloadFinish() {
        return downloadFinish;
    }

    public void setDownloadFinish(boolean downloadFinish) {
        this.downloadFinish = downloadFinish;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
    }

    public List<DownloadInfo> getClassList() {
        return mClassList;
    }

    public void setClassList(List<DownloadInfo> mClassList) {
        this.mClassList = mClassList;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getResourceType() {
        return resourceType;
    }

    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
    }

    public String getStartAt() {
        return StartAt == null ? "" : StartAt;
    }

    public void setStartAt(String startAt) {
        StartAt = startAt;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public long getAddColumnTime() {
        return addColumnTime;
    }

    public void setAddColumnTime(long addColumnTime) {
        this.addColumnTime = addColumnTime;
    }

    public String getResourceDetails() {
        return resourceDetails;
    }

    public void setResourceDetails(String resourceDetails) {
        this.resourceDetails = resourceDetails;
    }

    /**
     * 获取保存文件下载进度json格式
     * @return
     */
    public String getSaveProgerssConfigJson(){
        String json = "{\"resourceId\":\""+getResourceId()+"\",\"resourceDownloadPath\":\""+getResourceDownloadPath()+"\","
                +"\"progress\":"+getProgress()+",\"fileSize\":"+getFileSize()+"}";
        return json;
    }
    /**
     * 获取保存下载文件json格式
     * @return
     */
    public String getDownloadFileJson(){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("{")
                .append("\"resourceId\":\""+getResourceId()+"\",")
                .append("\"columnResourceId\":\""+getColumnResourceId()+"\",")
                .append("\"columnImageUrl\":\""+getColumnImageUrl()+"\",")
                .append("\"localColumnImageUrl\":\""+getLocalColumnImageUrl()+"\",")
                .append("\"resourceImageUrl\":\""+getResourceImageUrl()+"\",")
                .append("\"localResourceImageUrl\":\""+getLocalResourceImageUrl()+"\",")
                .append("\"title\":\""+getTitle()+"\",")
                .append("\"columnTitle\":\""+getColumnTitle()+"\",")
                .append("\"resourceDownloadPath\":\""+getResourceDownloadPath()+"\",")
                .append("\"localFilePath\":\""+getLocalFilePath()+"\",")
                .append("\"fileSize\":"+getFileSize()+",")
                .append("\"progress\":"+getProgress()+",")
                .append("\"downloadFinish\":"+isDownloadFinish()+",")
                .append("\"downloadState\":"+getDownloadState()+",")
                .append("\"isSelect\":"+isSelect()+",")
                .append("\"playCount\":"+getPlayCount()+",")
                .append("\"commentCount\":"+getCommentCount()+",")
                .append("\"resourceType\":"+getResourceType()+",")
                .append("\"StartAt\":\""+getStartAt()+"\",")
                .append("\"addTime\":"+getAddTime()+",")
                .append("\"addColumnTime\":"+getAddColumnTime())
                .append("}");
        String json = stringBuffer.toString();
        return json;
    }
}
