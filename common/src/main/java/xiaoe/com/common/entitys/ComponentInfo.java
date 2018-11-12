package xiaoe.com.common.entitys;

import java.util.List;

/**
 * 组件数据实体类
 */
public class ComponentInfo {

    // 店铺组件类型
    private String type;
    // 组件的子类型，eg: 信息流分为图文、音频、视频
    private String subType;
    // 标题
    private String title;
    // 内容
    private String desc;
    // 价格
    private String price;
    // 参与人数描述，eg: **人在听
    private String joinedDesc;
    // 定制的图片链接
    private String imgUrl;
    // 是否购买
    private boolean hasBuy;
    // 最近更新子列表集合
    private List<RecentUpdateListItem> subList;
    // 轮播图集合
    private List<ShufflingItem> shufflingList;
    // 知识商品分组子列表集合
    private List<KnowledgeCommodityItem> knowledgeCommodityItemList;
    // 信息流分组子列表集合
    private List<FlowInfoItem> flowInfoItemList;
    // 导航子列表集合
    private List<GraphicNavItem> graphicNavItemList;
    // 是否需要渲染
    private boolean needDecorate = true;
    // 是否隐藏 title
    private boolean hideTitle = false;
    // 分组 id
    private String groupId;
    // 专栏 id
    private String columnId;
    // 是否正式用户
    private boolean isFormUser;

    public ComponentInfo() { }

    public void setNeedDecorate(boolean needDecorate) {
        this.needDecorate = needDecorate;
    }

    public boolean isHideTitle() {
        return hideTitle;
    }

    public void setHideTitle(boolean hideTitle) {
        this.hideTitle = hideTitle;
    }

    public boolean isNeedDecorate() {
        return needDecorate;
    }

    public void setGraphicNavItemList(List<GraphicNavItem> graphicNavItemList) {
        this.graphicNavItemList = graphicNavItemList;
    }

    public List<GraphicNavItem> getGraphicNavItemList() {
        return graphicNavItemList;
    }

    public void setFlowInfoItemList(List<FlowInfoItem> flowInfoItemList) {
        this.flowInfoItemList = flowInfoItemList;
    }

    public List<FlowInfoItem> getFlowInfoItemList() {
        return flowInfoItemList;
    }

    public void setKnowledgeCommodityItemList(List<KnowledgeCommodityItem> knowledgeCommodityItemList) {
        this.knowledgeCommodityItemList = knowledgeCommodityItemList;
    }

    public List<KnowledgeCommodityItem> getKnowledgeCommodityItemList() {
        return knowledgeCommodityItemList;
    }

    public void setShufflingList(List<ShufflingItem> shufflingList) {
        this.shufflingList = shufflingList;
    }

    public List<ShufflingItem> getShufflingList() {
        return shufflingList;
    }

    public void setSubList(List<RecentUpdateListItem> subList) {
        this.subList = subList;
    }

    public List<RecentUpdateListItem> getSubList() {
        return subList;
    }

    public void setHasBuy(boolean hasBuy) {
        this.hasBuy = hasBuy;
    }

    public boolean isHasBuy() {
        return hasBuy;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSubType() {
        return subType;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setJoinedDesc(String joinedDesc) {
        this.joinedDesc = joinedDesc;
    }

    public String getJoinedDesc() {
        return joinedDesc;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getColumnId() {

        return columnId;
    }

    public boolean isFormUser() {
        return isFormUser;
    }

    public void setFormUser(boolean formUser) {
        isFormUser = formUser;
    }

    @Override
    public String toString() {
        return "type --- " + getType() + " title --- " + getTitle() + " desc --- " + getDesc() + " price --- " + getPrice();
    }
}
