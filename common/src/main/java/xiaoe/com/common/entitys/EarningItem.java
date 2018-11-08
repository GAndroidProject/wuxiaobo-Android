package xiaoe.com.common.entitys;

// 赚取列表 item
public class EarningItem {

    private String itemTitle;
    private String itemContent;
    private String itemMoney;

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public void setItemContent(String itemContent) {
        this.itemContent = itemContent;
    }

    public void setItemMoney(String itemMoney) {
        this.itemMoney = itemMoney;
    }

    public String getItemTitle() {

        return itemTitle;
    }

    public String getItemContent() {
        return itemContent;
    }

    public String getItemMoney() {
        return itemMoney;
    }
}
