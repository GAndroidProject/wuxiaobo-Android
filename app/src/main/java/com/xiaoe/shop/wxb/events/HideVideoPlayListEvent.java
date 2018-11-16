package com.xiaoe.shop.wxb.events;

public class HideVideoPlayListEvent {
    private boolean isHide;

    public boolean isHide() {
        return isHide;
    }

    public HideVideoPlayListEvent(boolean isHide){
        this.isHide = isHide;
    }
}
