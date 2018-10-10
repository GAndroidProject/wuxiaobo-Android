package xiaoe.com.common.entitys;

/**
 * 店铺装修组件类型
 */
public class DecorateEntityType {

    public static final int FLOW_INFO = 0; // 信息流组件
    public static final String FLOW_INFO_STR = "flow_info";
    public static final int RECENT_UPDATE = 1; // 最近更新组件
    public static final String RECENT_UPDATE_STR = "recent_update";
    public static final int KNOWLEDGE_COMMODITY = 2; // 知识商品组件
    public static final String KNOWLEDGE_COMMODITY_STR = "knowledge_commodity";
    public static final int SHUFFLING_FIGURE = 3; // 图片广告组件（轮播图）
    public static final String SHUFFLING_FIGURE_STR = "shuffling_figure";
    public static final int BOOKCASE = 4; // 书架组件
    public static final String BOOKCASE_STR = "bookcase";
    public static final int GRAPHIC_NAVIGATION = 5; // 图文导航组件
    public static final String GRAPHIC_NAVIGATION_STR = "graphic_navigation";
    public static final int SEARCH = 6; // 搜索组件
    public static final String SEARCH_STR = "search";

    // 信息流
    public static final int FLOW_INFO_IMG_TEXT = 0;
    public static final String FLOW_INFO_IMG_TEXT_STR = "imgText"; // 图文子类型
    public static final int FLOW_INFO_AUDIO = 1;
    public static final String FLOW_INFO_AUDIO_STR = "audio"; // 音频子类型
    public static final int FLOW_INFO_VIDEO = 2;
    public static final String FLOW_INFO_VIDEO_STR = "video"; // 视频子类型

    // 知识商品
    public static final String KNOWLEDGE_LIST = "knowledgeList"; // 列表类型
    public static final String KNOWLEDGE_GROUP = "knowledgeGroup"; // 分组类型
}
