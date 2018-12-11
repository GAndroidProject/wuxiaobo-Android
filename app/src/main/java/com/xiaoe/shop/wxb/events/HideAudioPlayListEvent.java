package com.xiaoe.shop.wxb.events;

public class HideAudioPlayListEvent {
    private boolean isHide;

    public boolean isHide() {
        return isHide;
    }

    public HideAudioPlayListEvent(boolean isHide){
        this.isHide = isHide;
    }
}
