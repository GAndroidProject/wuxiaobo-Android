package com.xiaoe.common.entitys;

// 更换登录身份事件
public class ChangeLoginIdentityEvent {

    private boolean changeSuccess;

    public ChangeLoginIdentityEvent() {

    }

    public void setChangeSuccess(boolean changeSuccess) {
        this.changeSuccess = changeSuccess;
    }

    public boolean isChangeSuccess() {

        return changeSuccess;
    }
}
