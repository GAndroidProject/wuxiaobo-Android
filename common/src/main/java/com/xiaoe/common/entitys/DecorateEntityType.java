package com.xiaoe.common.entitys;

/**
 * 店铺装修组件类型
 */
public class DecorateEntityType {

    public static final int FLOW_INFO = 0; // 信息流组件
    public static final String FLOW_INFO_STR = "information_flow";

    public static final String GOODS_STR = "goods"; // 商品的总类型
    // 频道和知识商品的父类型 goods
    public static final int RECENT_UPDATE = 1; // 最近更新组件
    public static final String RECENT_UPDATE_STR = "频道"; // type 是 goods，type_title 是频道 recent_update goods

    public static final int KNOWLEDGE_COMMODITY = 2; // 知识商品组件
    public static final String KNOWLEDGE_COMMODITY_STR = "知识商品"; // type 是 goods，type_title 是知识商品 knowledge_commodity

    public static final int SHUFFLING_FIGURE = 3; // 图片广告组件（轮播图）
    public static final String SHUFFLING_FIGURE_STR = "image_ad"; // shuffling_figure 图片广告

    public static final int BOOKCASE = 4; // 书架组件
    public static final String BOOKCASE_STR = "bookcase";

    public static final int GRAPHIC_NAVIGATION = 5; // 图文导航组件
    public static final String GRAPHIC_NAVIGATION_STR = "category_nav"; // graphic_navigation 图文导航

    public static final int SEARCH = 6; // 搜索组件
    public static final String SEARCH_STR = "search_bar"; // 课程搜索

    // 信息流
    public static final int FLOW_INFO_IMG_TEXT = 0;
    public static final String FLOW_INFO_IMG_TEXT_STR = "image_text"; // 图文子类型
    public static final int FLOW_INFO_AUDIO = 1;
    public static final String FLOW_INFO_AUDIO_STR = "audio"; // 音频子类型
    public static final int FLOW_INFO_VIDEO = 2;
    public static final String FLOW_INFO_VIDEO_STR = "video"; // 视频子类型

    // 知识商品
    public static final int KNOWLEDGE_LIST = 1001;
    public static final String KNOWLEDGE_LIST_STR = "knowledgeList"; // 列表类型
    public static final int KNOWLEDGE_GROUP = 1002;
    public static final String KNOWLEDGE_GROUP_STR = "knowledgeGroup"; // 分组类型

    // 商品子类型
    public static final String IMAGE_TEXT = "image_text"; // 图文
    public static final String AUDIO = "audio"; // 音频
    public static final String VIDEO = "video"; // 视频
    public static final String COLUMN = "column"; // 专栏
    public static final String TOPIC = "topic"; // 大专栏
    public static final String MEMBER = "member"; // 会员

    public static final String SUPER_VIP = "super_vip"; // 超级会员

    // 信息流中专栏和大专栏的数字
    public static final int FLOW_INFO_COLUMN = 3;
    public static final int FLOW_INFO_TOPIC = 4;

    // 商品分组类型
    public static final String RESOURCE_TAG = "resource_tag";

    // 频道组件播放、暂定 flag
    public static final String ITEM_RECENT_PLAY = "play";
    public static final String ITEM_RECENT_STOP = "stop";
}
