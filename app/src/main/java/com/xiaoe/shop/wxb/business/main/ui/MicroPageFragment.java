package com.xiaoe.shop.wxb.business.main.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.CacheData;
import com.xiaoe.common.entitys.ChangeLoginIdentityEvent;
import com.xiaoe.common.entitys.ComponentInfo;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.FlowInfoItem;
import com.xiaoe.common.entitys.GraphicNavItem;
import com.xiaoe.common.entitys.KnowledgeCommodityItem;
import com.xiaoe.common.entitys.LabelItemEntity;
import com.xiaoe.common.entitys.RecentUpdateListItem;
import com.xiaoe.common.entitys.ShufflingItem;
import com.xiaoe.common.utils.CacheDataUtil;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.widget.CommonRefreshHeader;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.ColumnListRequst;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.PageFragmentRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.decorate.DecorateRecyclerAdapter;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.business.column.presenter.ColumnPresenter;
import com.xiaoe.shop.wxb.business.main.presenter.PageFragmentPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.common.datareport.EventReportManager;
import com.xiaoe.shop.wxb.common.datareport.MobclickEvent;
import com.xiaoe.shop.wxb.events.AudioPlayEvent;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.widget.StatusPagerView;
import com.youth.banner.Banner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;

public class MicroPageFragment extends BaseFragment implements OnRefreshListener {

    private static final String TAG = "MicroPageFragment";

    protected static final int NETWORK_DECORATE = 2001;
    protected static final int CACHE_DECORATE = 2002;

    @BindView(R.id.status_bar_blank)
    TextView mStatusBarBlank;

    private Unbinder unbinder;
    private Context mContext;
    private String microPageId = "";

    @BindView(R.id.micro_page_wrap)
    FrameLayout microPageWrap;
    @BindView(R.id.micro_page_fresh)
    SmartRefreshLayout microPageFresh;

    @BindView(R.id.micro_page_title_bg)
    SimpleDraweeView microPageTitleBg;
//    @BindView(R.id.micro_page_title_bg2)
//    SimpleDraweeView microPageTitleBg2;
    @BindView(R.id.micro_page_content)
    RecyclerView microPageContent;

    @BindView(R.id.micro_page_toolbar)
    FrameLayout microPageToolbar;
    @BindView(R.id.micro_page_toolbar_title)
    TextView microPageToolbarTitle;
    @BindView(R.id.micro_page_toolbar_search)
    ImageView microPageToolbarSearch;
    @BindView(R.id.micro_header)
    CommonRefreshHeader microPageHeader;

    @BindView(R.id.micro_page_loading)
    StatusPagerView microPageLoading;
    // @BindView(R.id.micro_page_logo)
    // ImageView microPageLogo;
//    @BindView(R.id.bottom_wrap)
//    LinearLayout microPageBottom;

    List<ComponentInfo> microPageList;

    int toolbarHeight;
    boolean isMain = true;
    boolean hasSearch = false;
    boolean networkDecorate = false; // 网络渲染
    boolean cacheDecorate = false; // 缓存渲染

    MainActivity mainActivity;
    private String mColumnTitle;
    DecorateRecyclerAdapter microPageAdapter;
    PageFragmentPresenter hp;

    private float alpha;
    public static float maxAlpha;
    boolean obtainDataInCache = false; // 从缓存中获取数据
    SparseArray<Banner> bannerArr;
    int totalScrollY = 0; // 垂直滑动总距离
    /**
     * 页面停留时长（开始时间）
     */
    private long pageDuration;
    /**
     * 在主页面中的索引位置
     */
    private int tabIndex;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            // 获取微页面 id
            microPageId = bundle.getString("microPageId");
            tabIndex = bundle.getInt("tabIndex");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_micro_page, null, false);
        unbinder = ButterKnife.bind(this, view);
        maxAlpha = Dp2Px2SpUtil.dp2px(getActivity(),20);
        mContext = getContext();
        mainActivity = (MainActivity) getActivity();
        int top = 0;
        if (microPageId.equals(MainActivity.MICRO_PAGE_MAIN)) {
            top = StatusBarUtil.getStatusBarHeight(mContext);
        }
        view.setPadding(0, top, 0, 0);
        mStatusBarBlank.setBackgroundColor(Color.argb(255, 255, 255, 255));
        mStatusBarBlank.setHeight(StatusBarUtil.getStatusBarHeight(mContext));
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 设置状态栏颜色
//        if (!isMain) {
//            StatusBarUtil.setStatusBarColor(getActivity(), getResources().getColor(R.color.recent_update_btn_pressed));
//            isMain = true;
//        } else {
//            StatusBarUtil.setStatusBarColor(getActivity(), getResources().getColor(R.color.white));
//            isMain = false;
//        }
        pageDuration = System.currentTimeMillis();
//        Log.e("EventReportManager", "onResume: pageDuration " + pageDuration);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (microPageId.equals(MainActivity.MICRO_PAGE_MAIN)) {
            if (tabIndex == mainActivity.getCurrentPosition()) {
                eventReportDuration();
            }
        } else {
            if (tabIndex == mainActivity.getCurrentPosition()) {
                eventReportDuration();
            }
        }
//        Log.e("EventReportManager", "onPause: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void eventReportDuration() {
        if (microPageId.equals(MainActivity.MICRO_PAGE_MAIN)) {
            EventReportManager.onEventValue(mContext, MobclickEvent.TODAY_PAGEVIEW_DURATION, (int) (System.currentTimeMillis() - pageDuration));
        } else {
            EventReportManager.onEventValue(mContext, MobclickEvent.COURSE_PAGEVIEW_DURATION, (int) (System.currentTimeMillis() - pageDuration));
        }
    }

    @Override
    public void refreshReportDuration() {
        pageDuration = System.currentTimeMillis();
//        if (microPageId.equals(MainActivity.MICRO_PAGE_MAIN)) {
//            Log.e("EventReportManager", "refresh 今日: pageDuration " + pageDuration);
//        } else {
//            Log.e("EventReportManager", "refresh 课程: pageDuration " + pageDuration);
//        }
    }

    public float getAlpha() {
        return alpha;
    }

    public static MicroPageFragment newInstance(String microPageId, int tabIndex) {
        MicroPageFragment fragment = new MicroPageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("microPageId", microPageId);
        bundle.putInt("tabIndex", tabIndex);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        // 网络请求数据代码
        if (microPageList == null) {
            microPageList = new ArrayList<>();
        }
        setDataByDB();
        hp = new PageFragmentPresenter(this);
        hp.requestMicroPageData(microPageId);
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        if (isFragmentDestroy) {
            return;
        }
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        microPageFresh.finishRefresh();
        if (success) { // 请求成功
            int code = result.getInteger("code");
            if (iRequest instanceof PageFragmentRequest) {
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = (JSONObject) result.get("data");
                    if (microPageList != null) microPageList.clear();
                    obtainDataInCache = false;
                    initPageData(data);
                    if (!networkDecorate) {
                        initMainContent(NETWORK_DECORATE);
                    }
                    // 请求成功之后隐藏 loading
                    // microPageLogo.setVisibility(View.VISIBLE);
                    microPageLoading.setLoadingFinish();
                    microPageAdapter.notifyDataSetChanged();
                } else if (code == NetworkCodes.CODE_GOODS_DELETE) { // 微页面不存在
                    Log.d(TAG, "onMainThreadResponse: micro_page --- " + result.get("msg"));
                    microPageLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
                } else if (code == NetworkCodes.CODE_TINY_PAGER_NO_FIND) { // 微页面已被删除
                    Log.d(TAG, "onMainThreadResponse: micro_page --- " + result.get("msg"));
                    microPageLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
                }else{
                    microPageLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
                }
            } else if (iRequest instanceof ColumnListRequst) {
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONArray data = (JSONArray) result.get("data");
                    refreshRecentComponent(data, iRequest.getDataParams().getString("goods_id"));
                } else {
                    Log.d(TAG, "onMainThreadResponse: 获取最近更新组件的列表失败..");
                    microPageLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
                }
            }
        } else {
            if (iRequest != null) {
                if (microPageList.size() <= 0) { // 页面没数据
                    microPageLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
                } else { // 有数据
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.network_error_text), Toast.LENGTH_SHORT).show();
                    microPageLoading.setLoadingFinish();
                }
            }
        }
    }

    private void setDataByDB() {
        obtainDataInCache = true;
        SQLiteUtil sqLiteUtil = SQLiteUtil.init(getContext(), new CacheDataUtil());
        String sql = "select * from " + CacheDataUtil.TABLE_NAME + " where app_id='" + Constants.getAppId()
                + "' and resource_id='" + microPageId + "' and user_id='"+CommonUserInfo.getLoginUserIdOrAnonymousUserId()+"'";
        List<CacheData> cacheDataList = sqLiteUtil.query(CacheDataUtil.TABLE_NAME, sql, null);
        if (cacheDataList != null && cacheDataList.size() > 0) {
            JSONObject result = JSONObject.parseObject(cacheDataList.get(0).getContent());
            JSONObject data = (JSONObject) result.get("data");
            if (microPageList != null) microPageList.clear();
            initPageData(data);
            if (!cacheDecorate) {
                initMainContent(CACHE_DECORATE);
            }
            // 请求成功之后隐藏 loading
//            microPageLogo.setVisibility(View.VISIBLE);
            microPageLoading.setLoadingFinish();
            microPageFresh.finishRefresh();
            microPageAdapter.notifyDataSetChanged();
        } else {
            microPageLoading.setVisibility(View.VISIBLE);
            microPageLoading.setLoadingState(View.VISIBLE);
        }
    }

    /**
     * 初始化首页内容
     * @param decorateType 渲染方式
     */
    private synchronized void initMainContent(int decorateType) {
        // 初始化布局管理器
        LinearLayoutManager llm_content = new LinearLayoutManager(mContext);
        llm_content.setOrientation(LinearLayout.VERTICAL);
        llm_content.setSmoothScrollbarEnabled(true);
        llm_content.setAutoMeasureEnabled(true);
        microPageContent.setLayoutManager(llm_content);
        microPageContent.setHasFixedSize(true);
//        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration();
//        spacesItemDecoration.setMargin(Dp2Px2SpUtil.dp2px(mContext, 20), 0, Dp2Px2SpUtil.dp2px(mContext, 20), 0);
//        microPageContent.addItemDecoration(spacesItemDecoration);
        // 初始化适配器
        microPageAdapter = new DecorateRecyclerAdapter(mContext, microPageList);
        if (isMain) {
            microPageAdapter.showLastItem(true);
        } else {
            microPageAdapter.showLastItem(false);
        }
        microPageContent.setAdapter(microPageAdapter);
        if (decorateType == NETWORK_DECORATE) {
            networkDecorate = true;
        } else if (decorateType == CACHE_DECORATE) {
            cacheDecorate = true;
        }
    }

    // 刷新最近更新组件的子列表
    private void refreshRecentComponent(JSONArray data, String columnId) {
        List<RecentUpdateListItem> itemList = new ArrayList<>();
        int audioCount = 0; // 记录音频数据条数
        // 获取专栏 list
        for (Object dataItem : data) {
            JSONObject dataJsonItem = (JSONObject) dataItem;
            RecentUpdateListItem recentUpdateListItem = new RecentUpdateListItem();
            String title = dataJsonItem.getString("title");
            // 频道组件如果 playState 传空的话会隐藏播放 icon
            String resourceType = convertInt2Str(dataJsonItem.getInteger("resource_type"));
            String resourceId = dataJsonItem.getString("resource_id");
            recentUpdateListItem.setListTitle(title);
            recentUpdateListItem.setColumnTitle(mColumnTitle);
            if (resourceType.equals(DecorateEntityType.AUDIO)) { // 只有音频才需要显示播放 icon
                recentUpdateListItem.setListPlayState(DecorateEntityType.ITEM_RECENT_PLAY);
                audioCount++; // 是音频就加 1
            } else {
                recentUpdateListItem.setListPlayState("");
            }
            recentUpdateListItem.setListResourceId(resourceId);

            recentUpdateListItem.setAppId(dataJsonItem.getString("app_id"));
            recentUpdateListItem.setImgUrl(dataJsonItem.getString("img_url"));
            recentUpdateListItem.setImgUrlCompress(dataJsonItem.getString("img_url_compress"));
            int rt = dataJsonItem.getInteger("resource_type") == null ? 0 : dataJsonItem.getInteger("resource_type");
            recentUpdateListItem.setResourceType(rt);
            int al = dataJsonItem.getInteger("audio_length") == null ? 0 : dataJsonItem.getInteger("audio_length");
            recentUpdateListItem.setAudioLength(al);
            int vl = dataJsonItem.getInteger("video_length") == null ? 0 : dataJsonItem.getInteger("video_length");
            recentUpdateListItem.setVideoLength(vl);
            recentUpdateListItem.setAudioUrl(dataJsonItem.getString("audio_url"));
            recentUpdateListItem.setColumnId(columnId);
            recentUpdateListItem.setBigColumnId("");
            recentUpdateListItem.setListIsFormUser(mainActivity.isFormalUser);

            itemList.add(recentUpdateListItem);
        }
        // 更新频道组件
        for (int i = 0; i < microPageList.size(); i++) {
            ComponentInfo componentInfo = microPageList.get(i);
            if (componentInfo.getType().equals(DecorateEntityType.RECENT_UPDATE_STR)) {
                if (columnId.equals(componentInfo.getColumnId())) { // 是同一个组件才添加数据
                    componentInfo.setSubList(itemList);
                    // 12.4 需求变更，无论是否全部音频都显示收听全部按钮（不想加字段，就用这个 hideTitle 来判断）
                    if (audioCount == 0) {
                        componentInfo.setHideTitle(true);
                    } else {
                        componentInfo.setHideTitle(false);
                    }
                    // 根据音频数量显示收听全部按钮
//                    if (audioCount == data.size()) { // 全部都是音频，就显示收听全部按钮，不想加字段，就用这个 hideTitle 来判断
//                        componentInfo.setHideTitle(false);
//                    } else {
//                        componentInfo.setHideTitle(true);
//                    }
                    // 局部刷新...
                    if (microPageAdapter != null)
                        microPageAdapter.notifyItemChanged(i);
                }
            }
        }
    }

    // 初始化微页面数据
    private void initPageData(JSONObject data) {
        JSONArray microPageData = (JSONArray) data.get("components");
        if (microPageData == null) {
            return;
        }
        for (Object item : microPageData) {
            JSONObject itemObj = ((JSONObject) item);
            switch (itemObj.getString("type")) {
                case DecorateEntityType.SEARCH_STR: // 搜索组件
                    ComponentInfo componentInfo_title = new ComponentInfo();
                    componentInfo_title.setTitle(getString(R.string.micro_fragment_title));
                    componentInfo_title.setType(DecorateEntityType.SEARCH_STR);
                    microPageList.add(componentInfo_title);
                    hasSearch = true;
                    break;
                case DecorateEntityType.SHUFFLING_FIGURE_STR: // 轮播图
                    ComponentInfo componentInfo_shuffling = new ComponentInfo();
                    componentInfo_shuffling.setType(DecorateEntityType.SHUFFLING_FIGURE_STR);
                    List<ShufflingItem> urlList = new ArrayList<>();
                    JSONArray shufflingSubList = (JSONArray) itemObj.get("list");
                    for (Object subItem : shufflingSubList) {
                        JSONObject subItemObj = (JSONObject) subItem;
                        String title = subItemObj.getString("title");
                        String imgUrl = subItemObj.getString("img_url");
                        String resourceType = subItemObj.getString("src_type");
                        String resourceId = subItemObj.getString("src_id");

                        ShufflingItem shufflingItem = new ShufflingItem();
                        shufflingItem.setTitle(title);
                        shufflingItem.setImgUrl(imgUrl);
                        shufflingItem.setSrcId(resourceId);
                        shufflingItem.setSrcType(resourceType);

                        urlList.add(shufflingItem);
                    }
                    componentInfo_shuffling.setShufflingList(urlList);
                    microPageList.add(componentInfo_shuffling);
                    break;
                case DecorateEntityType.GRAPHIC_NAVIGATION_STR: // 图文导航
                    ComponentInfo componentInfo_navigator = new ComponentInfo();
                    componentInfo_navigator.setType(DecorateEntityType.GRAPHIC_NAVIGATION_STR);
                    if (!networkDecorate) {
                        componentInfo_navigator.setNeedDecorate(true);
                    } else {
                        componentInfo_navigator.setNeedDecorate(false);
                    }
                    List<GraphicNavItem> graphicNavItemList = new ArrayList<>();
                    JSONArray navigatorSubList = (JSONArray) itemObj.get("list");
                    for (Object subItem : navigatorSubList) {
                        JSONObject subItemObj = (JSONObject) subItem;
                        GraphicNavItem graphicNavItem = new GraphicNavItem();
                        graphicNavItem.setNavIcon(subItemObj.getString("img_url"));
                        graphicNavItem.setNavContent(subItemObj.getString("title"));
                        graphicNavItem.setNavResourceType(subItemObj.getString("src_type"));
                        graphicNavItem.setNavResourceId(subItemObj.getString("src_id"));
                        graphicNavItem.setNavJumpUrl(subItemObj.getString("jump_url"));
                        graphicNavItemList.add(graphicNavItem);
                    }
                    componentInfo_navigator.setGraphicNavItemList(graphicNavItemList);
                    microPageList.add(componentInfo_navigator);
                    break;
                case DecorateEntityType.GOODS_STR:
                    initGoods(itemObj);
                    break;
                case DecorateEntityType.FLOW_INFO_STR:
                    initFlowInfo(itemObj);
                    break;
                default:
                    break;
            }
        }
    }

    // 初始化商品
    private void initGoods(JSONObject itemObj) {
        String type_title = itemObj.getString("type_title");
        switch (type_title) {
            case DecorateEntityType.RECENT_UPDATE_STR: // 频道
                // 初始化频道数据
                JSONArray recentList = (JSONArray) itemObj.get("list");
                String recentTopTitle = itemObj.getString("title");
                boolean isShowTitle = itemObj.getInteger("show_title") == 1;
                for (Object listItem : recentList) {
                    ComponentInfo component_recent = new ComponentInfo();
                    component_recent.setType(DecorateEntityType.RECENT_UPDATE_STR);
                    component_recent.setFormUser(mainActivity.isFormalUser);
                    JSONObject jsonItem = (JSONObject) listItem;

                    String recentTitle = jsonItem.getString("title");
                    mColumnTitle = recentTitle;
                    // 频道组件对应的专栏 id
                    String recentId = jsonItem.getString("src_id");
                    int updateCount = jsonItem.getInteger("resource_count") == null ? 0 : jsonItem.getInteger("resource_count");
                    String updateCountStr = String.format(getString(R.string.updated_to_issue), updateCount);
                    String srcType = jsonItem.getString("src_type");
                    component_recent.setJoinedDesc(recentTopTitle); // 使用 joinedDesc 字段表示组件的 top title
                    component_recent.setTitle(recentTitle); // 组件内 title
                    component_recent.setHideTitle(!isShowTitle);
                    String imgUrl;
                    if ("appe0MEs6qX8480".equals(Constants.getAppId()) || "app38itOR341547".equals(Constants.getAppId())) { // 吴晓波 app，头像换成他的头像
                        imgUrl = "res:///" + R.mipmap.wu_logo;
                    } else {
                        imgUrl = jsonItem.getString("img_url");
                    }
                    component_recent.setImgUrl(imgUrl);

                    component_recent.setDesc(updateCountStr);
                    // 最近更新需要设置专栏 id
                    component_recent.setColumnId(recentId);
                    component_recent.setSubType(srcType);
                    if (TextUtils.isEmpty(jsonItem.getString("show_price")) || "0.00".equals(jsonItem.getString("show_price"))) { // 已购或者免费
                        component_recent.setHasBuy(true);
                    } else {
                        component_recent.setHasBuy(false);
                    }
                    if (recentId != null && !obtainDataInCache) {
                        ColumnPresenter columnPresenter = new ColumnPresenter(this);
                        // 请求该专栏下三条数据
                        columnPresenter.requestColumnListByNum(recentId, "0", 3);
                    }
                    // 先把频道组件存起来，等网络请求的回调回来后再操作
                    microPageList.add(component_recent);
                    break; // 只取第一个
                }
                break;
            case DecorateEntityType.KNOWLEDGE_COMMODITY_STR: // 知识商品
                if (itemObj.getInteger("list_style") == 0) { // 列表形式
                    // 若数据有问题，默认不显示标题
                    ComponentInfo componentInfo_know_list = new ComponentInfo();
                    componentInfo_know_list.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
                    componentInfo_know_list.setSubType(DecorateEntityType.KNOWLEDGE_LIST_STR);
                    int showTitle = itemObj.getInteger("show_title") == null ? 0 : itemObj.getInteger("show_title");
                    boolean showCheckAll = itemObj.getBoolean("check_all") == null ? false : itemObj.getBoolean("check_all");
                    // 微页面列表形式的知识商品组件都隐藏 title
                    if (showTitle == 0) {
                        componentInfo_know_list.setHideTitle(true);
                    } else if (showTitle == 1) {
                        componentInfo_know_list.setHideTitle(false);
                        String title = itemObj.getString("title") == null ? getString(R.string.learn_manage_wealth) : itemObj.getString("title");
                        componentInfo_know_list.setTitle(title);
                        if (showCheckAll) {
                            componentInfo_know_list.setDesc(getString(R.string.see_more));
                            componentInfo_know_list.setInMicro(true); // 标识是在微页面中
                        } else {
                            componentInfo_know_list.setDesc("");
                        }
                    }
                    componentInfo_know_list.setGroupId(itemObj.getString("tag_id"));
                    List<KnowledgeCommodityItem> listItems = new ArrayList<>();
                    JSONArray knowledgeListSubList = (JSONArray) itemObj.get("list");
                    if (knowledgeListSubList == null) {
                        return;
                    }
                    String colorStr = itemObj.getString("color") == null ? "" : itemObj.getString("color");
                    int bgColor;
                    if (TextUtils.isEmpty(colorStr)) { // 为空，设置为白色
                        bgColor = Color.parseColor("#ffffff");
                    } else { // 否则，设置为后台的颜色
                        if (colorStr.length() == 4) { // #fff 这种情况
                            String tailStr = colorStr.substring(1);
                            String resultStr = colorStr + tailStr;
                            bgColor = Color.parseColor(resultStr);
                        } else {
                            bgColor = Color.parseColor(colorStr);
                        }
                    }
                    componentInfo_know_list.setKnowledgeCompBg(bgColor);
                    initKnowledgeData(listItems, knowledgeListSubList);
                    componentInfo_know_list.setKnowledgeCommodityItemList(listItems);
                    microPageList.add(componentInfo_know_list);
                } else if (itemObj.getInteger("list_style") == 2) { // （小图）宫格形式
                    ComponentInfo componentInfo_know_group = new ComponentInfo();
                    componentInfo_know_group.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
                    componentInfo_know_group.setSubType(DecorateEntityType.KNOWLEDGE_GROUP_STR);
                    componentInfo_know_group.setGroupId(itemObj.getString("tag_id"));
                    int showTitle = itemObj.getInteger("show_title") == null ? 0 : itemObj.getInteger("show_title");
                    boolean showCheckAll = itemObj.getBoolean("check_all") == null ? false : itemObj.getBoolean("check_all");
                    if (showTitle == 0) {
                        componentInfo_know_group.setHideTitle(true);
                    } else if (showTitle == 1) {
                        componentInfo_know_group.setHideTitle(false);
                        String title = itemObj.getString("title") == null ? getString(R.string.learn_manage_wealth) : itemObj.getString("title");
                        componentInfo_know_group.setTitle(title);
                        if (showCheckAll) {
                            componentInfo_know_group.setDesc(getString(R.string.see_more));
                            componentInfo_know_group.setInMicro(true); // 标识是在微页面中
                        } else {
                            componentInfo_know_group.setDesc("");
                        }
                    }
                    if (!networkDecorate) {
                        componentInfo_know_group.setNeedDecorate(true);
                    } else {
                        componentInfo_know_group.setNeedDecorate(false);
                    }
                    List<KnowledgeCommodityItem> groupItems = new ArrayList<>();
                    String colorStr = itemObj.getString("color") == null ? "" : itemObj.getString("color");
                    int bgColor;
                    if (TextUtils.isEmpty(colorStr)) { // 为空，设置为白色
                        bgColor = Color.parseColor("#ffffff");
                    } else { // 否则，设置为后台的颜色
                        if (colorStr.length() == 4) { // #fff 这种情况
                            String tailStr = colorStr.substring(1);
                            String resultStr = colorStr + tailStr;
                            bgColor = Color.parseColor(resultStr);
                        } else {
                            bgColor = Color.parseColor(colorStr);
                        }
                    }
                    componentInfo_know_group.setKnowledgeCompBg(bgColor);
                    JSONArray knowledgeGroupSubList = (JSONArray) itemObj.get("list");
                    if (knowledgeGroupSubList == null) {
                        return;
                    }
                    initKnowledgeData(groupItems, knowledgeGroupSubList);
                    componentInfo_know_group.setKnowledgeCommodityItemList(groupItems);
                    microPageList.add(componentInfo_know_group);
                }
                break;
            default:
                break;
        }
    }

    // 从 JSON 对象数组中获取知识商品的数据并初始化到已设置好的对象中
    private void initKnowledgeData(List<KnowledgeCommodityItem> itemList, JSONArray knowledgeListSubList) {
        for (Object subItem : knowledgeListSubList) {
            JSONObject listSubItemObj = (JSONObject) subItem;
            KnowledgeCommodityItem item = new KnowledgeCommodityItem();
            int paymentType = listSubItemObj.getInteger("payment_type") == null ? -1 : listSubItemObj.getInteger("payment_type");
            item.setItemTitle(listSubItemObj.getString("title"));
            item.setItemTitleColumn(listSubItemObj.getString("summary"));
            item.setItemImg(listSubItemObj.getString("img_url_compressed_larger"));
//            item.setItemImg(listSubItemObj.getString("img_url"));
            String price = listSubItemObj.getString("show_price") + getContext().getString(R.string.wxb_virtual_unit);
            String linePrice = listSubItemObj.getString("line_price") + getContext().getString(R.string.wxb_virtual_unit);
            boolean isPriceEmpty = getContext().getString(R.string.wxb_virtual_unit).equals(price) ||
                    ("0.00" + getContext().getString(R.string.wxb_virtual_unit)).equals(price) ||
                    ("null" + getContext().getString(R.string.wxb_virtual_unit)).equals(price);
            if (isPriceEmpty) {
                boolean hasBuy = false;
                boolean isFree = false;
                item.setItemPrice("");
                if (paymentType == 1) { // 免费
                    isFree = true;
                } else if (paymentType == 2) { // 已购
                    hasBuy = true;
                }
                item.setHasBuy(hasBuy);
                item.setFree(isFree);
            } else {
                item.setItemPrice(price);
                item.setHasBuy(false);
                item.setFree(false);
            }
            String srcType = listSubItemObj.getString("src_type");
            String srcId = listSubItemObj.getString("src_id");
            // view_count -- 浏览次数 / resource_count -- 更新期数 / purchase_count -- 订阅数
            String viewCount = TextUtils.isEmpty(listSubItemObj.getString("view_count")) ? "" : listSubItemObj.getString("view_count");
            String resourceCount = TextUtils.isEmpty(listSubItemObj.getString("resource_count")) ? "" : listSubItemObj.getString("resource_count");
            item.setSrcType(srcType);
            item.setResourceId(srcId);
            item.setLinePrice(linePrice);
            item.setResourceCount(resourceCount);
            // 专栏或者大专栏订阅量就是 purchaseCount
            if (srcType.equals(DecorateEntityType.COLUMN) || srcType.equals(DecorateEntityType.TOPIC)) {
                viewCount = resourceCount;
            }
            String viewDesc = obtainViewCountDesc(srcType, viewCount);
            item.setItemDesc(viewDesc);
            itemList.add(item);
        }
    }

    // 根据子类型获取浏览字段
    private String obtainViewCountDesc(String srcType, String viewCount) {
        if (TextUtils.isEmpty(viewCount)) {
            return "";
        }
        switch (srcType) {
            case DecorateEntityType.IMAGE_TEXT: // 图文
            case DecorateEntityType.AUDIO: // 音频
            case DecorateEntityType.VIDEO: // 视频
                return String.format(getString(R.string.learn_count_str), viewCount);
            case DecorateEntityType.TOPIC: // 大专栏
            case DecorateEntityType.COLUMN: // 专栏
            case DecorateEntityType.MEMBER: // 会员
                return String.format(getString(R.string.stages_text_str), viewCount);
            default:
                return "";
        }
    }

    // 初始化信息流组件
    private void initFlowInfo(JSONObject data) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        // 信息流 information_list
        JSONArray informationList = (JSONArray) data.get("information_list");
        for (int i = 0; i < informationList.size(); i++) {
            ComponentInfo flowInfoComponent = new ComponentInfo();
            flowInfoComponent.setType(DecorateEntityType.FLOW_INFO_STR);
            flowInfoComponent.setFormUser(mainActivity.isFormalUser);
            JSONObject flowInfoItem = (JSONObject) informationList.get(i);
            // 拿到信息流 title 信息
            JSONArray dateTag = (JSONArray) flowInfoItem.get("date_tag");
            // 0 -- 今日；1 -- *年*月*日；2 -- 星期*
            String today = dateTag.getString(0);
            String yearString = dateTag.getString(1);
            String titleOne = yearString.split("年")[0]; // 标题年之前的部分
            String titleTwo = yearString.split("年")[1]; // 标题年之后的部分
            int yearStr = 0;
            try {
                yearStr = Integer.parseInt(titleOne);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (i == 0) { // 第一个要显示我正在学
                String flowInfoTitle;
                if (yearStr != 0 && year == yearStr) {
                    if (TextUtils.isEmpty(today)) { // 今日为空
                        flowInfoTitle = titleTwo;
                    } else {
                        flowInfoTitle = titleTwo + " " + dateTag.getString(2);
                    }
                } else {
                    flowInfoTitle = dateTag.getString(1);
                }
                flowInfoComponent.setTitle(flowInfoTitle);
                if ("".equals(today)) {
                    flowInfoComponent.setDesc(dateTag.getString(2));
                } else {
                    flowInfoComponent.setDesc(today);
                }
                flowInfoComponent.setJoinedDesc(getString(R.string.learning_tab_title));
                flowInfoComponent.setImgUrl("res:///" + R.mipmap.icon_taday_learning);
            } else { // 非今日，不显示我正在学
                String flowInfoTitle;
                String flowInfoDesc = dateTag.getString(2);
                if (yearStr != 0 && year == yearStr) {
                    flowInfoTitle = titleTwo;
                } else {
                    flowInfoTitle = dateTag.getString(1);
                }
                flowInfoComponent.setTitle(flowInfoTitle);
                flowInfoComponent.setDesc(flowInfoDesc);
                flowInfoComponent.setJoinedDesc("");
                flowInfoComponent.setImgUrl("");
            }
            // 拿到信息流的 content 信息
            List<FlowInfoItem> dataList = new ArrayList<>();
            JSONArray flowInfoList = (JSONArray) flowInfoItem.get("list");
            for (Object item : flowInfoList) {
                JSONObject flowInfo = (JSONObject) item;
                FlowInfoItem fii = new FlowInfoItem();
                String resourceType = flowInfo.getString("src_type");
                // true 表示关联售卖，否则为非关联售卖（字段不准确，不用）
                // boolean isRelated = flowInfo.getInteger("is_related") != null && flowInfo.getInteger("is_related") == 1;
                String resourceId = flowInfo.getString("src_id");
                String tag = flowInfo.getString("info_tag") == null ? "" : flowInfo.getString("info_tag");
                String title = flowInfo.getString("title");
                String desc = flowInfo.getString("summary");
                String imgUrl = flowInfo.getString("img_url");
                String showPrice = TextUtils.isEmpty(flowInfo.getString("show_price")) ? "" : flowInfo.getString("show_price") + getContext().getString(R.string.wxb_virtual_unit);
                String linePrice = TextUtils.isEmpty(flowInfo.getString("line_price")) ? "" : flowInfo.getString("line_price") + " " + getContext().getString(R.string.wxb_virtual_unit);
                int paymentType = flowInfo.getInteger("payment_type") == null ? -1 : flowInfo.getInteger("payment_type");
                boolean isPriceExist = !TextUtils.isEmpty(showPrice) && !("0.00" + getContext().getString(R.string.wxb_virtual_unit)).equals(showPrice) &&
                        !getContext().getString(R.string.wxb_virtual_unit).equals(showPrice);
                boolean hasBuy = false;
                boolean isFree = false;
                if (isPriceExist && paymentType == 1) { // 免费
                    isFree = true;
                } else if (isPriceExist && paymentType == 2) { // 已购
                    hasBuy = true;
                }
                JSONArray infoLabels = flowInfo.getJSONArray("info_label");
                if (infoLabels != null) {
                    List<LabelItemEntity> labelItemEntityList = new ArrayList<>();
                    LabelItemEntity headItem = new LabelItemEntity();
                    // 使用 labelBackground、labelFontColor 表示第一个元素是显示价格还是划线价格
                    if (TextUtils.isEmpty(linePrice)) {
                        if (isPriceExist) {
                            headItem.setLabelContent(showPrice);
                            headItem.setLabelBackground("showPrice");
                            headItem.setLabelFontColor("showPrice");
                            labelItemEntityList.add(headItem);
                        }
                    } else {
                        headItem.setLabelContent(linePrice);
                        headItem.setLabelBackground("linePrice");
                        headItem.setLabelFontColor("linePrice");
                        labelItemEntityList.add(headItem);
                    }
                    for (Object labelItem : infoLabels) {
                        JSONObject labelItemJson = (JSONObject) labelItem;
                        LabelItemEntity labelItemEntity = new LabelItemEntity();
                        labelItemEntity.setLabelContent(labelItemJson.getString("content"));
                        labelItemEntity.setLabelBackground(labelItemJson.getString("background_color"));
                        labelItemEntity.setLabelFontColor(labelItemJson.getString("font_color"));
                        labelItemEntityList.add(labelItemEntity);
                    }
                    fii.setLabelList(labelItemEntityList);
                }

                fii.setItemType(resourceType);
                fii.setItemId(resourceId);
                fii.setItemTag(tag);
                fii.setItemTitle(title);
                fii.setItemDesc(desc);
                fii.setItemPrice(showPrice);
                fii.setLinePrice(linePrice);
//                if (isRelated) { // 关联售卖，则不显示价格
//                    hasBuy = true; // 仅仅是将价格隐藏，不是真的已购
//                } else {
//                    fii.setItemPrice(showPrice);
//                }
                fii.setItemHasBuy(hasBuy);
                fii.setItemIsFree(isFree);
                fii.setItemImg(imgUrl);

                dataList.add(fii);
            }
            flowInfoComponent.setFlowInfoItemList(dataList);
            microPageList.add(flowInfoComponent);
        }
    }


    /**
     * 拿到请求到的数据后进行界面初始化
     */
    public void init() {
        if (microPageList == null) {
            microPageList = new ArrayList<>();
        }
//        microScrollView.setScrollChanged(this);
        microPageContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                newUpdateToolbar();
            }
        });
        microPageFresh.setOnRefreshListener(this);
        microPageFresh.setEnableOverScrollBounce(false);
//        if (!microPageId.equals("") && !microPageId.equals(MainActivity.MICRO_PAGE_MAIN))
//            microPageFresh.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
//                @Override
//                public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent,
//                                           int offset, int headerHeight, int maxDragHeight) {
//                    if (microPageTitleBg2.getVisibility() == View.GONE){
//                        microPageTitleBg2.setVisibility(View.VISIBLE);
//                        microPageTitleBg.setVisibility(View.GONE);
//                    }
//                }
//            });
        toolbarHeight = Dp2Px2SpUtil.dp2px(mContext, 160);

        // 微页面 id 存在并且不是首页的微页面 id，默认是课程页面
        if (!microPageId.equals("") && !microPageId.equals(MainActivity.MICRO_PAGE_MAIN)) { // 课程页设置一个顶部背景
            microPageTitleBg.setImageURI("res:///" + R.mipmap.line_bg);
//            microPageTitleBg2.setImageURI("res:///" + R.mipmap.class_bg);
            microPageToolbar.setVisibility(View.VISIBLE);
            // 初始化 toolbar
            int top = 0;
            if (microPageId.equals(MainActivity.MICRO_PAGE_MAIN)) {
                top = StatusBarUtil.getStatusBarHeight(mContext);
            }
//            int statusBarHeight = StatusBarUtil.getStatusBarHeight(mContext);
            // 沉浸式初始化
            microPageWrap.setPadding(0, top, 0, 0);
            // toolbar 的内容都设置为透明
            microPageToolbar.setBackgroundColor(Color.argb(0, 255, 255, 255));
            microPageToolbarTitle.setVisibility(View.GONE);
            microPageToolbarSearch.setVisibility(View.GONE);
            microPageToolbarSearch.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
                @Override
                public void singleClick(View v) {
                    JumpDetail.jumpSearch(mContext);

                    EventReportManager.onEvent(mContext, MobclickEvent.COURSE_SEARCH_BTN_CLICK);
                }
            });
//            microPageFresh.setEnableHeaderTranslationContent(false);
            microPageHeader.setBackgroundColor(getResources().getColor(R.color.white));
            isMain = false;
        } else {
            microPageTitleBg.setVisibility(View.GONE);
            microPageTitleBg.setImageURI("");
//            microPageTitleBg2.setVisibility(View.GONE);
            microPageToolbar.setVisibility(View.GONE);
//            microPageFresh.setEnableHeaderTranslationContent(true);
            microPageHeader.setBackgroundColor(getResources().getColor(R.color.white));
            isMain = true;
        }

        // 转场动画 SimpleDraweeView 处理
        getActivity().setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                Log.d(TAG, "onSharedElementEnd: ");
                for (View view : sharedElements) {
                    if (view instanceof SimpleDraweeView) {
                        view.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                super.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots);
                Log.d(TAG, "onSharedElementStart: ");
            }

            @Override
            public void onSharedElementsArrived(List<String> sharedElementNames, List<View> sharedElements, OnSharedElementsReadyListener listener) {
                super.onSharedElementsArrived(sharedElementNames, sharedElements, listener);
                Log.d(TAG, "onSharedElementsArrived: ");
            }
        });

        microPageLoading.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                Log.d(TAG, "doClick: do nothing");
            }
        });
    }

    /**
     * 资源类型转换 int - str
     *
     * @param resourceType 资源类型
     * @return 资源类型的字符串形式
     */
    protected String convertInt2Str(int resourceType) {
        switch (resourceType) {
            case 1: // 图文
                return DecorateEntityType.IMAGE_TEXT;
            case 2: // 音频
                return DecorateEntityType.AUDIO;
            case 3: // 视频
                return DecorateEntityType.VIDEO;
            case 6: // 专栏
                return DecorateEntityType.COLUMN;
            case 8: // 大专栏
                return DecorateEntityType.TOPIC;
            default:
                return "";
        }
    }

    @Subscribe
    public void onEventMainThread(AudioPlayEvent event) {
        switch (event.getState()) {
            case AudioPlayEvent.NEXT:
            case AudioPlayEvent.LAST:
            case AudioPlayEvent.PAUSE:
            case AudioPlayEvent.PLAY:
            case AudioPlayEvent.STOP:
                if (microPageAdapter != null) {
                    microPageAdapter.notifyDataSetChangedRecentUpdate();
                }
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void onEventMainThread(ChangeLoginIdentityEvent changeLoginIdentityEvent) {
        if (changeLoginIdentityEvent != null && changeLoginIdentityEvent.isChangeSuccess()) {
            if (hp == null) {
                hp = new PageFragmentPresenter(this);
            }
            hp.requestMicroPageData(microPageId);
        }
    }

    /**
     * 监听 recyclerView 的滑动事件对 toolbar 进行显示与隐藏
     */
    private void newUpdateToolbar() {
        if (!isMain) {
//            microPageBottom.setVisibility(View.GONE);
            if (getScrollYDistance() > maxAlpha) {
                if (microPageToolbarTitle.getVisibility() != View.VISIBLE) {
                    microPageToolbarTitle.setVisibility(View.VISIBLE);
                    mStatusBarBlank.setVisibility(View.VISIBLE);
                    int color = Color.argb(255, 255, 255, 255);
                    microPageToolbar.setBackgroundColor(color);
                    if (hasSearch) {
                        microPageToolbarSearch.setVisibility(View.VISIBLE);
                    } else {
                        microPageToolbarSearch.setVisibility(View.GONE);
                    }
                }
            } else {
                microPageToolbarTitle.setVisibility(View.GONE);
                mStatusBarBlank.setVisibility(View.GONE);
                microPageToolbarSearch.setVisibility(View.GONE);
            }
            if (getScrollYDistance() > Dp2Px2SpUtil.dp2px(mContext, 120)) {
                microPageTitleBg.setVisibility(View.GONE);
            } else {
                microPageTitleBg.setVisibility(View.VISIBLE);
            }
        } else {
            mStatusBarBlank.setVisibility(View.GONE);
//            if (!microPageContent.canScrollVertically(1)) { // 滑到底部
//                microPageBottom.setVisibility(View.VISIBLE);
//            } else {
//                microPageBottom.setVisibility(View.GONE);
//            }
        }
    }

    /**
     * 更新 toolbar（监听 scrollerView 的滚动事件）
     *
     * @deprecated
     * @param scrollY
     */
    private void updateToolbar(int scrollY) {
        if (!isMain) {
            alpha = (scrollY / (toolbarHeight * 1.0f)) * 255;
//            if (alpha >= 0 && microPageTitleBg2.getVisibility() == View.VISIBLE){
//                microPageTitleBg.setVisibility(View.VISIBLE);
//                microPageTitleBg2.setVisibility(View.GONE);
//            }
            if (alpha > maxAlpha) {
                if (microPageToolbarTitle.getVisibility() != View.VISIBLE) {
                    microPageToolbarTitle.setVisibility(View.VISIBLE);
                    mStatusBarBlank.setVisibility(View.VISIBLE);
                    int color = Color.argb(255, 255, 255, 255);
                    microPageToolbar.setBackgroundColor(color);
                    if (hasSearch) {
                        microPageToolbarSearch.setVisibility(View.VISIBLE);
                    } else {
                        microPageToolbarSearch.setVisibility(View.GONE);
                    }
                }
            } else {
                microPageToolbarTitle.setVisibility(View.GONE);
                mStatusBarBlank.setVisibility(View.GONE);
                microPageToolbarSearch.setVisibility(View.GONE);
            }
        } else {
            mStatusBarBlank.setVisibility(View.GONE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (hp == null) {
            hp = new PageFragmentPresenter(this);
        }
//        microPageLogo.setVisibility(View.GONE);
//        microPageList.clear();
//        microPageAdapter.notifyDataSetChanged();
        // 刷新前，设置需要重新渲染
        networkDecorate = false;
        microPageAdapter = null;
        hp.requestMicroPageData(microPageId);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            totalScrollY = 0;
            if (microPageToolbar != null) {
                microPageToolbar.setVisibility(View.GONE);
            }
        }
    }

    /**
     * LinearLayoutManager 时获取 y 方向滑动距离
     * @return 竖直方向滑动距离
     */
    public int getScrollYDistance() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) microPageContent.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisibleChildView = layoutManager.findViewByPosition(position);
        int itemHeight = firstVisibleChildView.getHeight();
        return (position) * itemHeight - firstVisibleChildView.getTop();
    }
}
