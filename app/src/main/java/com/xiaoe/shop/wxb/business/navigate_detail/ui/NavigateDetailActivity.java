package com.xiaoe.shop.wxb.business.navigate_detail.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoe.common.app.Global;
import com.xiaoe.common.entitys.ComponentInfo;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.KnowledgeCommodityItem;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.decorate.DecorateRecyclerAdapter;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.navigate_detail.presenter.NavigateDetailPresenter;
import com.xiaoe.shop.wxb.business.search.ui.SearchActivity;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;

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
    @BindView(R.id.navigate_content)
    RecyclerView navigateContent;

    Intent intent;
    String pageTitle;
    String groupId;
    String resourceType = DecorateEntityType.RESOURCE_TAG; // 商品分组的资源类型

    NavigateDetailPresenter navigateDetailPresenter;

    JSONArray columnsJSONList; // 专栏/大专栏的集合
    JSONArray itemJSONList; // 单品的集合（图文，音视频）

    List<ComponentInfo> pageDataList; // 页面数据

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

        navigateToolbar.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);

        // 发送网络请求
        navigateDetailPresenter = new NavigateDetailPresenter(this);
        navigateDetailPresenter.requestData(groupId);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        navigateTitle.setText(pageTitle);
    }

    private void initData() {

        columnsJSONList = new JSONArray();
        itemJSONList = new JSONArray();
        pageDataList = new ArrayList<>();

//        List<ComponentInfo> tempData = new ArrayList<>();
//
//        // 知识商品假数据 -- 列表形式
//        ComponentInfo componentInfo_know_list = new ComponentInfo();
//        componentInfo_know_list.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
//        componentInfo_know_list.setSubType("knowledgeList");
//        List<KnowledgeCommodityItem> listItems = new ArrayList<>();
//        // 有价格没买
//        KnowledgeCommodityItem listItem_1 = new KnowledgeCommodityItem();
//        listItem_1.setItemTitle("我的财富计划新中产必修理财课程");
//        listItem_1.setItemImg("http://img05.tooopen.com/images/20141020/sy_73154627197.jpg");
//        listItem_1.setItemPrice("￥999.99");
//        listItem_1.setHasBuy(false);
//        listItem_1.setItemDesc("已更新至135期");
//        // 有价格买了
//        KnowledgeCommodityItem listItem_2 = new KnowledgeCommodityItem();
//        listItem_2.setItemTitle("我的财富计划新中产必修理财课程杀杀杀");
//        listItem_2.setItemImg("http://img.zcool.cn/community/01f39a59a7affba801211d25185cd3.jpg@1280w_1l_2o_100sh.jpg");
//        listItem_2.setItemPrice("￥85.55");
//        listItem_2.setHasBuy(true);
//        listItem_2.setItemDesc("已更新至120期");
//        // 没有价格，免费的
//        KnowledgeCommodityItem listItem_3 = new KnowledgeCommodityItem();
//        listItem_3.setItemTitle("我的财富计划新中产必修理财课程哒哒哒");
//        listItem_3.setItemImg("http://img.zcool.cn/community/01951d55dd8f336ac7251df845a2ae.jpg");
//        listItem_3.setItemDesc("已更新至101期");
//        listItems.add(listItem_1);
//        listItems.add(listItem_2);
//        listItems.add(listItem_3);
//        componentInfo_know_list.setKnowledgeCommodityItemList(listItems);
//        // 知识商品假数据 -- 分组形式
//        ComponentInfo componentInfo_know_group = new ComponentInfo();
//        componentInfo_know_group.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
//        componentInfo_know_group.setSubType("knowledgeGroup");
//        componentInfo_know_group.setTitle("栏目二");
//        componentInfo_know_group.setDesc("");
//        List<KnowledgeCommodityItem> groupItems = new ArrayList<>();
//        KnowledgeCommodityItem groupItem_1 = new KnowledgeCommodityItem();
//        groupItem_1.setItemTitle("我的财富计划 新中产必修理财课程");
//        groupItem_1.setItemImg("http://pic15.nipic.com/20110619/7763155_101852942332_2.jpg");
//        groupItem_1.setHasBuy(false);
//        groupItem_1.setItemDesc("已更新至135期");
//        groupItem_1.setItemPrice("￥980");
//        KnowledgeCommodityItem groupItem_2 = new KnowledgeCommodityItem();
//        groupItem_2.setItemTitle("我的财富计划");
//        groupItem_2.setItemImg("http://pic38.nipic.com/20140212/17942401_101320663138_2.jpg");
//        groupItem_2.setHasBuy(true);
//        groupItem_2.setItemPrice("￥555.21");
//        groupItem_2.setItemDesc("187次播放");
//        KnowledgeCommodityItem groupItem_3 = new KnowledgeCommodityItem();
//        groupItem_3.setItemTitle("学会规划自己的职场 必修理财课程");
//        groupItem_3.setItemImg("http://gbres.dfcfw.com/Files/picture/20170925/9B00CEC6F06B756A4A9C256E870A324B.jpg");
//        groupItem_3.setHasBuy(false);
//        groupItem_3.setItemDesc("已更新至134期");
//        KnowledgeCommodityItem groupItem_4 = new KnowledgeCommodityItem();
//        groupItem_4.setItemTitle("这个文案是测试的，随便搞搞");
//        groupItem_4.setItemImg("http://img.zcool.cn/community/01a9e45a60510ca8012113c7899c89.jpg@1280w_1l_2o_100sh.jpg");
//        groupItem_4.setHasBuy(true);
//        groupItem_4.setItemPrice("￥123.12");
//        groupItem_4.setItemDesc("130次播放");
//        groupItems.add(groupItem_1);
//        groupItems.add(groupItem_2);
//        groupItems.add(groupItem_3);
//        groupItems.add(groupItem_4);
//        componentInfo_know_group.setKnowledgeCommodityItemList(groupItems);
//
//        tempData.add(componentInfo_know_list);
//        tempData.add(componentInfo_know_group);
    }

    private void initListener() {
        navigateBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        navigateSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigateDetailActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            int code = result.getInteger("code");
            if (code == NetworkCodes.CODE_SUCCEED) {
                JSONObject data = (JSONObject) result.get("data");
                JSONObject component = (JSONObject) data.get("component");
                separateComponent(component);
                initPageData();
            } else if (code == NetworkCodes.CODE_GOODS_GROUPS_NOT_FIND) {
                Log.d(TAG, "onMainThreadResponse: 商品分组不存在");
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: 请求失败...");
            onBackPressed();
        }
    }

    // 分类数据 -- 将专栏和大专栏分一组，单品分一组
    private void separateComponent(JSONObject component) {
        JSONArray resultList = (JSONArray) component.get("list");
        for (Object item : resultList) {
            String srcType = ((JSONObject) item).getString("src_type");
            if (srcType.equals(DecorateEntityType.COLUMN) ||
                    srcType.equals(DecorateEntityType.TOPIC)) { // 专栏、大专栏
                columnsJSONList.add(item);
            } else if (srcType.equals(DecorateEntityType.IMAGE_TEXT) ||
                    srcType.equals(DecorateEntityType.AUDIO) ||
                    srcType.equals(DecorateEntityType.VIDEO)) { // 图文、音频、视频
                itemJSONList.add(item);
            }
        }
    }

    // 初始化页面数据
    private void initPageData() {
        ComponentInfo knowledgeListComponent = new ComponentInfo();
        ComponentInfo knowledgeGroupComponent = new ComponentInfo();
        List<KnowledgeCommodityItem> listItems = new ArrayList<>();
        List<KnowledgeCommodityItem> groupItems = new ArrayList<>();

        // 初始化专栏/大专栏的信息
        knowledgeListComponent.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
        knowledgeListComponent.setSubType(DecorateEntityType.KNOWLEDGE_LIST);
        // 导航页面的 title 不隐藏
        knowledgeListComponent.setHideTitle(false);
        knowledgeListComponent.setTitle("栏目一");
        initKnowledgeData(listItems, columnsJSONList);
        knowledgeListComponent.setKnowledgeCommodityItemList(listItems);
        pageDataList.add(knowledgeListComponent);

        // 初始化单品的信息
        knowledgeGroupComponent.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
        knowledgeGroupComponent.setSubType(DecorateEntityType.KNOWLEDGE_GROUP);
        // 当行页面的宫格显示是单品，所以没有查看更多
        knowledgeGroupComponent.setTitle("栏目二");
        knowledgeGroupComponent.setDesc("");
        initKnowledgeData(groupItems, itemJSONList);
        knowledgeGroupComponent.setKnowledgeCommodityItemList(groupItems);
        pageDataList.add(knowledgeGroupComponent);
        initRecycler();
    }

    // 初始化 recyclerView
    private void initRecycler() {
        DecorateRecyclerAdapter decorateRecyclerAdapter = new DecorateRecyclerAdapter(this, pageDataList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        navigateContent.setLayoutManager(llm);
        navigateContent.setAdapter(decorateRecyclerAdapter);
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
}
