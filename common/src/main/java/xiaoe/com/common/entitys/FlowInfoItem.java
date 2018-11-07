package xiaoe.com.common.entitys;

public class FlowInfoItem {

    // item 类型
    private String itemType;
    // item id（信息流的 id 跳转详情页会做转换）
    private String itemId;
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
}
