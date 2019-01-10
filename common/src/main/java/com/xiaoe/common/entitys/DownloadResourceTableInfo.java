package com.xiaoe.common.entitys;

import java.util.List;

public class DownloadResourceTableInfo {
    private String appId;
    private String resourceId;
    private String title;
    private String imgUrl;
    private String desc;
    private int resourceType;//1-音频，2-视频，3-专栏，4-大专栏
    private int depth; // 深度，大专栏-2，小专栏-1，单品-0
    private String updateAt;
    private String createAt;


    private List<DownloadTableInfo> childResourceList;//表中无此字段
    private List<DownloadResourceTableInfo> childList;
    private int state = 0;
    private long totalSize = 0;
    private long progress = 0;
    private String localFilePath;//下载本地文件路径
    private String fileUrl;//网络地址
    private DownloadTableInfo resource;

    // 父级资源 id
    private String parentId = "";
    // 父级资源类型
    private int parentType = 0;
    // 顶级资源 id
    private String topParentId = "";
    // 顶级资源类型
    private int topParentType = 0;

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

    public int getResourceType() {
        return resourceType;
    }

    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public List<DownloadTableInfo> getChildResourceList() {
        return childResourceList;
    }

    public void setChildResourceList(List<DownloadTableInfo> childResourceList) {
        this.childResourceList = childResourceList;
    }

    public DownloadTableInfo getResource() {
        return resource;
    }

    public void setResource(DownloadTableInfo resource) {
        this.resource = resource;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public List<DownloadResourceTableInfo> getChildList() {
        return childList;
    }

    public void setChildList(List<DownloadResourceTableInfo> childList) {
        this.childList = childList;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setParentType(int parentType) {
        this.parentType = parentType;
    }

    public void setTopParentId(String topParentId) {
        this.topParentId = topParentId;
    }

    public void setTopParentType(int topParentType) {
        this.topParentType = topParentType;
    }

    public String getParentId() {

        return parentId;
    }

    public int getParentType() {
        return parentType;
    }

    public String getTopParentId() {
        return topParentId;
    }

    public int getTopParentType() {
        return topParentType;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
}
