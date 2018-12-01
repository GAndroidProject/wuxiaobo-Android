package com.xiaoe.shop.wxb.business.navigate_detail.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xiaoe.common.app.Global;
import com.xiaoe.common.entitys.ComponentInfo;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.KnowledgeCommodityItem;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.CommodityGroupRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.decorate.DecorateRecyclerAdapter;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.navigate_detail.presenter.NavigateDetailPresenter;
import com.xiaoe.shop.wxb.business.search.ui.SearchActivity;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.widget.StatusPagerView;

public class NavigateDetailActivity extends XiaoeActivity {

    private static final String TAG = "NavigateDetailActivity";

    @BindView(R.id.navigate_tool_bar)
    Toolbar navigateToolbar;
    @BindView(R.id.navigate_back)
    ImageView navigateBack;
    @BindView(R.id.navigate_title)
    TextView navigateTitle;
    @BindView(R.id.navigate_search)
    ImageView navigateSearch;
    @BindView(R.id.navigate_refresh)
    SmartRefreshLayout navigateRefresh;
    @BindView(R.id.navigate_content)
    RecyclerView navigateContent;
    @BindView(R.id.navigate_loading)
    StatusPagerView navigateLoading;

    Intent intent;
    String pageTitle;
    String groupId;
    String resourceType = DecorateEntityType.RESOURCE_TAG; // 商品分组的资源类型

    NavigateDetailPresenter navigateDetailPresenter;

    JSONArray columnsJSONList; // 专栏/大专栏的集合
    JSONArray itemJSONList; // 单品的集合（图文，音视频）

    List<ComponentInfo> pageDataList; // 页面数据
    List<KnowledgeCommodityItem> dataList; // 列表 item 数据
    String lastId;
    int pageIndex = 1;
    int pageSize = 10;

    DecorateRecyclerAdapter decorateRecyclerAdapter;
    ComponentInfo listComponentInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_detail);
        ButterKnife.bind(this);

        intent = getIntent();
        pageTitle = intent.getStringExtra("pageTitle");
        groupId = intent.getStringExtra("resourceId");

        //状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // 发送网络请求
        navigateDetailPresenter = new NavigateDetailPresenter(this);
        navigateDetailPresenter.requestData(groupId, pageIndex, pageSize, lastId);

        initView();
        newInitData();
        initListener();
    }

    private void initView() {
        navigateTitle.setText(pageTitle);
        navigateRefresh.setEnableRefresh(true);
        navigateRefresh.setEnableLoadMore(true);
        navigateLoading.setLoadingState(View.VISIBLE);
    }

    /**
     * 使用 newInitData 替代（无需进行分类展示）
     *
     * @deprecated
     */
    private void initData() {
        columnsJSONList = new JSONArray();
        itemJSONList = new JSONArray();
        pageDataList = new ArrayList<>();
    }

    private void newInitData() {
        pageDataList = new ArrayList<>();
        listComponentInfo = new ComponentInfo();
        dataList = new ArrayList<>();
    }

    private void initListener() {
        navigateBack.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                onBackPressed();
            }
        });
        navigateSearch.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                Intent intent = new Intent(NavigateDetailActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        navigateRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (pageIndex != 1) {
                    pageIndex = 1;
                    navigateRefresh.setEnableLoadMore(true);
                }
                lastId = "";
                decorateRecyclerAdapter = null;
                dataList.clear();
                pageDataList.clear();
                if (navigateDetailPresenter == null) {
                    navigateDetailPresenter = new NavigateDetailPresenter(NavigateDetailActivity.this);
                }
                navigateDetailPresenter.requestData(groupId, pageIndex, pageSize, lastId);
            }
        });
        navigateRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (navigateDetailPresenter == null) {
                    navigateDetailPresenter = new NavigateDetailPresenter(NavigateDetailActivity.this);
                }
                if (TextUtils.isEmpty(lastId)) { // lastId 为空，必要请求更多
                    navigateRefresh.setNoMoreData(true);
                    navigateRefresh.setEnableLoadMore(false);
                    navigateRefresh.finishLoadMore();
                    return;
                }
                navigateDetailPresenter.requestData(groupId, ++pageIndex, pageSize, lastId);
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof CommodityGroupRequest) {
                int code = result.getInteger("code");
                navigateRefresh.finishRefresh();
                navigateRefresh.finishLoadMore();
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = (JSONObject) result.get("data");
                    newInitPageData(data);
                    navigateLoading.setLoadingFinish();
                } else if (code == NetworkCodes.CODE_GOODS_GROUPS_NOT_FIND) {
                    Log.d(TAG, "onMainThreadResponse: 获取数据失败");
                    navigateLoading.setPagerState(StatusPagerView.FAIL, getString(R.string.request_fail), R.mipmap.error_page);
                } else {
                    Log.d(TAG, "onMainThreadResponse: 其他异常情况");
                    navigateLoading.setPagerState(StatusPagerView.FAIL, getString(R.string.request_fail), R.mipmap.error_page);
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: 请求失败...");
            navigateRefresh.finishRefresh();
            navigateRefresh.finishLoadMore();
            if (dataList.size() > 0) {
                navigateLoading.setLoadingFinish();
                Toast.makeText(this, getString(R.string.network_error_text), Toast.LENGTH_SHORT).show();
            } else {
                navigateLoading.setPagerState(StatusPagerView.FAIL, getString(R.string.request_fail), R.mipmap.error_page);
            }
        }
    }

    /**
     * 分类数据 -- 将专栏和大专栏分一组，单品分一组（对齐 h5，不再分类，全部以列表的形式显示 12.1）
     * @deprecated
     */
    private void separateComponent(JSONObject component) {
        JSONArray resultList = (JSONArray) component.get("list");
        for (int i = 0; i < resultList.size(); i++) {
            String srcType = ((JSONObject) resultList.get(i)).getString("src_type");
            if (srcType.equals(DecorateEntityType.COLUMN) ||
                    srcType.equals(DecorateEntityType.TOPIC)) { // 专栏、大专栏
                columnsJSONList.add(resultList.get(i));
            } else if (srcType.equals(DecorateEntityType.IMAGE_TEXT) ||
                    srcType.equals(DecorateEntityType.AUDIO) ||
                    srcType.equals(DecorateEntityType.VIDEO)) { // 图文、音频、视频
                itemJSONList.add(resultList.get(i));
            }
            if (i == resultList.size() - 1) { // 最后一个
                lastId = ((JSONObject) resultList.get(i)).getString("src_id");
            }
        }
    }

    /**
     * 初始化页面数据（无需再分类展示）
     *
     * @deprecated 使用 newInitPageData 替代
     */
    private void initPageData() {
        ComponentInfo knowledgeListComponent = new ComponentInfo();
        ComponentInfo knowledgeGroupComponent = new ComponentInfo();
        List<KnowledgeCommodityItem> listItems = new ArrayList<>();
        List<KnowledgeCommodityItem> groupItems = new ArrayList<>();

        // 初始化专栏/大专栏的信息
        knowledgeListComponent.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
        knowledgeListComponent.setSubType(DecorateEntityType.KNOWLEDGE_LIST_STR);
        // 导航页面的 title 不隐藏
        knowledgeListComponent.setHideTitle(false);
        knowledgeListComponent.setTitle(getString(R.string.series_courses));
        knowledgeGroupComponent.setDesc("");
        initKnowledgeData(columnsJSONList);
        knowledgeListComponent.setKnowledgeCommodityItemList(listItems);
        pageDataList.add(knowledgeListComponent);

        // 初始化单品的信息
        knowledgeGroupComponent.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
        knowledgeGroupComponent.setSubType(DecorateEntityType.KNOWLEDGE_GROUP_STR);
        // 当行页面的宫格显示是单品，所以没有查看更多
        knowledgeListComponent.setHideTitle(false);
        knowledgeGroupComponent.setTitle(getString(R.string.item_course));
        knowledgeGroupComponent.setDesc("");
        initKnowledgeData(itemJSONList);
        knowledgeGroupComponent.setKnowledgeCommodityItemList(groupItems);
        pageDataList.add(knowledgeGroupComponent);
        initRecycler();
    }

    /**
     * 初始化页面数据
     *
     * @param data
     */
    private void newInitPageData(JSONObject data) {
        JSONObject component = (JSONObject) data.get("component");
        this.lastId = data.getString("last_id");

        String title = component.getString("title");
        JSONArray componentList = (JSONArray) component.get("list");

        if (decorateRecyclerAdapter == null) {
            listComponentInfo.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
            listComponentInfo.setSubType(DecorateEntityType.KNOWLEDGE_LIST_STR);
            if (TextUtils.isEmpty(title)) {
                listComponentInfo.setHideTitle(true);
            } else {
                listComponentInfo.setHideTitle(false);
                listComponentInfo.setTitle(title);
                listComponentInfo.setDesc("");
            }
        }

        initKnowledgeData(componentList);
        if (dataList.size() != 0) {
            listComponentInfo.setKnowledgeCommodityItemList(dataList);
            initRecycler();
        }
    }

    // 初始化 recyclerView
    private void initRecycler() {
        if (decorateRecyclerAdapter != null) {
            decorateRecyclerAdapter.notifyDataSetChanged();
        } else {
            pageDataList.add(listComponentInfo);
            decorateRecyclerAdapter = new DecorateRecyclerAdapter(this, pageDataList);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            navigateContent.setLayoutManager(llm);
            navigateContent.setAdapter(decorateRecyclerAdapter);
        }
    }

    // 从 JSON 对象数组中获取知识商品的数据并初始化到已设置好的对象中
    private void initKnowledgeData(JSONArray knowledgeListSubList) {
        if (dataList.size() > 0 && knowledgeListSubList.size() == 0) { // 上拉加载没有数据
            navigateRefresh.setEnableLoadMore(false);
            navigateRefresh.setNoMoreData(true);
            return;
        }
        for (int i = 0; i < knowledgeListSubList.size(); i++) {
            JSONObject listSubItemObj = (JSONObject) knowledgeListSubList.get(i);
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
            int viewCount = listSubItemObj.getInteger("view_count") == null ? 0 : listSubItemObj.getInteger("view_count");
            int purchaseCount = listSubItemObj.getInteger("purchase_count") == null ? 0 : listSubItemObj.getInteger("purchase_count");
            item.setSrcType(srcType);
            item.setResourceId(srcId);
            // 专栏或者大专栏订阅量就是 purchaseCount
            if (srcType.equals(DecorateEntityType.COLUMN) || srcType.equals(DecorateEntityType.TOPIC)) {
                viewCount = purchaseCount;
            }
            String viewDesc = obtainViewCountDesc(srcType, viewCount);
            item.setItemDesc(viewDesc);
            dataList.add(item);
        }
    }

    // 根据子类型获取浏览字段
    private String obtainViewCountDesc (String srcType, int viewCount) {
        if (viewCount == 0) {
            return "";
        }
        switch (srcType) {
            case DecorateEntityType.IMAGE_TEXT: // 图文
            case DecorateEntityType.AUDIO: // 音频
            case DecorateEntityType.VIDEO: // 视频
                return viewCount + "次学习";
            case DecorateEntityType.TOPIC: // 大专栏
            case DecorateEntityType.COLUMN: // 专栏
                return String.format(getString(R.string.stages_text), viewCount);
            default:
                return "";
        }
    }
}
