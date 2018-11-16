package com.xiaoe.shop.wxb.business.upgrade;

/**
 * Created by Haley.Yang on 2018/10/15.
 * <p>
 * 描述：
 */
public class UpgradeProgressUpdateEvent {

    public int state;
    public int progress;

    public UpgradeProgressUpdateEvent(int state,int progress){
        this.state = state;
        this.progress = progress;
    }
    public UpgradeProgressUpdateEvent(int state){
        this.state = state;
    }
}
