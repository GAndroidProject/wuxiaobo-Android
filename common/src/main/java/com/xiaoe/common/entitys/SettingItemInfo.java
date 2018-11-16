package com.xiaoe.common.entitys;

public class SettingItemInfo {

    private String itemTitle;
    private String itemContent;
    private String itemIcon;

    public SettingItemInfo() { }

    public SettingItemInfo(String itemTitle, String itemIcon, String itemContent) {
        this.itemTitle = itemTitle;
        this.itemIcon = itemIcon;
        this.itemContent = itemContent;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemContent() {
        return itemContent;
    }

    public String getItemIcon() {
        return itemIcon;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public void setItemContent(String itemContent) {
        this.itemContent = itemContent;
    }

    public void setItemIcon(String itemIcon) {
        this.itemIcon = itemIcon;
    }
}
