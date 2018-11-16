package com.xiaoe.common.entitys;

public class TestInfo {

    /**
     * app_id : 123456
     * entity : 哈哈哈
     */

    private String app_id;
    private String entity;

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    @Override
    public String toString() {
        return "\r\n app_id: " + getApp_id() + "\r\n entity: " + getEntity();
    }
}
