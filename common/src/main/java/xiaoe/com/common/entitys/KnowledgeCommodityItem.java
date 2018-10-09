package xiaoe.com.common.entitys;

public class KnowledgeCommodityItem {

    // 图片
    private String itemImg;
    // 标题
    private String itemTitle;
    // 价格
    private String itemPrice;
    // 描述
    private String itemDesc;
    // 是否购买
    private boolean hasBuy;

    public KnowledgeCommodityItem() {}

    public boolean isHasBuy() {
        return hasBuy;
    }

    public void setHasBuy(boolean hasBuy) {
        this.hasBuy = hasBuy;
    }

    public void setItemImg(String itemImg) {
        this.itemImg = itemImg;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getItemImg() {
        return itemImg;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public String getItemDesc() {
        return itemDesc;
    }
}
