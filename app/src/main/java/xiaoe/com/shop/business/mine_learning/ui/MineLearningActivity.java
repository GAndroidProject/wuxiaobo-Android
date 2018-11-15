package xiaoe.com.shop.business.mine_learning.ui;

import android.content.Intent;
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
import xiaoe.com.common.entitys.ComponentInfo;
import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.common.entitys.KnowledgeCommodityItem;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.CollectionListRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.MineLearningRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.decorate.DecorateRecyclerAdapter;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.mine_learning.presenter.MineLearningPresenter;
import xiaoe.com.shop.business.search.presenter.SpacesItemDecoration;
import xiaoe.com.shop.utils.CollectionUtils;
import xiaoe.com.shop.utils.StatusBarUtil;
import xiaoe.com.shop.widget.StatusPagerView;

public class MineLearningActivity extends XiaoeActivity {

    private static final String TAG = "MineLearningActivity";

    private String pageTitle;

    @BindView(R.id.mine_learning_tool_bar)
    Toolbar learningToolbar;
    @BindView(R.id.learning_back)
    ImageView learningBack;
    @BindView(R.id.learning_title)
    TextView learningTitle;
    @BindView(R.id.learning_list)
    RecyclerView learningList;
    @BindView(R.id.learning_loading)
    StatusPagerView learningLoading;

    // 我正在学请求
    MineLearningPresenter mineLearningPresenter;
    // 收藏请求
    CollectionUtils collectionUtils;

    List<ComponentInfo> pageList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_mine_learning);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        pageTitle = intent.getStringExtra("pageTitle");

        initTitle();
        initPage();
        initListener();
    }

    // 沉浸式初始化
    private void initTitle() {
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        learningToolbar.setPadding(0, statusBarHeight, 0, 0);
    }

    // 初始化页面数据
    private void initPage() {
        pageList = new ArrayList<>();
        switch (pageTitle) {
            case "我的收藏":
                learningTitle.setText(pageTitle);
                collectionUtils = new CollectionUtils(this);
                collectionUtils.requestCollectionList(1, 10);
                learningLoading.setVisibility(View.VISIBLE);
                learningLoading.setLoadingState(View.VISIBLE);
                break;
            case "我正在学":
                learningTitle.setText(pageTitle);
                mineLearningPresenter = new MineLearningPresenter(this);
                mineLearningPresenter.requestLearningData(1, 10);
                break;
            default:
                // 其他情况处理（没传 title）
                Log.d(TAG, "initPageData: 没传 title");
                learningLoading.setHintStateVisibility(View.VISIBLE);
                learningLoading.setLoadingState(View.GONE);
                learningLoading.setStateImage(R.mipmap.collection_none);
                learningLoading.setStateText("暂无收藏内容，快去首页逛逛吧");
                break;
        }
    }

    private void initListener() {
        learningBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof CollectionListRequest) {
                int code = result.getInteger("code");
                JSONObject data = (JSONObject) result.get("data");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    initPageData(data);
                } else if (code == NetworkCodes.CODE_COLLECT_LIST_FAILED) {
                    Log.d(TAG, "onMainThreadResponse: 获取收藏列表失败");
                }
            } else if (iRequest instanceof MineLearningRequest) {
                int code = result.getInteger("code");
                JSONObject data;
                try {
                    data = (JSONObject) result.get("data");
                } catch (Exception e) {
                    learningLoading.setHintStateVisibility(View.VISIBLE);
                    learningLoading.setLoadingState(View.GONE);
                    learningLoading.setStateImage(R.mipmap.collection_none);
                    learningLoading.setStateText("暂无正在学的内容，快去首页逛逛吧");
                    e.printStackTrace();
                    return;
                }
                if (code == NetworkCodes.CODE_SUCCEED) {
                    initPageData(data);
                } else if (code == NetworkCodes.CODE_OBTAIN_LEARNING_FAIL) {
                    Log.d(TAG, "onMainThreadResponse: 获取学习记录失败...");
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: 请求失败");
            onBackPressed();
        }
    }

    // 初始化收藏页和我正在学数据
    private void initPageData(JSONObject data) {
        JSONArray goodsList = (JSONArray) data.get("goods_list");
        List<KnowledgeCommodityItem> itemList = new ArrayList<>();
        if (goodsList == null) {
            // 收藏列表为空，显示为空的页面
            learningLoading.setHintStateVisibility(View.VISIBLE);
            learningLoading.setLoadingState(View.GONE);
            learningLoading.setStateImage(R.mipmap.collection_none);
            learningLoading.setStateText("暂无收藏内容，快去首页逛逛吧");
            return;
        }
        ComponentInfo knowledgeList = new ComponentInfo();
        knowledgeList.setTitle("");
        knowledgeList.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
        knowledgeList.setSubType(DecorateEntityType.KNOWLEDGE_LIST);
        // 隐藏 title
        knowledgeList.setHideTitle(true);
        // 因为这个接口拿到的 resourceType 是 int 类型，转成字符串存起来
        for (Object goodItem : goodsList) {
            KnowledgeCommodityItem item = new KnowledgeCommodityItem();
            JSONObject infoItem = (JSONObject) goodItem;
            if ("我正在学".equals(pageTitle)) { // 我正在学列表
                JSONObject learningInfo = (JSONObject) infoItem.get("info");
                String learningId = learningInfo.getString("goods_id");
                String learningType = convertInt2Str(learningInfo.getInteger("goods_type"));
                String learningTitle = learningInfo.getString("title");
                String learningImg = learningInfo.getString("img_url");
                String learningOrg = learningInfo.getString("org_summary");

                // 我正在学的数据
                item.setItemImg(learningImg);
                item.setItemTitle(learningTitle);
                item.setResourceId(learningId);
                item.setSrcType(learningType);
                item.setItemTitleColumn(learningOrg);
                item.setItemPrice("已购");
                item.setCollectionList(false);
                itemList.add(item);
            } else if ("我的收藏".equals(pageTitle)) { // 我的收藏列表
                JSONObject collectionInfo = (JSONObject) infoItem.get("org_content");
                String collectionId = infoItem.getString("content_id");
                String collectionType = convertInt2Str(infoItem.getInteger("content_type"));
                String collectionTitle = collectionInfo.getString("title");
                String collectionImg = collectionInfo.getString("img_url");
                String collectionPrice = collectionInfo.getString("price");

                item.setItemImg(collectionImg);
                item.setItemTitle(collectionTitle);
                item.setResourceId(collectionId);
                item.setSrcType(collectionType);
                item.setCollectionList(true);
                if ("0".equals(collectionPrice) || "0.0".equals(collectionPrice) || "".equals(collectionPrice) || collectionPrice == null) {
                    item.setItemPrice("已购");
                } else {
                    item.setItemPrice("￥" + collectionPrice);
                }
                itemList.add(item);
            }
        }
        knowledgeList.setKnowledgeCommodityItemList(itemList);
        pageList.add(knowledgeList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        learningList.setLayoutManager(layoutManager);
        DecorateRecyclerAdapter decorateRecyclerAdapter = new DecorateRecyclerAdapter(this, pageList);
        learningList.setAdapter(decorateRecyclerAdapter);
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration();
        spacesItemDecoration.setMargin(
                Dp2Px2SpUtil.dp2px(this, 20),
                Dp2Px2SpUtil.dp2px(this, 0),
                Dp2Px2SpUtil.dp2px(this, 20),
                Dp2Px2SpUtil.dp2px(this, 0));
        learningList.addItemDecoration(spacesItemDecoration);
        learningLoading.setVisibility(View.GONE);
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
