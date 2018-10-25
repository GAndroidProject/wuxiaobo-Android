package xiaoe.com.common.entitys;

public class GraphicNavItem {

    // 导航图标
    private String navIcon;
    // 导航文案
    private String navContent;
    // 跳转的资源类型
    private String navResourceType;
    // 跳转的资源 id
    private String navResourceId;

    public GraphicNavItem() { }

    public void setNavIcon(String navIcon) {
        this.navIcon = navIcon;
    }

    public void setNavContent(String navContent) {
        this.navContent = navContent;
    }

    public String getNavIcon() {
        return navIcon;
    }

    public String getNavContent() {
        return navContent;
    }

    public String getNavResourceType() {
        return navResourceType;
    }

    public String getNavResourceId() {
        return navResourceId;
    }

    public void setNavResourceType(String navResourceType) {
        this.navResourceType = navResourceType;
    }

    public void setNavResourceId(String navResourceId) {
        this.navResourceId = navResourceId;
    }
}
