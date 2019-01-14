package com.xiaoe.shop.wxb.business.course_more.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.xiaoe.shop.wxb.business.course_more.presenter.CourseMorePresenter;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;

public class CourseMoreActivity extends XiaoeActivity {

    private static final String TAG = "CourseMoreActivity";

    @BindView(R.id.more_wrap)
    LinearLayout courseMoreWrap;
    @BindView(R.id.course_more_tool_bar)
    Toolbar courseMoreToolbar;
    @BindView(R.id.course_more_back)
    ImageView courseMoreBack;
    @BindView(R.id.course_more_title)
    TextView courseMoreTitle;
    @BindView(R.id.course_more_content)
    RecyclerView courseMoreContent;

    Intent intent;
    String groupId; // 分组 id
    String title; // 分组 title
    int pageNum = 1; // 页码
    int pageSize = 10; // 页数
    String lastId; // 上次请求最后一个的 id
    boolean loadFinished; // 是否请求完成 -- 方便扩展加载更多，现在先不用

    List<ComponentInfo> dataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_more);
        ButterKnife.bind(this);

        //状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        title = intent.getStringExtra("title") == null? getString(R.string.course_more_title) : intent.getStringExtra("title");
        courseMoreTitle.setText(title);
        CourseMorePresenter courseMorePresenter = new CourseMorePresenter(this);
        // 第一次请求 lastId 为 ""
        courseMorePresenter.requestData(groupId, pageNum, pageSize, lastId);

        initData();
        initListener();
    }

    private void initData() {
        dataList = new ArrayList<>();
    }

    private void initListener() {
        courseMoreBack.setOnClickListener(new View.OnClickListener() {
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
            int code = result.getInteger("code");
            JSONObject data = (JSONObject) result.get("data");
            lastId = data.getInteger("last_id").toString();
            loadFinished = data.getBoolean("finished");
            JSONObject component = (JSONObject) data.get("component");
            if (code == NetworkCodes.CODE_SUCCEED) {
                initPageData(component);
            } else if (code == NetworkCodes.CODE_GOODS_GROUPS_NOT_FIND) {
                Log.d(TAG, "onMainThreadResponse: 商品分组不存在");
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: 请求失败...");
        }
    }

    private void initPageData(JSONObject component) {
        // 0 列表形式，2 宫格形式(默认)
        int showStyle = component.getInteger("list_style") == null ? 2 : component.getInteger("list_style");
        ComponentInfo componentInfo_knowledge = new ComponentInfo();
        componentInfo_knowledge.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
        if (showStyle == 0) {
            componentInfo_knowledge.setSubType(DecorateEntityType.KNOWLEDGE_LIST_STR);
        } else if (showStyle == 2) {
            componentInfo_knowledge.setSubType(DecorateEntityType.KNOWLEDGE_GROUP_STR);
        }
        // 隐藏知识商品组件的 title
        componentInfo_knowledge.setTitle("");
        componentInfo_knowledge.setDesc("");
        componentInfo_knowledge.setHideTitle(true);
        JSONArray componentList = (JSONArray) component.get("list");
        List<KnowledgeCommodityItem> itemListObj = new ArrayList<>();
        for (Object itemObj : componentList) {
            JSONObject listSubItemObj = (JSONObject) itemObj;
            KnowledgeCommodityItem item = new KnowledgeCommodityItem();
            item.setItemTitle(listSubItemObj.getString("title"));
            item.setItemTitleColumn(listSubItemObj.getString("summary"));
            item.setItemImg(listSubItemObj.getString("img_url_compressed_larger"));
//            item.setItemImg(listSubItemObj.getString("img_url"));
            String price = listSubItemObj.getString("show_price") + getString(R.string.wxb_virtual_unit);
            int paymentType = listSubItemObj.getInteger("payment_type") == null ? -1 : listSubItemObj.getInteger("payment_type");
            boolean hasBuy = false;
            boolean isFree = false;
            if (price.equals(getString(R.string.wxb_virtual_unit)) && paymentType == 1) { // 免费
                price = "";
                isFree = true;
            } else if (price.equals(getString(R.string.wxb_virtual_unit)) && paymentType == 2) { // 已购
                price = "";
                hasBuy = true;
            }
            item.setItemPrice(price);
            item.setHasBuy(hasBuy);
            item.setFree(isFree);
            String srcType = listSubItemObj.getString("src_type");
            String srcId = listSubItemObj.getString("src_id");
            String viewCount = TextUtils.isEmpty(listSubItemObj.getString("view_count")) ? "" : listSubItemObj.getString("view_count");
            String resourceCount = TextUtils.isEmpty(listSubItemObj.getString("resource_count")) ? "" : listSubItemObj.getString("resource_count");
            item.setSrcType(srcType);
            item.setResourceId(srcId);
            // 专栏或者大专栏订阅量就是 purchaseCount
            if (srcType.equals(DecorateEntityType.COLUMN) || srcType.equals(DecorateEntityType.TOPIC)) {
                viewCount = resourceCount;
            }
            String viewDesc = obtainViewCountDesc(srcType, viewCount);
            item.setResourceCount(resourceCount);
            item.setItemDesc(viewDesc);
            itemListObj.add(item);
        }
        componentInfo_knowledge.setKnowledgeCommodityItemList(itemListObj);
        dataList.add(componentInfo_knowledge);
        initRecycler();
    }

    // 根据子类型获取浏览字段
    private String obtainViewCountDesc (String srcType, String viewCount) {
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
                return String.format(getString(R.string.stages_text_str), viewCount);
            default:
                return "";
        }
    }

    private void initRecycler() {

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayout.VERTICAL);
        courseMoreContent.setLayoutManager(llm);

        DecorateRecyclerAdapter adapter = new DecorateRecyclerAdapter(this, dataList);
        adapter.showLastItem(false);
        courseMoreContent.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
