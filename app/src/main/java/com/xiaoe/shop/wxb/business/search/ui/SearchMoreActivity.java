package com.xiaoe.shop.wxb.business.search.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.xiaoe.common.app.Global;
import com.xiaoe.common.entitys.ComponentInfo;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.KnowledgeCommodityItem;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.decorate.DecorateRecyclerAdapter;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.search.presenter.SearchPresenter;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.widget.StatusPagerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SearchMoreActivity extends XiaoeActivity {

    private static final String TAG = "SearchMoreActivity";

    Unbinder unbinder;
    String cmd = "xe.shop.search.more/1.0.0"; // 加载更多接口
    SearchPresenter searchPresenter;

    Intent intent;
    String keyword;
    int pageIndex = 1;
    int pageSize = 10;
    String requestId;
    String typeTitle;
    int type;

    @BindView(R.id.search_more_refresh)
    SmartRefreshLayout searchMoreRefresh;
    @BindView(R.id.search_more_content)
    RecyclerView searchMoreRecycler;
    @BindView(R.id.search_more_loading)
    StatusPagerView searchMoreLoading;

    @BindView(R.id.title_back)
    ImageView searchMoreBack;
    @BindView(R.id.title_content)
    TextView searchMoreTitle;
    @BindView(R.id.title_end)
    TextView searchMoreDesc;

    DecorateRecyclerAdapter decorateRecyclerAdapter;
    LinearLayoutManager linearLayoutManager;
    boolean hasDecorate;
    List<ComponentInfo> pageList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_more);
        unbinder = ButterKnife.bind(this);

        intent = getIntent();

        //状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initData();
        initListener();
        sendRequest(pageIndex);
    }

    private void initData() {
        keyword = intent.getStringExtra("keyword");
        type = intent.getIntExtra("resourceType", -1);
        typeTitle = obtainTitle(type);
        searchMoreTitle.setText(typeTitle);
        searchMoreDesc.setVisibility(View.GONE);
        if (pageList == null) {
            pageList = new ArrayList<>();
        }
    }

    private void initListener() {
        searchMoreLoading.setVisibility(View.VISIBLE);
        searchMoreLoading.setLoadingState(View.VISIBLE);

        searchMoreRefresh.setEnableRefresh(false); // 禁止下拉刷新
        searchMoreRefresh.setEnableLoadMore(true);

        searchMoreRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                sendRequest(++pageIndex);
            }
        });

        searchMoreBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void sendRequest(int pageIndex) {
        if (type != -1) {
            if (searchPresenter == null) {
                searchPresenter = new SearchPresenter(this, cmd);
            }
            requestId = obtainRandomStr();
            searchPresenter.requestSearchMore(keyword, pageIndex, pageSize, requestId, type);
        } else {
            Toast("页面加载异常，请重试");
        }
    }

    // 生成唯一字符串，用以请求下一页的数据
    private String obtainRandomStr() {
        return "zak" + System.currentTimeMillis();
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        if (success) {
            JSONObject result = (JSONObject) entity;
            if ((NetworkEngine.API_THIRD_BASE_URL + cmd).equals(iRequest.getCmd())) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = (JSONObject) result.get("data");
                    initPageData(data);
                    searchMoreLoading.setLoadingFinish();
                } else {
                    Log.d(TAG, "onMainThreadResponse: 搜索结果有误 --- " + result.getString("msg"));
                    searchMoreLoading.setPagerState(StatusPagerView.FAIL, getString(R.string.search_error), R.mipmap.error_page);
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: 加载更多请求失败...");
            searchMoreLoading.setPagerState(StatusPagerView.FAIL, getString(R.string.search_error), R.mipmap.error_page);
        }
    }

    private void initPageData(JSONObject data) {
        JSONArray dataList = (JSONArray) data.get("list");

        if (dataList.size() == 0 && decorateRecyclerAdapter != null) { // 上拉加载没有更多数据了
            searchMoreRefresh.setNoMoreData(true);
            searchMoreRefresh.setEnableLoadMore(false);
            searchMoreRefresh.finishLoadMore();
            return;
        }

        searchMoreRefresh.finishLoadMore();

        ComponentInfo itemComponentInfo = new ComponentInfo();
        itemComponentInfo.setHideTitle(true);
        // itemComponentInfo.setTitle(typeTitle); // 暂时隐藏组件的 title
        itemComponentInfo.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
        itemComponentInfo.setSubType(DecorateEntityType.KNOWLEDGE_LIST_STR);
        itemComponentInfo.setSearchType(type);
        itemComponentInfo.setDesc("");

        List<KnowledgeCommodityItem> knowledgeList = new ArrayList<>();
        for (Object listItem : dataList) {
            KnowledgeCommodityItem commodityItem = new KnowledgeCommodityItem();
            JSONObject jsonItem = (JSONObject) listItem;

            String title = jsonItem.getString("title");
            String imgUrl = jsonItem.getString("img_url");
            float price = jsonItem.getFloat("price");
            String resourceId = jsonItem.getString("id");
            String resourceType = convertInt2Str(type);
            String summary = jsonItem.getString("summary");
            String priceStr;
            if (price == 0) {
                priceStr = "";
            } else {
                priceStr = "￥" + price;
            }

            commodityItem.setItemTitle(title);
            commodityItem.setItemImg(imgUrl);
            commodityItem.setItemTitleColumn(summary);
            commodityItem.setSrcType(resourceType);
            commodityItem.setResourceId(resourceId);
            commodityItem.setItemPrice(priceStr);

            knowledgeList.add(commodityItem);
        }

        // list 大于 0 进行赋值
        if (knowledgeList.size() > 0) {
            itemComponentInfo.setKnowledgeCommodityItemList(knowledgeList);
            if (hasDecorate) {
                itemComponentInfo.setNeedDecorate(false);
            } else {
                itemComponentInfo.setNeedDecorate(true);
            }
            pageList.add(itemComponentInfo);
        } else {
            // 暂时显示这个..
            searchMoreLoading.setPagerState(StatusPagerView.FAIL, getString(R.string.loading_error), R.mipmap.error_page);
        }

        if (!hasDecorate) {
            initRecycler();
        }
    }

    private void initRecycler() {
        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchMoreRecycler.setLayoutManager(linearLayoutManager);
        decorateRecyclerAdapter = new DecorateRecyclerAdapter(this, pageList,true);
        searchMoreRecycler.setAdapter(decorateRecyclerAdapter);
        decorateRecyclerAdapter.notifyDataSetChanged();
        hasDecorate = true;
    }

    // 根据资源类型获取对应资源的名称...
    private String obtainTitle(int type) {
        switch (type) {
            case 1:
                return getString(R.string.image_text);
            case 2:
                return getString(R.string.audio_text);
            case 3:
                return getString(R.string.video_text);
            case 5:
                return getString(R.string.member_text);
            case 6:
                return getString(R.string.column_text);
            case 8:
                return getString(R.string.big_column_text);
            default:
                return "";
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
            case 5: // 会员
                return DecorateEntityType.MEMBER;
            case 6: // 专栏
                return DecorateEntityType.COLUMN;
            case 8: // 大专栏
                return DecorateEntityType.TOPIC;
            default:
                return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
