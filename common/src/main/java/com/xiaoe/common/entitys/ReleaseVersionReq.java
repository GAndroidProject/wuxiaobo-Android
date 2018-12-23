package com.xiaoe.common.entitys;

import java.util.List;

public class ReleaseVersionReq {

    /**
     * 店铺 id
     */
    private String app_id;
    private String shop_id;
    /**
     * 版本名称
     */
    private String version;
    /**
     * 1-安卓，2-ios
     */
    private String type;
    /**
     * 版本变更信息
     */
    private List<String> msg;
    /**
     * 新版本下载地址
     */
    private String download_url;
    /**
     * 0-弹窗提示，1-强制更新，2-红点提示更新
     */
    private int update_mode;
    /**
     * 审核版本号，如果当前没有审核版本号的话，传空””
     */
    private String audit_version;

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getMsg() {
        return msg;
    }

    public void setMsg(List<String> msg) {
        this.msg = msg;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public int getUpdate_mode() {
        return update_mode;
    }

    public void setUpdate_mode(int update_mode) {
        this.update_mode = update_mode;
    }

    public String getAudit_version() {
        return audit_version;
    }

    public void setAudit_version(String audit_version) {
        this.audit_version = audit_version;
    }
}
