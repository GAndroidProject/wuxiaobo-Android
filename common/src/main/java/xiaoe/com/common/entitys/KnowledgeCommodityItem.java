package xiaoe.com.common.entitys;

public class KnowledgeCommodityItem {

    // 图片
    private String itemImg;
    // 标题
    private String itemTitle;
    // 标题专栏描述
    private String itemTitleColumn;
    // 价格
    private String itemPrice;
    // 描述
    private String itemDesc;
    // 是否购买
    private boolean hasBuy;
    // 子类型（图文、音频、视频、专栏）
    private String srcType;

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

    public void setItemTitleColumn(String itemTitleColumn) {
        this.itemTitleColumn = itemTitleColumn;
    }

    public String getItemTitleColumn() {

        return itemTitleColumn;
    }

    public void setSrcType(String srcType) {
        this.srcType = srcType;
    }

    public String getSrcType() {

        return srcType;
    }
}
