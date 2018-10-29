package xiaoe.com.shop.business.main.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.entitys.ComponentInfo;
import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.common.entitys.GraphicNavItem;
import xiaoe.com.common.entitys.KnowledgeCommodityItem;
import xiaoe.com.common.entitys.RecentUpdateListItem;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.ColumnListRequst;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.PageFragmentRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.decorate.DecorateRecyclerAdapter;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.column.presenter.ColumnPresenter;
import xiaoe.com.shop.business.main.presenter.PageFragmentPresenter;
import xiaoe.com.shop.utils.StatusBarUtil;
import xiaoe.com.shop.widget.StatusPagerView;

public class MicroPageFragment extends BaseFragment {

    private static final String TAG = "MicroPageFragment";

    private Unbinder unbinder;
    private Context mContext;
    private int microPageId = -1;

    private boolean destroyView = false;

    @BindView(R.id.micro_page_app_bar)
    AppBarLayout microPageAppBar;
    @BindView(R.id.micro_page_head_recycler)
    RecyclerView microPageTitleRecyclerView;
    @BindView(R.id.micro_page_content_recycler)
    RecyclerView microPageContentRecyclerView;
    @BindView(R.id.micro_page_collapsing_toolbar)
    CollapsingToolbarLayout microPageCollLayout;
    @BindView(R.id.micro_page_loading)
    StatusPagerView microPageLoading;

    @BindView(R.id.micro_page_toolbar)
    Toolbar microToolBar;
    @BindView(R.id.micro_page_toolbar_title)
    TextView microToolBarTitle;
    @BindView(R.id.micro_page_toolbar_search_icon)
    ImageView microToolBarSearch;

    List<ComponentInfo> microPageList;

    public static MicroPageFragment newInstance(int microPageId) {
        MicroPageFragment microPageFragment = new MicroPageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("microPageId", microPageId);
        microPageFragment.setArguments(bundle);
        return microPageFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_micro_page, null, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            // 获取微页面 id
            microPageId = bundle.getInt("microPageId");
        }
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            // fragment 显示的时候显示 loading
            microPageLoading.setHintStateVisibility(View.GONE);
            microPageLoading.setLoadingState(View.VISIBLE);
        }
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        // 网络请求数据代码
        PageFragmentPresenter hp = new PageFragmentPresenter(this);
        hp.requestMicroPageData(microPageId);
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        if (success) { // 请求成功
            JSONObject result = (JSONObject) entity;
            int code = result.getInteger("code");
            if (iRequest instanceof PageFragmentRequest) {
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = (JSONObject) result.get("data");
                    initPageData(data);
                    initMainContent();
                    // 请求成功之后隐藏 loading
                    microPageLoading.setVisibility(View.GONE);
                } else if (code == NetworkCodes.CODE_GOODS_DELETE) { // 微页面不存在
                    Log.d(TAG, "onMainThreadResponse: micro_page --- " + result.get("msg"));
                } else if (code == NetworkCodes.CODE_TINY_PAGER_NO_FIND) { // 微页面已被删除
                    Log.d(TAG, "onMainThreadResponse: micro_page --- " + result.get("msg"));
                }
            } else if (iRequest instanceof ColumnListRequst) {
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONArray data = (JSONArray) result.get("data");
                    refreshRecentComponent(data);
                } else {
                    Log.d(TAG, "onMainThreadResponse: 获取最近更新组件的列表失败..");
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: fail");
        }
    }

    // 刷新最近更新组件的子列表
    private void refreshRecentComponent(JSONArray data) {
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
            if (resourceType.equals(DecorateEntityType.AUDIO)) { // 只有音频才需要显示播放 icon
                recentUpdateListItem.setListPlayState(DecorateEntityType.ITEM_RECENT_PLAY);
                audioCount++; // 视音频就加 1
            } else {
                recentUpdateListItem.setListPlayState("");
            }
            recentUpdateListItem.setListResourceId(resourceId);
            itemList.add(recentUpdateListItem);
        }
        // 更新频道组件
        for (ComponentInfo componentInfo : microPageList) {
            if (componentInfo.getType().equals(DecorateEntityType.RECENT_UPDATE_STR)) {
                componentInfo.setSubList(itemList);
                if (audioCount == 3) { // 3 个都是音频，就显示收听全部按钮，不想加字段，就用这个 hideTitle 来判断
                    componentInfo.setHideTitle(false);
                } else {
                    componentInfo.setHideTitle(true);
                }
            }
        }
        // 刷新操作
        microPageAdapter.notifyDataSetChanged();
    }

    // 初始化微页面数据
    private void initPageData(JSONObject data) {
        JSONArray microPageData = (JSONArray) data.get("components");
        for (Object item : microPageData) {
            JSONObject itemObj = ((JSONObject) item);
            switch (itemObj.getString("type")) {
                case DecorateEntityType.SEARCH_STR: // 搜索组件
                    ComponentInfo componentInfo_title = new ComponentInfo();
                    componentInfo_title.setTitle("课程");
                    componentInfo_title.setType(DecorateEntityType.SEARCH_STR);
                    microPageList.add(componentInfo_title);
                    break;
                case DecorateEntityType.SHUFFLING_FIGURE_STR: // 轮播图
                    ComponentInfo componentInfo_shuffling = new ComponentInfo();
                    componentInfo_shuffling.setType(DecorateEntityType.SHUFFLING_FIGURE_STR);
                    List<String> urlList = new ArrayList<>();
                    JSONArray shufflingSubList = (JSONArray) itemObj.get("list");
                    for (Object subItem : shufflingSubList) {
                        JSONObject subItemObj = (JSONObject) subItem;
                        urlList.add(subItemObj.getString("img_url"));
                    }
                    componentInfo_shuffling.setShufflingList(urlList);
                    microPageList.add(componentInfo_shuffling);
                    break;
                case DecorateEntityType.GRAPHIC_NAVIGATION_STR: // 图文导航
                    ComponentInfo componentInfo_navigator = new ComponentInfo();
                    componentInfo_navigator.setType(DecorateEntityType.GRAPHIC_NAVIGATION_STR);
                    List<GraphicNavItem> graphicNavItemList = new ArrayList<>();
                    JSONArray navigatorSubList = (JSONArray) itemObj.get("list");
                    for (Object subItem : navigatorSubList) {
                        JSONObject subItemObj = (JSONObject) subItem;
                        GraphicNavItem graphicNavItem = new GraphicNavItem();
                        graphicNavItem.setNavIcon(subItemObj.getString("img_url"));
                        graphicNavItem.setNavContent(subItemObj.getString("title"));
                        graphicNavItem.setNavResourceType(subItemObj.getString("src_type"));
                        graphicNavItem.setNavResourceId(subItemObj.getString("src_id"));
                        graphicNavItemList.add(graphicNavItem);
                    }
                    componentInfo_navigator.setGraphicNavItemList(graphicNavItemList);
                    microPageList.add(componentInfo_navigator);
                    break;
                case DecorateEntityType.GOODS:
                    initGoods(itemObj);
                    break;
            }
        }
    }

    // 初始化商品
    private void initGoods(JSONObject itemObj) {
        String type_title = itemObj.getString("type_title");
        switch (type_title) {
            case DecorateEntityType.RECENT_UPDATE_STR: // 频道
                // TODO: 初始化频道数据
                JSONArray recentList = (JSONArray) itemObj.get("list");
                for (Object listItem : recentList) {
                    ComponentInfo component_recent = new ComponentInfo();
                    component_recent.setType(DecorateEntityType.RECENT_UPDATE_STR);
                    JSONObject jsonItem = (JSONObject) listItem;
                    String recentTitle = jsonItem.getString("title");
                    // 频道组件对应的专栏 id
                    String recentId = jsonItem.getString("src_id");
                    String imgUrl = jsonItem.getString("img_url");
                    int updateCount = jsonItem.getInteger("resource_count");
                    String updateCountStr = "已更新至" + updateCount + "期";
                    component_recent.setTitle(recentTitle);
                    component_recent.setImgUrl(imgUrl);
                    component_recent.setDesc(updateCountStr);
                    // 最近更新需要设置专栏 id
                    component_recent.setColumnId(recentId);
                    if (recentId != null) {
                        ColumnPresenter columnPresenter = new ColumnPresenter(this);
                        // 请求该专栏下三条数据
                        columnPresenter.requestColumnListByNum(recentId, "0", 3);
                    }
                    // 先把频道组件存起来，等网络请求的回调回来后再操作
                    microPageList.add(component_recent);
                }
                break;
            case DecorateEntityType.KNOWLEDGE_COMMODITY_STR: // 知识商品
                if (itemObj.getInteger("list_style") == 0) { // 列表形式
                    ComponentInfo componentInfo_know_list = new ComponentInfo();
                    componentInfo_know_list.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
                    componentInfo_know_list.setSubType(DecorateEntityType.KNOWLEDGE_LIST);
                    // 微页面列表形式的知识商品组件都隐藏 title
                    componentInfo_know_list.setHideTitle(true);
                    componentInfo_know_list.setGroupId(itemObj.getString("tag_id"));
                    List<KnowledgeCommodityItem> listItems = new ArrayList<>();
                    JSONArray knowledgeListSubList = (JSONArray) itemObj.get("list");
                    initKnowledgeData(listItems, knowledgeListSubList);
                    componentInfo_know_list.setKnowledgeCommodityItemList(listItems);
                    microPageList.add(componentInfo_know_list);
                } else if (itemObj.getInteger("list_style") == 2) { // （小图）宫格形式
                    ComponentInfo componentInfo_know_group = new ComponentInfo();
                    componentInfo_know_group.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
                    componentInfo_know_group.setSubType(DecorateEntityType.KNOWLEDGE_GROUP);
                    componentInfo_know_group.setTitle("学会管理自己的财富");
                    componentInfo_know_group.setDesc("查看更多");
                    componentInfo_know_group.setGroupId(itemObj.getString("tag_id"));
                    List<KnowledgeCommodityItem> groupItems = new ArrayList<>();
                    JSONArray knowledgeGroupSubList = (JSONArray) itemObj.get("list");
                    initKnowledgeData(groupItems, knowledgeGroupSubList);
                    componentInfo_know_group.setKnowledgeCommodityItemList(groupItems);
                    microPageList.add(componentInfo_know_group);
                }
                break;
        }
    }

    // 从 JSON 对象数组中获取知识商品的数据并初始化到已设置好的对象中
    private void initKnowledgeData(List<KnowledgeCommodityItem> itemList, JSONArray knowledgeListSubList) {
        for (Object subItem : knowledgeListSubList) {
            JSONObject listSubItemObj = (JSONObject) subItem;
            KnowledgeCommodityItem item = new KnowledgeCommodityItem();
            item.setItemTitle(listSubItemObj.getString("title"));
            item.setItemTitleColumn(listSubItemObj.getString("summary"));
            item.setItemImg(listSubItemObj.getString("img_url"));
            String price = "￥" + listSubItemObj.getString("show_price");
            if (price.equals("￥")) { // 表示买了，所以没有价格
                item.setItemPrice(null);
                item.setHasBuy(true);
            } else {
                item.setItemPrice(price);
                item.setHasBuy(false);
            }
            String srcType = listSubItemObj.getString("src_type");
            String srcId = listSubItemObj.getString("src_id");
            // view_count -- 浏览次数 / resource_count -- 更新期数 / purchase_count -- 订阅数
            int viewCount = listSubItemObj.getInteger("view_count") == null ? 0 : listSubItemObj.getInteger("view_count");
            int resourceCount = listSubItemObj.getInteger("resource_count") == null ? 0 : listSubItemObj.getInteger("resource_count");
            item.setSrcType(srcType);
            item.setResourceId(srcId);
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
    private String obtainViewCountDesc (String srcType, int viewCount) {
        if (viewCount == 0) {
            return "";
        }
        switch (srcType) {
            case DecorateEntityType.IMAGE_TEXT: // 图文
                return viewCount + "次阅读";
            case DecorateEntityType.AUDIO: // 音频
            case DecorateEntityType.VIDEO: // 视频
                return viewCount + "次播放";
            case DecorateEntityType.TOPIC: // 大专栏
            case DecorateEntityType.COLUMN: // 专栏
                return "已更新" + viewCount + "期";
            default:
                return "";
        }
    }

    // 拿到请求到的数据后进行界面初始化
    public void init() {
        microPageList = new ArrayList<>();

        // 微页面 id 存在并且不是首页的微页面 id，默认是课程页面
        if (microPageId != -1 && microPageId != 0) {
            microPageCollLayout.setBackground(mContext.getResources().getDrawable(R.mipmap.class_bg));
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
        // 沉浸式初始化
//        microPageAppBar.setPadding(0, statusBarHeight, 0, 0);
    }

    // 初始化头部
    private void initTitle() {
        final int statusBarHeight = StatusBarUtil.getStatusBarHeight(getActivity());
        microPageAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    microToolBar.setVisibility(View.GONE);
                } else if (Math.abs(verticalOffset) >= (appBarLayout.getTotalScrollRange() / 3)) {
                    microToolBar.setVisibility(View.VISIBLE);
                    microToolBar.setPadding(0, statusBarHeight, Dp2Px2SpUtil.dp2px(getActivity(), 20), 0);
                } else {
                    microToolBar.setVisibility(View.GONE);
                }
            }
        });
    }

    DecorateRecyclerAdapter microPageAdapter;
    // 初始化首页内容
    private void initMainContent () {
        // 初始化布局管理器
        LinearLayoutManager llm_content = new LinearLayoutManager(getActivity());
        llm_content.setOrientation(LinearLayout.VERTICAL);
        microPageContentRecyclerView.setLayoutManager(llm_content);
        LinearLayoutManager llm_title = new LinearLayoutManager(getActivity());
        llm_title.setOrientation(LinearLayoutManager.VERTICAL);
        microPageTitleRecyclerView.setLayoutManager(llm_title);
        if (microPageList.get(0).getType().equals(DecorateEntityType.SEARCH_STR)) { // 标题在头顶
            if (microPageId == 0) {
                List<ComponentInfo> titleData = new ArrayList<>();
                microPageAppBar.setVisibility(View.VISIBLE);
                titleData.add(microPageList.get(0));
                DecorateRecyclerAdapter dra = new DecorateRecyclerAdapter(getActivity(), titleData);
                microPageTitleRecyclerView.setAdapter(dra);
                microPageList.remove(0);
            } else {
                List<ComponentInfo> titleData = new ArrayList<>();
                microPageAppBar.setVisibility(View.VISIBLE);
                // 如果有搜索栏，就取前三个，因为已经 remove 掉了，所以都 remove 第一个
                titleData.add(microPageList.remove(0));
                titleData.add(microPageList.remove(0));
                titleData.add(microPageList.remove(0));
                DecorateRecyclerAdapter dra = new DecorateRecyclerAdapter(getActivity(), titleData);
                microPageTitleRecyclerView.setAdapter(dra);
                // 课程页需要一个 title
                initTitle();
            }
        } else {
            microPageAppBar.setVisibility(View.GONE);
        }
        // 初始化适配器
        microPageAdapter = new DecorateRecyclerAdapter(getActivity(), microPageList);
        microPageContentRecyclerView.setAdapter(microPageAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyView = true;
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    /**
     * 资源类型转换 int - str
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
                return null;
        }
    }
}
