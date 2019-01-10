package com.xiaoe.common.entitys;

// 更换登录身份事件
public class ChangeLoginIdentityEvent {

    private boolean changeSuccess;
    private boolean hasBalanceChange;

    public ChangeLoginIdentityEvent() {

    }

    public void setChangeSuccess(boolean changeSuccess) {
        this.changeSuccess = changeSuccess;
    }

    public boolean isChangeSuccess() {
        return changeSuccess;
    }

    public boolean isHasBalanceChange() {
        return hasBalanceChange;
    }

    public void setHasBalanceChange(boolean hasBalanceChange) {
        this.hasBalanceChange = hasBalanceChange;
    }
}
