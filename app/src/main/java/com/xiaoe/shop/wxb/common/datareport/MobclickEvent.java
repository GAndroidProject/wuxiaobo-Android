package com.xiaoe.shop.wxb.common.datareport;

/*
    "注明：PV：点击数量    UV：点击独立用户数量"

          埋点需求点	                            友盟事件ID/页面名称	                自定义属性	                    事件类型	    生效版本号   备注

     1    一级菜单-今日点击监控	                tabbar-today-click	                无	                            计数事件	    V1.0
     2    一级菜单-课程点击监控	                tabbar-course-click	                无	                            计数事件    	V1.0
     3    一级菜单-奖学金点击监控	                tabbar-scholarship-click	        无	                            计数事件	    V1.0
     4    一级菜单-我的点击监控	                tabbar-mine-click	                无	                            计数事件	    V1.0

     5    今日-已购课程按钮	                    today-purchased-btn-click	        无	                            计数事件    	V1.0
     6    今日-信息流每个item的点击	            today-list-item-click	            "index：对应展示位置的索引值"	    计数事件	    V1.0

     7    课程-搜索按钮点击	                    course-search-btn-click	            无	                            计数事件	    V1.0
     8    课程-banner点击	                    course-banner-click	                "index：对应展示位置的索引值"	    计数事件    	V1.0
     9    课程-财富点击	                        course-menu-wealth-click	        无	                            计数事件    	V1.0
     10   课程-职场点击	                        course-menu-job-click	            无	                            计数事件    	V1.0
     11   课程-见识点击	                        course-menu-knowledge-click	        无	                            计数事件    	V1.0
     12   课程-家庭点击	                        course-menu-family-click	        无	                            计数事件    	V1.0
     13   课程-每天听见吴晓波的收听全部点击	    course-wxb-everyday-all-click	    无	                            计数事件    	V1.0
     14   课程-每天听见吴晓波-节目1播放点击	    course-wxb-everyday-item1-click	    无	                            计数事件	    V1.0
     15   课程-每天听见吴晓波-节目2播放点击	    course-wxb-everyday-item2-click	    无	                            计数事件	    V1.0
     16   课程-每天听见吴晓波-节目3播放点击	    course-wxb-everyday-item3-click	    无	                            计数事件    	V1.0

     17   奖学金-去瓜分点击	                    scholarship-carveup-btn-click	    无	                            计数事件	    V1.0
     18   奖学金-去选购课程弹窗-去选购课程点击	    scholarship-choosebox-ok-click	    无	                            计数事件	    V1.0
     19   奖学金-去选购课程弹窗-关闭按钮点击	    scholarship-choosebox-close-click	无	                            计数事件	    V1.0
     20   奖学金-进入分享音频列表页次数统计	    scholarship-shareviocelist-click	无	                            计数事件	    V1.0
     21   奖学金-成功获得奖学金次数统计	        scholarship-getburse-success-count	无	                            计数事件	    V1.0
     22   奖学金-成功获得积分次数统计	            scholarship-getgrade-success-count	无	                            计数事件    	V1.0

     23   我的-已购课程点击	                    mine-purchased-btn-click	        无	                            计数事件	    V1.0
     24   我的-我的收藏点击	                    mine-fav-btn-click	                无	                            计数事件	    V1.0
     25   我的-下载列表点击	                    mine-downloaded-btn-click	        无	                            计数事件    	V1.0
     26   我的-优惠券点击	                    mine-discounts-btn-click	        无	                            计数事件    	V1.0
     27   我的-兑换码点击	                    mine-redeem-btn-click	            无	                            计数事件	    V1.0
     28   我的-开通超级会员按钮             	    mine-openmembership-btn-click	    无	                            计数事件    	V1.0
     29   我的-浏览开通超级会员页面	            mine-supermemberview-view-count	    无	                            计数事件	    V1.0
     30   我的-开通超级会员页面支付按钮点击	    mine-supermemberview-paybtn-click	无	                            计数事件	    V1.0

     31   悬浮播放条关闭按钮点击	                voiceplayer-closebtn-click	        无	                            计数事件	    V1.0

     32   一级菜单-今日-浏览时长统计	            today-pageview-duration	            无	                            计算事件	    V1.0
     33   一级菜单-课程-浏览时长统计	            course-pageview-duration	        无	                            计算事件	    V1.0
     34   一级菜单-奖学金-浏览时长统计	        scholarship-pageview-duration	    无	                            计算事件    	V1.0
     35   一级菜单-我的-浏览时长统计	            mine-pageview-duration	            无	                            计算事件    	V1.0
    */

/**
 * @author flynnWang
 * @date 2018/12/12
 * <p>
 * 描述：友盟统计（埋点）事件名称
 */
public class MobclickEvent {

    /**
     * 自定义属性（item展示位置的索引值）
     */
    public static final String INDEX = "index";
    /**
     * 自定义属性（item展示的标题）
     */
    public static final String TITLE = "title";

    /**
     * 一级菜单-今日点击监控
     */
    public static final String TABBAR_TODAY_CLICK = "tabbar-today-click";
    /**
     * 一级菜单-课程点击监控
     */
    public static final String TABBAR_COURSE_CLICK = "tabbar-course-click";
    /**
     * 一级菜单-奖学金点击监控
     */
    public static final String TABBAR_SCHOLARSHIP_CLICK = "tabbar-scholarship-click";
    /**
     * 一级菜单-我的点击监控
     */
    public static final String TABBAR_MINE_CLICK = "tabbar-mine-click";

    /**
     * 今日-已购课程按钮
     */
    public static final String TODAY_PURCHASED_BTN_CLICK = "today-purchased-btn-click";
    /**
     * 今日-信息流每个item的点击
     */
    public static final String TODAY_LIST_ITEM_CLICK = "today-list-item-click";

    /**
     * 课程-搜索按钮点击
     */
    public static final String COURSE_SEARCH_BTN_CLICK = "course-search-btn-click";
    /**
     * 课程-banner点击
     */
    public static final String COURSE_BANNER_CLICK = "course-banner-click";
    /**
     * 课程-导航栏点击（财富、职场、见识、家庭）
     */
    public static final String COURSE_MENU_CLICK = "course-menu-click";
    /**
     * 课程-财富点击
     */
    @Deprecated
    public static final String COURSE_MENU_WEALTH_CLICK = "course-menu-wealth-click";
    /**
     * 课程-职场点击
     */
    @Deprecated
    public static final String COURSE_MENU_JOB_CLICK = "course-menu-job-click";
    /**
     * 课程-见识点击
     */
    @Deprecated
    public static final String COURSE_MENU_KNOWLEDGE_CLICK = "course-menu-knowledge-click";
    /**
     * 课程-家庭点击
     */
    @Deprecated
    public static final String COURSE_MENU_FAMILY_CLICK = "course-menu-family-click";
    /**
     * 课程-每天听见吴晓波的收听全部点击
     */
    public static final String COURSE_WXB_EVERYDAY_ALL_CLICK = "course-wxb-everyday-all-click";
    /**
     * 课程-每天听见吴晓波-节目播放点击
     */
    public static final String COURSE_WXB_EVERYDAY_ITEM_CLICK = "course-wxb-everyday-item-click";
    /**
     * 课程-每天听见吴晓波-节目1播放点击
     */
    @Deprecated
    public static final String COURSE_WXB_EVERYDAY_ITEM1_CLICK = "course-wxb-everyday-item1-click";
    /**
     * 课程-每天听见吴晓波-节目2播放点击
     */
    @Deprecated
    public static final String COURSE_WXB_EVERYDAY_ITEM2_CLICK = "course-wxb-everyday-item2-click";
    /**
     * 课程_每天听见吴晓波_节目3播放点击
     */
    @Deprecated
    public static final String COURSE_WXB_EVERYDAY_ITEM3_CLICK = "course-wxb-everyday-item3-click";

    /**
     * 奖学金-去瓜分点击
     */
    public static final String SCHOLARSHIP_CARVEUP_BTN_CLICK = "scholarship-carveup-btn-click";
    /**
     * 奖学金-去选购课程弹窗-去选购课程点击
     */
    public static final String SCHOLARSHIP_CHOOSEBOX_OK_CLICK = "scholarship-choosebox-ok-click";
    /**
     * 奖学金-去选购课程弹窗-关闭按钮点击
     */
    public static final String SCHOLARSHIP_CHOOSEBOX_CLOSE_CLICK = "scholarship-choosebox-close-click";
    /**
     * 奖学金-进入分享音频列表页次数统计
     */
    public static final String SCHOLARSHIP_SHAREVIOCELIST_CLICK = "scholarship-shareviocelist-click";
    /**
     * 奖学金-成功获得奖学金次数统计
     */
    public static final String SCHOLARSHIP_GETBURSE_SUCCESS_COUNT = "scholarship-getburse-success-count";
    /**
     * 奖学金-成功获得积分次数统计
     */
    public static final String SCHOLARSHIP_GETGRADE_SUCCESS_COUNT = "scholarship-getgrade-success-count";

    /**
     * 我的-已购课程-封面课程点击
     */
    public static final String MINE_PURCHASED_COURSE_CLICK = "mine-purchased-course-click";
    /**
     * 我的-已购课程-查看全部点击
     */
    public static final String MINE_PURCHASED_ALL_CLICK = "mine-purchased-all-click";
    /**
     * 我的-我的收藏点击
     */
    public static final String MINE_FAV_BTN_CLICK = "mine-fav-btn-click";
    /**
     * 我的-下载列表点击
     */
    public static final String MINE_DOWNLOADED_BTN_CLICK = "mine-downloaded-btn-click";
    /**
     * 我的-优惠券点击
     */
    public static final String MINE_DISCOUNTS_BTN_CLICK = "mine-discounts-btn-click";
    /**
     * 我的-兑换码点击
     */
    public static final String MINE_REDEEM_BTN_CLICK = "mine-redeem-btn-click";
    /**
     * 我的-开通超级会员按钮
     */
    public static final String MINE_OPENMEMBERSHIP_BTN_CLICK = "mine-openmembership-btn-click";
    /**
     * 我的-浏览开通超级会员页面
     */
    public static final String MINE_SUPERMEMBERVIEW_VIEW_COUNT = "mine-supermemberview-view-count";
    /**
     * 我的-开通超级会员页面支付按钮点击
     */
    public static final String MINE_SUPERMEMBERVIEW_PAYBTN_CLICK = "mine-supermemberview-paybtn-click";

    /**
     * 悬浮播放条关闭按钮点击
     */
    public static final String VOICEPLAYER_CLOSEBTN_CLICK = "voiceplayer-closebtn-click";

    /**
     * 一级菜单-今日-浏览时长统计
     */
    public static final String TODAY_PAGEVIEW_DURATION = "today-pageview-duration";
    /**
     * 一级菜单-课程-浏览时长统计
     */
    public static final String COURSE_PAGEVIEW_DURATION = "course-pageview-duration";
    /**
     * 一级菜单-奖学金-浏览时长统计
     */
    public static final String SCHOLARSHIP_PAGEVIEW_DURATION = "scholarship-pageview-duration";
    /**
     * 一级菜单-我的-浏览时长统计
     */
    public static final String MINE_PAGEVIEW_DURATION = "mine-pageview-duration";

}
