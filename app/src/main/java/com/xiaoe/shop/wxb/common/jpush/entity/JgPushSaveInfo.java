package com.xiaoe.shop.wxb.common.jpush.entity;

/**
 * @author flynnWang
 * @date 2018/11/15
 * <p>
 * 描述：
 */
public class JgPushSaveInfo {

    private boolean formalUser;
    private boolean bindRegIdSuccess;
    private String registrationID;

    public boolean isFormalUser() {
        return formalUser;
    }

    public void setFormalUser(boolean formalUser) {
        this.formalUser = formalUser;
    }

    public boolean isBindRegIdSuccess() {
        return bindRegIdSuccess;
    }

    public void setBindRegIdSuccess(boolean bindRegIdSuccess) {
        this.bindRegIdSuccess = bindRegIdSuccess;
    }

    public String getRegistrationID() {
        return registrationID;
    }

    public void setRegistrationID(String registrationID) {
        this.registrationID = registrationID;
    }

    @Override
    public String toString() {
        return "JgPushSaveInfo{" +
                "formalUser=" + formalUser +
                ", bindRegIdSuccess=" + bindRegIdSuccess +
                ", registrationID='" + registrationID + '\'' +
                '}';
    }
}
