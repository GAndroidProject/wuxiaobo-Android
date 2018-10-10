package xiaoe.com.common.entitys;

public class GraphicNavItem {

    // 导航图标
    private String navIcon;
    // 导航文案
    private String navContent;

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
}
