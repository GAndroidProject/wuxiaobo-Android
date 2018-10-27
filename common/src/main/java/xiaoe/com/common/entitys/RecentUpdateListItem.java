package xiaoe.com.common.entitys;

/**
 * 最近更新列表项
 */
public class RecentUpdateListItem {

    // item 标题
    private String listTitle;
    // item 图标
    private String listPlayState;
    // item 资源 id
    private String listResourceId;

    public RecentUpdateListItem() {

    }

    public RecentUpdateListItem(String listTitle, String listPlayState) {

        this.listTitle = listTitle;
        this.listPlayState = listPlayState;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }

    public void setListPlayState(String listPlayState) {
        this.listPlayState = listPlayState;
    }

    public String getListTitle() {
        return listTitle;
    }

    public String getListPlayState() {
        return listPlayState;
    }

    public void setListResourceId(String listResourceId) {
        this.listResourceId = listResourceId;
    }

    public String getListResourceId() {
        return listResourceId;
    }
}
