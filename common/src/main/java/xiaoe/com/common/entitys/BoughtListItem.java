package xiaoe.com.common.entitys;

public class BoughtListItem {

    // 资源 id
    private String itemResourceId;
    // 资源类型
    private String itemResourceType;
    private String itemIcon;
    private String itemTitle;
    private String itemShareLink; // 分享链接

    public String getItemResourceId() {
        return itemResourceId;
    }

    public String getItemResourceType() {
        return itemResourceType;
    }

    public String getItemIcon() {
        return itemIcon;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemResourceId(String itemResourceId) {
        this.itemResourceId = itemResourceId;
    }

    public void setItemResourceType(String itemResourceType) {
        this.itemResourceType = itemResourceType;
    }

    public void setItemIcon(String itemIcon) {
        this.itemIcon = itemIcon;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getItemShareLink() {
        return itemShareLink;
    }

    public void setItemShareLink(String itemShareLink) {
        this.itemShareLink = itemShareLink;
    }
}
