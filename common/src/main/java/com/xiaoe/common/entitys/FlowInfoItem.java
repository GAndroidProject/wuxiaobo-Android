package com.xiaoe.common.entitys;

import java.util.List;

public class FlowInfoItem {

    // item 类型
    private String itemType;
    // item id（信息流的 id 跳转详情页会做转换）
    private String itemId;
    // item 标签
    private String itemTag;
    // item 标题
    private String itemTitle;
    // item 描述
    private String itemDesc;
    // item 是否购买
    private boolean itemHasBuy;
    // item 图片
    private String itemImg;
    // item 参与描述（音频的）
    private String itemJoinedDesc;
    // item 价格
    private String itemPrice;
    // item 标签集合
    private List<LabelItemEntity> labelList;
    // item 划线价
    private String linePrice;
    // item 是否免费
    private boolean itemIsFree;

    public FlowInfoItem() { }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public void setItemHasBuy(boolean itemHasBuy) {
        this.itemHasBuy = itemHasBuy;
    }

    public void setItemImg(String itemImg) {
        this.itemImg = itemImg;
    }

    public void setItemJoinedDesc(String itemJoinedDesc) {
        this.itemJoinedDesc = itemJoinedDesc;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemType() {
        return itemType;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public boolean isItemHasBuy() {
        return itemHasBuy;
    }

    public String getItemImg() {
        return itemImg;
    }

    public String getItemJoinedDesc() {
        return itemJoinedDesc;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemTag() {
        return itemTag;
    }

    public void setItemTag(String itemTag) {
        this.itemTag = itemTag;
    }

    public List<LabelItemEntity> getLabelList() {
        return labelList;
    }

    public void setLabelList(List<LabelItemEntity> labelList) {
        this.labelList = labelList;
    }

    public String getLinePrice() {
        return linePrice;
    }

    public void setLinePrice(String linePrice) {
        this.linePrice = linePrice;
    }

    public boolean getItemIsFree() {
        return itemIsFree;
    }

    public void setItemIsFree(boolean itemIsFree) {
        this.itemIsFree = itemIsFree;
    }
}
