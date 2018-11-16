package com.xiaoe.common.entitys;

public class ScholarshipRangeItem {

    private String itemRange;
    private String itemAvatar;
    private String itemName;
    private String itemScholarship;
    private String itemUserId;
    private boolean isSuperVip;

    public void setSuperVip(boolean superVip) {
        isSuperVip = superVip;
    }

    public boolean isSuperVip() {
        return isSuperVip;
    }

    public void setItemRange(String itemRange) {
        this.itemRange = itemRange;
    }

    public void setItemAvatar(String itemAvatar) {
        this.itemAvatar = itemAvatar;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemScholarship(String itemScholarship) {
        this.itemScholarship = itemScholarship;
    }

    public String getItemRange() {
        return itemRange;
    }

    public String getItemAvatar() {
        return itemAvatar;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemScholarship() {
        return itemScholarship;
    }

    public String getItemUserId() {
        return itemUserId;
    }

    public void setItemUserId(String itemUserId) {
        this.itemUserId = itemUserId;
    }
}
