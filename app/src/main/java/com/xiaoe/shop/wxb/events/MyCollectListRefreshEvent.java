package com.xiaoe.shop.wxb.events;

public class MyCollectListRefreshEvent {
    private boolean isRefresh;

    private String resId;

    public boolean isRefresh() {
        return isRefresh;
    }

    public String getResId() {
        return resId;
    }

    public MyCollectListRefreshEvent(boolean isRefresh, String resId){
        this.isRefresh = isRefresh;
        this.resId = resId;
    }
}
